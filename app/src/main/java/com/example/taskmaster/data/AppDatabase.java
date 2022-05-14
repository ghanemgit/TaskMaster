package com.example.taskmaster.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    private static AppDatabase appDatabase;

    public AppDatabase() {
    }


    public static synchronized AppDatabase getInstance(Context context){
        if (appDatabase == null){
            appDatabase = Room.databaseBuilder(context,
                    AppDatabase.class,
                    "AppDatabase").allowMainThreadQueries().build();
        }

        return appDatabase;
    }
}
