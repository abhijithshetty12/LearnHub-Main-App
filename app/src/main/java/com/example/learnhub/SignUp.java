package com.example.learnhub;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.learnhub.faculty.Faculty;
import com.example.learnhub.faculty.Facultyhome;
import com.example.learnhub.model.UserSession;
import com.example.learnhub.student.Students;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    Spinner spinnerprofession ;
    EditText editTextusername;
    EditText editTextpassword,editemail;
    Button signupbtn;
    DatabaseReference databaseReference;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseReference = FirebaseDatabase.getInstance().getReference();
        spinnerprofession = findViewById(R.id.spinner);
        editemail= findViewById(R.id.email);
        editTextusername = findViewById(R.id.uname);
        editTextpassword = findViewById(R.id.password);
        signupbtn=findViewById(R.id.btnSign);
        progressBar = findViewById(R.id.progressbarSignup);
        changeinProgress(false);

        signupbtn.setOnClickListener(v -> {
            String profession = spinnerprofession.getSelectedItem().toString();
            String username = editTextusername.getText().toString();
            String password = editTextpassword.getText().toString();
            String email = editemail.getText().toString().trim();

                changeinProgress(true);
                if (!profession.isEmpty() && !email.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                    if(isValidEmail(email)) {

                    String userId = databaseReference.push().getKey();
                    if (profession.equals("Student")) {
                        Students obj = new Students(username, email);
                        CreateAccount("Students",userId,obj,email,password);

                    } else {
                        Faculty obj = new Faculty(username, email);
                        CreateAccount("Faculty",userId,obj,email,password);

                    }
                        UserSession userSession = new UserSession(getApplicationContext());
                        userSession.saveUserType(profession);
                        Intent intent = new Intent(SignUp.this, Facultyhome.class);

                        intent.putExtra("username",username);
                        intent.putExtra("email",email);
                        startActivity(intent);
                        finish();
                } else {
                        changeinProgress(false);
                    Toast.makeText(SignUp.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
            }else {
                    changeinProgress(false);
                Toast.makeText(SignUp.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void changeinProgress(boolean inProgress) {
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            signupbtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            signupbtn.setVisibility(View.VISIBLE);
        }
    }

    private void CreateAccount(String usertype ,String userId,Object obj,String email,String password ){
        assert userId != null;
        FirebaseAuth firebaseAuth  = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).
                addOnCompleteListener(new
                        OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          Toast.makeText(SignUp.this, "Sign in sucessfully", Toast.LENGTH_SHORT).show();
                          firebaseAuth.getCurrentUser().sendEmailVerification();
                          firebaseAuth.signOut();
                          finish();
                      }

                  });

                databaseReference.child(usertype).child(userId).setValue(obj).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUp.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUp.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    }
