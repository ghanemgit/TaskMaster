package com.example.taskmaster.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taskmaster.R;

import java.util.Objects;

public class SettingActivity extends AppCompatActivity {

    public static final String USERNAME = "username";
    public static final String NO_OF_TASK_TO_SHOW = "No of Task to show";
    private static final String TAG = SettingActivity.class.getSimpleName();
    private EditText mUsername;
    private EditText mNoOfTaskToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mUsername = findViewById(R.id.edit_text_username);
        mNoOfTaskToShow = findViewById(R.id.edit_text_no_of_task);
        Button saveUserButton = findViewById(R.id.save_user_button);

        /*
        https://www.youtube.com/watch?v=FcPUFp8Qrps&ab_channel=LemubitAcademy
        In this video i learned how to add back button in action bar
        */
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        saveUserButton.setOnClickListener(view -> {

            if (TextUtils.isEmpty(mUsername.getText())) {
                mUsername.setError("username is required");
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
        String NoOfTaskToShow = mNoOfTaskToShow.getText().toString();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        preferenceEditor.putString(USERNAME, username);
        preferenceEditor.apply();
        preferenceEditor.putString(NO_OF_TASK_TO_SHOW, NoOfTaskToShow);
        preferenceEditor.apply();
        navigateToHomePage();
    }

    public void navigateToHomePage() {
        startActivity(new Intent(this, MainActivity.class));
    }
}