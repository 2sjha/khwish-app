package com.khwish.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.khwish.app.R;
import com.khwish.app.adapters.homepage.EventsPagerAdapter;
import com.khwish.app.constants.AuthConstants;
import com.khwish.app.constants.Constants;
import com.khwish.app.responses.HomePageResponse;
import com.khwish.app.retrofit.ServerApi;
import com.khwish.app.utils.ParsingUtil;
import com.khwish.app.utils.SnackbarUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private HomePageResponse mHomePageResponse;
    private ViewPager2 mEventsPager;
    private EventsPagerAdapter mEventsPagerAdapter;
    private TextView mWalletAmountText;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        mCoordinatorLayout = findViewById(R.id.coordinator);
        mEventsPager = findViewById(R.id.events_pager);
        RelativeLayout addEventLayout = findViewById(R.id.add_event_layout);
        mWalletAmountText = findViewById(R.id.wallet_amount_text);
        TextView userNameText = findViewById(R.id.user_name_text);
        RelativeLayout withdrawalButtonLayout = findViewById(R.id.withdraw_layout);
        ImageButton profileButton = findViewById(R.id.profile_button);

        userNameText.setText(AuthConstants.USER_NAME);
        mEventsPagerAdapter = new EventsPagerAdapter(HomeActivity.this);
        mEventsPager.setAdapter(mEventsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.open_closed_events_tab);
        new TabLayoutMediator(tabLayout, mEventsPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.open_events);
            }
//            } else if (position == 1) {
//                tab.setText(R.string.closed_events);
//            }
        }
        ).attach();

        addEventLayout.setOnClickListener(v -> openAddNewEventIntent());

        View.OnClickListener withdrawalClickListener = v -> {
            Intent intent = new Intent(this, WalletActivity.class);
            startActivity(intent);
        };

        withdrawalButtonLayout.setOnClickListener(withdrawalClickListener);
        mWalletAmountText.setOnClickListener(withdrawalClickListener);

        profileButton.setOnClickListener(v -> {
            Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        });
        getHomePageDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHomePageDetails();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            getHomePageDetails();
        }
    }

    private void openAddNewEventIntent() {
        Intent intent = new Intent(HomeActivity.this, AddModifyEventActivity.class);
        startActivityForResult(intent, Constants.ADD_NEW_EVENT_INTENT_CODE);
    }

    private void getHomePageDetails() {
        Call<HomePageResponse> getHomepageCall = ServerApi.getHomePage();

        getHomepageCall.enqueue(new Callback<HomePageResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomePageResponse> call, @NonNull Response<HomePageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mHomePageResponse = response.body();
                    mEventsPagerAdapter.getOpenEventsFragment().stopLoading();

                    String walletAmountStr = getString(R.string.inr) + " " + mHomePageResponse.getWalletAmount();
                    mWalletAmountText.setText(walletAmountStr);
                } else if (response.errorBody() != null) {
                    mEventsPagerAdapter.getOpenEventsFragment().stopLoading();

                    try {
                        HomePageResponse errorResponse = ParsingUtil.fromJson(response.errorBody().string(), HomePageResponse.class);
                        SnackbarUtil.makeLongSnack(mCoordinatorLayout,
                                errorResponse != null ? errorResponse.getMessage() : getString(R.string.oops));
                    } catch (Exception e) {
                        Log.e(TAG, e.toString(), e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<HomePageResponse> call, @NonNull Throwable t) {
                Handler handler = new Handler();
                handler.postDelayed(() -> mEventsPagerAdapter.getOpenEventsFragment().stopLoading(), Constants.CHECK_INTERNET_DELAY);
                SnackbarUtil.makeLongClickableSnack(mCoordinatorLayout, getString(R.string.check_internet), getString(R.string.retry), v -> getHomePageDetails());
                Log.e(TAG, t.toString());
            }
        });
    }

    public HomePageResponse getHomePageResponse() {
        return mHomePageResponse;
    }
}
