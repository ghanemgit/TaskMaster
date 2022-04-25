package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Button addTaskButton = findViewById(R.id.createTaskButton);
        EditText taskTitle = (EditText) findViewById(R.id.taskTitleBox);
        EditText taskDescription = (EditText) findViewById(R.id.taskDescriptionBox);

        addTaskButton.setOnClickListener(view -> {

            if (TextUtils.isEmpty(taskTitle.getText()) || TextUtils.isEmpty(taskDescription.getText())){

                taskTitle.setError("Title is Required");
                taskDescription.setError("Description is required");
            }
            else
            Toast.makeText(this,"Submitted!",Toast.LENGTH_SHORT).show();
        });

    }
}