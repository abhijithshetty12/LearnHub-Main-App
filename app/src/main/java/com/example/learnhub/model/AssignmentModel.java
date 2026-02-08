package com.example.learnhub.model;

import android.net.Uri;

import java.util.List;
import java.util.Map;

public class AssignmentModel {
    private String assignTitle, assignDescription, duedate;
    private List<FileInfo> fileMap;

    public AssignmentModel() {
    }

    public AssignmentModel(String assignTitle, String assignDescription, String duedate, List<FileInfo> fileMap) {
        this.assignTitle = assignTitle;
        this.assignDescription = assignDescription;
        this.duedate = duedate;
        this.fileMap = fileMap;
    }

    public AssignmentModel(String assignTitle) {
        this.assignTitle = assignTitle;
    }

    public String getAssignTitle() {
        return assignTitle;
    }

    public void setAssignTitle(String assignTitle) {
        this.assignTitle = assignTitle;
    }

    public String getAssignDescription() {
        return assignDescription;
    }

    public void setAssignDescription(String assignDescription) {
        this.assignDescription = assignDescription;
    }

    public String getDuedate() {
        return duedate;
    }

    public void setDuedate(String duedate) {
        this.duedate = duedate;
    }

    public List<FileInfo>  getFileMap() {
        return fileMap;
    }

    public void setFileMap(List<FileInfo> fileMap) {
        this.fileMap = fileMap;
    }

    // Create a FileInfo model
    public static class FileInfo {
        private String fileName;
        private String fileUrl;

        // Default constructor for Firebase
        public FileInfo() {
        }

        public FileInfo(String fileName, String fileUrl) {
            this.fileName = fileName;
            this.fileUrl = fileUrl;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }

    public static class StudentAssignment {
        private String studentName ,studentemail,submissionDate,submissionStatus;
        private List<FileInfo> fileMap ;

        public StudentAssignment() {
        }

        public StudentAssignment(String studentName, String studentemail, String submissionDate,String submissionStatus, List<FileInfo> fileMap) {
            this.studentName = studentName;
            this.studentemail = studentemail;
            this.submissionDate = submissionDate;
            this.fileMap = fileMap;
            this.submissionStatus =submissionStatus;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getStudentemail() {
            return studentemail;
        }

        public void setStudentemail(String studentemail) {
            this.studentemail = studentemail;
        }

        public String getSubmissionDate() {
            return submissionDate;
        }

        public void setSubmissionDate(String submissionDate) {
            this.submissionDate = submissionDate;
        }

        public List<FileInfo> getFileMap() {
            return fileMap;
        }

        public void setFileMap(List<FileInfo> fileMap) {
            this.fileMap = fileMap;
        }

        public String getSubmissionStatus() {
            return submissionStatus;
        }

        public void setSubmissionStatus(String submissionStatus) {
            this.submissionStatus = submissionStatus;
        }
    }
}

