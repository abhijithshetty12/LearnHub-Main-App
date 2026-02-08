package com.example.learnhub.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhub.Class;
import com.example.learnhub.Join;
import com.example.learnhub.R;
import com.example.learnhub.classroom;

import java.util.List;

public class RecyclerViewAdapterStd extends RecyclerView.Adapter<RecyclerViewAdapterStd.ViewHolder> {

    private Context context;
    private List<Join> joinList;

    public RecyclerViewAdapterStd(Context context,List<Join> joinList) {
        this.context=context;
        this.joinList = joinList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_showstdclass, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Join join = joinList.get(position);
        if (join != null) {
            holder.classname.setText(join.getClassname());
        } else {
            Log.e("RecyclerViewAdapterStd", "Join object is null");
        }
    }

    @Override
    public int getItemCount() {
        return  joinList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView classname;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            classname = itemView.findViewById(R.id.classnamestd);

        }
        @Override
        public void onClick(View v) {
            int position = this.getAdapterPosition();
            Join selectedJoin = joinList.get(position);
            String classname = selectedJoin.getClassname();
            String section = selectedJoin.getSection();
            String subject = selectedJoin.getSubject();
            Intent intent = new Intent(context, classroom.class);
            intent.putExtra("classname",classname);
            intent.putExtra("section",section);
            intent.putExtra("subject",subject);
            intent.putExtra("classcode",selectedJoin.getClassCode());
            context.startActivity(intent);


        }
    }
}
