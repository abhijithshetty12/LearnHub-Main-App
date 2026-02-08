package com.example.learnhub.classroomUi.classwork;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhub.R;
import com.example.learnhub.adapter.RecyclerViewAdapterDocs;
import com.example.learnhub.model.AssignmentModel;
import com.example.learnhub.model.DocumentModel;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class ShowStudentAssignment extends AppCompatActivity {
    TextView stdname;
    String username , uid;
    RecyclerView stdasignRecyclerview ;
    RecyclerViewAdapterDocs adapterDocs;
    List<DocumentModel> documentModelList;
    List<AssignmentModel.FileInfo> fileInfoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_student_assignment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        username  = intent.getStringExtra("username");
        uid = intent.getStringExtra("uid");
        Log.d("UID","Student uid"+uid);
        stdname = findViewById(R.id.aname);
        stdasignRecyclerview = findViewById(R.id.studentAssignFile);
        stdasignRecyclerview.setHasFixedSize(true);
        documentModelList=new ArrayList<>();
        fileInfoList = new ArrayList<>();
        stdname.setText(username);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(ShowStudentAssignment.this);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        stdasignRecyclerview.setLayoutManager(flexboxLayoutManager);
        adapterDocs = new RecyclerViewAdapterDocs(getApplicationContext(),documentModelList);
        stdasignRecyclerview.setAdapter(adapterDocs);
        fetchAssignmentFile();
    }

    private void fetchAssignmentFile() {
        documentModelList.clear();
        fileInfoList.clear();

        DatabaseReference studentAssignmentRef = FirebaseDatabase.getInstance().getReference("StudentAssignment");

        studentAssignmentRef.child("classroom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot classroomSnapshot) {
                boolean uidFound = false; // Flag to check if UID was found

                // Traverse each classcode
                for (DataSnapshot classcodeSnapshot : classroomSnapshot.getChildren()) {
                    // Traverse each title
                    for (DataSnapshot titleSnapshot : classcodeSnapshot.getChildren()) {
                        // Check if the uid exists under this title
                        if (titleSnapshot.hasChild(username)) {
                            uidFound = true; // Set flag since UID was found

                            DataSnapshot fileMapSnapshot = titleSnapshot.child(username).child("fileMap");

                            // Iterate through the fileMap and get file details
                            for (DataSnapshot fileSnapshot : fileMapSnapshot.getChildren()) {
                                String fileName = fileSnapshot.child("fileName").getValue(String.class);
                                String fileUrl = fileSnapshot.child("fileUrl").getValue(String.class);
                                documentModelList.add(new DocumentModel(fileName, Uri.parse(fileUrl)));
                                Log.d("UID", "fileName: " + fileName + ", fileUrl: " + fileUrl);
                            }
                            // Exit loop early since uid is already found and processed
                            break;
                        }
                    }
                    if (uidFound) break; // Exit outer loop if UID was found
                }

                if (!uidFound) {
                    Log.d("UID", "UID not found in any classroom/classcode/title");
                }
                // Notify adapter once after data is completely loaded
                adapterDocs.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UID", "Database error: " + databaseError.getMessage());
            }
        });
    }

}