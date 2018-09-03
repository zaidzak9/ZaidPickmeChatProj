package com.zaid.zaidpickmeproj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zaid.zaidpickmeproj.helper.ValidationHelper;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends AppCompatActivity {

    @BindView(R.id.username_et)
    EditText usernameField;
    @BindView(R.id.password_et)
    EditText passwordField;
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
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString();

        if (TextUtils.isEmpty(username)) {
            usernameField.requestFocus();
            usernameField.setError(getString(R.string.error_message_username));
        } else if (!ValidationHelper.isValidEmail(username)) {
            usernameField.requestFocus();
            usernameField.setError(getString(R.string.error_message_username));
        } else if (TextUtils.isEmpty(password)) {
            passwordField.requestFocus();
            passwordField.setError(getString(R.string.error_message_password));
        } else {

            if (username.matches("abc@abc.com") && password.matches("123456") ) {
                Intent gotoHome = new Intent(this, ChatPage.class);
                startActivity(gotoHome);
            }else{
                Toast.makeText(this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
