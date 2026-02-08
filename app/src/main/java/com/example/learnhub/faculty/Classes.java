package com.example.learnhub.faculty;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.learnhub.Class;
import com.example.learnhub.R;
import com.example.learnhub.faculty.ui.home.HomeFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.SecureRandom;
import java.util.Objects;

public class Classes extends AppCompatActivity {
    TextView classcode,resettext;
    Button createbtn;
    EditText cname,cdescription,csection,cRoom,cSubject;
    Toolbar toolbar;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_classes);
        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Drawable upArrow = getResources().getDrawable(R.drawable.backbtn); // Default back icon for AppCompat
            upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Objects.requireNonNull(getSupportActionBar()).setTitle("Class settings"); // Add this line if you want to set a title


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseReference= FirebaseDatabase.getInstance().getReference();

        classcode = findViewById(R.id.classcode);
        classcode.setText(generatecode());
        cname=findViewById(R.id.cname);
        cdescription=findViewById(R.id.cdescipton);
        csection=findViewById(R.id.csection);
        cRoom=findViewById(R.id.cRoom);
        cSubject=findViewById(R.id.cSubject);
        resettext=findViewById(R.id.reset);
        resettext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classcode.setText(generatecode());
            }
        });
        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Classes.this, Facultyhome.class);
                startActivity(intent);
            }
        });*/
        createbtn=findViewById(R.id.createbtn);
        createbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String Classname= cname.getText().toString();
                String ClassDescription= cdescription.getText().toString();
                String Section= csection.getText().toString();
                String Room= cRoom.getText().toString();
                String Subject= cSubject.getText().toString();
                String Classcode = classcode.getText().toString();
                Intent intent = getIntent();
                String username = intent.getStringExtra("username");
                String email = intent.getStringExtra("email");
                if (!Classname.isEmpty() ){
                    Createclass(Classname, ClassDescription, Section, Room, Subject, Classcode,username,email);

                }else  {
                    Toast.makeText(Classes.this, "Pls filled the section!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private String generatecode(){
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(10);
        for(int i=0;i<8;i++){
            int randomindex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomindex));
        }
        return  code.toString();
   }
   private void Createclass(String Classname,String ClassDescription,String Section,String Room,String Subject,String Classcode,String username,String email){
        String classid = databaseReference.push().getKey();
       Class classes = new Class(Classname,ClassDescription,Section,Room,Subject,Classcode,username,email);
       assert classid != null;
       databaseReference.child("Class").child(classid).setValue(classes).addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               Toast.makeText(Classes.this, "Class Created!!", Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(Classes.this, HomeFragment.class);
               /*intent.putExtra("username",username);
               intent.putExtra("email",email);*/
               startActivity(intent);


           }else {
               Toast.makeText(Classes.this, "Class not created", Toast.LENGTH_SHORT).show();
           }});

   }
}