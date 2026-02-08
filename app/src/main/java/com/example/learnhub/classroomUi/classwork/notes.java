package com.example.learnhub.classroomUi.classwork;

import android.adservices.topics.Topic;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhub.R;
import com.example.learnhub.adapter.RecyclerViewAdapterDocs;
import com.example.learnhub.model.DocumentModel;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class notes extends AppCompatActivity {
    String topic ,description;
    TextView ntopic ,ndescription;
    ArrayList<String> documentList;
    List<DocumentModel> documentModelList;
    RecyclerView docsrecyclerview;
    RecyclerViewAdapterDocs docsadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        documentList= new ArrayList<>();
        documentModelList= new ArrayList<>();
        Intent intent = getIntent();
        topic = intent.getStringExtra("topic");
        description = intent.getStringExtra("description");
        documentList = intent.getStringArrayListExtra("documentList");
        Log.d("notesActivity", String.valueOf(documentList.size()));

        ntopic = findViewById(R.id.heading);
        ndescription =findViewById(R.id.descriptionnotes);
        ntopic.setText(topic);
        ndescription.setText(description);
        docsrecyclerview = findViewById(R.id.docsrecyclerview);
        docsrecyclerview.setHasFixedSize(true);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        docsrecyclerview.setLayoutManager(flexboxLayoutManager);
        documentModelList.clear();
        for(String documentUri : documentList){
            Uri documentURi = Uri.parse(documentUri);
            String encodeFilePath= documentUri.split("\\?")[0].split("/o/")[1];
            try {
                String decodeFilePath = URLDecoder.decode(encodeFilePath,"UTF-8");
                String[] pathSegments = decodeFilePath.split("/");
                 String filename = pathSegments[pathSegments.length-1];
                documentModelList.add(new DocumentModel(filename,documentURi));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            /*Uri documentURi = Uri.parse(documentUri);
            String filename = documentURi.getLastPathSegment();
            filename = Uri.decode(filename);
            if (filename.contains(":")){
                String[] split = filename.split(":");
                filename =split[1];
            }*/
        }

        docsadapter = new RecyclerViewAdapterDocs(this,documentModelList);
        docsrecyclerview.setAdapter(docsadapter);
        docsadapter.notifyDataSetChanged();
    }




}