package com.example.taskmaster.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TeamDao {

    @Query("SELECT * FROM teams")
    List<Team> getAll();

    @Query("SELECT * FROM teams WHERE id = :id")
    Team getTeamById(Long id);

    @Insert
    void insertTeam(Team task);

    @Update
    void update(Team task);

    @Delete
    void deleteTeam(Team task);


}
