package com.zaid.zaidpickmeproj.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zaid.zaidpickmeproj.R;
import com.zaid.zaidpickmeproj.helper.ValidationHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends AppCompatActivity {

    @BindView(R.id.channel_et)
    EditText channelField;
    @BindView(R.id.password_et)
    EditText passwordField;
    @BindView(R.id.chatid_et)
    EditText chatid;
    @BindView(R.id.parentLayout)
    LinearLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login_btn)
    public void login() {
        String chatname = chatid.getText().toString();
        String channel = channelField.getText().toString().trim();
        String password = passwordField.getText().toString();

        if (TextUtils.isEmpty(channel)) {
            channelField.requestFocus();
            channelField.setError(getString(R.string.error_message_channel));
        }
       else if (TextUtils.isEmpty(password)) {
            passwordField.requestFocus();
            passwordField.setError(getString(R.string.error_message_password));
        }else if (TextUtils.isEmpty(chatname)) {
            chatid.requestFocus();
            chatid.setError(getString(R.string.error_chatid));
        } else {

            if (password.matches("123456") ) {
                Intent gotoHome = new Intent(this, ChatPage.class);
                gotoHome.putExtra("chatname",chatname);
                gotoHome.putExtra("channelid",channel);
                startActivity(gotoHome);
            }else{
                Toast.makeText(this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
