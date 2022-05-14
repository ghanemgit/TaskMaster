package com.example.taskmaster.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmaster.R;
import com.example.taskmaster.data.AppDatabase;
import com.example.taskmaster.data.Task;
import com.example.taskmaster.data.TaskDao;

import java.util.List;
import java.util.Objects;

public class TaskDetailsActivity extends AppCompatActivity {

    private static final String TAG = TaskDetailsActivity.class.getSimpleName();
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);


        TextView state = findViewById(R.id.task_state_in_details_page);
        TextView body = findViewById(R.id.task_body_in_details_page);

        Task currentTask = AppDatabase.getInstance(this).taskDao().getTaskById(getIntent().getLongExtra("Position", 1));

        state.setText("State => " + currentTask.getTaskState().getDisplayValue());
        body.setText("Description:\n" + currentTask.getBody());
        setActionBarTitle(currentTask.getTitle());

        Button deleteButton = findViewById(R.id.delete_button);
        Button editButton = findViewById(R.id.edit_button);

        deleteButton.setOnClickListener(view -> {
            deleteTaskFromDatabaseById();
        });

        editButton.setOnClickListener(view -> {
            editTask();
        });





        /*
        https://www.youtube.com/watch?v=FcPUFp8Qrps&ab_channel=LemubitAcademy
        In this video i learned how to add back button in action bar
         */
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }


    public void setActionBarTitle(String title) {

       /*
       https://stackoverflow.com/questions/10138007/how-to-change-android-activity-label
       */

        Objects.requireNonNull(getSupportActionBar()).setTitle(title);


    }

    public void deleteTaskFromDatabaseById() {

        AlertDialog.Builder deleteAlert = new AlertDialog.Builder(TaskDetailsActivity.this);
        deleteAlert.setTitle("Warning!");
        deleteAlert.setMessage("Are you sure to delete the task");
        /*
        How to add alert to my program
        https://stackoverflow.com/questions/23195208/how-to-pop-up-a-dialog-to-confirm-delete-when-user-long-press-on-the-list-item
        */
        deleteAlert.setPositiveButton("Yes", (dialogInterface, i) -> {
            Intent intent = new Intent(TaskDetailsActivity.this, MainActivity.class);
            task = AppDatabase.getInstance(getApplicationContext()).taskDao().getTaskById(getIntent().getLongExtra("Position", 0));
            AppDatabase.getInstance(getApplicationContext()).taskDao().deleteTask(task);
            Toast.makeText(TaskDetailsActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });
        deleteAlert.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

        deleteAlert.show();
    }

    public void editTask() {

        task = AppDatabase.getInstance(getApplicationContext()).taskDao().getTaskById(getIntent().getLongExtra("Position", 0));
        Intent intent = new Intent(TaskDetailsActivity.this, UpdateActivity.class);
        intent.putExtra("Position", task.getId());
        startActivity(intent);

    }
}