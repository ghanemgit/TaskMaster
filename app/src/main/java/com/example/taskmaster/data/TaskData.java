package com.example.taskmaster.data;

public class TaskData {

    private final String title;
    private final String body;
    private final TaskState taskState;

    public TaskData(String title, String body, TaskState taskState) {
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
}
