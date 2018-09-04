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

import com.zaid.zaidpickmeproj.R;
import com.zaid.zaidpickmeproj.adapter.ChatAdapter;
import com.zaid.zaidpickmeproj.helper.Database;
import com.zaid.zaidpickmeproj.model.ChatMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    @BindView(R.id.ChatParentLayout)
    RelativeLayout ChatParentLayout;
    Cursor cursor;
    Database db;
    private ChatAdapter adapter;
    String DateandTime;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat_page);
        //butterknife library used for binding between ui elements and activity
        ButterKnife.bind(this);
        adapter = new ChatAdapter(ChatPage.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        //creating object of database for usage
        db = new Database(this);

        //checking if database contains any past messages oncreate to load.
        if (db.checkformessages() > 1) {
            loadHistory();
        }
        //getting date and time for referral
        DateandTime = DateFormat.getDateTimeInstance().format(new Date());
    }

    @OnClick(R.id.chatSendButton)
    public void sendMessage() {
        //checking if message box is empty
        String messageText = messageET.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        replicateMessage(messageText,"User ","false");
        messageET.setText("");
        replicateMessage("Acknowledged by driver!","Driver ","true");
    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
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
        insertChatMessagetoDb(message,name + DateandTime,type); //inserting message details in database
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
                replicateMessage((String) menuItem.getTitle(),"User","false");
                replicateMessage("Ok noted i will be there asap!","driver","true");
                return true;
            case R.id.message_2:
                replicateMessage((String) menuItem.getTitle(),"User","false");
                replicateMessage("Yes i am here","driver","true");
                return true;
            case R.id.message_3:
                replicateMessage((String) menuItem.getTitle(),"User","false");
                replicateMessage("Depends on the traffic","driver","true");
                return true;
            case R.id.message_4:
                replicateMessage((String) menuItem.getTitle(),"User","false");
                replicateMessage("Great,im here too","driver","true");
                return true;
            case R.id.cancel:
                PopupMenu popup = new PopupMenu(ChatPage.this,view);
                popup.dismiss();
                return true;
            default:
                return false;
        }

    }

    public void quickmessage(View view) {
        PopupMenu popup = new PopupMenu(ChatPage.this,view);
        popup.setOnMenuItemClickListener(ChatPage.this);
        popup.inflate(R.menu.popup_menu);
        popup.show();

    }
}
