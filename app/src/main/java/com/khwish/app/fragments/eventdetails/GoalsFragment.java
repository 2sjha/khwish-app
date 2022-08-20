package com.khwish.app.fragments.eventdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khwish.app.R;
import com.khwish.app.activities.EventDetailsActivity;
import com.khwish.app.adapters.eventdetails.GoalCardsAdapter;
import com.khwish.app.responses.GoalDetailsResponse;

import java.util.ArrayList;

public class GoalsFragment extends Fragment {

    private EventDetailsActivity mParentActivity;
    private GoalCardsAdapter mGoalsAdapter;

    public static GoalsFragment newInstance() {
        return new GoalsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_event_details_goals, container, true);
        if (fragment != null && getActivity() != null && getContext() != null) {
            RecyclerView goalsRecycler = fragment.findViewById(R.id.goals_recycler);
            mParentActivity = (EventDetailsActivity) getActivity();
            ArrayList<GoalDetailsResponse> resp = mParentActivity.getGoals();
            if (resp != null && !resp.isEmpty()) {
                mGoalsAdapter = new GoalCardsAdapter(mParentActivity.getEventDetails(), resp);
                goalsRecycler.setLayoutManager(new LinearLayoutManager(mParentActivity));
                goalsRecycler.setAdapter(mGoalsAdapter);
            } else {
                TextView noGoalsText = fragment.findViewById(R.id.no_goals_text);
                noGoalsText.setVisibility(View.VISIBLE);
            }
            return fragment;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mParentActivity.fetchEventDetails();
    }

    public void updateGoals() {
        ArrayList<GoalDetailsResponse> goals = mParentActivity.getGoals();
        if (goals != null && mGoalsAdapter != null) {
            mGoalsAdapter.updateGoalCards(goals);
        }
    }
}