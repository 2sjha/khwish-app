package com.khwish.app.adapters.homepage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khwish.app.R;
import com.khwish.app.responses.GoalDetailsResponse;
import com.khwish.app.utils.ParsingUtil;

import java.util.ArrayList;
import java.util.Collections;

class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalDetailViewHolder> {

    private ArrayList<GoalDetailsResponse> goalDetails;
    private String INR_SYMBOL;

    GoalsAdapter(ArrayList<GoalDetailsResponse> goalDetails) {
        if (goalDetails != null) {
            Collections.sort(goalDetails, (o1, o2) -> (int) (o2.getCreatedAt() - o1.getCreatedAt()));
        }
        this.goalDetails = goalDetails;
    }

    @NonNull
    @Override
    public GoalsAdapter.GoalDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout goalDetailLayout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_goal_details, parent, false);
        TextView goalName = goalDetailLayout.findViewById(R.id.goal_name);
        TextView goalAmount = goalDetailLayout.findViewById(R.id.goal_amount);
        TextView goalPercentage = goalDetailLayout.findViewById(R.id.goal_percentage);
        ProgressBar goalProgress = goalDetailLayout.findViewById(R.id.goal_progress);
        INR_SYMBOL = parent.getContext().getString(R.string.inr);

        return new GoalsAdapter.GoalDetailViewHolder(goalDetailLayout, goalName, goalAmount, goalPercentage, goalProgress);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalsAdapter.GoalDetailViewHolder holder, int position) {
        holder.goalName.setText(goalDetails.get(position).getName());
        String goalAmountStr = INR_SYMBOL + " " +
                goalDetails.get(position).getCollectedAmount() + "/" + goalDetails.get(position).getTotalAmount();
        holder.goalAmount.setText(goalAmountStr);
        double goalProgress = ParsingUtil.getProgress(goalDetails.get(position).getCollectedAmount(), goalDetails.get(position).getTotalAmount());
        String percentageText = goalProgress + " %";
        holder.goalPercentage.setText(percentageText);
        holder.goalProgress.setProgress((int) (goalProgress));
    }

    @Override
    public int getItemCount() {
        if (goalDetails == null) {
            return 0;
        } else {
            return goalDetails.size();
        }
    }

    static class GoalDetailViewHolder extends RecyclerView.ViewHolder {

        private TextView goalName;
        private TextView goalAmount;
        private TextView goalPercentage;
        private ProgressBar goalProgress;

        GoalDetailViewHolder(@NonNull View itemView, TextView goalName, TextView goalAmount, TextView goalPercentage, ProgressBar goalProgress) {
            super(itemView);
            this.goalName = goalName;
            this.goalAmount = goalAmount;
            this.goalPercentage = goalPercentage;
            this.goalProgress = goalProgress;
        }
    }
}
