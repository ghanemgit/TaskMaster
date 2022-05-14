package com.example.taskmaster.ui;


import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.taskmaster.data.AppDatabase;
import com.example.taskmaster.data.Task;
import com.example.taskmaster.data.TaskDao;
import com.example.taskmaster.data.TaskState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(AndroidJUnit4.class)
public class AppDatabaseTest {

    private TaskDao taskDao;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        taskDao = db.taskDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void writeTaskAndReadInList() {

        Task task1 = new Task("First Task","Change clothes", TaskState.In_progress);
        Task task2 = new Task("Second Task","Clean teeth", TaskState.Assigned);
        Task task3 = new Task("Third Task","Sleep", TaskState.Assigned);

        taskDao.insertTask(task1);
        taskDao.insertTask(task2);
        taskDao.insertTask(task3);

        List<Task> tasks = taskDao.getAll();

        //Iterate over all task and convert them to string by lambda formula and add them to string list
        List<String> tasksString = taskDao.getAll().stream().map(Task::toString).collect(Collectors.toList());

        //convert the task from the database to string and check if it equal to the task from the list
        assertThat(tasksString,hasItems(taskDao.getTaskById(1L).toString(),
                                        taskDao.getTaskById(2L).toString(),
                                        taskDao.getTaskById(3L).toString()
        ));

        assertThat(tasks.size(),is(3));
    }

    /*
    * Resources
    * https://mkyong.com/unittest/junit-how-to-test-a-list/
    * https://developer.android.com/training/data-storage/room/testing-db
    * of course with some edit from me
    */

}
