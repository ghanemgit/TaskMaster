package com.example.taskmaster.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;
import com.example.taskmaster.data.TaskDatabase;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmaster.data.TeamDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddTaskActivity extends AppCompatActivity {

    public static final String TASK_TITLE = "Task Title";
    public static final String TASK_DESCRIPTION = "Task Description";
    public static final String TASK_STATE = "Task State";
    public static final String TEAM_ID = "teamId";
    public static final String DATA = "Data";
    private Handler handler;

    private static final String TAG = AddTaskActivity.class.getSimpleName();
    private EditText taskTitle;
    private EditText taskDescription;
    private Spinner taskState;
    private Spinner selectTeamSpinner;
    private TextView totalTask;
    private Button addTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        findAllViewsByIdMethod();

//        saveTeamToLocalDB();

        backToPreviousButton();

        setAdapterToStatesTaskArraySpinner();

        setAdapterToStatesTeamArraySpinner();

        showTotalTaskNumber();

        //manuallyInitializeTheTeams();

        addTaskButton.setOnClickListener(view -> addTaskButtonAction());

    }

    private void saveTask() {

        String taskTitleString = taskTitle.getText().toString();
        String taskDescriptionString = taskDescription.getText().toString();
        String taskStateString = taskState.getSelectedItem().toString();
        String teamString = selectTeamSpinner.getSelectedItem().toString();


        //saveTaskToLocalDB(taskTitleString,taskDescriptionString,taskStateString);

        saveToCloudDB(taskTitleString, taskDescriptionString, taskStateString);
    }

    private void setAdapterToStatesTaskArraySpinner() {

        /*
        https://developer.android.com/guide/topics/ui/controls/spinner
         */

        Spinner spinner = findViewById(R.id.task_states_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_states_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void setAdapterToStatesTeamArraySpinner() {

        /*
        https://developer.android.com/guide/topics/ui/controls/spinner
         */
        List<String> teams = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            teams = TeamDatabase.getInstance(this).teamDao().getAll().stream().map(com.example.taskmaster.data.Team::getName).collect(Collectors.toList());
        }

        /*
        https://www.codegrepper.com/code-examples/java/android+studio+how+to+fill+spinner
         */
        Spinner spinner = findViewById(R.id.task_team_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
                teams);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void addTaskButtonAction() {
        if (TextUtils.isEmpty(taskTitle.getText()) || TextUtils.isEmpty(taskDescription.getText())) {

            taskTitle.setError("Title is Required");
            taskDescription.setError("Description is required");
        } else {
            Toast.makeText(this, "Submitted!", Toast.LENGTH_SHORT).show();
            saveTask();
            navigateToHomePage();
        }

        View view2 = this.getCurrentFocus();
        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
        }


    }

    private void backToPreviousButton() {
        /*
        https://www.youtube.com/watch?v=FcPUFp8Qrps&ab_channel=LemubitAcademy
        In this video I learned how to add back button in action bar
        */
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void showTotalTaskNumber() {

        totalTask.setText("Total Task => " + TaskDatabase.getInstance(this).taskDao().getAll().size());
    }

    private void findAllViewsByIdMethod() {

        addTaskButton = findViewById(R.id.createTaskButton);
        taskTitle = findViewById(R.id.taskTitleBox);
        taskDescription = findViewById(R.id.taskDescriptionBox);
        taskState = findViewById(R.id.task_states_spinner);
        totalTask = findViewById(R.id.tasksCount);
        selectTeamSpinner = findViewById(R.id.task_team_spinner);

    }

    private void createHandlerMethode() {

        handler = new Handler(Looper.getMainLooper(), message -> {
            String data = message.getData().getString(DATA);
            String teamId = message.getData().getString(TEAM_ID);

//            Toast.makeText(this, "The Toast Works " + data, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(TEAM_ID, teamId);

            startActivity(intent);
            return true;
        });
    }

    private void manuallyInitializeTheTeams() {
        //Add the team to the task
        Team team1 = Team.builder()
                .name("First Team")
                .build();
        Amplify.DataStore.save(team1, pass -> {
                    Log.i(TAG, "Saved Task To the Team => " + pass.item().getName());
                },
                failure -> {
                    Log.e(TAG, "Could not save Team to Task ", failure);
                });
        //Add the team to the task
        Team team2 = Team.builder()
                .name("Second Team")
                .build();
        Amplify.DataStore.save(team2, pass -> {
                    Log.i(TAG, "Saved Task To the Team => " + pass.item().getName());
                },
                failure -> {
                    Log.e(TAG, "Could not save Team to Task ", failure);
                });
        //Add the team to the task
        Team team3 = Team.builder()
                .name("Third Team")
                .build();
        Amplify.DataStore.save(team3, pass -> {
                    Log.i(TAG, "Saved Task To the Team => " + pass.item().getName());
                },
                failure -> {
                    Log.e(TAG, "Could not save Team to Task ", failure);
                });
    }

    private void saveToCloudDB(String taskTitleString, String taskDescriptionString, String taskStateString) {

        // Lab 32 \\


        Team team1 = Team.builder().name("First Team").build();
        Team team2 = Team.builder().name("Second Team").build();
        Team team3 = Team.builder().name("Third Team").build();

        Task task = Task.builder()
                .title(taskTitleString)
                .description(taskDescriptionString)
                .status(taskStateString)
                .teamTasksId(team3.getId())
                .build();

        // Data store save
        Amplify.DataStore.save(team3,
                success -> {
                    Amplify.DataStore.save(task,
                            savedTask -> {
                                Log.i("Task in add task page ", team3.getId());

                                Bundle bundle = new Bundle();
                                bundle.putString(TEAM_ID, team3.getId());

                                Message message = new Message();
                                message.setData(bundle);

                                handler.sendMessage(message);
                            },
                            failure -> Log.e("ask in add task page", "Task not saved.", failure)
                    );

                    Log.i(TAG, "Team in add task page " + success.item().getName());
                },
                error -> Log.e(TAG, "Could not save item to DataStore ", error)
        );

        // API save to backend
        Amplify.API.mutate(ModelMutation.create(team3),
                success -> {
                    Log.i(TAG, "Saved item: " + success.getData().getName());

                    Amplify.API.mutate(ModelMutation.create(task),
                            successTask -> {
                        Log.i(TAG, "Task saved to team from API => " + successTask.getData().getId());

                                Bundle bundle = new Bundle();
                                bundle.putString(TEAM_ID, team3.getId());

                                Message message = new Message();
                                message.setData(bundle);

                                handler.sendMessage(message);
                            },
                            failure -> Log.i(TAG, "Task not saved to the team from API => "));
                },
                error -> Log.e(TAG, "Could not save team to API ", error)
        );

        createHandlerMethode();
    }

    private void dataStoreSync(){

        // Datastore and API sync
//        Amplify.DataStore.observe(Team.class,
//                started -> {
//                    Log.i(TAG, "Observation began. ");
//                },
//                change -> {
//                    Log.i(TAG, change.item().toString());
//
//                    Bundle bundle = new Bundle();
//                    bundle.putString(DATA, change.item().toString());
//
//                    Message message = new Message();
//                    message.setData(bundle);
//
//                    handler.sendMessage(message);
//                },
//                failure -> Log.e(TAG, "Observation failed. ", failure),
//                () -> Log.i(TAG, "Observation complete. ")
//        );
    }

    private void saveTeamToLocalDB() {

        //        Log.i(TAG, "saveTask: TaskStateString is =>  " + taskStateString);
//
//        SharedPreferences addTaskPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = addTaskPreferences.edit();
//
//        editor.putString(TASK_TITLE, taskTitleString);
//        editor.apply();
//        editor.putString(TASK_DESCRIPTION, taskDescriptionString);
//        editor.apply();
//        editor.putString(TASK_STATE, taskStateString);
//        editor.apply();
//
//        Log.i(TAG, "saveTask: The title is " + taskTitleString);
//        Log.i(TAG, "saveTask: The Description is " + taskDescriptionString);
//
//        TaskState taskState;
//        switch (taskStateString) {
//            case "Assigned":
//                taskState = TaskState.Assigned;
//                break;
//            case "In progress":
//                taskState = TaskState.In_progress;
//                break;
//            case "Completed":
//                taskState = TaskState.Completed;
//                break;
//            default:
//                taskState = TaskState.New;
//        }


        //        Task newTask = new Task(taskTitleString, taskDescriptionString, taskState);
//        TaskDatabase.getInstance(getApplicationContext()).taskDao().insertTask(newTask);

        com.example.taskmaster.data.Team team1 = new com.example.taskmaster.data.Team("First Team");
        com.example.taskmaster.data.Team team2 = new com.example.taskmaster.data.Team("Second Team");
        com.example.taskmaster.data.Team team3 = new com.example.taskmaster.data.Team("Third Team");

        TeamDatabase.getInstance(this).teamDao().insertTeam(team1);
        TeamDatabase.getInstance(this).teamDao().insertTeam(team2);
        TeamDatabase.getInstance(this).teamDao().insertTeam(team3);

    }
}
