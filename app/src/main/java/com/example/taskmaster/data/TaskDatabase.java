package com.example.taskmaster.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class},version = 1,exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    private static TaskDatabase taskDatabase;

    public TaskDatabase() {
    }


    public static synchronized TaskDatabase getInstance(Context context){
        if (taskDatabase == null){
            taskDatabase = Room.databaseBuilder(context,
                    TaskDatabase.class,
                    "TaskDatabase").allowMainThreadQueries().build();
        }

        return taskDatabase;
    }
}
