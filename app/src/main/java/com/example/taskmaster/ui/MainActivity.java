package com.example.taskmaster.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmaster.R;
import com.example.taskmaster.data.TaskData;
import com.example.taskmaster.data.TaskState;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView usernameWelcoming;
    List<TaskData> taskDataList = new ArrayList<>();


    private View.OnClickListener mAddTaskButtonListener = view -> {

        Intent startAddTaskActivityIntent = new Intent(getApplicationContext(), AddTaskActivity.class);
        startActivity(startAddTaskActivityIntent);

    };
    private Button taskTitleButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: called");

        usernameWelcoming = findViewById(R.id.username_welcoming);
//        Button addTaskButton =  findViewById(R.id.addTaskButton);
//        Button allTasksButton =  findViewById(R.id.allTaskButton);
//        taskTitleButton = findViewById(R.id.task_details_button);


        getTasksListToHomePage();


//        addTaskButton.setOnClickListener(view -> {
//            navigateToAddTaskPage();
//        });
//
//        allTasksButton.setOnClickListener(view -> {
//            navigateToAllTaskPage();
//        });


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
            case R.id.action_add_task:
                navigateToAddTaskPage();
                return true;
            case R.id.action_all_tasks:
                navigateToAllTaskPage();
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

    public void navigateToAddTaskPage() {
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        startActivity(intent);

    }

    public void navigateToAllTaskPage() {
        Intent intent = new Intent(MainActivity.this, AllTasksActivity.class);
        startActivity(intent);
    }

    public void navigateToSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }




    public void setUsername() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        usernameWelcoming.setText(sharedPreferences.getString(SettingActivity.USERNAME, "Welcome Guest"));

        Log.i(TAG, "setUsername: username is => " + SettingActivity.USERNAME);
    }


    private void initialiseData() {

        taskDataList.add(new TaskData("Bring Ingredients", "Go to market and bring some " +
                "milk and 5 eggs and some butter and don't forget the flour", TaskState.In_progress));
        taskDataList.add(new TaskData("Sort Ingredients", "Sort our Ingredients according to" +
                " when we will use it and start clean the place where we will work", TaskState.Assigned));
        taskDataList.add(new TaskData("Bring Helper Tools", "Go and bring all the necessary helper tools" +
                " like Wooden spoon ,Measuring cup ,Mixing bowl and spatula etc..", TaskState.Assigned));

    }


    private void getTasksListToHomePage() {
        initialiseData();

        ListView tasksList = findViewById(R.id.list_tasks_main);

        ArrayAdapter<TaskData> taskDataArrayAdapter = new ArrayAdapter<TaskData>(
                this
                , android.R.layout.simple_list_item_2
                , android.R.id.text2
                , taskDataList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView title = (TextView) view.findViewById(android.R.id.text1);
                TextView state = (TextView) view.findViewById(android.R.id.text2);

                title.setText(taskDataList.get(position).getTitle());
                state.setText(taskDataList.get(position).getTaskState().getDisplayValue());

                return view;
            }
        };
        tasksList.setAdapter(taskDataArrayAdapter);

        tasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), TaskDetailsActivity.class);
                intent.putExtra("Title",taskDataList.get(i).getTitle());
                intent.putExtra("State",taskDataList.get(i).getTaskState().getDisplayValue());
                intent.putExtra("Body",taskDataList.get(i).getBody());

                startActivity(intent);
            }
        });
    }
}