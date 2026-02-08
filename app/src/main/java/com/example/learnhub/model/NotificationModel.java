package com.example.learnhub.model;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationModel {
    private String notificationTitle;
    private String notificationDescription;
    private String notificationDateTime;
    private String notificationUsername;
    private String notificationClasscode;
    private boolean read;

    public NotificationModel() {
    }

    public NotificationModel(String notificationTitle, String notificationDescription, String notificationDateTime, String notificationUsername, String notificationClasscode, boolean read) {
        this.notificationTitle = notificationTitle;
        this.notificationDescription = notificationDescription;
        this.notificationDateTime = notificationDateTime;
        this.notificationUsername = notificationUsername;
        this.notificationClasscode = notificationClasscode;
        this.read = read;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationDescription() {
        return notificationDescription;
    }

    public void setNotificationDescription(String notificationDescription) {
        this.notificationDescription = notificationDescription;
    }

    public String getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(String notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }

    public String getNotificationUsername() {
        return notificationUsername;
    }

    public void setNotificationUsername(String notificationUsername) {
        this.notificationUsername = notificationUsername;
    }

    public String getNotificationClasscode() {
        return notificationClasscode;
    }

    public void setNotificationClasscode(String notificationClasscode) {
        this.notificationClasscode = notificationClasscode;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public static class NotificationUtils {
       static String usertype;
        // Static method to send a notification
        public static void sendNotification(Context context, String title, String description, String facultyName, String classcode) {
            // Get a reference to the Firebase database
            UserSession userSession = new UserSession(context);
            usertype = userSession.getUserType();
            DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("Notifications")
                    .child(usertype).child(classcode);

            // Generate a unique notification ID using push().getKey()
            String notificationId = notificationsRef.push().getKey();

            // Create a new Notification object
            NotificationModel notification = new NotificationModel(
                    title,
                    description,
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                    facultyName,
                    classcode,
                    false // notification has not been read yet
            );

            // Save the notification to Firebase
            if (notificationId != null) {
                notificationsRef.child(notificationId).setValue(notification);
            }
        }

    }
}
