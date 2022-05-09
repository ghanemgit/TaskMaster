package com.example.taskmaster.data;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "Tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "Title")
    private final String title;

    @ColumnInfo(name = "Body")
    private final String body;

    @ColumnInfo(name = "Task_state")
    private final TaskState taskState;

    public Task(String title, String body, TaskState taskState) {
        this.title = title;
        this.body = body;
        this.taskState = taskState;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
