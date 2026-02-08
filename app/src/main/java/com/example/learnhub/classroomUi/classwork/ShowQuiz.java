package com.example.learnhub.classroomUi.classwork;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhub.R;
import com.example.learnhub.adapter.RecyclerViewAdapterQuiz;
import com.example.learnhub.model.NotificationModel;
import com.example.learnhub.model.QuizModel;
import com.example.learnhub.model.UserSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowQuiz extends AppCompatActivity {
     TextView quizTitle ,quizscore ;
     RecyclerView quizRecyclerview;
     Button submitQuiz;
     RecyclerViewAdapterQuiz quizAdpter;
     List<QuizModel> quizList;
     List<String> answerList;
     String classcode , Title;
     int totalScore = 0 , totalQuestion= 0 ;
     float userScore = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
     quizTitle = findViewById(R.id.quizTitle);
     quizscore =findViewById(R.id.totalscore);
     quizRecyclerview = findViewById(R.id.quizRecyclerview);
     submitQuiz=findViewById(R.id.submitQuiz);
     quizList = new ArrayList<>();
     answerList = new ArrayList<>();
     Intent intent = getIntent();
     classcode = intent.getStringExtra("classcode").trim();
     Title = intent.getStringExtra("title").trim();
     quizTitle.setText(Title);

        fetchQuizData(() -> {
            // Once data is loaded, check submission status
            checkQuizSubmissionStatus();
        });

        quizRecyclerview.setHasFixedSize(true);
     quizRecyclerview.setLayoutManager(new LinearLayoutManager(this));
     quizAdpter = new RecyclerViewAdapterQuiz(getApplication(),quizList);
     quizRecyclerview.setAdapter(quizAdpter);
     checkQuizSubmissionStatus();
     submitQuiz.setOnClickListener(v -> {
         calculateScore();
         SubmitQuiz();
         submitQuiz.setEnabled(false);
         submitQuiz.setText("Quiz Submitted");
         Toast.makeText(ShowQuiz.this, "Your quiz is submitted", Toast.LENGTH_SHORT).show();
     });
    }




    private void fetchQuizData(Runnable callback) {
        DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference("Quiz")
                .child(classcode)
                ; // Querying based on the classCode (e.g., "classcode123")
     Query qeury = quizRef.orderByChild("title").equalTo(Title);
        qeury.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    // Loop through all quiz IDs for this classcode
                    for (DataSnapshot quizSnapshot : snapshot.getChildren()) {
                        String quizTitle = quizSnapshot.child("title").getValue(String.class);
                        String Score = quizSnapshot.child("score").getValue(String.class);
                        totalScore = Integer.parseInt(Score);
                        quizscore.setText(Score);
                        // Loop through each question under this quiz
                        for (DataSnapshot titleSnapshot : quizSnapshot.child(quizTitle).getChildren()) {
                            String questionId = titleSnapshot.getKey();  // Unique question ID
                            String quizQuestion = titleSnapshot.child("quizQuestion").getValue(String.class);
                            String correctAnswer = titleSnapshot.child("correctAnswer").getValue(String.class);
                            List<String> options = new ArrayList<>();

                            // Fetch the options
                            for (DataSnapshot optionSnapshot : titleSnapshot.child("options").getChildren()) {
                                String option = optionSnapshot.getValue(String.class);
                                if (option != null) {
                                    options.add(option);
                                }
                            }

                            // Create and add the quiz model
                            QuizModel quiz = new QuizModel(quizQuestion, options, correctAnswer);
                            quizList.add(quiz);
                            totalQuestion++;
                            if (callback != null) callback.run();
                           else {
                            Log.e("ShowQuiz", "No quizzes found for classCode: " + classcode);
                            }
                        }
                    }

                    // Log the number of quizzes loaded
                    Log.d("ShowQuiz", "Loaded " + quizList.size() + " quizzes.");
                    quizAdpter.updateData(quizList);

                } else {
                    Log.e("ShowQuiz", "No quizzes found for classCode: " + classcode);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ShowQuiz", "Error fetching quiz data: " + error.getMessage());
                Toast.makeText(ShowQuiz.this, "Error fetching quiz data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateScore() {
        userScore = 0;
        float ScorePerQuestion = (float)totalScore / totalQuestion;
        Log.d("ShowQuiz","scoreperques" + ScorePerQuestion);

        for (QuizModel quiz : quizList) {
            String userAnswer = quiz.getUserAnswer();
            String correctAnswer = quiz.getCorrectAnswer();
            answerList.add(userAnswer);
            // Log answers for debugging
            Log.d("ShowQuiz", "User Answer: " + userAnswer);
            Log.d("ShowQuiz", "Correct Answer: " + correctAnswer);

            if (userAnswer != null && !userAnswer.isEmpty() && userAnswer.equalsIgnoreCase(correctAnswer)) {
                userScore += ScorePerQuestion;
            }
        }
        Log.d("ShowQuiz", "Total User Score: " + userScore);
        Toast.makeText(this, "Your Total Score: " + userScore, Toast.LENGTH_SHORT).show();
    }

    private void SubmitQuiz() {
        UserSession userSession = new UserSession(getApplicationContext());
        String username = userSession.getUserName();
        DatabaseReference scoreRef = FirebaseDatabase.getInstance().getReference("QuizUser")
                .child("classroom")
                .child(classcode)
                .child(Title)
                .child(username)
                .child("Score");

        Map<String, Object> submissionData = new HashMap<>();
        submissionData.put("score", userScore);
        submissionData.put("answers", answerList);
        scoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Save the score if it hasn't been submitted yet
                    scoreRef.setValue(submissionData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                            NotificationModel.NotificationUtils.sendNotification(getApplicationContext(),Title,"Submiited the Quiz",username,classcode);
                            Toast.makeText(ShowQuiz.this, "Your quiz is submitted!", Toast.LENGTH_SHORT).show();
                            // Disable quiz options (if required)
                            disableQuizOptions();
                        } else {
                            Toast.makeText(ShowQuiz.this, "Error saving score", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // If score already exists, show message that the quiz has already been submitted
                    Toast.makeText(ShowQuiz.this, "You have already submitted the quiz.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ShowQuiz", "Error submitting quiz: " + error.getMessage());
                Toast.makeText(ShowQuiz.this, "Error submitting quiz", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void disableQuizOptions() {
        // Disable individual RadioButtons for all quiz questions
        for (int i = 0; i < quizRecyclerview.getChildCount(); i++) {
            RecyclerView.ViewHolder holder = quizRecyclerview.findViewHolderForAdapterPosition(i);

            if (holder != null && holder instanceof RecyclerViewAdapterQuiz.ViewHolder) {
                RecyclerViewAdapterQuiz.ViewHolder quizViewHolder = (RecyclerViewAdapterQuiz.ViewHolder) holder;
                String userAnswer = quizList.get(i).getUserAnswer();
                for (QuizModel quiz : quizList) {
                    Log.d("QuizAnswerCheck", "Question: " + quiz.getQuizQuestion() + ", User Answer: " + quiz.getUserAnswer());
                }

                // Check the correct RadioButton based on the user's answer
                if (userAnswer != null) {
                    if (userAnswer.equals(quizViewHolder.opt1.getText().toString())) {
                        quizViewHolder.opt1.setChecked(true);
                    } else if (userAnswer.equals(quizViewHolder.opt2.getText().toString())) {
                        quizViewHolder.opt2.setChecked(true);
                    } else if (userAnswer.equals(quizViewHolder.opt3.getText().toString())) {
                        quizViewHolder.opt3.setChecked(true);
                    } else if (userAnswer.equals(quizViewHolder.opt4.getText().toString())) {
                        quizViewHolder.opt4.setChecked(true);
                    }
                }

                // Disable each individual RadioButton
                quizViewHolder.opt1.setEnabled(false);
                quizViewHolder.opt2.setEnabled(false);
                quizViewHolder.opt3.setEnabled(false);
                quizViewHolder.opt4.setEnabled(false);
            }
        }
    }

    private void checkQuizSubmissionStatus() {
        UserSession userSession = new UserSession(getApplicationContext());
        String username = userSession.getUserName();
        DatabaseReference scoreRef = FirebaseDatabase.getInstance().getReference("QuizUser")
                .child("classroom")
                .child(classcode)
                .child(Title)
                .child(username)
                .child("Score");

        // Check if the score already exists
        scoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // If the score exists, it means the quiz is already submitted, disable quiz.
                    Toast.makeText(ShowQuiz.this, "You have already submitted this quiz.", Toast.LENGTH_SHORT).show();
                    submitQuiz.setEnabled(false);
                    submitQuiz.setText("Quiz Submitted");
                    disableQuizOptions();
                    List<String> savedAnswers = (List<String>) snapshot.child("answers").getValue();
                    for (int i = 0; i < quizList.size(); i++) {
                        quizList.get(i).setUserAnswer(savedAnswers.get(i)); // assuming answers are stored in order
                    }

                    quizAdpter.updateData(quizList);
                    // Disable all options (make the quiz read-only)

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ShowQuiz", "Error checking submission status: " + error.getMessage());
            }
        });
    }

}






