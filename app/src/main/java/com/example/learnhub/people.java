package com.example.learnhub;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnhub.adapter.RecyclerViewAdapterpeople;
import com.example.learnhub.model.UserClass;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class people extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    Button  invitebtn;
    TextView username ;
    DatabaseReference db;
    String classcode,classTitle;
    CircleImageView prof;
    RecyclerView stdrecyclerview, facrecyclerview;
    LinearLayout invitelinearlayout;
    RecyclerViewAdapterpeople peopleadapter,facAdapter;
    List<UserClass> joinList ,facList;


    public people() {
        // Required empty public constructor
    }


    public static people newInstance(String param1, String param2) {
        people fragment = new people();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people, container, false);



        invitebtn =  view.findViewById(R.id.invitebtn);

        Intent intent =  getActivity().getIntent();
        classcode = intent.getStringExtra("classcode");
        classTitle = intent.getStringExtra("classname");

        invitebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendcode(classcode);
            }
        });
        stdrecyclerview = view.findViewById(R.id.stdrecyclerview);
        facrecyclerview = view.findViewById(R.id.facRecyclerView);
        invitelinearlayout = view.findViewById(R.id.invitelayout);
        joinList = new ArrayList<>();
        facList = new ArrayList<>();

        facrecyclerview.setHasFixedSize(true);
        facrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        facAdapter = new RecyclerViewAdapterpeople(getContext(),facList);
        facrecyclerview.setAdapter(facAdapter);


        stdrecyclerview.setHasFixedSize(true);
        stdrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        addStdinList();
        peopleadapter = new RecyclerViewAdapterpeople(getContext(),joinList);
        stdrecyclerview.setAdapter(peopleadapter);
    return view;
    }

    private void CheckStudent() {

        if (!joinList.isEmpty()){
            invitelinearlayout.setVisibility(View.GONE);
            stdrecyclerview.setVisibility(View.VISIBLE);

        }else {
            invitelinearlayout.setVisibility(View.VISIBLE);
            stdrecyclerview.setVisibility(View.GONE);
        }
    }

    private void addStdinList() {
        DatabaseReference stdjoinref = FirebaseDatabase.getInstance().getReference("Classes");
        stdjoinref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot classSnapshot : snapshot.getChildren()) { // Iterate through classes
                    for (DataSnapshot subjectSnapshot : classSnapshot.getChildren()) { // Iterate through subjects
                        if (subjectSnapshot.hasChild(classcode)) {
                            DatabaseReference keyRef = subjectSnapshot.child(classcode).getRef();

                            // Fetch Faculty
                            keyRef.child("Faculty").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot facultySnapshot) {
                                    if (facultySnapshot.exists()) {
                                        for (DataSnapshot student : facultySnapshot.getChildren()) {
                                            String studentname = student.child("name").getValue(String.class);
                                            String email = student.child("email").getValue(String.class);
                                            String usertype = student.child("usertype").getValue(String.class);
                                            facList.add(new UserClass(studentname,email,usertype));
                                        }
                                    }
                                    facAdapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    System.out.println("Failed to fetch faculty: " + error.getMessage());
                                }
                            });

                            // Fetch Students
                            keyRef.child("Students").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot studentSnapshot) {
                                    if (studentSnapshot.exists()) {
                                        for (DataSnapshot student : studentSnapshot.getChildren()) {
                                            String studentname = student.child("name").getValue(String.class);
                                            String email = student.child("email").getValue(String.class);
                                            String usertype = student.child("usertype").getValue(String.class);
                                            joinList.add(new UserClass(studentname,email,usertype));
                                        }
                                    }
                                    peopleadapter.notifyDataSetChanged();
                                    CheckStudent();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    System.out.println("Failed to fetch students: " + error.getMessage());
                                }
                            });

                            return; // Exit once the unique key is found
                        }
                    }

                }
                System.out.println("Unique key not found in any class/subject.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Failed to search database: " + error.getMessage());
            }
        });




    }

    private void sendcode(String classcode) {
        String shareClassCode = classcode;
        String shareMessage = "Join my class "+classTitle+" on Learnhub with the code: " + shareClassCode;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        // Check if WhatsApp is installed
        try {
            PackageManager pm = getActivity().getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES); // Throws exception if not installed
            // WhatsApp is installed, set package
            shareIntent.setPackage("com.whatsapp");
        } catch (PackageManager.NameNotFoundException e) {
            // WhatsApp is not installed, do nothing and show all apps in chooser
        }

        // Start chooser dialog, either with WhatsApp or with all sharing apps
        startActivity(Intent.createChooser(shareIntent, "Share class code via"));
    }


        private void setProfile (String email){
            DatabaseReference facultyref = FirebaseDatabase.getInstance().getReference("Faculty");
                facultyref.child(email.replace(".", ",")).child("imageUrl").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String imageUrl = task.getResult().getValue(String.class);
                        if (imageUrl != null) {
                            loadImageFromUrl(imageUrl);
                        }
                    }
                });

        }
        private void loadImageFromUrl (String imageUrl){

            Glide.with(this)
                    .load(imageUrl).fitCenter()
                    .placeholder(R.drawable.profileimg) // Add a placeholder image if needed
                    .into(prof);
        }
    }
