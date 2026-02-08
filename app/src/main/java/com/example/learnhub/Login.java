package com.example.learnhub;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.learnhub.faculty.Facultyhome;
import com.example.learnhub.model.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    Spinner spinnerprofession ;
    EditText editTextusername;
    EditText editTextpassword,editemail;
    Button loginbtn ,signupbtn;
    DatabaseReference databaseReference;
    public static final String SHARED_PREFS = "shared_prefs";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        spinnerprofession = findViewById(R.id.spinner);
        editTextusername = findViewById(R.id.uname);
        editTextpassword = findViewById(R.id.password);
        editemail=findViewById(R.id.email);
        loginbtn = findViewById(R.id.signupbtn);
        signupbtn=findViewById(R.id.siginbtn);
        progressBar = findViewById(R.id.progressBarlogin);
        checkBox();
        changeinProgress(false);

        loginbtn.setOnClickListener(v -> {
            String profession = spinnerprofession.getSelectedItem().toString();
            String username = editTextusername.getText().toString();
            String password = editTextpassword.getText().toString();
            String email = editemail.getText().toString().trim();

            if (!profession.isEmpty() && !email.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                if(isValidEmail(email)){
                if (profession.equals("Student")) {
                    CheckLogin("Students", username,email, password);

                } else if(profession.equals("Faculty")) {
                    CheckLogin("Faculty",username, email, password);
                }else {
                    CheckParentLogin("Parent",username, email, password);
                }


            }
            else {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            }}
        else {
                Toast.makeText(Login.this, "PLease fill all the option", Toast.LENGTH_SHORT).show();
            }});

       signupbtn.setOnClickListener(v -> {
           Toast.makeText(this, "Sign up", Toast.LENGTH_SHORT).show();
           startActivity(new Intent(Login.this, SignUp.class));
           finish();
       });




            }



    private void checkBox() {
        SharedPreferences sharedPreferences =  getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String  check = sharedPreferences.getString("name","");
        String  username = sharedPreferences.getString("username","");
        String  email = sharedPreferences.getString("email","");
        String  usertype = sharedPreferences.getString("usertype","");
        String  stdname = sharedPreferences.getString("stdName","");
        String  stdclass = sharedPreferences.getString("stdClass","");
        if (check.equals("true")&& !usertype.equals("Parent")){
            Intent intent = new Intent(Login.this, Facultyhome.class);
            intent.putExtra("username",username);
            intent.putExtra("email",email);
            intent.putExtra("usertype",usertype);
            startActivity(intent);
            finish();
        }else if (check.equals("true") && usertype.equals("Parent")){
            Intent intent = new Intent(Login.this, Parent.class);
            intent.putExtra("username",username);
            intent.putExtra("email",email);
            intent.putExtra("usertype",usertype);
            intent.putExtra("stdName",stdname);
            intent.putExtra("stdClass",stdclass);
            startActivity(intent);
            finish();
        }
    }

    private void CheckLogin(String usertype ,String username,String email,String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeinProgress(true);
/*
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeinProgress(false);
                if (task.isSuccessful()) {
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                        user.updateProfile(profileChangeRequest);
                        SharedPreferences sharedPreferences =  getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                        SharedPreferences.Editor editor  = sharedPreferences.edit();
                        editor.putString("name","true");
                        editor.putString("username",username);
                        editor.putString("email",email);
                        editor.putString("usertype",usertype);
                        editor.apply();
                        UserSession userSession = new UserSession(getApplicationContext());
                        userSession.saveUserType(usertype);
                        Intent intent = new Intent(Login.this, Facultyhome.class);
                        intent.putExtra("username",username);
                        intent.putExtra("email",email);
                        intent.putExtra("usertype",usertype);
                        startActivity(intent);
                        finish();


                    } else {
                        Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Login.this, "Authentication failed. Check your credentials.", Toast.LENGTH_SHORT).show();

                    }
            }
        });
*/
       Query emailquery = databaseReference.child(usertype).orderByChild("name").equalTo(username);
        emailquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot passsnapshot : snapshot.getChildren()){
                        String storedpass = passsnapshot.child("password").getValue(String.class);
                        if(storedpass!=null && storedpass.equals(password)){
                            String userid = databaseReference.getKey();
                            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                            changeinProgress(false);
                            // Proceed to next activity or whatever your login success action is
                            UserSession userSession = new UserSession(getApplicationContext());
                            userSession.saveUserName(username);
                            userSession.saveUserEmail(email);
                            userSession.saveUserType(usertype);
                            userSession.saveUserPassword(password);
                            SharedPreferences sharedPreferences =  getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                            SharedPreferences.Editor editor  = sharedPreferences.edit();
                            editor.putString("name","true");
                            editor.putString("username",username);
                            editor.putString("email",email);
                            editor.putString("usertype",usertype);
                            editor.apply();
                            Intent intent = new Intent(Login.this, Facultyhome.class);
                            intent.putExtra("username",username);
                            intent.putExtra("email",email);
                            intent.putExtra("usertype",usertype);

                            startActivity(intent);
                            finish();

                            return;
                        } else {
                            Toast.makeText(Login.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            changeinProgress(false);
                        }
                    }
                } else {
                    Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                    changeinProgress(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Login.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void changeinProgress(boolean inProgress) {
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginbtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            loginbtn.setVisibility(View.VISIBLE);
        }
    }

    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void CheckParentLogin(String parent, String username, String email, String password) {
        DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference("Parent");
        Query emailquery = parentRef.orderByChild("parentName").equalTo(username);
        emailquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot passsnapshot : snapshot.getChildren()){
                        String storedpass = passsnapshot.child("parentPassword").getValue(String.class);
                        String stdname = passsnapshot.child("stdName").getValue(String.class);
                        String stdclass = passsnapshot.child("stdClass").getValue(String.class);
                        if(storedpass!=null && storedpass.equals(password)){
                            String userid = parentRef.getKey();
                            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                            changeinProgress(false);
                            // Proceed to next activity or whatever your login success action is
                            UserSession userSession = new UserSession(getApplicationContext());
                            userSession.saveUserName(username);
                            userSession.saveUserEmail(email);
                            userSession.saveUserType(parent);
                            userSession.saveUserPassword(password);
                            userSession.saveStdName(stdname);
                            SharedPreferences sharedPreferences =  getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                            SharedPreferences.Editor editor  = sharedPreferences.edit();
                            editor.putString("name","true");
                            editor.putString("username",username);
                            editor.putString("email",email);
                            editor.putString("usertype",parent);
                            editor.putString("stdName",stdname);
                            editor.putString("stdClass",stdclass);
                            editor.apply();
                            Intent intent = new Intent(Login.this, Parent.class);
                            intent.putExtra("username",username);
                            intent.putExtra("email",email);
                            intent.putExtra("usertype",parent);
                            intent.putExtra("stdName",stdname);
                            intent.putExtra("stdClass",stdclass);
                            startActivity(intent);
                            finish();

                            return;
                        } else {
                            Toast.makeText(Login.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            changeinProgress(false);
                        }
                    }
                } else {
                    Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                    changeinProgress(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Login.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

