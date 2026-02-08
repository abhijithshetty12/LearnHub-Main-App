package com.example.learnhub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnhub.R;
import com.example.learnhub.model.QuizModel;

import java.util.List;

public class RecyclerViewAdapterQuiz extends RecyclerView.Adapter<RecyclerViewAdapterQuiz.ViewHolder> {
    Context context;
    List<QuizModel> quizModelList;

    public RecyclerViewAdapterQuiz(Context context, List<QuizModel> quizModelList) {
        this.context = context;
        this.quizModelList = quizModelList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterQuiz.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_question_layout,parent,false);
        return new ViewHolder(view)  ;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterQuiz.ViewHolder holder, int position) {
        QuizModel quizModel = quizModelList.get(position);
        holder.question.setText(quizModel.getQuizQuestion());
        List<String> options = quizModel.getOptions();
        if (options.size() > 0) holder.opt1.setText(options.get(0));
        if (options.size() > 1) holder.opt2.setText(options.get(1));
        if (options.size() > 2) holder.opt3.setText(options.get(2));
        if (options.size() > 3) holder.opt4.setText(options.get(3));

        // Set the previously selected answer, if any
        String userAnswer = quizModel.getUserAnswer();
        if (userAnswer != null) {
            if (userAnswer.equals(holder.opt1.getText().toString())) {
                holder.optiongroup.check(holder.opt1.getId());
            } else if (userAnswer.equals(holder.opt2.getText().toString())) {
                holder.optiongroup.check(holder.opt2.getId());
            } else if (userAnswer.equals(holder.opt3.getText().toString())) {
                holder.optiongroup.check(holder.opt3.getId());
            } else if (userAnswer.equals(holder.opt4.getText().toString())) {
                holder.optiongroup.check(holder.opt4.getId());
            }

        } else {
            holder.optiongroup.clearCheck();

        }

        // Update user's answer when an option is selected
        holder.optiongroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedAnswer = null;
            if (checkedId == holder.opt1.getId()) {
                selectedAnswer = holder.opt1.getText().toString();
            } else if (checkedId == holder.opt2.getId()) {
                selectedAnswer = holder.opt2.getText().toString();
            } else if (checkedId == holder.opt3.getId()) {
                selectedAnswer = holder.opt3.getText().toString();
            } else if (checkedId == holder.opt4.getId()) {
                selectedAnswer = holder.opt4.getText().toString();
            }
            quizModel.setUserAnswer(selectedAnswer);
        });
    }

    // Method to update the user's answer in the quiz list


    @Override
    public int getItemCount() {
        return quizModelList.size();
    }
    public void updateData(List<QuizModel> newQuizList) {
        this.quizModelList = newQuizList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView question ;
        public RadioButton opt1,opt2,opt3,opt4;
        public RadioGroup optiongroup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView .setOnClickListener(this);
            question = itemView.findViewById(R.id.quizQues);
            opt1 = itemView .findViewById(R.id.option1);
            opt2 = itemView .findViewById(R.id.option2);
            opt3 = itemView .findViewById(R.id.option3);
            opt4 = itemView .findViewById(R.id.option4);
            optiongroup=itemView.findViewById(R.id.options_group);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
