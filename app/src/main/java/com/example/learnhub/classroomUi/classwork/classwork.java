package com.example.learnhub.classroomUi.classwork;

import android.content.Intent;
import android.health.connect.datatypes.units.Length;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.learnhub.R;
import com.example.learnhub.adapter.RecyclerViewAdapterNotes;
import com.example.learnhub.model.AssignmentModel;
import com.example.learnhub.model.AttendanceModel;
import com.example.learnhub.model.Document;
import com.example.learnhub.model.UserSession;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

public class classwork extends Fragment {

   FloatingActionButton assignbtn,quizbtn,notesbtn,attenbtn;
   FloatingActionMenu fabmenu;
   RecyclerView notesrecyclerview;
   RecyclerViewAdapterNotes notesAdapter;
    List<String> quizTitleList ;
    String usertype;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2,classCode;

    public classwork() {
        // Required empty public constructor
    }


    public static classwork newInstance(String param1, String param2) {
        classwork fragment = new classwork();
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_classwork, container, false);

        fabmenu = view.findViewById(R.id.fab_menu);
        assignbtn = view.findViewById(R.id.fab_assignment);
        quizbtn = view.findViewById(R.id.fab_quiz);
        notesbtn = view.findViewById(R.id.fab_notes);
        attenbtn = view.findViewById(R.id.fab_attendance);
        classCode = getArguments().getString("classcode");
        quizTitleList = new ArrayList<>();
        UserSession userSession = new UserSession(getContext());
        usertype = userSession.getUserType();
        notesrecyclerview =view.findViewById(R.id.notesrecyclerview);
        notesrecyclerview.setHasFixedSize(true);
        notesrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        /*addDocument();
        notesAdapter = new RecyclerViewAdapterNotes(getContext(),documentList);*/
        addDocument();
        notesAdapter = new RecyclerViewAdapterNotes(getContext(),new ArrayList<>(),classCode);
        notesrecyclerview.setAdapter(notesAdapter);

        notesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Notes selected", Toast.LENGTH_SHORT).show();
                addDocument();
            }
        });
        assignbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Assignment Selected", Toast.LENGTH_SHORT).show();
                addAssignment();
            }
        });

        quizbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Quiz selected", Toast.LENGTH_SHORT).show();
                addQuiz();
            }
        });
        attenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Attendance selected", Toast.LENGTH_SHORT).show();
                addAttendance();
            }
        });
        return view;
    }

    private void addDocument() {
        DatabaseReference notesref = FirebaseDatabase.getInstance().getReference("Document");
        Query query = notesref.child(classCode);
        query .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Object> documentList = new ArrayList<>();
                for(DataSnapshot topicSnapshot : snapshot.getChildren()){

                        String topicname = topicSnapshot.child("topic").getValue(String.class).toString();
                        String description = topicSnapshot.child("description").getValue(String.class).toString();
                        List<String> documentURllist = new ArrayList<>();
                        for (DataSnapshot docSnapshot : topicSnapshot.child("documentList").getChildren()){
                            String documentUrl = docSnapshot.getValue(String.class);

                            documentURllist.add(documentUrl);
                        }
                        documentList.add(new Document(topicname,description,documentURllist));
            }
            notesAdapter.updateData(documentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.classwork_toolbar_menu, menu); // Inflate classwork menu
        super.onCreateOptionsMenu(menu, inflater);
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (usertype.equals("Students")) {
            if (R.id.menu_notes == (item.getItemId())) {
                Toast.makeText(getContext(), "Notes selected", Toast.LENGTH_SHORT).show();
                addDocument();
            } else if (R.id.menu_assignment == (item.getItemId())) {
                Toast.makeText(getContext(), "Assignment selected", Toast.LENGTH_SHORT).show();
                addAssignment();
            } else if (R.id.menu_quiz == (item.getItemId())) {
                Toast.makeText(getContext(), "Quiz selected", Toast.LENGTH_SHORT).show();
                addQuiz();
            } else if (R.id.menu_attendance == (item.getItemId())) {
                Toast.makeText(getContext(), "Attendance selected", Toast.LENGTH_SHORT).show();
                addAttendance();
            }
        }else if (usertype.equals("Faculty")){
            if (R.id.menu_notes == (item.getItemId())) {
                startActivity(new Intent(getActivity(), UploadDocument.class)
                        .putExtra("classcode",classCode));
            } else if (R.id.menu_assignment == (item.getItemId())) {
                startActivity(new Intent(getActivity(), CreateAssignment.class)
                        .putExtra("classcode",classCode));
            } else if (R.id.menu_quiz == (item.getItemId())) {
                startActivity(new Intent(getActivity(), CreateQuiz.class)
                        .putExtra("classcode",classCode));
            } else if (R.id.menu_attendance == (item.getItemId())) {
                startActivity(new Intent(getActivity(), CreateAttendance.class)
                        .putExtra("classcode",classCode));
            }
        }
        return super.onOptionsItemSelected(item);

    }
    private void addQuiz(){
        DatabaseReference quizref  =FirebaseDatabase.getInstance().getReference("Quiz");
        quizTitleList.clear();
        quizref.child(classCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Object> quiztitle = new ArrayList<>(); // Get existing items
                for (DataSnapshot quizsnapshot :snapshot.getChildren()){
                        String title = quizsnapshot.child("title").getValue(String.class);
                        quiztitle.add(title);
}
             notesAdapter.updateData(quiztitle);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error fetching quiz data", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void addAssignment() {
        List<Object> assignTitleList = new ArrayList<>();
        DatabaseReference asignRef = FirebaseDatabase.getInstance().getReference("AssignmentModel").child(classCode);
        asignRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                assignTitleList.clear();
                for (DataSnapshot useridsnapshot : snapshot.getChildren()) {
                    for (DataSnapshot usernameSnapshot : useridsnapshot.getChildren()) {
                        String title = usernameSnapshot.child("assignTitle").getValue(String.class);
                        if (title != null) {
                            assignTitleList.add(new AssignmentModel(title));
                        }
                    }
                }
                notesAdapter.updateData(assignTitleList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error fetching assignment data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  void addAttendance(){
        List<Object> attendanceList = new ArrayList<>();
        DatabaseReference attenRef = FirebaseDatabase.getInstance().getReference("Attendance").child(classCode);
        attenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot idSnapshot : snapshot.getChildren()){
                    String title = idSnapshot.child("attentitle").getValue(String.class);
                    String date = idSnapshot.child("date").getValue(String.class);
                    boolean open = idSnapshot.child("open").getValue(Boolean.class);
                    long timeLimit = idSnapshot.child("timeLimit").getValue(Long.class);
                    long startTime = idSnapshot.child("startTime").getValue(Long.class);
                    attendanceList.add(new AttendanceModel(title,date,timeLimit,startTime,open));
                }
                notesAdapter.updateData(attendanceList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}




