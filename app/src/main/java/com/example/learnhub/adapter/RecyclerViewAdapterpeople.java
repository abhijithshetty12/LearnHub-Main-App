package com.example.learnhub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnhub.Join;
import com.example.learnhub.R;
import com.example.learnhub.model.UserClass;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RecyclerViewAdapterpeople extends RecyclerView.Adapter<RecyclerViewAdapterpeople.ViewHolder> {
    private Context context;
    private List<UserClass> peopleList;

    public RecyclerViewAdapterpeople(Context context, List<UserClass> peopleList) {
        this.context = context;
        this.peopleList = peopleList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterpeople.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
      View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_showpeople,parent,false);
      return new ViewHolder(view);
     }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterpeople.ViewHolder holder, int position) {
     UserClass join = peopleList.get(position);
     holder.stdname.setText(join.getUserName());
     DatabaseReference stdref = FirebaseDatabase.getInstance().getReference(join.getUserType());
     stdref.child(join.getUserEmail().replace(".", ",")).child("imageUrl").get().addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            String imageUrl = task.getResult().getValue(String.class);
            if (imageUrl != null) {
                Glide.with(context).
                        load(imageUrl)
                        .fitCenter()
                        .placeholder(R.drawable.profileimg)
                        .into(holder.stdprof);
            }else {
                holder.stdprof.setImageResource(R.drawable.profileimg);
            }
        }
    });



    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView stdname;
        public ImageView stdprof;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            itemView.setOnClickListener(this);
            stdname = itemView.findViewById(R.id.pname);
            stdprof =itemView.findViewById(R.id.profileimg);

        }

        @Override
        public void onClick(View v) {

        }
    }}

