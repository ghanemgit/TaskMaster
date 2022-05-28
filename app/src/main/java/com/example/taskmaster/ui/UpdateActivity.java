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
public class UpdateActivity extends AppCompatActivity {

    private Task newTask;
    private Team newTeam;
    private EditText taskTitle;
    private EditText taskDescription;
    private Spinner taskStateSpinner;
    private Spinner taskTeamSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);


        newTask = MainActivity.tasksList.stream().filter(task -> task.getId()
                .equals(getIntent().getStringExtra("Id"))).collect(Collectors.toList()).get(0);
        newTeam = SplashActivity.teamsList.stream().filter(team ->
                team.getId().equals(newTask.getTeamTasksId())).collect(Collectors.toList()).get(0);
        Button saveButton = findViewById(R.id.update_task_button);
        Button backButton = findViewById(R.id.backToDescriptionPage);

        initializeForm();

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

        Amplify.API.mutate(ModelMutation.update(newTeam),
                response -> {

                    Log.i("MyAmplifyApp", "Todo with id: " + response.getData().getId());
                    Amplify.API.mutate(ModelMutation.update(newTask),
                            pass -> Log.i("MyAmplifyApp", "Todo with id: " + pass.getData().getId()),
                            error -> Log.e("MyAmplifyApp", "Create failed", error)
                    );
                },
                error -> Log.e("MyAmplifyApp", "Create failed", error)
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
                                    Log.i("MyAmplifyApp", "Updated a post.");

                                    Amplify.DataStore.query(Task.class, Where.id(newTask.getId()),
                                            matchesTask -> {
                                                if (matchesTask.hasNext()) {
                                                    Task originalTask = matchesTask.next();
                                                    Task editedTask = originalTask.copyOfBuilder()
                                                            .title(taskTitle.getText().toString())
                                                            .description(taskDescription.getText().toString())
                                                            .status(taskStateSpinner.getSelectedItem().toString())
                                                            .teamTasksId(taskTeamSpinner.getSelectedItem().toString())
                                                            .build();
                                                    Amplify.DataStore.save(editedTask,
                                                            updatedTask -> Log.i("MyAmplifyApp", "Updated a post."),
                                                            failureTask -> Log.e("MyAmplifyApp", "Update failed.", failureTask)
                                                    );
                                                }
                                            },
                                            failure -> Log.e("MyAmplifyApp", "Query failed.", failure)
                                    );
                                },
                                failure -> Log.e("MyAmplifyApp", "Update failed.", failure)
                        );

                    }
                },
                failure -> Log.e("MyAmplifyApp", "Query failed.", failure)
        );


        //TaskDatabase.getInstance(getApplicationContext()).taskDao().update(newTask);

    }

    public void initializeForm() {

        setAdapterToStatesTaskArraySpinner();
        setAdapterToStatesTeamArraySpinner();

        taskTitle = findViewById(R.id.update_task_title_box);
        taskDescription = findViewById(R.id.update_task_description_box);
        taskStateSpinner = findViewById(R.id.update_task_states_spinner);
        taskTeamSpinner = findViewById(R.id.task_team_update_spinner);


        String compareValue = newTask.getStatus();

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
        taskTitle.setText(newTask.getTitle());
        taskDescription.setText(newTask.getDescription());


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