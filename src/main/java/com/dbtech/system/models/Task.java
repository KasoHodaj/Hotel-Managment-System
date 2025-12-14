package com.dbtech.system.models;

import java.time.LocalDate;

public class Task {
    private int id;
    private String description;
    private LocalDate date;
    private boolean completed;
    private String type;

    public Task(int id, String description, LocalDate date, boolean completed, String type) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.completed = completed;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}