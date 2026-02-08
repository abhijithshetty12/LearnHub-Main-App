package com.example.learnhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhub.adapter.RecyclerViewAdapterNotes;
import com.example.learnhub.classroomUi.classwork.CreateAssignment;
import com.example.learnhub.classroomUi.classwork.CreateAttendance;
import com.example.learnhub.classroomUi.classwork.CreateQuiz;
import com.example.learnhub.classroomUi.classwork.UploadDocument;
import com.example.learnhub.faculty.Facultyhome;
import com.example.learnhub.model.AssignmentModel;
import com.example.learnhub.model.AttendanceModel;
import com.example.learnhub.model.Document;
import com.example.learnhub.model.UserSession;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Parent extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView notesrecyclerview;
    RecyclerViewAdapterNotes notesAdapter;
    List<String> quizTitleList ,classCodeList;
    String usertype,stdName,stdClass,stdEmail;
    public static final String SHARED_PREFS = "shared_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parent);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent =  getIntent();
        stdName = intent.getStringExtra("stdName");
        stdClass = intent.getStringExtra("stdClass");
        stdEmail = intent.getStringExtra("email");
        Log.d("Parent","name"+stdName);
        Log.d("Parent","email"+stdEmail);
        Log.d("Parent","class"+stdClass);
        toolbar =findViewById(R.id.parenttoolbar);
        toolbar.setTitle(stdName);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.getOverflowIcon().setTint(getResources().getColor(R.color.white));
        //toolbar.getNavigationIcon().setTint(R.color.white);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Drawable upArrow = getResources().getDrawable(R.drawable.logout); // Default back icon for AppCompat
            upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        quizTitleList = new ArrayList<>();
        classCodeList = new ArrayList<>();
        UserSession userSession = new UserSession(getApplicationContext());
        usertype = userSession.getUserType();
        notesrecyclerview =findViewById(R.id.parentRecyclerView);
        notesrecyclerview.setHasFixedSize(true);
        notesrecyclerview.setLayoutManager(new LinearLayoutManager(Parent.this));
        /*addDocument();
        notesAdapter = new RecyclerViewAdapterNotes(getContext(),documentList);*/
        fetchClasscode(()-> addDocument());

        notesAdapter = new RecyclerViewAdapterNotes(getApplicationContext(),new ArrayList<>(),"no");
        notesrecyclerview.setAdapter(notesAdapter);


        toolbar.setNavigationOnClickListener(v -> logout());
    }


    private void logout() {

            new AlertDialog.Builder(Parent.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes",((dialog, which) -> {
                        SharedPreferences sharedPreferences =  getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                        SharedPreferences.Editor editor  = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        startActivity(new Intent(Parent.this, Login.class));
                        finish();
                    }))
                    .setNegativeButton("Cancel",((dialog, which) -> {
                        dialog.dismiss();
                    }))
                    .show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.classwork_toolbar_menu, menu);
     return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            if (R.id.menu_notes == (item.getItemId())) {
                Toast.makeText(Parent.this, "Notes selected", Toast.LENGTH_SHORT).show();
                addDocument();
            } else if (R.id.menu_assignment == (item.getItemId())) {
                Toast.makeText(Parent.this, "Assignment selected", Toast.LENGTH_SHORT).show();
                addAssignment();
            } else if (R.id.menu_quiz == (item.getItemId())) {
                Toast.makeText(Parent.this, "Quiz selected", Toast.LENGTH_SHORT).show();
                addQuiz();
            } else if (R.id.menu_attendance == (item.getItemId())) {
                Toast.makeText(Parent.this, "Attendance selected", Toast.LENGTH_SHORT).show();
                addAttendance();
            }

        return super.onOptionsItemSelected(item);

    }
    private void addDocument() {
        if (classCodeList.isEmpty()) {
            Toast.makeText(this, "No class found", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference notesRef = FirebaseDatabase.getInstance().getReference("Document");
        List<Object> documentList = new ArrayList<>();

        for (String classcode : classCodeList) {
            notesRef.child(classcode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot topicSnapshot : snapshot.getChildren()) {
                            String topicName = topicSnapshot.child("topic").getValue(String.class);
                            String description = topicSnapshot.child("description").getValue(String.class);
                            List<String> documentUrlList = new ArrayList<>();

                            for (DataSnapshot docSnapshot : topicSnapshot.child("documentList").getChildren()) {
                                String documentUrl = docSnapshot.getValue(String.class);
                                if (documentUrl != null) {
                                    documentUrlList.add(documentUrl);
                                }
                            }

                            documentList.add(new Document(topicName, description, documentUrlList));
                        }
                    }
                    notesAdapter.updateData(documentList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Parent.this, "Error fetching documents", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void addQuiz() {
        if (classCodeList.isEmpty()) {
            Toast.makeText(this, "No class found", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference("Quiz");
        List<Object> quizList = new ArrayList<>();

        for (String classcode : classCodeList) {
            quizRef.child(classcode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot quizSnapshot : snapshot.getChildren()) {
                            String title = quizSnapshot.child("title").getValue(String.class);
                            quizList.add(title); // Adding the quiz title to the list
                        }
                    }
                    notesAdapter.updateData(quizList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Parent.this, "Error fetching quiz data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void addAssignment() {
        if (classCodeList.isEmpty()) {
            Toast.makeText(this, "No class found", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference assignRef = FirebaseDatabase.getInstance().getReference("AssignmentModel");
        List<Object> assignmentList = new ArrayList<>();

        for (String classcode : classCodeList) {
            assignRef.child(classcode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            for (DataSnapshot assignmentSnapshot : userSnapshot.getChildren()) {
                                String title = assignmentSnapshot.child("assignTitle").getValue(String.class);
                                if (title != null) {
                                    assignmentList.add(new AssignmentModel(title));
                                }
                            }
                        }
                    }
                    notesAdapter.updateData(assignmentList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Parent.this, "Error fetching assignment data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void addAttendance() {
        if (classCodeList.isEmpty()) {
            Toast.makeText(this, "No class found", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference attenRef = FirebaseDatabase.getInstance().getReference("Attendance");
        List<Object> attendanceList = new ArrayList<>();

        for (String classcode : classCodeList) {
            attenRef.child(classcode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot attendanceSnapshot : snapshot.getChildren()) {
                            String title = attendanceSnapshot.child("attentitle").getValue(String.class);
                            String date = attendanceSnapshot.child("date").getValue(String.class);
                            boolean open = attendanceSnapshot.child("open").getValue(Boolean.class);
                            long timeLimit = attendanceSnapshot.child("timeLimit").getValue(Long.class);
                            long startTime = attendanceSnapshot.child("startTime").getValue(Long.class);

                            attendanceList.add(new AttendanceModel(title, date, timeLimit, startTime, open));
                        }
                    }
                    notesAdapter.updateData(attendanceList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Parent.this, "Error fetching attendance data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchClasscode(FetchClassCodesCallback callback){
        classCodeList.clear();
        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("Classes").child(stdClass);
        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot classcodeSnapshot: subjectSnapshot.getChildren()) {
                        String classcode = classcodeSnapshot.getKey(); // e.g., gTe5GhWQ
                        DataSnapshot studentsSnapshot = classcodeSnapshot.child("Students");

                        for (DataSnapshot studentSnapshot : studentsSnapshot.getChildren()) {
                            String name = studentSnapshot.child("name").getValue(String.class);
                            String email = studentSnapshot.child("email").getValue(String.class);

                            if (stdName.equals(name) && stdEmail.equals(email)) {
                                classCodeList.add(classcode);
                                break;// Add classcode to the array
                            }
                        }
                    }
                }
                callback.onClassCodesFetched();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    interface FetchClassCodesCallback {
        void onClassCodesFetched();
    }
}