package com.example.learnhub.classroomUi.classwork;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhub.R;
import com.example.learnhub.adapter.RecyclerViewAdapterQuizResult;
import com.example.learnhub.model.QuizResultModel;
import com.example.learnhub.model.UserSession;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowQuizResult extends AppCompatActivity {
   RecyclerView quizResultRecyclerView;
   List<Object> quizResultList;
   RecyclerViewAdapterQuizResult quizResultAdapter;
   String classcode,title,score,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_quiz_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        classcode = intent.getStringExtra("classcode");
        title = intent.getStringExtra("title");
        quizResultList = new ArrayList<>();
        quizResultRecyclerView = findViewById(R.id.quizResultRecyclerview);
        quizResultRecyclerView.setHasFixedSize(true);
        quizResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchQuizResult();
        quizResultAdapter = new RecyclerViewAdapterQuizResult(getApplicationContext(),quizResultList);
        quizResultRecyclerView.setAdapter(quizResultAdapter);

    }

    private void fetchQuizResult() {
        UserSession userSession  =new UserSession(getApplicationContext());
        String usertype  =userSession.getUserType();
        String stdname  =userSession.getStdName();
        if (usertype.equals("Parent")){
            DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference("QuizUser")
                    .child("classroom");
            quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    quizResultList.clear(); // Clear existing results
                    for (DataSnapshot classcode :snapshot.getChildren()) {
                        for (DataSnapshot titleSnaphot : classcode.getChildren()) {
                            String dbTitle = titleSnaphot.getKey();
                            if (dbTitle.equals(title)) {
                                for (DataSnapshot usernameSnapshot : titleSnaphot.getChildren()) {
                                    String username = usernameSnapshot.getKey(); // Get username
                                    Log.d("fetchQuizResult", "Username: " + username);
                                    if (username.equals(stdname)) {
                                        // Access the "Score" node under each username
                                        DataSnapshot scoreSnapshot = usernameSnapshot.child("Score");
                                        if (scoreSnapshot.exists()) {
                                            // Retrieve score value from "Score" node
                                            float score = scoreSnapshot.child("score").getValue(Float.class);
                                            Log.d("fetchQuizResult", "Score for " + username + ": " + score);

                                            // Add the username and score to the result list
                                            quizResultList.add(new QuizResultModel(username, String.valueOf(score)));
                                        } else {
                                            Log.d("fetchQuizResult", "Score not found for username: " + username);
                                        }
                                    }
                                }
                            }
                        }
                    }


                    // Update the adapter with the new data
                    quizResultAdapter.updateData(quizResultList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("fetchQuizResult", "Error retrieving data: " + error.getMessage());
                }
            });
        }else {
            DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference("QuizUser")
                    .child("classroom")
                    .child(classcode)
                    .child(title);

            quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    quizResultList.clear(); // Clear existing results

                    // Loop through each userID under the class

                    // Loop through each username under the userID
                    for (DataSnapshot usernameSnapshot : snapshot.getChildren()) {
                        String username = usernameSnapshot.getKey(); // Get username
                        Log.d("fetchQuizResult", "Username: " + username);

                        // Access the "Score" node under each username
                        DataSnapshot scoreSnapshot = usernameSnapshot.child("Score");
                        if (scoreSnapshot.exists()) {
                            // Retrieve score value from "Score" node
                            float score = scoreSnapshot.child("score").getValue(Float.class);
                            Log.d("fetchQuizResult", "Score for " + username + ": " + score);

                            // Add the username and score to the result list
                            quizResultList.add(new QuizResultModel(username, String.valueOf(score)));
                        } else {
                            Log.d("fetchQuizResult", "Score not found for username: " + username);
                        }
                    }


                    // Update the adapter with the new data
                    quizResultAdapter.updateData(quizResultList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("fetchQuizResult", "Error retrieving data: " + error.getMessage());
                }
            });
        }
    }
}



