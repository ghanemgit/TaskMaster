package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import java.util.Objects;

public class TaskDetailsActivity extends AppCompatActivity {

    private TextView taskDescriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        taskDescriptionView = findViewById(R.id.task_description_lorem);

        setActionBarTitle();
        getTaskDescription();
        /*
        https://www.youtube.com/watch?v=FcPUFp8Qrps&ab_channel=LemubitAcademy
        In this video i learned how to add back button in action bar
         */
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }







    public void getTaskDescription(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        taskDescriptionView.setText(sharedPreferences.getString(AddTaskActivity.TASK_DESCRIPTION,"Description"));
    }

    public void setActionBarTitle(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //https://stackoverflow.com/questions/10138007/how-to-change-android-activity-label
        Objects.requireNonNull(getSupportActionBar()).setTitle(sharedPreferences.getString(AddTaskActivity.TASK_TITLE,"Task"));
    }
}