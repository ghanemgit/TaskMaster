package com.example.taskmaster;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";



    private View.OnClickListener mAddTaskButtonListener = view -> {

        Intent startAddTaskActivityIntent = new Intent(getApplicationContext(),AddTaskActivity.class);
        startActivity(startAddTaskActivityIntent);

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: called");

        Button addTaskButton = (Button) findViewById(R.id.addTaskButton);
        Button allTasksButton = (Button) findViewById(R.id.allTaskButton);

        allTasksButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,AllTasksActivity.class));
        });

        addTaskButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,AddTaskActivity.class));
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
}