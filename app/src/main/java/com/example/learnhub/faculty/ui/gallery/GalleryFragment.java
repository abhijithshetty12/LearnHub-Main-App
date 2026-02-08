package com.example.learnhub.faculty.ui.gallery;

import android.app.Notification;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhub.R;
import com.example.learnhub.adapter.RecyclerViewAdapterNotification;
import com.example.learnhub.databinding.FragmentGalleryBinding;
import com.example.learnhub.model.NotificationModel;
import com.example.learnhub.model.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {
    RecyclerView notificationRecyclerview;
    List<NotificationModel> notificationModelList;
    RecyclerViewAdapterNotification notificationAdapter ;
    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
     
        notificationRecyclerview = root.findViewById(R.id.notificationRecyclerView); 
        notificationModelList  =new ArrayList<>();

        notificationRecyclerview.setHasFixedSize(true);
        notificationRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        notificationAdapter =new RecyclerViewAdapterNotification(getActivity(),notificationModelList);
        notificationRecyclerview.setAdapter(notificationAdapter);
        fetchNotification();
        return root;
    }

    private void fetchNotification() {
        UserSession userSession = new UserSession(getContext());
        String usertype = userSession.getUserType();
        if (usertype.equals("Faculty")){
            usertype = "Students";
        }else {
            usertype = "Faculty";
        }
        Log.d("Gallerfragment","usertype : "+usertype);
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("Notifications").child(usertype);

        // Fetch all classes under the user type
        notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notificationModelList.clear();
                for (DataSnapshot classcodeSnapShot : dataSnapshot.getChildren()) {
                    // Access each notification within the class
                    for (DataSnapshot notificationSnap : classcodeSnapShot.getChildren()) {
                        NotificationModel notification = notificationSnap.getValue(NotificationModel.class);
                        Log.d("Gallerfragment","notification : "+notification);

                        if (notification != null && !notification.isRead()) { // Check for unread notifications
                            notificationModelList.add(notification);
                            Log.d("Gallerfragment","notificationlist : "+notificationModelList);

                        }
                    }
                }
                // Update RecyclerView with unread notifications
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}