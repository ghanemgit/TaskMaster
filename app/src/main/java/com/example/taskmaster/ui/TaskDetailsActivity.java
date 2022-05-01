package com.example.taskmaster.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.taskmaster.R;
import com.example.taskmaster.data.TaskData;
import com.example.taskmaster.data.TaskState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskDetailsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);


        TextView state = findViewById(R.id.task_state_in_details_page);
        TextView body = findViewById(R.id.task_body_in_details_page);


        state.setText("State => " + getIntent().getStringExtra("State"));
        body.setText("Description:\n" + getIntent().getStringExtra("Body"));


        setActionBarTitle(getIntent().getStringExtra("Title"));
        /*
        https://www.youtube.com/watch?v=FcPUFp8Qrps&ab_channel=LemubitAcademy
        In this video i learned how to add back button in action bar
         */
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }


//    public void getTaskDescription(){
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        taskDescriptionView.setText(sharedPreferences.getString(AddTaskActivity.TASK_DESCRIPTION,"Description"));
//    }

    public void setActionBarTitle(String title) {

       /*
       https://stackoverflow.com/questions/10138007/how-to-change-android-activity-label
        */

        Objects.requireNonNull(getSupportActionBar()).setTitle(title);


    }
}