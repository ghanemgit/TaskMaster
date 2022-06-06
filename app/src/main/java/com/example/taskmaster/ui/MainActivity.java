package com.example.taskmaster.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.analytics.pinpoint.AWSPinpointAnalyticsPlugin;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.query.Where;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.taskmaster.Auth.LoginActivity;
import com.example.taskmaster.R;
import com.example.taskmaster.data.UserInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
@SuppressLint("NotifyDataSetChanged")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView usernameWelcoming;
    private FloatingActionButton floatAddTaskButton;
    public static List<Task> tasksList = new ArrayList<>();
    private String statusSelected;
    private LoadingDialog loadingDialog;
    private CustomListRecyclerViewAdapter customListRecyclerViewAdapter;
    private RecyclerView taskRecyclerView;
    private Spinner sortSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: called");

        recordEvents();
        
        findAllViewsById();

        showUserNameOrTeam();

        if (isOnline()) onlineFetchTasksData();
        else offlineFetchTasksData();

        //set adapter for filter tasks spinner
        setAdapterToStatesTaskArraySpinner();

        taskFilterSpinner();

        getTasksListToHomePage();

        //Floating add task button in main activity
        floatAddTaskButton.setOnClickListener(view -> navigateToAddTaskPage());
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

        if (isOnline()) onlineFetchTasksData();
        else offlineFetchTasksData();
        sortSpinner.setSelection(0);
        showUserNameOrTeam();
        Log.i(TAG, "onResume: called");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: called");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void recordEvents(){

        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("Task Master Opened")
                .addProperty("Successful", true)
                .addProperty("ProcessDuration", 792)
                .build();

        Amplify.Analytics.recordEvent(event);

    }

    private void findAllViewsById() {
        usernameWelcoming = findViewById(R.id.username_welcoming);
        /*
        https://developer.android.com/guide/topics/ui/floating-action-button
         */
        floatAddTaskButton = findViewById(R.id.add_task_button_floating);
        loadingDialog = new LoadingDialog(MainActivity.this);
        taskRecyclerView = findViewById(R.id.recycler_view);
    }

    //Set up the username details to show it in the home screen
    @SuppressLint("SetTextI18n")
    public void showUserNameOrTeam() {

        usernameWelcoming.setText(UserInfo.getDefaults(UserInfo.FIRST_NAME, "Guest", this) + " " + UserInfo.getDefaults(UserInfo.LAST_NAME, "", this) + " Tasks");

        usernameWelcoming.setText(UserInfo.getDefaults(UserInfo.USER_TEAM, UserInfo.getDefaults(UserInfo.FIRST_NAME, "Guest", this), this) + " Tasks");
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void onlineFetchTasksData() {

        Amplify.API.query(
                ModelQuery.list(Task.class, Task.TEAM_TASKS_ID.eq(UserInfo.getDefaults(UserInfo.USER_TEAM_ID, null, this))),
                tasks -> {
                    tasksList.clear();
                    if (tasks.hasData()) {
                        for (Task task : tasks.getData()) {
                            tasksList.add(task);
                        }
                    }
                    runOnUiThread(() -> {
                        customListRecyclerViewAdapter.notifyDataSetChanged();
                    });
                },
                error -> {
                    Log.e(TAG, error.toString());
                    Toast.makeText(this, "Error in data sync from cloud", Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void offlineFetchTasksData() {

        Amplify.DataStore.query(Task.class, Where.matches(Task.TEAM_TASKS_ID.eq(UserInfo.getDefaults(UserInfo.USER_TEAM_ID, null, this))),
                allTasks -> {
                    tasksList.clear();
                    while (allTasks.hasNext()) {
                        Task task = allTasks.next();
                        Log.i(TAG, "Title: " + task.getTitle());
                        tasksList.add(task);
                    }
                    runOnUiThread(() -> {
                        customListRecyclerViewAdapter.notifyDataSetChanged();
                    });
                },
                failure -> {
                    Log.e(TAG, "Query failed.", failure);
                    Toast.makeText(this, "Error in data sync from local", Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void filterTaskAccordingToStatus(String str) {

        if (isOnline()) {
            Amplify.API.query(
                    ModelQuery.list(Task.class, Task.TEAM_TASKS_ID.eq(UserInfo.getDefaults(UserInfo.USER_TEAM_ID, null, this)).and(Task.STATUS.eq(str))),
                    tasks -> {
                        tasksList.clear();
                        if (tasks.hasData()) {
                            for (Task task : tasks.getData()) {
                                tasksList.add(task);
                            }
                        }
                        runOnUiThread(() -> {
                            customListRecyclerViewAdapter.notifyDataSetChanged();
                        });
                    },
                    error -> {
                        Log.e(TAG, error.toString());
                        Toast.makeText(this, "Error in data sync from cloud", Toast.LENGTH_SHORT).show();
                    }
            );
        } else {
            Amplify.DataStore.query(Task.class, Where.matches(
                            Task.TEAM_TASKS_ID.eq(UserInfo.getDefaults(UserInfo.USER_TEAM_ID, null, this))
                                    .and(Task.STATUS.eq(str))),
                    allTasks -> {
                        tasksList.clear();
                        while (allTasks.hasNext()) {
                            Task task = allTasks.next();
                            Log.i(TAG, "Title: " + task.getTitle());
                            tasksList.add(task);
                        }
                        runOnUiThread(() -> {
                            customListRecyclerViewAdapter.notifyDataSetChanged();
                        });
                    },
                    failure -> {
                        Log.e(TAG, "Query failed.", failure);
                        Toast.makeText(this, "Error in data sync from local", Toast.LENGTH_SHORT).show();
                    }
            );
        }
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
        sortSpinner = findViewById(R.id.task_states_filter_spinner);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!(i==0)) {
                    statusSelected = adapterView.getItemAtPosition(i).toString();
                    filterTaskAccordingToStatus(statusSelected);
                    /*
                     * https://stackoverflow.com/questions/3053761/reload-activity-in-android
                     * how to refresh the activity
                     */
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    private void getTasksListToHomePage() {


        customListRecyclerViewAdapter = new CustomListRecyclerViewAdapter(tasksList, position -> {
            Intent intent = new Intent(getApplicationContext(), TaskDetailsActivity.class);
            intent.putExtra("Position", tasksList.get(position).getId());
            startActivity(intent);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);

        taskRecyclerView.setLayoutManager(linearLayoutManager);
        taskRecyclerView.setHasFixedSize(true);
        taskRecyclerView.setAdapter(customListRecyclerViewAdapter);
    }

    //Set what happens when click on item from the overflow menu
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                navigateToSetting();
                return true;
            case R.id.action_copyright:
                Toast.makeText(this, "Copyright 2022", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_sign_out:
                loadingDialog.startLoadingDialog();
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        /*
        https://stackoverflow.com/questions/3519277/how-to-change-the-text-color-of-menu-item-in-android
        how to change an item color in overflow menu
        */
        int positionOfMenuItem = 2; // or whatever...
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("Sign out");
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        item.setTitle(s);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    private void signOut() {

        Amplify.Auth.signOut(
                () -> {
                    Log.i(TAG, "Signed out successfully");
                    navigateToLoginPage();
                },
                error -> {
                    Log.e(TAG, error.toString());
                    loadingDialog.dismissLoadingDialog();
                    onResume();
                }
        );
    }

    private void navigateToLoginPage() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        loadingDialog.dismissLoadingDialog();
        finish();
    }

    private void navigateToAddTaskPage() {
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra(MainActivity.class.getSimpleName(), MainActivity.class.getSimpleName());
        startActivity(intent);
    }

    private void navigateToSetting() {
        overridePendingTransition(0, 0);
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra("tasksListSize", tasksList.size());
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

}