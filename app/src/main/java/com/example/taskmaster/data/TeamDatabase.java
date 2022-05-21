package com.example.taskmaster.data;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Team.class},version = 1)
public abstract class TeamDatabase extends RoomDatabase {

    public abstract TeamDao teamDao();

    private static TeamDatabase teamDatabase;

    public TeamDatabase() {
    }


    public static synchronized TeamDatabase getInstance(Context context){
        if (teamDatabase == null){
            teamDatabase = Room.databaseBuilder(context,
                    TeamDatabase.class,
                    "TeamDatabase").allowMainThreadQueries().build();
        }

        return teamDatabase;
    }



}
