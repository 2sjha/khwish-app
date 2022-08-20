package com.khwish.app.adapters.eventdetails;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khwish.app.R;
import com.khwish.app.responses.EventContributorsResponse;

public class ContributorsListAdapter extends RecyclerView.Adapter<ContributorsListAdapter.ContributionListItemViewHolder> {

    private EventContributorsResponse mContributors;
    private String INR_SYMBOL;

    public ContributorsListAdapter(EventContributorsResponse contributors) {
        if (contributors != null) {
            contributors.sort();
        }
        this.mContributors = contributors;
    }

    @NonNull
    @Override
    public ContributionListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout contributorListItemLayout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_contibution, parent, false);
        TextView contributorName = contributorListItemLayout.findViewById(R.id.contributor_name);
        TextView contributionAmount = contributorListItemLayout.findViewById(R.id.contribution_amount);
        TextView goalName = contributorListItemLayout.findViewById(R.id.goal_name);
        INR_SYMBOL = parent.getContext().getString(R.string.inr);
        return new ContributionListItemViewHolder(contributorListItemLayout, contributorName, contributionAmount, goalName);
    }

    @Override
    public void onBindViewHolder(@NonNull ContributionListItemViewHolder holder, int position) {
        EventContributorsResponse.ContributionResponse contributionDetails = mContributors.getContributions().get(position);

        holder.contributorName.setText(contributionDetails.getContributorName());
        String contributionAmountStr = INR_SYMBOL + " " + contributionDetails.getContributionAmount();
        holder.contributionAmount.setText(contributionAmountStr);
        holder.goalName.setText(contributionDetails.getGoalName());
    }

    @Override
    public int getItemCount() {
        if (mContributors != null && mContributors.getContributions() != null) {
            return mContributors.getContributions().size();
        } else {
            return 0;
        }
    }

    public void updateContributors(EventContributorsResponse updatedContributors) {
        mContributors = updatedContributors;
        this.notifyDataSetChanged();
    }

    static class ContributionListItemViewHolder extends RecyclerView.ViewHolder {
        private TextView contributorName;
        private TextView contributionAmount;
        private TextView goalName;


        ContributionListItemViewHolder(@NonNull RelativeLayout contributorListItemLayout,
                                       TextView contributorName, TextView contributionAmount, TextView goalName) {
            super(contributorListItemLayout);
            this.contributorName = contributorName;
            this.contributionAmount = contributionAmount;
            this.goalName = goalName;
        }
    }
}
