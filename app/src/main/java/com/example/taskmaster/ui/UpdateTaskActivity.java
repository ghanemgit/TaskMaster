package com.example.taskmaster.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.query.Where;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.N)
public class UpdateTaskActivity extends AppCompatActivity {

    private static final String TAG = UpdateTaskActivity.class.getSimpleName();
    private Task oldTask;
    private Task newTask;
    private Team newTeam;
    private EditText taskTitle;
    private EditText taskDescription;
    private Spinner taskStateSpinner;
    private Spinner taskTeamSpinner;
    private Button saveButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        findAllViewById();

        prepareTaskAndTeam();

        initializeForm();

        setButtonClickListener();

        setAdapterToStatesTaskArraySpinner();

        setAdapterToStatesTeamArraySpinner();
    }

    private void findAllViewById() {

        taskTitle = findViewById(R.id.update_task_title_box);
        taskDescription = findViewById(R.id.update_task_description_box);
        taskStateSpinner = findViewById(R.id.update_task_states_spinner);
        taskTeamSpinner = findViewById(R.id.task_team_update_spinner);
        saveButton = findViewById(R.id.update_task_button);
        backButton = findViewById(R.id.backToDescriptionPage);
    }

    private void prepareTaskAndTeam() {

        oldTask = MainActivity.tasksList.stream().filter(task -> task.getId()
                .equals(getIntent().getStringExtra("Id"))).collect(Collectors.toList()).get(0);
        Log.i(TAG, "prepareTaskAndTeam: old task id -> "+oldTask.getId());
        newTeam = SplashActivity.teamsList.stream().filter(team ->
                team.getId().equals(oldTask.getTeamTasksId())).collect(Collectors.toList()).get(0);
    }

    private void setButtonClickListener() {

        saveButton.setOnClickListener(view -> {
            updateTask();
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            backToTaskDetailsPage();
        });
        backButton.setOnClickListener(view -> {
            backToTaskDetailsPage();
        });

    }

    public void updateTask() {

        newTask = Task.builder().title(taskTitle.getText().toString())
                .id(oldTask.getId())
                .description(taskDescription.getText().toString())
                .status(taskStateSpinner.getSelectedItem().toString())
                .teamTasksId(newTeam.getId())
                .build();


        Amplify.API.mutate(ModelMutation.update(newTask),
                pass -> {
                    Log.i(TAG, "API update done the task id is: " + pass.getData().getId());
                },
                error -> Log.e(TAG, "Update failed", error)
        );

        Amplify.DataStore.query(Team.class, Where.id(newTeam.getId()),
                matchesTeam -> {
                    if (matchesTeam.hasNext()) {
                        Team originalTeam = matchesTeam.next();
                        Team editedTeam = originalTeam.copyOfBuilder()
                                .name(newTeam.getName())
                                .build();
                        Amplify.DataStore.save(editedTeam,

                                updated -> {
                                    Log.i(TAG, "Updated a team.");

                                    Amplify.DataStore.query(Task.class, Where.id(oldTask.getId()),
                                            matchesTask -> {
                                                if (matchesTask.hasNext()) {
                                                    Task originalTask = matchesTask.next();
                                                    Task editedTask = originalTask.copyOfBuilder()
                                                            .title(taskTitle.getText().toString())
                                                            .id(originalTask.getId())
                                                            .description(taskDescription.getText().toString())
                                                            .status(taskStateSpinner.getSelectedItem().toString())
                                                            .teamTasksId(newTeam.getId())
                                                            .build();
                                                    Amplify.DataStore.save(editedTask,
                                                            updatedTask -> Log.i(TAG, "Updated a task."),
                                                            failureTask -> Log.e(TAG, "Update failed.", failureTask)
                                                    );
                                                }
                                            },
                                            failure -> Log.e(TAG, "Query failed.", failure)
                                    );
                                },
                                failure -> Log.e(TAG, "Update failed.", failure)
                        );

                    }
                },
                failure -> Log.e(TAG, "Query failed.", failure)
        );


        //TaskDatabase.getInstance(getApplicationContext()).taskDao().update(newTask);

    }

    public void initializeForm() {

        String compareValue = oldTask.getStatus();

        int spinnerPosition;
        switch (compareValue) {
            case "Assigned":
                spinnerPosition = 2;
                break;
            case "In progress":
                spinnerPosition = 3;
                break;
            case "Completed":
                spinnerPosition = 4;
                break;
            default:
                spinnerPosition = 1;
        }
        taskTeamSpinner.setSelection(0);
        taskStateSpinner.setSelection(spinnerPosition);
        taskTitle.setText(oldTask.getTitle());
        taskDescription.setText(oldTask.getDescription());
    }

    private void setAdapterToStatesTaskArraySpinner() {

        /*
        https://developer.android.com/guide/topics/ui/controls/spinner
         */
        taskStateSpinner = findViewById(R.id.update_task_states_spinner);


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_states_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        taskStateSpinner.setAdapter(adapter);


    }

    private void setAdapterToStatesTeamArraySpinner() {

        /*
        https://developer.android.com/guide/topics/ui/controls/spinner
         */
        List<String> teams = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            teams = SplashActivity.teamsList.stream().map(Team::getName).sorted().collect(Collectors.toList());
        }

        /*
        https://www.codegrepper.com/code-examples/java/android+studio+how+to+fill+spinner
         */
        Spinner spinner = findViewById(R.id.task_team_update_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
                teams);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void backToTaskDetailsPage() {
        Intent intent = new Intent(this, TaskDetailsActivity.class);
        intent.putExtra("Title", newTask.getTitle());
        intent.putExtra("State", newTask.getStatus());
        intent.putExtra("Body", newTask.getDescription());
        intent.putExtra("TeamId", newTask.getTeamTasksId());
        intent.putExtra("Position", newTask.getId());
        startActivity(intent);
        finish();
    }
}