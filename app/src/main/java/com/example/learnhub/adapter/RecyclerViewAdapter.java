package com.example.learnhub.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhub.Class;
import com.example.learnhub.Join;
import com.example.learnhub.R;
import com.example.learnhub.classroom;
import com.example.learnhub.classroomUi.chatroom.chatroom;
import com.example.learnhub.faculty.Facultyhome;
import com.example.learnhub.model.UserClass;

import java.util.HashMap;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<UserClass> classList;
    private int currentIndex = 0;
    ImageView imageView;
    private HashMap<Integer, Integer> imageMap = new HashMap<>();

    private int[] imageIds = {
            R.drawable.classbg6,
            R.drawable.classbg7,
            R.drawable.classbg11,
            R.drawable.classbg14,
            R.drawable.classbg12,
            R.drawable.classbg13,
            R.drawable.classbg14
    };

    private String usertype;

    public RecyclerViewAdapter(Context context, List<UserClass> classList) {
        this.context = context;
        this.classList = classList;
    }



    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_showclasses, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            UserClass aClass = classList.get(position);
            holder.classname.setText(aClass.getClassName() != null ? aClass.getClassName() : "No Class Name");
            holder.subject.setText(aClass.getClassSubject() != null ? aClass.getClassSubject() : "No Subject");
        if (!imageMap.containsKey(position)) {
            int assignedImage = imageIds[position % imageIds.length]; // Cycle through available images
            imageMap.put(position, assignedImage); // Store the association
        }

        // Retrieve the image ID for this position
        int imageId = imageMap.get(position);

        // Set the background with a darkened effect
        Drawable drawable = ContextCompat.getDrawable(holder.itemView.getContext(), imageId);
        if (drawable != null) {
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setScale(0.75f, 0.75f, 0.75f, 1.0f); // Darken by 25%
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
            drawable.setColorFilter(filter);
        }
        holder.classbg.setBackground(drawable);


    }

    @Override
    public int getItemCount() {
        return  classList.size() ;
    }


    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView classname,section,subject;
        public ConstraintLayout classbg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);


            classname = itemView.findViewById(R.id.classname);
           classbg = itemView.findViewById(R.id.classbg);
            subject = itemView.findViewById(R.id.subject);

        }
        @Override
        public void onClick(View v) {
         int position = this.getAdapterPosition();
         UserClass selectedClass = classList.get(position);
         String classname = selectedClass.getClassName();
         String subject = selectedClass.getClassSubject();
            Intent intent = new Intent(context, classroom.class);
            intent.putExtra("classname",classname);
            intent.putExtra("subject",subject);
            intent.putExtra("classcode",selectedClass.getClasscode());
            intent.putExtra("classimg",imageMap.get(position));
            context.startActivity(intent);




        }
    }
}
