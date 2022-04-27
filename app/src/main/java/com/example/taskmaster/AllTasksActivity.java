package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AllTasksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);


        ConstraintLayout containerLayout = findViewById(R.id.all_tasks_cons_layout);

        for (int i = 0; i < 2; i++) {
            TextView dynaText = new TextView(this);

            ConstraintSet set = new ConstraintSet();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            dynaText.setText(sharedPreferences.getString(AddTaskActivity.TASK_TITLE,"No Tasks"));

            set.connect(dynaText.getId(), ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 100);
            dynaText.setId(i+1);

            set.applyTo(containerLayout);



            set.constrainDefaultWidth(dynaText.getId(),350);
            set.constrainDefaultHeight(dynaText.getId(),90);

            dynaText.setTextSize(30);
            dynaText.setTag("" + i);

            dynaText.setOnClickListener(btnClickListener);

            containerLayout.addView(dynaText);
        }

        
    }

    View.OnClickListener btnClickListener = v -> {

        Toast.makeText(this, "Text view clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,TaskDetailsActivity.class);
        startActivity(intent);
    };
}