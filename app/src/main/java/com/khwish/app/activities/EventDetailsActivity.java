package com.khwish.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.khwish.app.R;
import com.khwish.app.adapters.eventdetails.GoalsContributorsPagerAdapter;
import com.khwish.app.constants.Constants;
import com.khwish.app.responses.EventContributorsResponse;
import com.khwish.app.responses.EventDetailsResponse;
import com.khwish.app.responses.GoalDetailsResponse;
import com.khwish.app.retrofit.ServerApi;
import com.khwish.app.firebase.AnalyticsService;
import com.khwish.app.utils.DateTimeUtil;
import com.khwish.app.utils.ParsingUtil;
import com.khwish.app.utils.SnackbarUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsActivity extends AppCompatActivity {

    private static final int EVENT_EDIT_REQUEST_CODE = 254;
    private final String TAG = this.getClass().getSimpleName();
    private EventDetailsResponse mEventDetails;
    private EventContributorsResponse mEventContributors;
    private TextView mEventTitle;
    private TextView mEventDate;
    private TextView mEventDesc;
    private GoalsContributorsPagerAdapter mGoalsContributorsPagerAdapter;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        mCoordinatorLayout = findViewById(R.id.coordinator);

        AnalyticsService.log(getApplicationContext(),"VIEWED_EVENT_DETAILS","EventCardsAdapter");

        Intent intent = getIntent();
        if (intent == null) {
            SnackbarUtil.makeLongSnack(mCoordinatorLayout, getString(R.string.oops));
            finish();
            return;
        }

        mEventDetails = (EventDetailsResponse) intent.getSerializableExtra(Constants.EVENT_DETAILS_INTENT_KEY);

        mEventTitle = findViewById(R.id.event_title_header);
        mEventDate = findViewById(R.id.event_date_text);
        mEventDesc = findViewById(R.id.event_desc_text);
        TextView eventAmount = findViewById(R.id.event_amount_text);
        TextView eventPercentage = findViewById(R.id.event_percentage_text);
        ProgressBar eventProgress = findViewById(R.id.event_progress);
        TextView editTextButton = findViewById(R.id.edit_text_button);
        ImageButton backButton = findViewById(R.id.back_button);
        ViewPager2 goalsContributorsPager = findViewById(R.id.goals_contributors_pager);
        mGoalsContributorsPagerAdapter = new GoalsContributorsPagerAdapter(EventDetailsActivity.this);
        goalsContributorsPager.setAdapter(mGoalsContributorsPagerAdapter);


        editTextButton.setOnClickListener(v -> {
            openEditEventIntent(mEventDetails);
        });

        TabLayout tabLayout = findViewById(R.id.goals_contributors_tabs);
        new TabLayoutMediator(tabLayout, goalsContributorsPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.contributors);
            } else if (position == 1) {
                tab.setText(R.string.goals);
            }
        }
        ).attach();

        mEventTitle.setText(mEventDetails.getName());
        String eventDateStr = DateTimeUtil.getReadableDateFromEpoch(EventDetailsActivity.this, mEventDetails.getEventDate());
        mEventDate.setText(eventDateStr);
        mEventDesc.setText(mEventDetails.getDescription());
        String eventAmountStr = getString(R.string.inr) + " " + mEventDetails.getCollectedAmount() + "/" + mEventDetails.getTotalAmount();
        eventAmount.setText(eventAmountStr);
        double progress = ParsingUtil.getProgress(mEventDetails.getCollectedAmount(), mEventDetails.getTotalAmount());
        eventProgress.setProgress((int) (progress));
        String percentageStr = progress + " %";
        eventPercentage.setText(percentageStr);

        backButton.setOnClickListener(v -> onNavigateUp());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchEventContributors();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            fetchEventDetails();
            mGoalsContributorsPagerAdapter.notifyItemChanged(0);
        }
    }

    private void openEditEventIntent(EventDetailsResponse event) {
        AnalyticsService.log(getApplicationContext(), "EDIT_EVENT", "EventDetailsActivity");
        Intent intent = new Intent(EventDetailsActivity.this, AddModifyEventActivity.class);
        intent.putExtra(Constants.EVENT_DETAILS_INTENT_KEY, event);
        intent.putExtra(Constants.EVENT_MODIFY_INTENT_KEY, true);
        startActivityForResult(intent, EVENT_EDIT_REQUEST_CODE);
    }

    public ArrayList<GoalDetailsResponse> getGoals() {
        if (mEventDetails != null) {
            mEventDetails.sort();
            return mEventDetails.getGoalDetails();
        } else return null;
    }

    public void fetchEventDetails() {
        ServerApi.getEventDetails(mEventDetails.getId()).enqueue(new Callback<EventDetailsResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventDetailsResponse> call, @NonNull Response<EventDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EventDetailsResponse resp = response.body();
                    if (resp.isSuccess()) {
                        mEventDetails = resp;
                        mEventTitle.setText(mEventDetails.getName());
                        mEventDesc.setText(mEventDetails.getDescription());
                        String eventDate = DateTimeUtil.getReadableDateFromEpoch(EventDetailsActivity.this, mEventDetails.getEventDate());
                        mEventDate.setText(eventDate);
                        if (mGoalsContributorsPagerAdapter.getGoalsFragment() != null) {
                            mGoalsContributorsPagerAdapter.getGoalsFragment().updateGoals();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventDetailsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, t.toString(), t);
                SnackbarUtil.makeLongClickableSnack(mCoordinatorLayout, getString(R.string.check_internet), getString(R.string.retry), v -> fetchEventDetails());
            }
        });
    }

    public void fetchEventContributors() {
        ServerApi.getEventContributors(mEventDetails.getId()).enqueue(new Callback<EventContributorsResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventContributorsResponse> call, @NonNull Response<EventContributorsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EventContributorsResponse resp = response.body();
                    if (resp.isSuccess()) {
                        mEventContributors = resp;
                    }
                }
                if (mGoalsContributorsPagerAdapter.getContributorsFragment() != null) {
                    mGoalsContributorsPagerAdapter.getContributorsFragment().updateContributors();
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventContributorsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, t.toString(), t);
                mGoalsContributorsPagerAdapter.getContributorsFragment().updateContributors();
                SnackbarUtil.makeLongClickableSnack(mCoordinatorLayout, getString(R.string.check_internet), getString(R.string.retry), v -> fetchEventContributors());
            }
        });
    }

    public EventDetailsResponse getEventDetails() {
        return mEventDetails;
    }

    public EventContributorsResponse getContributors() {
        return mEventContributors;
    }
}
