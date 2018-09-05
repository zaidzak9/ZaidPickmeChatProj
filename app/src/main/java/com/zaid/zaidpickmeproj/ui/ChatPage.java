package com.zaid.zaidpickmeproj.ui;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.zaid.zaidpickmeproj.R;
import com.zaid.zaidpickmeproj.adapter.ChatAdapter;
import com.zaid.zaidpickmeproj.helper.Database;
import com.zaid.zaidpickmeproj.helper.ValidationHelper;
import com.zaid.zaidpickmeproj.model.ChatBody;
import com.zaid.zaidpickmeproj.model.ChatMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatPage extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    @BindView(R.id.messageEdit)
    EditText messageET;
    @BindView(R.id.messagesContainer)
    ListView messagesContainer;
    @BindView(R.id.chatSendButton)
    Button chatSendButton;
    @BindView(R.id.quick_message)
    Button quick_message;
    @BindView(R.id.toolbarchat)
    Toolbar toolbarchat;
    @BindView(R.id.toolbarBtn_cancel_button)
    ImageView toolbarBtn_cancel_button;
    @BindView(R.id.toolbar_title_order)
    TextView toolbar_title_order;
    @BindView(R.id.ChatParentLayout)
    RelativeLayout ChatParentLayout;
    Cursor cursor;
    Database db;
    private ChatAdapter adapter;
    String DateandTime, messageText,chatname,channel_id;
    ChatBody chatBody;
    View view;
    PubNub pubnub;
    boolean didisend = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat_page);
        //butterknife library used for binding between ui elements and activity
        ButterKnife.bind(this);
        Intent intent = getIntent();
        chatname = intent.getStringExtra("chatname");
        channel_id = intent.getStringExtra("channelid");
        toolbar_title_order.setText(channel_id);
        adapter = new ChatAdapter(ChatPage.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-bc7e24b2-b0df-11e8-a4a4-da4a4ec5c754");
        pnConfiguration.setPublishKey("pub-c-1d95c273-73e2-4123-97dc-3dae17e385d5");

        pubnub = new PubNub(pnConfiguration);
        //creating object of database for usage
        db = new Database(this);


        //checking if database contains any past messages oncreate to load.
//        if (db.checkformessages() > 1) {
//            loadHistory();
//        }
        //getting date and time for referral
        DateandTime = DateFormat.getDateTimeInstance().format(new Date());

        //service to fetch history from
        pubnub.history()
                .channel(channel_id) // where to fetch history from
                .count(1000) // how many items to fetch
                .includeTimetoken(true)
                .async(new PNCallback<PNHistoryResult>() {
                    @Override
                    public void onResponse(PNHistoryResult result, PNStatus status) {
                        System.out.println(result);
                        if (result == null){
                            Log.wtf("History : ","is null");
                        }else {
                            for (int i = 0 ; i <result.getMessages().size();i++) {
                                System.out.println("histroy body array"+i + result);
                                long timetoken = result.getMessages().get(i).getTimetoken();
                                String senderMail = result.getMessages().get(i).getEntry().getAsJsonObject().get("sender").toString();
                                senderMail = senderMail.substring(1, senderMail.length() - 1);
                                String message = result.getMessages().get(i).getEntry().getAsJsonObject().get("body").toString();
                                message = message.substring(1, message.length() - 1);
                                //checking who sent it ,if it was user or driver.if it matches sendermail then its user.
                                if (!senderMail.matches(chatname)){
                                    loadHistoryMessages(message,nano_to_dateConvtr(timetoken),"true");
                                }else{
                                    loadHistoryMessages(message,nano_to_dateConvtr(timetoken),"false");
                                }
                            }
                        }
                    }
                });

        //service to listen to the incoming messages
        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {


                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    // This event happens when radio / connectivity is lost
                } else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {

                    // Connect event. You can do stuff like publish, and know you'll get it.
                    // Or just use the connected event to confirm you are subscribed for
                    // UI / internal notifications, etc

                    if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                    }
                } else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {

                    // Happens as part of our regular operation. This event happens when
                    // radio / connectivity is lost, then regained.
                } else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {

                    // Handle messsage decryption error. Probably client configured to
                    // encrypt messages and on live data feed it received plain text.
                }
            }

            @Override
            public void message(PubNub pubnub, final PNMessageResult message) {
                // Handle new message stored in message.message
                if (message.getChannel() != null) {

                    if (didisend == false) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String senderMail = message.getMessage().getAsJsonObject().get("sender").toString();
                                String body = message.getMessage().getAsJsonObject().get("body").toString();
                                if (senderMail ==null || body ==null){
                                    Toast.makeText(ChatPage.this, "Something went wrong please try again!", Toast.LENGTH_SHORT).show();
                                }else {
                                    senderMail = senderMail.substring(1, senderMail.length() - 1);
                                    body = body.substring(1, body.length() - 1);
                                    if (!senderMail.matches(chatname)) {
                                        replicateMessage(body,"Driver ","true");
                                    }else {
                                        Log.wtf("Message Validation", "sent from user dont print");
                                    }
                                }
                            }
                        });
                    }else {
                        Log.wtf("message true", message.getMessage().toString() + didisend);
                    }
                }
                else {
                    //if you want to save in a subscription service , code goes here.
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                Log.wtf("message4",presence.getChannel());
            }
        });


        pubnub.subscribe().channels(Arrays.asList(channel_id)).execute();

    }


    public String nano_to_dateConvtr(long nanotime) {
        long convertnano = nanotime / 10000000;
        Date date = new Date(convertnano * 1000L);
        // format of the date
        TimeZone tz = TimeZone.getDefault();
        System.out.println("TimeZone   " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());
        SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //jdf.setTimeZone(TimeZone.getTimeZone(tz.getDisplayName(false, TimeZone.SHORT)));
        String java_date = jdf.format(date);
        System.out.println("\n" + java_date + "\n");
        return java_date;
    }

    @OnClick(R.id.chatSendButton)
    public void sendMessage() {
        //checking if message box is empty
        messageText = messageET.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        //service for the outgoing messages
        chatBody = new ChatBody(messageText,chatname);
       pubnub.publish().channel(channel_id).message(chatBody).async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                // Check whether request successfully completed or not.
                if (!status.isError()) {
                    replicateMessage(messageText,"User ","false");
                    messageET.setText("");
                    // Message successfully published to specified channel.
                }
            }
        });
    }


    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        didisend = false;
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    @OnClick(R.id.toolbarchat)
    public void ToolbarBtn_cancel_buttonclick(){
        Intent gotoLogin = new Intent(this,Login.class);
        startActivity(gotoLogin);
        this.finish();
    }

    private void replicateMessage(String message,String name,String type){
//        true = message from driver
//        false = message from user

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(122);//dummy id if needed for future purpose.
        chatMessage.setMessage(message);//user message or driver message goes here
        chatMessage.setDate(name + DateandTime); //date and time of message sent
        chatMessage.setMe(Boolean.parseBoolean(type)); //this means message recieved from oppostite person
        displayMessage(chatMessage);
        //insertChatMessagetoDb(message,name + DateandTime,type); //inserting message details in database
    }

    private void insertChatMessagetoDb(String message,String time_name, String type){
        boolean result = db.insertChatInfo(message,time_name,type);

        if (result){
            Log.wtf("Database result :","Succesfully Item Inserted!");
        }else{
            Log.wtf("Database result :","Failed to insert Item!");
        }
    }

    private void loadHistory(){
        String chatmessage,time_date,type;

        cursor = db.getchatinfo();
        try {

            cursor.moveToFirst();
            do {
                chatmessage = cursor.getString(0);
                time_date = cursor.getString(1);
                type = cursor.getString(2);
                Log.wtf("Data of passed messages : ","Chat : " + chatmessage + " " + time_date + " " + type );
                loadHistoryMessages(chatmessage,time_date,type);
            }
            while (cursor.moveToNext());


        } catch (Exception e) {
            Log.wtf("Exception - ", e);
        }

    }

    private void loadHistoryMessages(String message,String time_name, String type){
        ChatMessage msgHistory = new ChatMessage();
        msgHistory.setMessage(message);
        msgHistory.setDate(time_name);
        msgHistory.setMe(Boolean.parseBoolean(type));
        displayMessage(msgHistory);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        //Toast.makeText(this, "Selected Item: " +menuItem.getTitle(), Toast.LENGTH_SHORT).show();
        switch (menuItem.getItemId()) {
            case R.id.message_1:
                sendquickmessage((String) menuItem.getTitle());
                //replicateMessage((String) menuItem.getTitle(),"User ","false");
                //replicateMessage("Ok noted i will be there asap!","Driver ","true");
                return true;
            case R.id.message_2:
                sendquickmessage((String) menuItem.getTitle());
                //replicateMessage((String) menuItem.getTitle(),"User ","false");
                //replicateMessage("Yes i am here","Driver ","true");
                return true;
            case R.id.message_3:
                sendquickmessage((String) menuItem.getTitle());
                //replicateMessage((String) menuItem.getTitle(),"User ","false");
                //replicateMessage("Depends on the traffic","Driver ","true");
                return true;
            case R.id.message_4:
                sendquickmessage((String) menuItem.getTitle());
               // replicateMessage((String) menuItem.getTitle(),"User ","false");
                //replicateMessage("Great,im here too","Driver ","true");
                return true;
            case R.id.cancel:
                PopupMenu popup = new PopupMenu(ChatPage.this,view);
                popup.dismiss();
                return true;
            default:
                return false;
        }

    }

    public void sendquickmessage(final String messageText){
        chatBody = new ChatBody(messageText,chatname);
        pubnub.publish().channel(channel_id).message(chatBody).async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                // Check whether request successfully completed or not.
                if (!status.isError()) {
                    replicateMessage(messageText,"User ","false");
                    messageET.setText("");
                    // Message successfully published to specified channel.
                }
            }
        });
    }

    public void quickmessage(View view) {
        //these quick messages can be hardcoded as well as retireved from server side
        PopupMenu popup = new PopupMenu(ChatPage.this,view);
        popup.setOnMenuItemClickListener(ChatPage.this);
        popup.inflate(R.menu.popup_menu);
        popup.show();

    }
}
