package com.example.mytodolist.data;

import java.util.Calendar;
import java.util.Date;

public class TaskModel {
    private long id;
    private String content;
    private Date datetime;
    private Boolean isDone;

    private TaskModel(Builder builder) {
        this.id = 0;
        this.content = builder.content;
        this.datetime = builder.datetime;
        this.isDone = builder.isDone;
    }

    // setId used for sharedprefs auto-incrementation method
    void setId(long id) {
        this.id = id;
    }

    // Getters for the fields (optional)

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Date getDatetime() {
        return new Date(datetime.getTime()); // Returns a copy, preventing modification
    }

    public Boolean getIsDone() {
        return isDone;
    }

    // Builder class for optional and required parameters

    public static class Builder {

        // REQUIRED FIELDS
        private String content;

        // OPTIONAL FIELDS
        private Date datetime = Calendar.getInstance().getTime();
        private Boolean isDone = false;

        public Builder(String content) {
            this.content = content;
        }

        // SETTERS FOR OPTIONAL FIELDS

        public Builder setDatetime(Date datetime) {
            this.datetime = new Date(datetime.getTime()); // Store a copy
            return this;
        }

        public Builder setIsDone(Boolean isDone) {
            this.isDone = isDone;
            return this;
        }

        // Build method creates the TaskModel instance.
        public TaskModel build() {
            return new TaskModel(this);
        }
    }

}
