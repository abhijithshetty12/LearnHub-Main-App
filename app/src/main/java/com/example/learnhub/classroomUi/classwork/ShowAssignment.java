package com.example.learnhub.classroomUi.classwork;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.learnhub.model.NotificationModel;
import com.example.learnhub.model.UserSession;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ShowAssignment extends AppCompatActivity {
    TextView assignTitle, assignDescription, assignDueDate, facultyAttachFile;
    ImageButton attachFile;
    RecyclerView facultyAttachFileRecyclerView, studentUploadRecyclerView;
    RecyclerViewAdapterDocs adapterDocs, facultyadapterDocs;
    Button assignSubmitbtn;
    String title, description, duedate, classcode, submissionDate, submissionStatus,email;
    List<String> fileUrls;
    List<DocumentModel> documentModelList, documentModelListoffaculty;
    List<Uri> documentList;
    ProgressBar progressBar;
    String submittedDate = "";
    boolean isAssignmentSubmitted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_assignment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        assignTitle = findViewById(R.id.assignmentTitle);
        assignDescription = findViewById(R.id.assignmentDescription);
        assignDueDate = findViewById(R.id.assignmentDueDate);
        facultyAttachFile = findViewById(R.id.assignmentFacultyFile);
        attachFile = findViewById(R.id.attchfileimgbtn);
        facultyAttachFileRecyclerView = findViewById(R.id.attachedFilesRecyclerView);
        studentUploadRecyclerView = findViewById(R.id.studentUploadedFilesRecyclerView);
        assignSubmitbtn = findViewById(R.id.assignmentSubmit);
        progressBar = findViewById(R.id.progressBarassign);
        fileUrls = new ArrayList<>();
        documentModelList = new ArrayList<>();
        documentModelListoffaculty = new ArrayList<>();
        documentList = new ArrayList<>();
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        classcode = intent.getStringExtra("classcode");
         email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        fetchSubmittedAssignment();
        initialize();
        studentUploadRecyclerView.setHasFixedSize(true);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(ShowAssignment.this);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        studentUploadRecyclerView.setLayoutManager(flexboxLayoutManager);
        adapterDocs = new RecyclerViewAdapterDocs(getApplicationContext(), documentModelList);
        studentUploadRecyclerView.setAdapter(adapterDocs);
        attachFile.setOnClickListener(v -> {
            openDocumentPicker();
        });

        assignSubmitbtn.setOnClickListener(v -> submitAssignment());
    }

    private void openDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        documentPickerLauncher.launch(Intent.createChooser(intent, "Select Document"));
    }

    private final ActivityResultLauncher<Intent> documentPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri documentUri = result.getData().getData();
                    if (documentUri != null) {
                        String fileName = getFileName(documentUri);
                        documentModelList.add(new DocumentModel(fileName, documentUri));
                        documentList.add(documentUri);
                        adapterDocs.notifyItemInserted(documentModelList.size() - 1);
                    }
                }
            });

    private String getFileName(Uri documentUri) {
        String result = null;
        if (documentUri.getScheme().equals("content")) {
            try (Cursor cursor = ShowAssignment.this.getContentResolver().query(documentUri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = documentUri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }

        }
        return result;
    }

private void initialize() {
        assignTitle.setText(title);
        facultyAttachFileRecyclerView.setHasFixedSize(true);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(ShowAssignment.this);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        facultyAttachFileRecyclerView.setLayoutManager(flexboxLayoutManager);
        facultyadapterDocs = new RecyclerViewAdapterDocs(getApplicationContext(), documentModelListoffaculty);
        facultyAttachFileRecyclerView.setAdapter(facultyadapterDocs);

    }
private void fetchAssignment() {
        DatabaseReference assignRef = FirebaseDatabase.getInstance().getReference("AssignmentModel").child(classcode);
        assignRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot assignmentSnapshot : dataSnapshot.getChildren()) {
                    // Check if the assignTitle matches the title you're looking for
                    for (DataSnapshot usernameSnapshot : assignmentSnapshot.getChildren()) {

                        String assignTitle = usernameSnapshot.child("assignTitle").getValue(String.class);
                        if (assignTitle != null && assignTitle.equals(title)) {
                            // Fetch assignment details
                            description = usernameSnapshot.child("assignDescription").getValue(String.class);
                            duedate = usernameSnapshot.child("duedate").getValue(String.class);
                            if (submittedDate == "") {
                                checkDueDate(duedate);
                            }
                            runOnUiThread(() -> {
                                assignDescription.setText(description);

                            });

                            // Fetch files in fileMap
                            List<String> fileNames = new ArrayList<>();
                            for (DataSnapshot fileSnapshot : usernameSnapshot.child("fileMap").getChildren()) {
                                String fileName = fileSnapshot.child("fileName").getValue(String.class);
                                String fileUrl = fileSnapshot.child("fileUrl").getValue(String.class);
                                fileNames.add(fileName);
                                fileUrls.add(fileUrl);
                            }

                            // Log or use the data
                            Log.d("FirebaseData", "Assignment Title: " + assignTitle);
                            Log.d("FirebaseData", "Description: " + description);
                            Log.d("FirebaseData", "Due Date: " + duedate);
                            for (int i = 0; i < fileNames.size(); i++) {
                                Log.d("FirebaseData", "File Name: " + fileNames.get(i) + ", File URL: " + fileUrls.get(i));
                            }
                            runOnUiThread(() -> {
                                if (fileUrls.isEmpty()) {

                                    if (submittedDate == "") {
                                        checkDueDate(duedate);
                                    }
                                    facultyAttachFileRecyclerView.setVisibility(View.GONE);
                                    facultyAttachFile.setText("No attachment");
                                    facultyAttachFile.setTextColor(getResources().getColor(R.color.grey));
                                } else {
                                    if (submittedDate == "") {
                                        checkDueDate(duedate);
                                    }
                                    for (int i = 0; i < fileUrls.size(); i++) {
                                        String filename = getFileName(Uri.parse(fileUrls.get(i)));
                                        documentModelListoffaculty.add(new DocumentModel(filename, Uri.parse(fileUrls.get(i))));
                                    }
                                    facultyadapterDocs.notifyDataSetChanged();
                                }// Notify the adapter that data has changed
                            });

                            break;  // Stop after finding the correct assignment
                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
private void submitAssignment() {
        changeProgress(true);
        if (isAssignmentSubmitted) {
            // If assignment is already submitted, perform unsubmit operation
            documentModelList.clear();
            adapterDocs.notifyDataSetChanged();
            clearDataInFireBase();
            assignSubmitbtn.setText("Submit");
            isAssignmentSubmitted = false; // Reset submission status
            changeProgress(false);
        } else {
            List<AssignmentModel.FileInfo> fileMap = new ArrayList<>();
            for (DocumentModel documentModel : documentModelList) {
                String filename = documentModel.getFileName();
                String fileUrl = documentModel.getFileURI().toString();
                fileMap.add(new AssignmentModel.FileInfo(filename, fileUrl));
            }
            if (!fileMap.isEmpty()) {
                submissionTime();
                checkSubmissionStatus(duedate);
                UserSession userSession = new UserSession(getApplicationContext());
                String username = userSession.getUserName();
                String email = userSession.getUserEmail();
                DatabaseReference assignSubref = FirebaseDatabase.getInstance().getReference("StudentAssignment")
                        .child("classroom")
                        .child(classcode)
                        .child(title)
                        .child(username);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("StudentAssignment/" + classcode + "/" + title + "/" + email + "/" + username + "/");
                List<AssignmentModel.FileInfo> fileInfoList = new ArrayList<>();
                for (int i = 0; i < documentList.size(); i++) {
                    Uri fileUri = documentList.get(i);
                    String fileName = documentModelList.get(i).getFileName();
                    StorageReference fileRef = storageReference.child(fileName);
                    int finalI = i;
                    fileRef.putFile(fileUri).addOnSuccessListener(taskSnapshot ->
                            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                fileInfoList.add(new AssignmentModel.FileInfo(fileName, uri.toString()));
                                // After all files are uploaded, save the assignment data
                                if (fileInfoList.size() == documentList.size()) {
                                    saveAssignment(assignSubref, fileInfoList);
                                }
                            })
                    );
                }
            } else {
                changeProgress(false);
                Toast.makeText(this, "Pls upload the assignment", Toast.LENGTH_SHORT).show();
            }
        }

    }
private void saveAssignment(DatabaseReference assignSubref , List<AssignmentModel.FileInfo> fileInfoList) {
    UserSession userSession = new UserSession(getApplicationContext());
    String username = userSession.getUserName();
    String email = userSession.getUserEmail();
    AssignmentModel.StudentAssignment studentAssignment = new AssignmentModel.StudentAssignment(username, email, submissionDate, submissionStatus, fileInfoList);
        assignSubref.setValue(studentAssignment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                UserSession userSession = new UserSession(getApplicationContext());
                String username = userSession.getUserName();
                NotificationModel.NotificationUtils.sendNotification(getApplicationContext(),title,"Submiited the Assignment",username,classcode);
                Toast.makeText(ShowAssignment.this, "Assignment Submitted Successfully", Toast.LENGTH_SHORT).show();
                changeProgress(false);
                assignDueDate.setText("Submitted on :" + submissionDate);
                assignSubmitbtn.setText("Unsubmit");
                isAssignmentSubmitted = true;
            }
        });


}
private void checkSubmissionStatus(String duedate){
    if (duedate.contains("No Due Date")){

    }else {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        Date currentDate = new Date();
        String formattedDate = dateFormat.format(currentDate);
        try {
            Date DueDate = dateFormat.parse(duedate);
            Date formattedCurrentDate = dateFormat.parse(formattedDate);
            if (DueDate.before(formattedCurrentDate)) {
                submissionStatus = "Done Late";
            } else {
                submissionStatus = "Done on Time";
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
    private void checkDueDate(String duedate) {
       if (duedate.contains("No Due Date")){
           assignDueDate.setText("Due Date : " + duedate);
       }else {
           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
           Date currentDate = new Date();
           String formattedDate = dateFormat.format(currentDate);
           try {
               Date DueDate = dateFormat.parse(duedate);
               Date formattedCurrentDate = dateFormat.parse(formattedDate);
               if (DueDate.before(formattedCurrentDate)) {
                   assignDueDate.setText("Missed Time");
                   submissionStatus = "Done Late";
               } else {
                   assignDueDate.setText("Due Date : " + duedate);
                   submissionStatus = "Done on Time";
               }
           } catch (ParseException e) {
               throw new RuntimeException(e);
           }
       }


    }
    private void submissionTime(){
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);
        submissionDate = formattedDate;
        Log.d("FormattedDate", "Formatted Date: " + formattedDate);

    }
    private void changeProgress(boolean progress){
        if (progress){
            progressBar.setVisibility(View.VISIBLE);
            assignSubmitbtn.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            assignSubmitbtn.setVisibility(View.VISIBLE);
        }
    }
    private void fetchSubmittedAssignment() {
        UserSession userSession = new UserSession(getApplicationContext());
        String username = userSession.getUserName();
        DatabaseReference assignRef = FirebaseDatabase.getInstance().getReference("StudentAssignment")
                .child("classroom")
                .child(classcode)
                .child(title)
                .child(username);

        assignRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assignment is already submitted
                    isAssignmentSubmitted = true;
                     submittedDate =  dataSnapshot.child("submissionDate").getValue(String.class);
                    submissionStatus =  dataSnapshot.child("submissionStatus").getValue(String.class);
                    assignDueDate.setText("Submitted on : "+submittedDate);

                    AssignmentModel.StudentAssignment studentAssignment = dataSnapshot.getValue(AssignmentModel.StudentAssignment.class);

                    if (studentAssignment != null) {
                        // Show the submitted files
                        List<AssignmentModel.FileInfo> submittedFiles = studentAssignment.getFileMap();
                        for (AssignmentModel.FileInfo fileInfo : submittedFiles) {
                            documentModelList.add(new DocumentModel(fileInfo.getFileName(), Uri.parse(fileInfo.getFileUrl())));
                        }
                        adapterDocs.notifyDataSetChanged();

                        // Change button text to Unsubmit
                        assignSubmitbtn.setText("UnSubmit");
                    }
                }
                // Fetch other assignment details (like title, description, due date)
                fetchAssignment();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }
    private void clearDataInFireBase(){
        UserSession userSession = new UserSession(getApplicationContext());
        String username = userSession.getUserName();
        DatabaseReference assignSubref = FirebaseDatabase.getInstance().getReference("StudentAssignment")
                .child("classroom")
                .child(classcode)
                .child(title)
                .child(username);
        assignSubref.child("fileMap").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                        String fileUrl = fileSnapshot.child("fileUrl").getValue(String.class);

                        // Delete file from Firebase Storage
                        if (fileUrl != null) {
                            deleteFileFromStorage(fileUrl);
                        }
                    }
                }
                assignSubref.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ShowAssignment.this, "Assignment is UnSubmitted", Toast.LENGTH_SHORT).show();
                        assignDueDate.setText("Due Date " + duedate);
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
                Toast.makeText(ShowAssignment.this, "Error clearing data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteFileFromStorage(String fileUrl) {
        StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl);
        fileRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseStorage", "File deleted successfully from Storage");
            } else {
                Log.e("FirebaseStorage", "Error deleting file", task.getException());
            }
        });
    }

}