package com.khwish.app.adapters.homepage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.khwish.app.R;
import com.khwish.app.activities.AddModifyGoalActivity;
import com.khwish.app.activities.EventDetailsActivity;
import com.khwish.app.constants.Constants;
import com.khwish.app.responses.EventDetailsResponse;
import com.khwish.app.firebase.AnalyticsService;
import com.khwish.app.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.Collections;

public class EventCardsAdapter extends RecyclerView.Adapter<EventCardsAdapter.EventCardViewHolder> {

    private ArrayList<EventDetailsResponse> openEvents;
    private boolean isShowingMore = false;

    private Context mContext;

    public EventCardsAdapter(ArrayList<EventDetailsResponse> openEvents,Context context) {
        if (openEvents != null) {
            Collections.sort(openEvents, (o1, o2) -> (int) (o2.getCreatedAt() - o1.getCreatedAt()));
        }
        this.openEvents = openEvents;
        mContext=context;
    }

    @NonNull
    @Override
    public EventCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialCardView eventCard = (MaterialCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_event, parent, false);
        TextView eventName = eventCard.findViewById(R.id.event_name);
        TextView eventDate = eventCard.findViewById(R.id.event_date);
        TextView collectedAmount = eventCard.findViewById(R.id.collected_amount);
        TextView goalCount = eventCard.findViewById(R.id.goal_count);
        RelativeLayout inviteLayout = eventCard.findViewById(R.id.invite_layout);
        RelativeLayout eventDetailsLayout = eventCard.findViewById(R.id.event_details_layout);
        RelativeLayout goalCountCollectedAmountLayout = eventCard.findViewById(R.id.goal_count_collected_amount_layout);
        ImageButton showMoreChevronButton = eventCard.findViewById(R.id.pull_down_chevron);
        RelativeLayout goalsLayout = eventCard.findViewById(R.id.goals_layout);
        goalsLayout.setVisibility(View.GONE);
        RecyclerView goalsRecycler = eventCard.findViewById(R.id.goals_recycler);
        RelativeLayout addGoalLayout = eventCard.findViewById(R.id.add_goal_layout);

        return new EventCardViewHolder(eventCard, eventName, eventDate, collectedAmount, goalCount,
                inviteLayout, eventDetailsLayout, goalCountCollectedAmountLayout, showMoreChevronButton, goalsLayout, goalsRecycler,
                addGoalLayout, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull EventCardViewHolder holder, int position) {

        EventDetailsResponse openEvent = openEvents.get(position);
        holder.eventName.setText(openEvent.getName());

        String eventDateStr = DateTimeUtil.getReadableDateFromEpoch(holder.context, openEvent.getEventDate());
        holder.eventDate.setText(eventDateStr);

        String goalCountText = openEvent.getGoalDetails().size() + " " + holder.context.getString(R.string.goals);
        holder.goalCount.setText(goalCountText);

        String amountStr = holder.context.getString(R.string.inr) + " " + openEvent.getCollectedAmount();
        holder.collectedAmount.setText(amountStr);

        holder.inviteLayout.setOnClickListener(v -> {
            openShareIntent(openEvent, holder.context);
        });
        holder.eventDetailsLayout.setOnClickListener(v -> openEventDetailsIntent(openEvent, holder.context));

        View.OnClickListener expandClickListener = v -> {
            if (isShowingMore) {
                holder.showMoreChevron.setBackground(holder.context.getDrawable(R.drawable.ic_expand_more));
                isShowingMore = false;
                holder.goalsLayout.setVisibility(View.GONE);
            } else {
                isShowingMore = true;
                holder.showMoreChevron.setBackground(holder.context.getDrawable(R.drawable.ic_expand_less));
                holder.goalsLayout.setVisibility(View.VISIBLE);
            }
        };

        holder.goalCountCollectedAmountLayout.setOnClickListener(expandClickListener);
        holder.showMoreChevron.setOnClickListener(expandClickListener);
        holder.addGoalLayout.setOnClickListener(v -> openAddGoalIntent(openEvent, holder.context));

        GoalsAdapter goalsAdapter = new GoalsAdapter(openEvent.getGoalDetails());
        holder.goalsRecycler.setLayoutManager(new LinearLayoutManager(holder.context));
        holder.goalsRecycler.setAdapter(goalsAdapter);
    }

    @Override
    public int getItemCount() {
        if (openEvents != null) {
            return openEvents.size();
        } else {
            return 0;
        }
    }

    private void openShareIntent(EventDetailsResponse eventDetails, Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        if (eventDetails.getInviteMessage() != null) {
            sendIntent.putExtra(Intent.EXTRA_TEXT, eventDetails.getInviteMessage());
        } else {
            sendIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.invite_message)
                    + " " + eventDetails.getName() + ". " + context.getString(R.string.checkout_my_event));
        }
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);

        AnalyticsService.log(mContext,"SHARE_EVENT","EventCardsAdapter");
    }

    private void openEventDetailsIntent(EventDetailsResponse event, Context context) {
        Intent sendIntent = new Intent(context, EventDetailsActivity.class);
        sendIntent.putExtra(Constants.EVENT_DETAILS_INTENT_KEY, event);
        context.startActivity(sendIntent);
    }

    private void openAddGoalIntent(EventDetailsResponse event, Context context) {
        Intent sendIntent = new Intent(context, AddModifyGoalActivity.class);
        sendIntent.putExtra(Constants.EVENT_DETAILS_INTENT_KEY, event);
        context.startActivity(sendIntent);
    }

    static class EventCardViewHolder extends RecyclerView.ViewHolder {

        private TextView eventName;
        private TextView eventDate;
        private TextView collectedAmount;
        private TextView goalCount;
        private RelativeLayout inviteLayout;
        private RelativeLayout eventDetailsLayout;
        private RelativeLayout goalCountCollectedAmountLayout;
        private ImageButton showMoreChevron;
        RelativeLayout goalsLayout;
        RecyclerView goalsRecycler;
        RelativeLayout addGoalLayout;
        private Context context;

        EventCardViewHolder(@NonNull MaterialCardView eventCard, TextView eventName, TextView eventDate,
                            TextView collectedAmount, TextView goalCount, RelativeLayout inviteLayout,
                            RelativeLayout eventDetailsLayout, RelativeLayout goalCountCollectedAmountLayout,
                            ImageButton showMoreChevron, RelativeLayout goalsLayout,
                            RecyclerView goalsRecycler, RelativeLayout addGoalLayout, Context context) {
            super(eventCard);
            this.eventName = eventName;
            this.eventDate = eventDate;
            this.collectedAmount = collectedAmount;
            this.goalCount = goalCount;
            this.inviteLayout = inviteLayout;
            this.eventDetailsLayout = eventDetailsLayout;
            this.goalCountCollectedAmountLayout = goalCountCollectedAmountLayout;
            this.showMoreChevron = showMoreChevron;
            this.goalsLayout = goalsLayout;
            this.goalsRecycler = goalsRecycler;
            this.addGoalLayout = addGoalLayout;
            this.context = context;
        }
    }
}
