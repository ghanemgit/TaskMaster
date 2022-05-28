package com.example.taskmaster.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmaster.Auth.LoginActivity;
import com.example.taskmaster.Auth.SignUpActivity;
import com.example.taskmaster.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.N)
@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView usernameWelcoming;
    private FloatingActionButton floatAddTaskButton;
    public static List<Task> tasksList = new ArrayList<>();
    private String theUserTeamString = "";
    private String theUserTeamId = "";
    private String selectedItem = "";
    private LoadingDialog loadingDialog;
    private TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: called");


        findAllViewsById();

        showSharedPreferencesInfo();

        setTheUserTeamString();

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
        super.onResume();
        showSharedPreferencesInfo();
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
            case R.id.action_all_tasks:
                navigateToAllTaskPage();
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
        int positionOfMenuItem = 3; // or whatever...
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("Sign out");
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        item.setTitle(s);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    private void navigateToLoginPage() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        loadingDialog.dismissLoadingDialog();
        finish();
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
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, SettingActivity.class));
        overridePendingTransition(0, 0);
    }

    //Set up the username details to show it in the home screen
    private void showSharedPreferencesInfo() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        usernameWelcoming.setText(sharedPreferences.getString(SignUpActivity.FIRST_NAME, "Guest")+" Tasks");

        usernameWelcoming.setText(sharedPreferences.getString(SignUpActivity.USER_TEAM,null) +" Tasks");
    }

    private void initializeData() {

        theUserTeamId = SplashActivity.teamsList.stream().filter(team ->
                team.getName().equals(theUserTeamString)).collect(Collectors.toList()).get(0).getId();
        tasksList = tasksList.stream().filter(task -> task.getTeamTasksId().equals(theUserTeamId)).collect(Collectors.toList());

        System.out.println("The user team stirng is  => "+theUserTeamString);
        System.out.println("The size of tasks list from the main activity is => "+tasksList.size());

        switch (selectedItem) {
            case "New":
            case "Assigned":
            case "In progress":
            case "Completed":
                tasksList = tasksList.stream().filter(task -> task.getStatus().equals(selectedItem)).collect(Collectors.toList());
                break;
            default:

        }
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
            System.out.println("The task from the onitemclicklistener is "+tasksList.get(i).getTitle());
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

    private void findAllViewsById() {
        usernameWelcoming = findViewById(R.id.username_welcoming);
        /*
        https://developer.android.com/guide/topics/ui/floating-action-button
         */
        floatAddTaskButton = findViewById(R.id.add_task_button_floating);
        loadingDialog = new LoadingDialog(MainActivity.this);

        loadingText = findViewById(R.id.text_view_in_loading_progress);
    }

    private void setTheUserTeamString(){
       theUserTeamString =  SettingActivity.getDefaults(SignUpActivity.USER_TEAM,this);
    }


    private void signOut(){

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

}