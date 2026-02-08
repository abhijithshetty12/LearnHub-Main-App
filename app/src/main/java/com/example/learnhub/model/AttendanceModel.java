package com.example.learnhub.model;

public class AttendanceModel {
        private String attentitle; // Title of the attendance (e.g., "Week 1 Attendance")
        private String date; // Date when the attendance was created
        private long timeLimit,startTime; // Time limit (in milliseconds) for marking attendance
        private boolean isOpen; // Status indicating if attendance is open or closed

        // Default constructor required for calls to DataSnapshot.getValue(AttendanceModel.class)
        public AttendanceModel() {}

        // Constructor with parameters


    public AttendanceModel(String attentitle, String date, long timeLimit, long startTime, boolean isOpen) {
        this.attentitle = attentitle;
        this.date = date;
        this.timeLimit = timeLimit;
        this.startTime = startTime;
        this.isOpen = isOpen;
    }

    // Getters and Setters
        public String getAttentitle() {
            return attentitle;
        }

        public void setAttentitle(String attentitle) {
            this.attentitle = attentitle;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public long getTimeLimit() {
            return timeLimit;
        }

        public void setTimeLimit(long timeLimit) {
            this.timeLimit = timeLimit;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public static class StudentAttendance {
            String stdAttendTitle ,SubmitDate ,stdName,stdEmail ;
            long SubmitTime;
            boolean isPresent;

        public StudentAttendance(String stdAttendTitle, String submitDate, String stdName, String stdEmail, long submitTime, boolean isPresent) {
            this.stdAttendTitle = stdAttendTitle;
            SubmitDate = submitDate;
            this.stdName = stdName;
            this.stdEmail = stdEmail;
            SubmitTime = submitTime;
            this.isPresent = isPresent;
        }

        public StudentAttendance(String stdName, boolean isPresent) {
            this.stdName = stdName;
            this.isPresent = isPresent;
        }

        public StudentAttendance() {
        }

        public String getStdAttendTitle() {
            return stdAttendTitle;
        }

        public void setStdAttendTitle(String stdAttendTitle) {
            this.stdAttendTitle = stdAttendTitle;
        }

        public String getSubmitDate() {
            return SubmitDate;
        }

        public void setSubmitDate(String submitDate) {
            SubmitDate = submitDate;
        }

        public String getStdName() {
            return stdName;
        }

        public void setStdName(String stdName) {
            this.stdName = stdName;
        }

        public String getStdEmail() {
            return stdEmail;
        }

        public void setStdEmail(String stdEmail) {
            this.stdEmail = stdEmail;
        }

        public long getSubmitTime() {
            return SubmitTime;
        }

        public void setSubmitTime(long submitTime) {
            SubmitTime = submitTime;
        }

        public boolean isPresent() {
            return isPresent;
        }

        public void setPresent(boolean present) {
            isPresent = present;
        }
    }
}

