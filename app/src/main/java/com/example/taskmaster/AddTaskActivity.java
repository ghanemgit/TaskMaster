package com.example.taskmaster;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class AddTaskActivity extends AppCompatActivity {

    public static final String TASK_TITLE = "Task Title";
    public static final String TASK_DESCRIPTION = "Task Description";
    private static final String TAG = AddTaskActivity.class.getSimpleName();
    private EditText taskTitle;
    private EditText taskDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Button addTaskButton = findViewById(R.id.createTaskButton);
        taskTitle = findViewById(R.id.taskTitleBox);
        taskDescription = findViewById(R.id.taskDescriptionBox);

        /*
        https://www.youtube.com/watch?v=FcPUFp8Qrps&ab_channel=LemubitAcademy
        In this video i learned how to add back button in action bar
         */
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        addTaskButton.setOnClickListener(view -> {

            if (TextUtils.isEmpty(taskTitle.getText()) || TextUtils.isEmpty(taskDescription.getText())){

                taskTitle.setError("Title is Required");
                taskDescription.setError("Description is required");
            }
            else{
                Toast.makeText(this,"Submitted!",Toast.LENGTH_SHORT).show();
                saveTask();
            }

            View view2 = this.getCurrentFocus();
            if (view2 != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
            }
        });

    }

    public void saveTask(){

        String taskTitleString = taskTitle.getText().toString();
        String taskDescriptionString = taskDescription.getText().toString();

        SharedPreferences addTaskPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = addTaskPreferences.edit();

        editor.putString(TASK_TITLE,taskTitleString);
        editor.apply();
        editor.putString(TASK_DESCRIPTION,taskDescriptionString);
        editor.apply();

        Log.i(TAG, "saveTask: The title is "+taskTitleString);
        Log.i(TAG, "saveTask: The Description is "+taskDescriptionString);


    }
}