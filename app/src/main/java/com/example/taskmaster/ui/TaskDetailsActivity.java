package com.example.taskmaster.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.query.Where;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmaster.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@RequiresApi(api = Build.VERSION_CODES.N)
public class TaskDetailsActivity extends AppCompatActivity {

    private static final String TAG = TaskDetailsActivity.class.getSimpleName();
    private Task currentTask = null;
    public static List<Task> tempTask = new ArrayList<>();
    private Task taskFromAddTaskPage=null;
    private String teamName = "";
    private TextView state;
    private TextView body;
    private TextView team;
    private Button deleteButton;
    private Button editButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        findAllViewById();

        loadTaskInfoFromMain();

        setTextForTaskView();

        editButton.setOnClickListener(view -> editTask());

        deleteButton.setOnClickListener(view -> deleteTaskButtonAction());
    }

    public void setActionBarTitleButton(String title) {

       /*
       https://stackoverflow.com/questions/10138007/how-to-change-android-activity-label
       */

        Objects.requireNonNull(getSupportActionBar()).setTitle(title);

        /*
        https://www.youtube.com/watch?v=FcPUFp8Qrps&ab_channel=LemubitAcademy
        In this video i learned how to add back button in action bar
         */
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    private void findAllViewById() {
        state = findViewById(R.id.task_state_in_details_page);
        body = findViewById(R.id.task_body_in_details_page);
        team = findViewById(R.id.task_team_in_details_page);
        deleteButton = findViewById(R.id.delete_button);
        editButton = findViewById(R.id.edit_button);
    }

    private void loadTaskInfoFromMain() {


            currentTask = MainActivity.tasksList.stream().filter(task1 -> task1.getId().equals(getIntent().getStringExtra("Position"))).collect(Collectors.toList()).get(0);

            Task finalCurrentTask = currentTask;

            teamName = SplashActivity.teamsList.stream().filter(team1 -> team1.getId().equals(finalCurrentTask.getTeamTasksId())).collect(Collectors.toList()).get(0).getName();

    }




    private void setTextForTaskView() {


        state.setText("State => " + currentTask.getStatus());
        body.setText("Description:\n" + currentTask.getDescription());
        team.setText("This task for => " + teamName);
        setActionBarTitleButton(currentTask.getTitle());
    }


    public void deleteTaskButtonAction() {

        AlertDialog.Builder deleteAlert = new AlertDialog.Builder(TaskDetailsActivity.this);
        deleteAlert.setTitle("Warning!");
        deleteAlert.setMessage("Are you sure to delete the task?");
        /*
        How to add alert to my program
        https://stackoverflow.com/questions/23195208/how-to-pop-up-a-dialog-to-confirm-delete-when-user-long-press-on-the-list-item
        */
        deleteAlert.setPositiveButton("Yes", (dialogInterface, i) -> {
            Intent intent = new Intent(TaskDetailsActivity.this, MainActivity.class);
            deleteTaskFromLocalAndApi();
            Toast.makeText(TaskDetailsActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });
        deleteAlert.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

        deleteAlert.show();
    }

    private void deleteTaskFromLocalAndApi() {

        Amplify.DataStore.query(Task.class, Where.id(currentTask.getId()),
                matches -> {
                    if (matches.hasNext()) {
                        Task task = matches.next();
                        Amplify.DataStore.delete(task,
                                deleted -> Log.i("MyAmplifyApp", "Deleted a task."),
                                failure -> Log.e("MyAmplifyApp", "Delete failed.", failure)
                        );
                    }
                },
                failure -> Log.e("MyAmplifyApp", "Query failed.", failure)
        );

        Amplify.API.mutate(ModelMutation.delete(currentTask),
                response -> Log.i("MyAmplifyApp", "Todo with id: "),
                error -> Log.e("MyAmplifyApp", "Create failed", error)
        );

    }


    public void editTask() {


        Intent intent = new Intent(TaskDetailsActivity.this, UpdateActivity.class);
        intent.putExtra("Id", currentTask.getId());
        startActivity(intent);


    }
}