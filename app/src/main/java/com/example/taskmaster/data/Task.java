package com.example.taskmaster.data;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "Tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "Title")
    private  String title;

    @ColumnInfo(name = "Body")
    private  String body;

    @ColumnInfo(name = "Task_state")
    private  TaskState taskState;

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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    @NonNull
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", taskState=" + taskState.getDisplayValue() +
                '}';
    }
}
