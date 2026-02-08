package com.example.learnhub;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.learnhub.classroomUi.chatroom.chatroom;
import com.example.learnhub.classroomUi.classwork.classwork;
import com.example.learnhub.faculty.ui.home.HomeFragment;
import com.example.learnhub.model.UserSession;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class classroom extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar ;
    String classcode,classTitle;
    int classbg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_classroom);
        toolbar = findViewById(R.id.toolbar3);
        Intent intent = getIntent();
         classTitle = intent.getStringExtra("classname");
         classcode = intent.getStringExtra("classcode");
         classbg = intent.getIntExtra("classimg",0);
        /*toolbar.setTitle(classTitle);*/
        toolbar.getOverflowIcon().setTint(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Drawable upArrow = getResources().getDrawable(R.drawable.backbtn); // Default back icon for AppCompat
            upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(classTitle);
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (savedInstanceState == null) {
            loadFragment(new chatroom()); // Load default fragment (chatroom)
        }
        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(classroom.this,HomeFragment.class));
                finish();
            }
        });*/


        bottomNavigationView = findViewById(R.id.classroombottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                if (item.getItemId()==R.id.nav_chat) {
                    fragment = new chatroom();
                }else if (item.getItemId()==R.id.nav_classwork) {
                    Toast.makeText(classroom.this, "classwrok", Toast.LENGTH_SHORT).show();
                    fragment = new classwork();
                } else if (item.getItemId()==R.id.nav_people) {
                        Toast.makeText(classroom.this, "people", Toast.LENGTH_SHORT).show();
                        fragment = new people();
                }
                if (fragment != null) {
                    loadFragment(fragment);
                }

                return true;
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


    private  void  loadFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction  =getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("classcode",classcode);
        args.putString("classname",classTitle);
        args.putInt("classimg",classbg);
        UserSession userSession=new UserSession(getApplicationContext());
        userSession.saveClassCode(classcode);
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }


}