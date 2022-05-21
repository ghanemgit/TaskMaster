package com.example.taskmaster.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmaster.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView usernameWelcoming;
    private FloatingActionButton floatAddTaskButton;
    private List<Task> tasksList = new ArrayList<>();
    private String selectedItem = "";
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: called");

        findById();

        //Floating add task button in main activity
        floatAddTaskButton.setOnClickListener(view -> navigateToAddTaskPage());

        RecyclerViewHandler();

        //set adapter for filter tasks spinner
        setAdapterToStatesTaskArraySpinner();

        taskFilterSpinner();
    }

    @Override
    protected void onStart() {
        super.onStart();
        onResume();
        Log.i(TAG, "onStart: called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: called");

    }

    @Override
    protected void onResume() {
        showSharedPreferencesInfo();
        //render the tasks to home page
        getTasksListToHomePage();
        super.onResume();
        Log.i(TAG, "onResume: called");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: called");
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

    private void navigateToAddTaskPage() {
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        startActivity(intent);

    }

    private void navigateToAllTaskPage() {
        Intent intent = new Intent(MainActivity.this, AllTasksActivity.class);
        startActivity(intent);
    }

    private void navigateToSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    //Set up the username details to show it in the home screen
    private void showSharedPreferencesInfo() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        usernameWelcoming.setText(sharedPreferences.getString(SettingActivity.USER_TEAM, SettingActivity.USERNAME));

    }

    private void initializeData() {

//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String noOfTaskToShow = sharedPreferences.getString(SettingActivity.NO_OF_TASK_TO_SHOW, "");
//        int num = 0;
//        if (!Objects.equals(noOfTaskToShow, "")) {
//            num = Integer.parseInt(noOfTaskToShow);
//        }
//        Log.i(TAG, "initialiseData: The value of num is = " + num);


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
//
        tasksList = SplashActivity.tasksList;
        //fetchDataFromLocal();


    }

    private void getTasksListToHomePage() {

        initializeData();

        ListView listViewTasksList = findViewById(R.id.list_tasks_main);

        ArrayAdapter<Task> taskDataArrayAdapter = new ArrayAdapter<Task>(
                this
                , android.R.layout.simple_list_item_2
                , android.R.id.text2
                , tasksList) {
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

                title.setText(tasksList.get(position).getTitle());
                state.setText(tasksList.get(position).getStatus());
                return view;
            }
        };
        listViewTasksList.setAdapter(taskDataArrayAdapter);

        listViewTasksList.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(), TaskDetailsActivity.class);
            intent.putExtra("Position", tasksList.get(i).getId());
            startActivity(intent);
        });
    }

    private void RecyclerViewHandler() {
        System.out.println("The final size of list is from recycler view =>" + tasksList.size());

        handler = new Handler(Looper.getMainLooper(), msg -> {

            new RecyclerViewActivity(tasksList);

            return true;

        });
    }

    private void fetchDataFromLocal() {
        /*
         * This switch to help user to show specific tasks according to states of tasks
         * This used the lambda form as we learned to filter the tasks from the database
         */
//        switch (selectedItem) {
//            case "Completed":
//            case "New":
//            case "Assigned":
//            case "In progress":
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    taskList = TaskDatabase.getInstance(this).taskDao().getAll().stream().filter(
//                            task -> task.getTaskState().getDisplayValue().equals(selectedItem)).collect(Collectors.toList());
//
//                }
//                break;
//            default:
//                taskList = TaskDatabase.getInstance(this).taskDao().getAll();
//        }

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

    private void taskFilterSpinner() {

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
                initializeData();
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

    private void findById() {
        usernameWelcoming = findViewById(R.id.username_welcoming);
        /*
        https://developer.android.com/guide/topics/ui/floating-action-button
         */
        floatAddTaskButton = findViewById(R.id.add_task_button_floating);
    }

}