package com.example.learnhub.classroomUi.chatroom;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnhub.ChatMessage;
import com.example.learnhub.R;
import com.example.learnhub.adapter.RecyclerViewAdapter;
import com.example.learnhub.adapter.RecyclerViewAdapterChat;
import com.example.learnhub.model.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link chatroom#newInstance} factory method to
 * create an instance of this fragment.
 */
public class chatroom extends Fragment {
    TextView cname,csection,csubject;
    EditText msgbox;
    Button sendmessage;
    DatabaseReference chatref;
    ConstraintLayout constraintLayout;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    String classcode;
    int classbg ;

    public chatroom() {
        // Required empty public constructor
    }

   RecyclerView recyclerViewChat;
    RecyclerViewAdapterChat chatAdapter;
    ArrayList<ChatMessage> chatMessageArrayList;
    public static chatroom newInstance(String param1, String param2) {
        chatroom fragment = new chatroom();
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
        View view  =  inflater.inflate(R.layout.fragment_chatroom, container, false);
        Intent intent = getActivity().getIntent();
        String classname = intent.getStringExtra("classname");
        String subject = intent.getStringExtra("subject");
        classcode = intent.getStringExtra("classcode");
        classbg = intent.getIntExtra("classimg",0);


        cname = view.findViewById(R.id.classname);
        csubject= view.findViewById(R.id.subject);
        sendmessage = view.findViewById(R.id.sendmsg);
        msgbox = view.findViewById(R.id.msgBox);
       constraintLayout = view.findViewById(R.id.classbgimg);
        chatref  = FirebaseDatabase.getInstance().getReference("ChatMessage");
        recyclerViewChat = view.findViewById(R.id.recyclerviewchat);
        chatMessageArrayList = new ArrayList<>();
        recyclerViewChat.setHasFixedSize(true);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new RecyclerViewAdapterChat(getContext(),chatMessageArrayList);
        recyclerViewChat.setAdapter(chatAdapter);
        addChatInList();
        cname.setText(classname);
        constraintLayout.setBackgroundResource(classbg);
        csubject.setText(subject);

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = msgbox.getText().toString();
                UserSession userSession = new UserSession(getContext());
                String user = userSession.getUserName();
                ChatMessage chatMessage = new ChatMessage(user,msg);
                chatref.child(classcode).push().setValue(chatMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!msg.isEmpty() && !user.isEmpty()) {
                            msgbox.setText("");
                            /*chatMessageArrayList.add(chatMessage);
                            chatAdapter.notifyItemInserted(chatMessageArrayList.size() - 1);
                            recyclerViewChat.scrollToPosition(chatMessageArrayList.size() - 1);*/
                            Toast.makeText(getActivity(), "message is send", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getActivity(), "message is not send", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return view;

    }
    private void addChatInList() {
        UserSession userSession = new UserSession(getContext());
        String user = userSession.getUserName();
        Query query = chatref.child(classcode).orderByChild("messagetime"); // Order by timestamp
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                    if (chatMessage != null) {
                        chatMessageArrayList.add(chatMessage);
                        chatAdapter.notifyItemInserted(chatMessageArrayList.size() - 1);
                        recyclerViewChat.scrollToPosition(chatMessageArrayList.size() - 1);
                    }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle updates if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle removals if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle moves if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load messages: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}