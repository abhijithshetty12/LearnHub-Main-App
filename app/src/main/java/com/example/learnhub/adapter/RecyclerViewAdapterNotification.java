package com.example.learnhub.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhub.R;
import com.example.learnhub.model.NotificationModel;
import com.example.learnhub.model.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapterNotification extends RecyclerView.Adapter<RecyclerViewAdapterNotification.ViewHolder> {
    Context context ;
    String name;
    List<NotificationModel> notificationModelList;
    private
    Map<String, String> classCache = new HashMap<>();


    public RecyclerViewAdapterNotification(Context context, List<NotificationModel> notificationModelList) {
        this.context = context;
        this.notificationModelList = notificationModelList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterNotification.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_show_notification,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterNotification.ViewHolder holder, int position) {
       NotificationModel notificationModel = notificationModelList.get(position);
        Log.d("Gallerfragment","notification : "+notificationModel.getNotificationUsername());
        Log.d("Gallerfragment","notification : "+notificationModel.getNotificationClasscode());
        Log.d("Gallerfragment","notification : "+notificationModel.getNotificationDescription());
        Log.d("Gallerfragment","notification : "+notificationModel.getNotificationTitle());
        Log.d("Gallerfragment","notification : "+notificationModel.getNotificationDateTime());
       holder.notiftitle.setText(notificationModel.getNotificationTitle());
       holder.notifdescription.setText(notificationModel.getNotificationUsername()+" has "+notificationModel.getNotificationDescription());
       holder.notifDate.setText(notificationModel.getNotificationDateTime());



        String classcode = notificationModel.getNotificationClasscode();
        if (classCache.containsKey(classcode)) {
            // Use cached class name if available
            holder.notifClass.setText("Class: " + classCache.get(classcode));
        } else {
            // Fetch class name from Firebase if not cached
            fetchClassName(classcode, holder);
        }


    }


    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }
    public void updateNotifications(List<NotificationModel> newNotifications) {
        notificationModelList.clear();
        notificationModelList.addAll(newNotifications);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
       public TextView notiftitle ,notifdescription ,notifDate ,notifClass;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            notiftitle = itemView.findViewById(R.id.notification_title);
            notifdescription = itemView.findViewById(R.id.notification_description);
            notifDate = itemView.findViewById(R.id.notification_date_time);

            notifClass = itemView.findViewById(R.id.notification_class_name);

        }

        @Override
        public void onClick(View v) {

        }
    }
    private   void fetchClassName(String classcode,ViewHolder holder){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Class");
        Query query = db.orderByChild("classCode").equalTo(classcode);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String classname = snapshot1.child("classname").getValue(String.class);
                    if (classname != null) {
                        classCache.put(classcode, classname);  // Cache the classname
                        holder.notifClass.setText("Class: " + classname);  // Update the view
                    }
                    break;
                }}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void changeStatus(int position) {
        NotificationModel notification = notificationModelList.get(position);
        UserSession userSession = new UserSession(context);
        String usertype = userSession.getUserType();
        DatabaseReference notificationRef = FirebaseDatabase.getInstance()
                .getReference("Notifications")
                .child(usertype) // Update if userType varies (e.g., notification.getUserType())
                .child(notification.getNotificationClasscode());

        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    if (uid != null) {
                        change(uid, position);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void change(String uid,int position){
        NotificationModel notification = notificationModelList.get(position);
        UserSession userSession = new UserSession(context);
        String usertype = userSession.getUserType();
        DatabaseReference notificationRef = FirebaseDatabase.getInstance()
                .getReference("Notifications")
                .child(usertype) // Update if userType varies (e.g., notification.getUserType())
                .child(notification.getNotificationClasscode())
                .child(uid);
        notificationRef.child("read").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Message marked as read", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to mark as read", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
