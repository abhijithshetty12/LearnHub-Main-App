package com.example.learnhub.faculty.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhub.Class;
import com.example.learnhub.Join;
import com.example.learnhub.R;
import com.example.learnhub.adapter.RecyclerViewAdapter;
import com.example.learnhub.adapter.RecyclerViewAdapterStd;
import com.example.learnhub.databinding.FragmentHomeBinding;
import com.example.learnhub.faculty.Classes;
import com.example.learnhub.model.UserClass;
import com.example.learnhub.model.UserSession;
import com.example.learnhub.student.JoinClass;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    RecyclerView showclass,showclassstd;
    RecyclerViewAdapter recyclerViewAdapter;
    RecyclerViewAdapterStd recyclerViewAdapterStd;
    ArrayList<UserClass> classArrayList;

    ArrayList<Join> joinArrayList;
     DatabaseReference databaseReference,databaseReferencejoin;
    LinearLayout linearLayoutfac,linearLayoutstd;
    String uname,emailid,usertype;

    Query query;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Button button = root.findViewById(R.id.createclassbtn);
        Button joinclassbtn =root.findViewById(R.id.joinclassbtn);
        Intent intent = getActivity().getIntent();
        uname = intent.getStringExtra("username");
        emailid = intent.getStringExtra("email");
        usertype = intent.getStringExtra("usertype");

        linearLayoutfac =root.findViewById(R.id.Faclayout);
        linearLayoutstd  =root.findViewById(R.id.stdlayout);
        showclass=root.findViewById(R.id.showclass);
        showclassstd =root.findViewById(R.id.showclassstd);
        classArrayList = new ArrayList<>();

        showclass.setHasFixedSize(true);
        showclass.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(),classArrayList);
        showclass.setAdapter(recyclerViewAdapter);
        fetchClass();



        /*joinArrayList = new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("Class");
        databaseReferencejoin= FirebaseDatabase.getInstance().getReference("Join");
        query = "Faculty".equals(usertype) ?
                databaseReference.orderByChild("email").equalTo(emailid) :
                databaseReferencejoin.orderByChild("email").equalTo(emailid);*/
/*
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classArrayList.clear();
                joinArrayList.clear();
                if("Faculty".equals(usertype)){
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Class classes = dataSnapshot.getValue(Class.class);
                    classArrayList.add(classes);
                }
                    if (!classArrayList.isEmpty()){
                        linearLayoutstd.setVisibility(View.GONE);
                        linearLayoutfac.setVisibility(View.GONE);
                        showclass.setHasFixedSize(true);
                        */
/*FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity());
                        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
                        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);*//*

                        showclass.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), classArrayList);
                        showclass.setAdapter(recyclerViewAdapter);
                    }else {
                        showclass.setVisibility(View.GONE);
                        linearLayoutfac.setVisibility(View.VISIBLE);
                        linearLayoutstd.setVisibility(View.GONE);
                    }
                }else{
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        Join join = dataSnapshot.getValue(Join.class);
                        joinArrayList.add(join);
                    }
                    if (!joinArrayList.isEmpty()){
                            linearLayoutstd.setVisibility(View.GONE);
                            linearLayoutfac.setVisibility(View.GONE);
                        showclassstd.setHasFixedSize(true);
                        showclassstd.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerViewAdapterStd = new RecyclerViewAdapterStd(getActivity(), joinArrayList);
                        showclassstd.setAdapter(recyclerViewAdapterStd);
                    }else {
                        showclass.setVisibility(View.GONE);
                        linearLayoutstd.setVisibility(View.VISIBLE);
                        linearLayoutfac.setVisibility(View.GONE);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FirebaseError", "Error fetching data", error.toException());

            }
        });
*/

        // Set a click listener for the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define what happens when the button is clicked

               Intent intent = new Intent(getActivity(), Classes.class);
               intent.putExtra("username",uname);
               intent.putExtra("email",emailid);
               startActivity(intent);
                // You can also navigate to another fragment, start an activity, or perform other actions here
            }
        });
        joinclassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Class joined", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), JoinClass.class);
                intent.putExtra("username",uname);
                intent.putExtra("email",emailid);
                startActivity(intent);
            }
        });
        return root;
    }

    private void checkClass() {
        if (classArrayList.isEmpty()) {
            /*if ("Faculty".equals(usertype)) {
                linearLayoutfac.setVisibility(View.VISIBLE);
                linearLayoutstd.setVisibility(View.GONE);
            } else {
                linearLayoutstd.setVisibility(View.VISIBLE);
                linearLayoutfac.setVisibility(View.GONE);
            }
            showclass.setVisibility(View.GONE);
            showclassstd.setVisibility(View.GONE);*/
            Toast.makeText(getActivity(), "No class found", Toast.LENGTH_SHORT).show();
        } else {
            linearLayoutfac.setVisibility(View.GONE);
            linearLayoutstd.setVisibility(View.GONE);
            showclass.setVisibility(View.VISIBLE);
            showclassstd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchClass(){
        classArrayList.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Classes");
        UserSession userSession = new UserSession(getContext());
        String password  = userSession.getUserPassword();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userFound = false;

                for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                    String className = classSnapshot.getKey(); // Get Class Name (e.g., "Class 10")

                    for (DataSnapshot subjectSnapshot : classSnapshot.getChildren()) {
                        String subjectName = subjectSnapshot.getKey(); // Get Subject Name (e.g., "Marathi")

                        for (DataSnapshot classcodeSnapshot : subjectSnapshot.getChildren()) {
                            String classcode = classcodeSnapshot.getKey();

                            DataSnapshot Snapshot = classcodeSnapshot.child(usertype);
                            if (Snapshot.exists()) {
                                for (DataSnapshot userSnapshot : Snapshot.getChildren()) {
                                    // Fetch user details from the database
                                    String dbName = userSnapshot.child("name").getValue(String.class);
                                    String dbEmail = userSnapshot.child("email").getValue(String.class);
                                    String dbPassword = userSnapshot.child("password").getValue(String.class);

                                    // Check if the name, email, and password match
                                    if (uname.equalsIgnoreCase(dbName) &&
                                            emailid.equalsIgnoreCase(dbEmail) &&
                                            password.equals(dbPassword)) {
                                        userFound = true;
                                        classArrayList.add(new UserClass(className, subjectName, dbName, dbEmail, usertype,classcode));

                                        break; // Exit loop once the user is found
                                    }
                                }
                            }
                        }
                    }
                }
                recyclerViewAdapter.notifyDataSetChanged();
                checkClass();
                if (!userFound) {
                    Toast.makeText(getActivity(), "No matching classes found for your account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });

    }
}