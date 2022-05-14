package com.example.taskmaster.data;

public enum TaskState {

    New("New"),
    Assigned("Assigned"),
    In_progress("In progress"),
    Completed("Completed");


    private final String displayValue;

    private TaskState(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

}
