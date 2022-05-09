package com.example.taskmaster.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM tasks")
    List<Task> getAll();

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task getTaskById(Long id);

    @Insert
    void insertTask(Task task);

    @Delete
    void deleteTask(Task task);

}
