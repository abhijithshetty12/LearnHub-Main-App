package com.example.learnhub.student;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.learnhub.Join;
import com.example.learnhub.R;
import com.example.learnhub.faculty.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class JoinClass extends AppCompatActivity {
    EditText classname ,classcode;
    Button joinbtn;
    Toolbar toolbar;
    DatabaseReference databaseReference,databaseReferencejoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_class);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Drawable upArrow = getResources().getDrawable(R.drawable.backbtn); // Default back icon for AppCompat
            upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Objects.requireNonNull(getSupportActionBar()).setTitle("Join class");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent =getIntent();
        String username = intent.getStringExtra("username");
        String email = intent.getStringExtra("email");
        databaseReference= FirebaseDatabase.getInstance().getReference("Class");
        databaseReferencejoin= FirebaseDatabase.getInstance().getReference("Join");
        classname=findViewById(R.id.classnamestd);
        classcode=findViewById(R.id.classcode);
        joinbtn=findViewById(R.id.joinbtn);

        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinClass.this, HomeFragment.class);
                startActivity(intent);
            }
        });*/
        joinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cname = classname.getText().toString();
                String ccode = classcode.getText().toString();
                joinclass(username,email,cname,ccode);
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
    private void joinclass(String username ,String email,String classname,String classcode){

        Query classcodequery =databaseReference.orderByChild("classCode").equalTo(classcode);
        classcodequery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String classnamesnap  = dataSnapshot.child("classname").getValue(String.class);
                        if (classnamesnap.equals(classname)){
                            String joinid = databaseReferencejoin.push().getKey();
                            Join join = new Join(username,email,classname,classcode);
                            databaseReferencejoin.child(joinid).setValue(join).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(JoinClass.this, "Class joined", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(JoinClass.this,HomeFragment.class));
                                    finish();
                                }

                            });

                        }
                    }
                }else {
                    Toast.makeText(JoinClass.this, "classcode does not exist", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(JoinClass.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }


        });

    }
}