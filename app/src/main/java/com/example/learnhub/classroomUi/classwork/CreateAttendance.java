package com.example.learnhub.classroomUi.classwork;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.learnhub.R;
import com.example.learnhub.model.AttendanceModel;
import com.example.learnhub.model.NotificationModel;
import com.example.learnhub.model.UserSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateAttendance extends AppCompatActivity {
    EditText attenTitle  ,attenTime;
    ProgressBar progressBar;
    TextView attenDate;
    ImageButton datebtn;
    Button createAttendancebtn;
    String classcode,date;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_attendance);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        toolbar = findViewById(R.id.attendance_Toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Drawable upArrow = getResources().getDrawable(R.drawable.backbtn); // Default back icon for AppCompat
            upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent  = getIntent();
        classcode = intent.getStringExtra("classcode");
        attenTitle = findViewById(R.id.attendanceTitle);
        attenDate = findViewById(R.id.attendanceDate);
        attenTime = findViewById(R.id.attendanceTimeLimit);
        datebtn = findViewById(R.id.datebtn);
        createAttendancebtn =findViewById(R.id.createAttendanceButton);
        progressBar = findViewById(R.id.attenProgressbar);

        datebtn.setOnClickListener(v -> showDatePicker());
        createAttendancebtn.setOnClickListener(v -> createAttendance());

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showDatePicker() {
        // Show date picker dialog for selecting due date
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    date = dateFormat.format(calendar.getTime());
                    Toast.makeText(this, "Due date: " + date, Toast.LENGTH_SHORT).show();
                    attenDate.setText(date);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
    private void createAttendance() {
        ChangeProgress(true);
        String title = attenTitle.getText().toString().trim();
        String timeLimitStr = attenTime.getText().toString().trim();

        // Validate inputs
        if (title.isEmpty() || date.isEmpty() || timeLimitStr.isEmpty()) {
            Toast.makeText(CreateAttendance.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        int timeLimitInMinutes = Integer.parseInt(timeLimitStr);
        long timeLimitInMillis = timeLimitInMinutes * 60 * 1000;

        long startTimeInMillis = System.currentTimeMillis();


        AttendanceModel attendanceModel = new AttendanceModel(title, date, timeLimitInMillis,startTimeInMillis, true);

        // Get reference to Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Attendance").child(classcode);

        // Push the data to Firebase under the "Attendance" node
        String attendanceId = databaseReference.push().getKey(); // Generate unique ID
        if (attendanceId != null) {
            databaseReference.child(attendanceId).setValue(attendanceModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserSession userSession = new UserSession(getApplicationContext());
                            String username = userSession.getUserName();
                            NotificationModel.NotificationUtils.sendNotification(getApplicationContext(),title,"Uploaded the Attendance",username,classcode);
                            Toast.makeText(CreateAttendance.this, "Attendance created successfully", Toast.LENGTH_SHORT).show();
                            ChangeProgress(false);
                        } else {
                            Toast.makeText(CreateAttendance.this, "Failed to create attendance", Toast.LENGTH_SHORT).show();
                            ChangeProgress(false);
                        }
                    });
        } else {
            Toast.makeText(CreateAttendance.this, "Error generating attendance ID", Toast.LENGTH_SHORT).show();
            ChangeProgress(false);
        }

    }
    private void ChangeProgress(Boolean progress){
        if (progress){
            progressBar.setVisibility(View.VISIBLE);
            createAttendancebtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            createAttendancebtn.setVisibility(View.VISIBLE);
        }
    }
}