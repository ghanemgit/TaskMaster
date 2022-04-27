package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView usernameWelcoming;


    private View.OnClickListener mAddTaskButtonListener = view -> {

        Intent startAddTaskActivityIntent = new Intent(getApplicationContext(),AddTaskActivity.class);
        startActivity(startAddTaskActivityIntent);

    };
    private Button taskTitleButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: called");

        usernameWelcoming = findViewById(R.id.username_welcoming);
        Button addTaskButton =  findViewById(R.id.addTaskButton);
        Button allTasksButton =  findViewById(R.id.allTaskButton);
        taskTitleButton = findViewById(R.id.task_details_button);


        addTaskButton.setOnClickListener(view -> {
            navigateToAddTaskPage();
        });

        allTasksButton.setOnClickListener(view -> {
            navigateToAllTaskPage();
        });
        setTaskTitleButton();
        taskTitleButton.setOnClickListener(view -> {
            navigateToTaskDetailsPage();
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: called");

    }

    @Override
    protected void onResume() {
        setUsername();
        setTaskTitleButton();
        super.onResume();
        Log.i(TAG, "onResume: called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: called");
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                navigateToSetting();
                return true;
            case R.id.action_copyright:
                Toast.makeText(this, "Copyright 2022", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void navigateToAddTaskPage(){
       Intent intent = new Intent(MainActivity.this,AddTaskActivity.class);
       startActivity(intent);

    }
    public void navigateToAllTaskPage(){
        Intent intent = new Intent(MainActivity.this,AllTasksActivity.class);
                startActivity(intent);
    }

    public void navigateToSetting(){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void navigateToTaskDetailsPage(){
        Intent intent = new Intent(this,TaskDetailsActivity.class);
        startActivity(intent);

    }


    public void setUsername(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        usernameWelcoming.setText(sharedPreferences.getString(SettingActivity.USERNAME,"Welcome Guest"));

        Log.i(TAG, "setUsername: username is => "+SettingActivity.USERNAME);
    }

    public void setTaskTitleButton(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.contains(AddTaskActivity.TASK_TITLE)) {
            taskTitleButton.setText(sharedPreferences.getString(AddTaskActivity.TASK_TITLE,"No Tasks"));
            taskTitleButton.setEnabled(true);

        }

    }

}