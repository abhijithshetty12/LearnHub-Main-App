package com.example.learnhub.classroomUi.classwork;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhub.R;
import com.example.learnhub.adapter.RecyclerViewAdapterQuizResult;
import com.example.learnhub.model.AttendanceModel;
import com.example.learnhub.model.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowAttendanceResult extends AppCompatActivity {
    RecyclerView attendResultRecyclerview;
    String username , isPresent,title,classcode;
    List<Object> attendResultList;
    RecyclerViewAdapterQuizResult attendAdapter;
    TextView attendTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_attendance_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        classcode = intent.getStringExtra("classcode");
        attendResultList = new ArrayList<>();
        attendTitle=findViewById(R.id.attendStdTitle);
        attendTitle.setText(title);
        fetchAttendResult();
        attendResultRecyclerview= findViewById(R.id.attendResultRecyclerview);
        attendResultRecyclerview.setHasFixedSize(true);
        attendResultRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        attendAdapter = new RecyclerViewAdapterQuizResult(getApplicationContext(),attendResultList);
        attendResultRecyclerview.setAdapter(attendAdapter);
    }

    private void fetchAttendResult() {
        UserSession userSession  =new UserSession(getApplicationContext());
        String usertype  =userSession.getUserType();
        String stdname = userSession.getStdName();
        if (usertype.equals("Parent")){
            DatabaseReference attendRef = FirebaseDatabase.getInstance().getReference("StudentAttendance");
            attendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot classcode : snapshot.getChildren()) {
                        for (DataSnapshot titleSnaphot : classcode.getChildren()) {
                            String dbTitle = titleSnaphot.getKey();
                            if (dbTitle.equals(title)) {
                                for (DataSnapshot uidSnapshot : titleSnaphot.getChildren()) {
                                    String username = uidSnapshot.child("stdName").getValue(String.class);
                                    if (username.equals(stdname)) {
                                        boolean isPresent = uidSnapshot.child("present").getValue(Boolean.class);
                                        attendResultList.add(new AttendanceModel.StudentAttendance(username, isPresent));
                                    }
                                }
                                attendAdapter.updateData(attendResultList);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
        DatabaseReference attendRef = FirebaseDatabase.getInstance().getReference("StudentAttendance")
                .child(classcode).child(title);
        attendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot uidSnapshot:snapshot.getChildren() ){
                    String username = uidSnapshot.child("stdName").getValue(String.class);
                    boolean isPresent = uidSnapshot.child("present").getValue(Boolean.class);
                    attendResultList.add(new AttendanceModel.StudentAttendance(username,isPresent));
                }
                attendAdapter.updateData(attendResultList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

     }
    }
}