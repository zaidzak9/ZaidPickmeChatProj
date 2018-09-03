package com.zaid.zaidpickmeproj;

import android.content.Intent;
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

    private ChatAdapter adapter;
    boolean didisend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat_page);
        ButterKnife.bind(this);
        adapter = new ChatAdapter(ChatPage.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
    }

    @OnClick(R.id.chatSendButton)
    public void sendMessage() {
        String messageText = messageET.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(122);//dummy
        chatMessage.setMessage(messageText);
        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatMessage.setMe(false);

        messageET.setText("");

        displayMessage(chatMessage);
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

}
