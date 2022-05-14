package com.example.taskmaster.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.taskmaster.R;
import com.example.taskmaster.data.AppDatabase;
import com.example.taskmaster.data.Task;
import com.example.taskmaster.data.TaskState;

public class UpdateActivity extends AppCompatActivity {

    private Task newTask;
    private EditText taskTitle;
    private EditText taskDescription;
    private Spinner taskStateSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);


        newTask = AppDatabase.getInstance(getApplicationContext()).taskDao().getTaskById(getIntent().getLongExtra("Position", 1));

        Button saveButton = findViewById(R.id.updateTaskButton);
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

        TaskState taskState;
        switch (taskStateSpinner.getSelectedItem().toString()) {
            case "Assigned":
                taskState = TaskState.Assigned;
                break;
            case "In progress":
                taskState = TaskState.In_progress;
                break;
            case "Completed":
                taskState = TaskState.Completed;
                break;
            default:
                taskState = TaskState.New;
        }


        newTask.setTitle(taskTitle.getText().toString());
        newTask.setBody(taskDescription.getText().toString());
        newTask.setTaskState(taskState);

        AppDatabase.getInstance(getApplicationContext()).taskDao().update(newTask);

    }

    public void initializeForm() {

        setAdapterToStatesTaskArraySpinner();

        taskTitle = findViewById(R.id.updateTaskTitleBox);
        taskDescription = findViewById(R.id.updateTaskDescriptionBox);
        taskStateSpinner = findViewById(R.id.update_task_states_spinner);

        TaskState compareValue = newTask.getTaskState();

        int spinnerPosition;
        switch (compareValue) {
            case Assigned:
                spinnerPosition = 2;
                break;
            case In_progress:
                spinnerPosition = 3;
                break;
            case Completed:
                spinnerPosition = 4;
                break;
            default:
                spinnerPosition = 1;
        }
        taskStateSpinner.setSelection(spinnerPosition);
        taskTitle.setText(newTask.getTitle());
        taskDescription.setText(newTask.getBody());


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

    public void backToTaskDetailsPage() {
        Intent intent = new Intent(this, TaskDetailsActivity.class);
        intent.putExtra("Title", newTask.getTitle());
        intent.putExtra("State", newTask.getTaskState().getDisplayValue());
        intent.putExtra("Body", newTask.getBody());
        intent.putExtra("Position", newTask.getId());
        startActivity(intent);
    }
}