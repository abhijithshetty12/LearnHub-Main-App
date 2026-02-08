package com.example.learnhub.classroomUi.classwork;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateAssignment extends AppCompatActivity {

    ImageButton dueDate , attachfile;
    EditText assignTitle , assignDes;
    RecyclerView fileRecyclerview;
    Button assignbtn;
    String title,description,DueDate,classcode;
    TextView duedateText;
    Switch aSwitch;
    List<DocumentModel> documentModelList;
    List<Uri> documentList;
    RecyclerViewAdapterDocs adapterDocs;
    ProgressBar progressBar;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_assignment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        toolbar  =findViewById(R.id.Assignment_Toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Drawable upArrow = getResources().getDrawable(R.drawable.backbtn); // Default back icon for AppCompat
            upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent  = getIntent();
        classcode= intent.getStringExtra("classcode");
        dueDate = findViewById(R.id.et_dueDate);
        attachfile = findViewById(R.id.et_attachfile);
        assignTitle  = findViewById(R.id.et_assignment_title);
        assignDes  = findViewById(R.id.et_description);
        fileRecyclerview = findViewById(R.id.assignmentRecyclerView);
        assignbtn = findViewById(R.id.assignbtn);
        aSwitch = findViewById(R.id.switch_due_date);
        duedateText = findViewById(R.id.setDuedate);
        progressBar = findViewById(R.id.assignProgressbar);
        documentModelList=new ArrayList<>();
        documentList=new ArrayList<>();
        fileRecyclerview.setHasFixedSize(true);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(CreateAssignment.this);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        fileRecyclerview.setLayoutManager(flexboxLayoutManager);
        adapterDocs = new RecyclerViewAdapterDocs(getApplicationContext(),documentModelList);
        fileRecyclerview.setAdapter(adapterDocs);
        dueDate.setOnClickListener(v -> {
           if (isSwitch()) {
               showDatePicker();
           }
           else{
               Toast.makeText(this, "Check the switch", Toast.LENGTH_SHORT).show();
           }
        });

        attachfile.setOnClickListener(v -> {openDocumentPicker();});
        assignbtn.setOnClickListener(v -> {submitAssign();});

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                DueDate = "No Due Date";
                duedateText.setText(DueDate);
            }else {
                showDatePicker();
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

    private void showDatePicker() {
        // Show date picker dialog for selecting due date
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DueDate = dateFormat.format(calendar.getTime());
                    Toast.makeText(this, "Due date: " + DueDate, Toast.LENGTH_SHORT).show();
                    duedateText.setText(DueDate);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void openDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        documentPickerLauncher.launch(Intent.createChooser(intent,"Select Document"));
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
                        adapterDocs.notifyDataSetChanged();
                    }
                }
            } );

    private String getFileName(Uri documentUri) {
        String result = null;
        if (documentUri.getScheme().equals("content")){
            try (Cursor cursor = CreateAssignment.this.getContentResolver().query(documentUri, null, null, null, null)) {
                if(cursor!= null && cursor.moveToFirst() ){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result==null){
            result = documentUri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut!=-1){
                result =result.substring(cut +1 );
            }

        }
        return result;
    }
    private void submitAssign() {

        title = assignTitle.getText().toString().trim();
        description = assignDes.getText().toString().trim();
        if (!isSwitch()) DueDate = "No Due Date";
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please enter title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        ChangeProgress(true);

        DatabaseReference assignRef = FirebaseDatabase.getInstance().getReference("AssignmentModel").child(classcode).push();
        String assignmentId = assignRef.getKey();
        UserSession userSession = new UserSession(getApplicationContext());
        String username = userSession.getUserName();
        StorageReference assignStorage = FirebaseStorage.getInstance().getReference("Assignments/" +classcode+"/"+ assignmentId+"/"+title+"/"+username);

        List<AssignmentModel.FileInfo> fileInfoList = new ArrayList<>();
        if (documentList.isEmpty()) {
            saveAssignmentData(assignRef, fileInfoList);  // Pass empty fileInfoList
            return;
        }
        for (int i = 0; i < documentList.size(); i++) {
            Uri fileUri = documentList.get(i);
            String fileName = documentModelList.get(i).getFileName();
            StorageReference fileRef = assignStorage.child(fileName);
            int finalI = i;
            fileRef.putFile(fileUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    fileInfoList.add(new AssignmentModel.FileInfo(fileName,uri.toString()));
                        // After all files are uploaded, save the assignment data
                        if (fileInfoList.size() == documentList.size()) {
                            saveAssignmentData(assignRef, fileInfoList);
                        }
                    })
            ).addOnFailureListener(e ->
                    Toast.makeText(CreateAssignment.this, "File upload failed", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void saveAssignmentData(DatabaseReference assignRef, List<AssignmentModel.FileInfo> fileMap) {
        AssignmentModel assignment = new AssignmentModel(title, description, DueDate,fileMap);
        UserSession userSession = new UserSession(getApplicationContext());
        String username = userSession.getUserName();
        assignRef.child(username).setValue(assignment).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                NotificationModel.NotificationUtils.sendNotification(getApplicationContext(),title,"Uploaded the Assignment",username,classcode);
                Toast.makeText(this, "Assignment uploaded successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to upload assignment", Toast.LENGTH_SHORT).show();
            }
        });
        ChangeProgress(false);
    }


    private boolean isSwitch(){
        return aSwitch.isChecked();
    }
    private void ChangeProgress(Boolean progress){
        if (progress){
            progressBar.setVisibility(View.VISIBLE);
            assignbtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            assignbtn.setVisibility(View.VISIBLE);
        }
    }
}