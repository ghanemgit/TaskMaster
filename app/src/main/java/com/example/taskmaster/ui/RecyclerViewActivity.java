package com.example.taskmaster.ui;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

@RequiresApi(api = Build.VERSION_CODES.N)
public class RecyclerViewActivity extends AppCompatActivity {

//    List<Task> taskList;
//
//    public RecyclerViewActivity(List<Task> taskList) {
//        this.taskList = taskList;
//    }
//
//    public RecyclerViewActivity() {
//
//    }
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_recycler_view);
//
//
//        RecyclerView recyclerView = findViewById(R.id.recycler_view);
//
//        CustomListRecyclerViewAdapter customListRecyclerViewAdapter = new CustomListRecyclerViewAdapter(taskList
//                , position -> navigateToTaskDetailsPage());
//
//        recyclerView.setAdapter(customListRecyclerViewAdapter);
//
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }
//
//
//    public void navigateToTaskDetailsPage() {
//
//        Intent intent = new Intent(this, TaskDetailsActivity.class);
//        startActivity(intent);
//
//    }


}