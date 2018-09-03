package com.zaid.zaidpickmeproj;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.zaid.zaidpickmeproj.adapter.ChatAdapter;
import com.zaid.zaidpickmeproj.helper.Database;
import com.zaid.zaidpickmeproj.model.ChatMessage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatPage extends AppCompatActivity {
    @BindView(R.id.messageEdit)
    EditText messageET;
    @BindView(R.id.messagesContainer)
    ListView messagesContainer;
    @BindView(R.id.chatSendButton)
    Button chatSendButton;
    @BindView(R.id.toolbarchat)
    Toolbar toolbarchat;
    @BindView(R.id.toolbarBtn_cancel_button)
    ImageView toolbarBtn_cancel_button;
    @BindView(R.id.ChatParentLayout)
    RelativeLayout ChatParentLayout;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    Database db;
    private ChatAdapter adapter;
    String DateandTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat_page);
        ButterKnife.bind(this);
        adapter = new ChatAdapter(ChatPage.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        db = new Database(this);

        if (db.checkformessages() > 1) {
            loadHistory();
        }
        DateandTime = DateFormat.getDateTimeInstance().format(new Date());
    }

    @OnClick(R.id.chatSendButton)
    public void sendMessage() {
        String messageText = messageET.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(122);//dummy id if needed for future purpose.
        chatMessage.setMessage(messageText);
        chatMessage.setDate(DateandTime);
        chatMessage.setMe(false);
        messageET.setText("");
        displayMessage(chatMessage);
        insertChatMessagetoDb(messageText,DateandTime,"false");
        replicateDriverMessage();
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

    private void replicateDriverMessage(){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(122);//dummy id if needed for future purpose.
        chatMessage.setMessage("Acknowledged by driver!");
        chatMessage.setDate(DateandTime);
        chatMessage.setMe(true); //this means message recieved from oppostite person
        displayMessage(chatMessage);
        insertChatMessagetoDb("Acknowledged by driver!",DateandTime,"true");
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

}
