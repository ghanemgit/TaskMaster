package com.example.taskmaster.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.example.taskmaster.R;
import com.example.taskmaster.data.TaskDatabase;
import com.example.taskmaster.data.Task;
import com.example.taskmaster.data.TaskState;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView usernameWelcoming;
    private List<Task> taskList;
    private String selectedItem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: called");

        configureAmplify();


        usernameWelcoming = findViewById(R.id.username_welcoming);
        /*
        https://developer.android.com/guide/topics/ui/floating-action-button
         */
        FloatingActionButton floatAddTaskButton = findViewById(R.id.add_task_button_floating);

        //Floating add task button in main activity
        floatAddTaskButton.setOnClickListener(view -> navigateToAddTaskPage());

        //set adapter for filter tasks spinner
        setAdapterToStatesTaskArraySpinner();

        //render the tasks to home page
        getTasksListToHomePage();

        /*
         * https://stackoverflow.com/questions/12108893/set-onclicklistener-for-spinner-item
         * And as mr.Json tell us in the lecture
         * This spinner is responsible to filter the task for user by states
         */
        Spinner sortSpinner = findViewById(R.id.task_states_filter_spinner);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = adapterView.getItemAtPosition(i).toString();
                initialiseData();
                /*
                 * https://stackoverflow.com/questions/3053761/reload-activity-in-android
                 * how to refresh the activity
                 */
                onResume();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
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
        getTasksListToHomePage();
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

    //Set what happens when click on item from the overflow menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                navigateToSetting();
                return true;
            case R.id.action_copyright:
                Toast.makeText(this, "Copyright 2022", Toast.LENGTH_SHORT).show();
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
        MenuCompat.setGroupDividerEnabled(menu, true);
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

    //Set up the username details to show it in the home screen
    public void setUsername() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        usernameWelcoming.setText(sharedPreferences.getString(SettingActivity.USERNAME, "Welcome Guest"));

        Log.i(TAG, "setUsername: username is => " + SettingActivity.USERNAME);
    }


    private void initialiseData() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String noOfTaskToShow = sharedPreferences.getString(SettingActivity.NO_OF_TASK_TO_SHOW, "");
        int num = 0;
        if (!Objects.equals(noOfTaskToShow, "")) {
            num = Integer.parseInt(noOfTaskToShow);
        }
        Log.i(TAG, "initialiseData: The value of num is = " + num);


        /*
         * To initial the app in first time open but if you keep it without comment,
         * every time you call the main activity will add these tasks to database and show it in the home screen
         */
//        TaskDatabase.getInstance(this).taskDao().insertTask(new Task("Bring Ingredients", "Go to market and bring some milk and 5 eggs and some butter and don't forget the flour"
//        , TaskState.In_progress));
//        TaskDatabase.getInstance(this).taskDao().insertTask(new Task("Sort Ingredients", "Sort our Ingredients according to" +
//                " when we will use it and start clean the place where we will work", TaskState.Assigned));
//        TaskDatabase.getInstance(this).taskDao().insertTask(new Task("Bring Helper Tools", "Go and bring all the necessary helper tools" +
//                " like Wooden spoon ,Measuring cup ,Mixing bowl and spatula etc..", TaskState.Completed));

        /*
         * This switch to help user to show specific tasks according to states of tasks
         * This used the lambda form as we learned to filter the tasks from the database
         */
        switch (selectedItem) {
            case "New":
            case "Assigned":
            case "In progress":
            case "Completed":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    taskList = TaskDatabase.getInstance(this).taskDao().getAll().stream().filter(
                            task -> task.getTaskState().getDisplayValue().equals(selectedItem)).collect(Collectors.toList());
                }
                break;
            default:
                taskList = TaskDatabase.getInstance(this).taskDao().getAll();
        }
    }


    private void getTasksListToHomePage() {

        initialiseData();

        ListView tasksList = findViewById(R.id.list_tasks_main);

        ArrayAdapter<Task> taskDataArrayAdapter = new ArrayAdapter<Task>(
                this
                , android.R.layout.simple_list_item_2
                , android.R.id.text2
                , taskList) {
            @SuppressLint("ResourceAsColor")
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);


                TextView title = view.findViewById(android.R.id.text1);
                TextView state = view.findViewById(android.R.id.text2);




                /*
                 * How to set the text style from java side
                 * https://www.codegrepper.com/code-examples/whatever/make+text+bold+android+studio
                 */
                title.setTypeface(null, Typeface.BOLD);


                title.setText(taskList.get(position).getTitle());



                state.setText(taskList.get(position).getTaskState().getDisplayValue());




                return view;
            }
        };
        tasksList.setAdapter(taskDataArrayAdapter);

        tasksList.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(), TaskDetailsActivity.class);
            intent.putExtra("Position", taskList.get(i).getId());
            startActivity(intent);
        });
    }


    private void setAdapterToStatesTaskArraySpinner() {

        /*
        https://developer.android.com/guide/topics/ui/controls/spinner
         */
        Spinner spinner = findViewById(R.id.task_states_filter_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_states_filter_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void configureAmplify(){
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.configure(getApplicationContext());

            Log.i("Tutorial", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("Tutorial", "Could not initialize Amplify", e);
        }
    }
}