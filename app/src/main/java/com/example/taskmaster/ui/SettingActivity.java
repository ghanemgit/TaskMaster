package com.example.taskmaster.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taskmaster.R;

import java.util.Objects;

public class SettingActivity extends AppCompatActivity {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    private static final String TAG = SettingActivity.class.getSimpleName();
    private EditText mUsername;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mUsername = findViewById(R.id.edit_text_username);
        mPassword = findViewById(R.id.edit_text_password);
        Button saveUserButton = findViewById(R.id.save_user_button);

        /*
        https://www.youtube.com/watch?v=FcPUFp8Qrps&ab_channel=LemubitAcademy
        In this video i learned how to add back button in action bar
         */
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        saveUserButton.setOnClickListener(view -> {

            if (TextUtils.isEmpty(mUsername.getText()) || TextUtils.isEmpty(mPassword.getText())) {
                mUsername.setError("username is required");
                mPassword.setError("password is required");
            } else {
                saveUser();
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            }

            View view2 = this.getCurrentFocus();
            if (view2 != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
            }
        });
    }

    private void saveUser() {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        preferenceEditor.putString(USERNAME, username);
        preferenceEditor.apply();
        preferenceEditor.putString(PASSWORD, password);
        preferenceEditor.apply();

        Log.i(TAG, "saveUser: The username is =>" + username);
    }
}