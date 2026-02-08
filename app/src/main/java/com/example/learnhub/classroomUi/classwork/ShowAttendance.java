package com.example.learnhub.classroomUi.classwork;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.learnhub.R;
import com.example.learnhub.model.AttendanceModel;
import com.example.learnhub.model.NotificationModel;
import com.example.learnhub.model.UserSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ShowAttendance extends AppCompatActivity {
   TextView attenTitle ,attenDate,attenTime;
   Button presentbtn ,absentbtn;
   String title,date,classcode ;
   boolean status,isPresent=false,isfetchAttendance=false;
   long time,starttime;
   CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_attendance);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Retrieve intent data
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        date = intent.getStringExtra("date");
        time = intent.getLongExtra("time", 0);
        starttime = intent.getLongExtra("starttime", 0);
        status = intent.getBooleanExtra("status", true);

        UserSession userSession = new UserSession(getApplicationContext());
        classcode = userSession.getClassCode();

        // Initialize views
        attenTitle = findViewById(R.id.attendancestdTitle);
        attenDate = findViewById(R.id.attendanceDatestd);
        attenTime = findViewById(R.id.attendanceTimerStd);
        presentbtn = findViewById(R.id.markPresentButton);
        absentbtn = findViewById(R.id.markAbsentButton);

        attenTitle.setText(title);
        attenDate.setText(date);
        fetchAttendance();
        // Button click listeners
        presentbtn.setOnClickListener(v -> {
            isPresent = true;
            markAttendance();
            stopTimer();
        });
        absentbtn.setOnClickListener(v -> {
            isPresent = false;
            markAttendance();
            stopTimer();
        });



    }

    private void startTimer(long timeInMillis) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer= new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                int hours = (int) (millisUntilFinished / 1000) / 3600;
                int minutes = (int) ((millisUntilFinished / 1000) % 3600) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;

                // Update the TextView with the formatted time
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                attenTime.setText("Time Remaining: " + timeFormatted);
            }

            @Override
            public void onFinish() {
                markAttendance();
                handleExpiredAttendance();

            }
        }.start();
    }
    private void stopTimer() {
        // Stop the countdown timer when a button is clicked
        if (countDownTimer != null) {
            countDownTimer.cancel();
            attenTime.setText("Submitted at " +formatTime(System.currentTimeMillis()));
        }
    }
    private void handleExpiredAttendance() {
        attenTime.setText("Time's up!");
        disableAttendanceButtons();
        Toast.makeText(ShowAttendance.this, "Attendance time has expired", Toast.LENGTH_SHORT).show();
    }

    private void markAttendance() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        DatabaseReference attendRef = FirebaseDatabase.getInstance().getReference("StudentAttendance")
                .child(classcode).child(title);
        UserSession userSession = new UserSession(getApplicationContext());
        String username = userSession.getUserName();
        String email = userSession.getUserEmail();

        AttendanceModel.StudentAttendance studentAttendance = new AttendanceModel.StudentAttendance(
                title, currentDate, username, email, System.currentTimeMillis(), isPresent
        );

        attendRef.child(username).setValue(studentAttendance).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                NotificationModel.NotificationUtils.sendNotification(getApplicationContext(),title,"Submiited the Attendance",username,classcode);
                Toast.makeText(this, "Attendance Submitted Successfully", Toast.LENGTH_SHORT).show();

                disableAttendanceButtons();// Re-fetch to update UI
            }
        });
    }

    private void fetchAttendance() {
        UserSession userSession = new UserSession(getApplicationContext());
        String username = userSession.getUserName();
        DatabaseReference attendRef = FirebaseDatabase.getInstance().getReference("StudentAttendance")
                .child(classcode).child(title).child(username);
        isfetchAttendance =false;
        attendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isfetchAttendance=true;
                    boolean isPresent = Boolean.TRUE.equals(snapshot.child("present").getValue(Boolean.class));
                    long submittedTime = snapshot.child("submitTime").getValue(Long.class);
                    if (submittedTime != 0) {
                            attenTime.setText("Submitted at " + formatTime(submittedTime));
                    }
                    disableAttendanceButtons();
                    isfetchAttendance=true; // Disable buttons if attendance is already submitted
                }else {
                    checktime();
                    Toast.makeText(ShowAttendance.this, "Not able to fetch ", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });


    }

    private void disableAttendanceButtons() {
        presentbtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.normalgrey)));
        absentbtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.normalgrey)));
        presentbtn.setEnabled(false);
        absentbtn.setEnabled(false);
    }

    public String formatTime(long millis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss ", Locale.getDefault());
        return dateFormat.format(new Date(millis));
    }

    private boolean isCurrentDate(String targetDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        return currentDate.equals(targetDate) ;
    }
    private void checktime(){
        long remainingTimeInMillis = (time + starttime) - System.currentTimeMillis();
        Log.d("ShowAttendance","remaining time "+remainingTimeInMillis);
        Log.d("ShowAttendance","System time "+System.currentTimeMillis());
        Log.d("ShowAttendance","date"+isCurrentDate(date));
        Log.d("ShowAttendance","isfetch"+isfetchAttendance);
        if (remainingTimeInMillis > 0 && isCurrentDate(date)) {
            startTimer(remainingTimeInMillis);
        } else {
            handleExpiredAttendance();
        }
    }
}