package com.example.learnhub.classroomUi.classwork;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
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
import com.example.learnhub.model.AssignmentResultModel;
import com.example.learnhub.model.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowAssignmentResult extends AppCompatActivity {
   RecyclerView assignResultRecyclerView;
   String username , submissionStatus,title,classcode;
   List<Object> assignResultList;
   RecyclerViewAdapterQuizResult assignAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_assignment_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        classcode = intent.getStringExtra("classcode");
        assignResultList = new ArrayList<>();
        fetchAssignResult();
        assignResultRecyclerView= findViewById(R.id.assignResultRecyclerview);
        assignResultRecyclerView.setHasFixedSize(true);
        assignResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        assignAdapter = new RecyclerViewAdapterQuizResult(getApplicationContext(),assignResultList);
        assignResultRecyclerView.setAdapter(assignAdapter);

    }
    private void fetchAssignResult(){
        UserSession userSession = new UserSession(getApplicationContext());
        String usertype = userSession.getUserType();
        String stdname=  userSession.getStdName();
        Log.d("Parent","usertype"+usertype);
        if (usertype.equals("Parent")){
            assignResultList.clear();
            DatabaseReference assignref = FirebaseDatabase.getInstance().getReference("StudentAssignment")
                    .child("classroom");
            assignref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot classcodeSnapshot : snapshot.getChildren()) {

                        for (DataSnapshot titleSnapshot : classcodeSnapshot.getChildren()) {
                            String dbtitle = titleSnapshot.getKey();
                            if (dbtitle.equals(title)) {
                                for (DataSnapshot uidSnapshot : titleSnapshot.getChildren()) {
                                    String uid = uidSnapshot.getKey();
                                    Log.d("UID", "Result uid" + uid);
                                    username = uidSnapshot.child("studentName").getValue(String.class);
                                    if (username.equals(stdname)) {
                                        submissionStatus = uidSnapshot.child("submissionStatus").getValue(String.class);
                                        assignResultList.add(new AssignmentResultModel(username, submissionStatus, uid));
                                        assignAdapter.updateData(assignResultList);
                                    }
                                }
                            }
                        }
                        }
                    }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            assignResultList.clear();
            DatabaseReference assignref = FirebaseDatabase.getInstance().getReference("StudentAssignment")
                    .child("classroom").child(classcode).child(title);
            assignref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot uidSnapshot : snapshot.getChildren()) {
                        String uid = uidSnapshot.getKey();
                        Log.d("UID", "Result uid" + uid);
                        username = uidSnapshot.child("studentName").getValue(String.class);
                        submissionStatus = uidSnapshot.child("submissionStatus").getValue(String.class);
                        assignResultList.add(new AssignmentResultModel(username, submissionStatus, uid));
                        assignAdapter.updateData(assignResultList);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}