package com.khwish.app.adapters.eventdetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khwish.app.R;
import com.khwish.app.activities.AddModifyGoalActivity;
import com.khwish.app.constants.Constants;
import com.khwish.app.responses.EventDetailsResponse;
import com.khwish.app.responses.GoalDetailsResponse;

import java.util.ArrayList;

public class GoalCardsAdapter extends RecyclerView.Adapter<GoalCardsAdapter.GoalCardViewHolder> {

    private EventDetailsResponse mEvent;
    private ArrayList<GoalDetailsResponse> mGoals;
    private Context mContext;

    public GoalCardsAdapter(EventDetailsResponse event, ArrayList<GoalDetailsResponse> goals) {
        this.mEvent = event;
        this.mGoals = goals;
    }

    @NonNull
    @Override
    public GoalCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout goalCardLayout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_goal, parent, false);
        mContext = parent.getContext();
        TextView goalName = goalCardLayout.findViewById(R.id.goal_name);
        TextView goalDescription = goalCardLayout.findViewById(R.id.goal_desc);
        TextView goalAmount = goalCardLayout.findViewById(R.id.goal_amount);
        ImageButton editButton = goalCardLayout.findViewById(R.id.edit_icon);
        return new GoalCardViewHolder(goalCardLayout, goalName, goalDescription, goalAmount, editButton, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull GoalCardViewHolder holder, int position) {
        GoalDetailsResponse goalDetails = mGoals.get(position);

        holder.goalName.setText(goalDetails.getName());
        holder.goalDescription.setText(goalDetails.getDescription());
        String goalAmountStr;
        if (goalDetails.getTotalAmount() == 0) {
            goalAmountStr = holder.context.getString(R.string.no_goal_amount);
            holder.goalAmount.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.context.getResources().getDimension(R.dimen.text_h6));
        } else {
            goalAmountStr = holder.context.getString(R.string.inr) + " " + goalDetails.getTotalAmount();
        }
        holder.goalAmount.setText(goalAmountStr);
        holder.editGoalButton.setOnClickListener(v -> openEditGoalIntent(goalDetails, mEvent, holder.context));
    }

    @Override
    public int getItemCount() {
        if (mGoals != null) {
            return mGoals.size();
        } else {
            return 0;
        }
    }

    private void openEditGoalIntent(GoalDetailsResponse goal, EventDetailsResponse event, Context context) {
        Intent intent = new Intent(context, AddModifyGoalActivity.class);
        intent.putExtra(Constants.EVENT_DETAILS_INTENT_KEY, event);
        intent.putExtra(Constants.GOAL_DETAILS_INTENT_KEY, goal);
        intent.putExtra(Constants.GOAL_MODIFY_INTENT_KEY, true);
        ((Activity) mContext).startActivityForResult(intent, Constants.MODIFY_GOAL_INTENT_CODE);
    }

    public void updateGoalCards(ArrayList<GoalDetailsResponse> updatedGoals) {
        mGoals.clear();
        mGoals.addAll(updatedGoals);
        this.notifyDataSetChanged();
    }

    static class GoalCardViewHolder extends RecyclerView.ViewHolder {
        private TextView goalName;
        private TextView goalDescription;
        private TextView goalAmount;
        private ImageButton editGoalButton;
        private Context context;

        GoalCardViewHolder(@NonNull RelativeLayout goalCardLayout, TextView goalName, TextView goalDescription,
                           TextView goalAmount, ImageButton editGoalButton, Context context) {
            super(goalCardLayout);
            this.goalName = goalName;
            this.goalDescription = goalDescription;
            this.goalAmount = goalAmount;
            this.editGoalButton = editGoalButton;
            this.context = context;
        }
    }
}
