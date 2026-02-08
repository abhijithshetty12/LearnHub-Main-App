package com.example.learnhub.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhub.R;
import com.example.learnhub.classroomUi.classwork.ShowStudentAssignment;
import com.example.learnhub.model.AssignmentResultModel;
import com.example.learnhub.model.AttendanceModel;
import com.example.learnhub.model.QuizResultModel;

import java.util.List;

public class RecyclerViewAdapterQuizResult extends RecyclerView.Adapter<RecyclerViewAdapterQuizResult.ViewHolder> {
    private Context context;
    private List<Object> quizResultModelList;


    public RecyclerViewAdapterQuizResult(Context context, List<Object> quizResultModelList) {
        this.context = context;
        this.quizResultModelList = quizResultModelList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterQuizResult.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_quiz_result,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterQuizResult.ViewHolder holder, int position) {
        if (quizResultModelList.get(position) instanceof QuizResultModel) {
            QuizResultModel quizResultModel = (QuizResultModel) quizResultModelList.get(position);
            holder.qname.setText(quizResultModel.getUsername());
            holder.score.setText(quizResultModel.getScore());
        }
        else if(quizResultModelList.get(position) instanceof AssignmentResultModel){
            AssignmentResultModel assignmentResultModel  =(AssignmentResultModel) quizResultModelList.get(position);
            holder.qname.setText(assignmentResultModel.getUsername());
            holder.score.setText(assignmentResultModel.getSubmissionStatus());
        } else if (quizResultModelList.get(position) instanceof AttendanceModel.StudentAttendance) {
            AttendanceModel.StudentAttendance studentAttendance = (AttendanceModel.StudentAttendance) quizResultModelList.get(position);
            holder.qname.setText(studentAttendance.getStdName());
            boolean isPresent = studentAttendance.isPresent();
            if (isPresent) {
                holder.score.setText("Present");
            }else{
                holder.score.setText("Absent");
            }


        }
    }

    @Override
    public int getItemCount() {
        return quizResultModelList.size();
    }
    public void updateData(List<Object> newQuizResultModelList) {
        this.quizResultModelList = newQuizResultModelList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView qname ,score;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            qname = itemView.findViewById(R.id.qname);
            score = itemView.findViewById(R.id.userscore);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                if (quizResultModelList.get(position) instanceof AssignmentResultModel) {
                    AssignmentResultModel assignmentResultModel = (AssignmentResultModel) quizResultModelList.get(position);
                    Toast.makeText(context, "Assignment Selected", Toast.LENGTH_SHORT).show();
                    String uid = assignmentResultModel.getUid();
                    String username = assignmentResultModel.getUsername();
                    Log.d("UID", "Adapter uid: " + uid);
                    Log.d("UID", "USername uid: " + username);
                    // Use itemView.getContext() for starting the intent
                    Intent intent = new Intent(itemView.getContext(), ShowStudentAssignment.class);
                    intent.putExtra("username", username);
                    intent.putExtra("uid", uid);
                    itemView.getContext().startActivity(intent);
                    Log.e("UID", "Context is not an instance of Activity");
                    }

                }
            }
        }
    }

