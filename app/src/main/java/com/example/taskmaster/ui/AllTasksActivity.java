package com.example.taskmaster.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmaster.R;
import com.example.taskmaster.data.Task;
import com.example.taskmaster.data.TaskState;

import java.util.ArrayList;
import java.util.List;

public class AllTasksActivity extends AppCompatActivity {

    List<Task> taskList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        initialiseData();

        ListView tasksList = findViewById(R.id.list_tasks);

        ArrayAdapter<Task> taskDataArrayAdapter = new ArrayAdapter<Task>(
                this
                , android.R.layout.simple_list_item_2
                , android.R.id.text2
                , taskList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView title = (TextView) view.findViewById(android.R.id.text1);
                TextView state = (TextView) view.findViewById(android.R.id.text2);

                title.setText(taskList.get(position).getTitle());
                state.setText(taskList.get(position).getTaskState().getDisplayValue());

                return view;
            }
        };

        tasksList.setAdapter(taskDataArrayAdapter);
    }

    View.OnClickListener btnClickListener = v -> {

        Toast.makeText(this, "Text view clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, TaskDetailsActivity.class);
        startActivity(intent);
    };

    private void initialiseData() {

        taskList.add(new Task("Bring Ingredients", "Go to market and bring some " +
                "milk and 5 eggs and some butter and don't forget the flour", TaskState.In_progress));
        taskList.add(new Task("Sort Ingredients", "Sort our Ingredients according to" +
                " when we will use it and start clean the place where we will work", TaskState.Assigned));
        taskList.add(new Task("Bring Helper Tools", "Go and bring all the necessary helper tools" +
                " like Wooden spoon ,Measuring cup ,Mixing bowl and spatula etc..", TaskState.Assigned));

    }
}