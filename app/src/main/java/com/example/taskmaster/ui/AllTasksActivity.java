package com.example.taskmaster.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmaster.R;
import com.example.taskmaster.data.TaskData;
import com.example.taskmaster.data.TaskState;

import java.util.ArrayList;
import java.util.List;

public class AllTasksActivity extends AppCompatActivity {

    List<TaskData> taskDataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        initialiseData();

        ListView tasksList = findViewById(R.id.list_tasks);

        ArrayAdapter<TaskData> taskDataArrayAdapter = new ArrayAdapter<TaskData>(
                this
                , android.R.layout.simple_list_item_2
                , android.R.id.text2
                , taskDataList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position,convertView,parent);

                TextView title = (TextView) view.findViewById(android.R.id.text1);
                TextView state = (TextView) view.findViewById(android.R.id.text2);

                title.setText(taskDataList.get(position).getTitle());
                state.setText(taskDataList.get(position).getTaskState().getDisplayValue());

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

        taskDataList.add(new TaskData("Bring Ingredients", "Go to market and bring some " +
                "milk and 5 eggs and some butter and don't forget the flour", TaskState.In_progress));
        taskDataList.add(new TaskData("Sort Ingredients", "Sort our Ingredients according to" +
                " when we will use it and start clean the place where we will work", TaskState.Assigned));
        taskDataList.add(new TaskData("Bring Helper Tools", "Go and bring all the necessary helper tools" +
                " like Wooden spoon ,Measuring cup ,Mixing bowl and spatula etc..", TaskState.Assigned));

    }
}