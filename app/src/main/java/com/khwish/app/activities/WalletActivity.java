package com.khwish.app.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.khwish.app.R;
import com.khwish.app.adapters.wallet.WalletActivitiesAdapter;
import com.khwish.app.constants.AuthConstants;
import com.khwish.app.fragments.wallet.WithdrawalFormFragment;
import com.khwish.app.fragments.wallet.WithdrawalResultFragment;
import com.khwish.app.requests.WithdrawalRequest;
import com.khwish.app.responses.BaseResponse;
import com.khwish.app.responses.WalletActivitiesResponse;
import com.khwish.app.retrofit.ServerApi;
import com.khwish.app.utils.ParsingUtil;
import com.khwish.app.utils.SnackbarUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private WalletActivitiesResponse mWalletDetails;
    private ImageButton mBackButton;
    private ImageButton mCloseButton;
    private TextView mWalletBalanceText;
    private ProgressBar mLoadingProgress;
    private TextView mErrorText;
    private RecyclerView mWalletActivitiesRecycler;
    private Button mWithdrawButton;
    private WalletActivitiesAdapter mWalletActivitiesAdapter;
    private FrameLayout mWithdrawalFragmentsContainer;
    private WithdrawalFormFragment mWithdrawalFormFragment;
    private WithdrawalResultFragment mWithdrawalResultFragment;
    private FragmentManager mFragmentManager;
    private boolean mInTransaction = false;
    private View mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        mCoordinatorLayout = findViewById(R.id.coordinator);

        mWalletBalanceText = findViewById(R.id.wallet_balance_value);
        mLoadingProgress = findViewById(R.id.loading_progress);
        mErrorText = findViewById(R.id.error_text);
        mWalletActivitiesRecycler = findViewById(R.id.wallet_activities_recycler);
        mBackButton = findViewById(R.id.back_button);
        mWithdrawButton = findViewById(R.id.withdraw_button);
        mCloseButton = findViewById(R.id.close_button);

        fetchWalletActivities();

        mFragmentManager = getSupportFragmentManager();
        mWithdrawalFragmentsContainer = findViewById(R.id.withdrawal_fragments_container);
        if (mWithdrawalFragmentsContainer != null) {
            if (savedInstanceState != null) {
                return;
            }
        }

        mCloseButton.setOnClickListener(v -> resetWithdrawalFragments());
        mBackButton.setOnClickListener(v -> onNavigateUp());
        mWithdrawButton.setOnClickListener(v -> {

            if (AuthConstants.ON_BOARD_BANK_INCOMPLETE) {
                goToWithdrawResult(getString(R.string.bank_information_not_present));
            } else {
                goToWithdrawForm();
            }
        });

        if (mWalletDetails != null && (mWalletDetails.getActivities() == null || mWalletDetails.getActivities().isEmpty())) {
            mLoadingProgress.setVisibility(View.GONE);
            mErrorText.setVisibility(View.VISIBLE);
            mErrorText.setText(R.string.no_wallet_activities);
            mWalletActivitiesRecycler.setVisibility(View.GONE);
        } else if (mWalletDetails != null && mWalletDetails.getActivities() != null && !mWalletDetails.getActivities().isEmpty()) {
            mWalletActivitiesAdapter = new WalletActivitiesAdapter(mWalletDetails);
            mWalletActivitiesRecycler.setLayoutManager(new LinearLayoutManager(WalletActivity.this));
            mWalletActivitiesRecycler.setAdapter(mWalletActivitiesAdapter);

            String walletAmount = getString(R.string.inr) + " " + mWalletDetails.getWalletAmount();
            mWalletBalanceText.setText(walletAmount);

            mLoadingProgress.setVisibility(View.GONE);
            mErrorText.setVisibility(View.GONE);
            mWalletActivitiesRecycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mInTransaction) {
            super.onBackPressed();
        }
    }

    public void fetchWalletActivities() {
        ServerApi.getWalletActivities().enqueue(new Callback<WalletActivitiesResponse>() {
            @Override
            public void onResponse(@NonNull Call<WalletActivitiesResponse> call, @NonNull Response<WalletActivitiesResponse> response) {
                mLoadingProgress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    WalletActivitiesResponse resp = response.body();
                    if (resp.isSuccess()) {
                        mWalletDetails = resp;
                        mWithdrawButton.setEnabled(true);
                        mWithdrawButton.setClickable(true);

                        if (mWalletDetails.getActivities() != null && !mWalletDetails.getActivities().isEmpty()) {
                            if (mWalletActivitiesAdapter == null) {
                                mWalletActivitiesAdapter = new WalletActivitiesAdapter(mWalletDetails);
                                mWalletActivitiesRecycler.setLayoutManager(new LinearLayoutManager(WalletActivity.this));
                                mWalletActivitiesRecycler.setAdapter(mWalletActivitiesAdapter);
                            } else {
                                mWalletActivitiesAdapter.updateActivities(mWalletDetails);
                            }
                            mErrorText.setVisibility(View.GONE);
                            mWalletActivitiesRecycler.setVisibility(View.VISIBLE);
                        } else {
                            mErrorText.setText(R.string.no_wallet_activities);
                            mErrorText.setVisibility(View.VISIBLE);
                            mWalletActivitiesRecycler.setVisibility(View.GONE);
                        }
                        String walletAmount = getString(R.string.inr) + " " + mWalletDetails.getWalletAmount();
                        mWalletBalanceText.setText(walletAmount);
                    } else {
                        mErrorText.setVisibility(View.VISIBLE);
                        mWalletActivitiesRecycler.setVisibility(View.GONE);
                    }
                } else {
                    mErrorText.setVisibility(View.VISIBLE);
                    mWalletActivitiesRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WalletActivitiesResponse> call, @NonNull Throwable t) {
                Log.e(TAG, t.toString(), t);
                mLoadingProgress.setVisibility(View.GONE);
                mErrorText.setVisibility(View.VISIBLE);
                mWalletActivitiesRecycler.setVisibility(View.GONE);
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.check_internet), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.retry), v -> {
                            fetchWalletActivities();
                        });
                snackbar.show();
            }
        });
    }

    public void makeWithdrawalRequest(WithdrawalRequest withdrawalRequest) {
        if (mWithdrawalFormFragment != null) {
            mWithdrawalFormFragment.enableWithdrawalProgress();
        }

        mCloseButton.setEnabled(false);
        mCloseButton.setClickable(false);
        mBackButton.setEnabled(false);
        mBackButton.setClickable(false);
        mInTransaction = true;

        ServerApi.withdraw(withdrawalRequest).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse resp = response.body();
                    if (resp.isSuccess()) {

                        if (mWithdrawalFormFragment != null) {
                            mWithdrawalFormFragment.disableWithdrawalProgress();
                        }

                        goToWithdrawResult(getString(R.string.withdrawal_request_submitted));
                        fetchWalletActivities();
                        SnackbarUtil.makeLongSnack(mCoordinatorLayout, resp.getMessage());

                        mCloseButton.setEnabled(true);
                        mCloseButton.setClickable(true);
                        mBackButton.setEnabled(true);
                        mBackButton.setClickable(true);
                        mInTransaction = false;
                    } else {
                        String resultString;
                        if (resp.getMessage() != null) {
                            resultString = resp.getMessage();
                        } else {
                            resultString = getString(R.string.oops);
                        }
                        goToWithdrawResult(resultString);
                    }
                } else {
                    String resultString;

                    if (response.errorBody() != null) {
                        try {
                            BaseResponse baseResponse = ParsingUtil.fromJson(response.errorBody().string(), BaseResponse.class);
                            if (baseResponse.getMessage() != null) {
                                resultString = baseResponse.getMessage();
                            } else {
                                resultString = getString(R.string.oops);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString(), e);
                            resultString = getString(R.string.oops);
                        }
                    } else {
                        resultString = getString(R.string.oops);
                    }

                    goToWithdrawResult(resultString);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                SnackbarUtil.makeLongClickableSnack(mCoordinatorLayout, getString(R.string.check_internet), getString(R.string.retry), v -> makeWithdrawalRequest(withdrawalRequest));
                resetWithdrawalFragments();
                Log.e(TAG, t.toString(), t);
            }
        });
    }

    public void goToWithdrawForm() {
        mWithdrawButton.setVisibility(View.GONE);
        mWithdrawalFragmentsContainer.setVisibility(View.VISIBLE);
        mCloseButton.setVisibility(View.VISIBLE);

        if (mWithdrawalFormFragment == null) {
            mWithdrawalFormFragment = new WithdrawalFormFragment();
        }

        Bundle args = new Bundle();
        args.putDouble(WithdrawalFormFragment.WALLET_AMOUNT, mWalletDetails.getWalletAmount());
        mWithdrawalFormFragment.setArguments(args);

        mFragmentManager.beginTransaction().add(R.id.withdrawal_fragments_container, mWithdrawalFormFragment).commit();
    }

    public void goToWithdrawResult(String resultText) {
        mWithdrawButton.setVisibility(View.GONE);
        mWithdrawalFragmentsContainer.setVisibility(View.VISIBLE);

        if (mWithdrawalResultFragment == null) {
            mWithdrawalResultFragment = new WithdrawalResultFragment(resultText);
        }
        mCloseButton.setVisibility(View.GONE);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.withdrawal_fragments_container, mWithdrawalResultFragment);
        transaction.commit();
    }

    public void resetWithdrawalFragments() {
        mWithdrawalFragmentsContainer.setVisibility(View.GONE);
        mWithdrawButton.setVisibility(View.VISIBLE);
        mCloseButton.setVisibility(View.GONE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mWithdrawalFormFragment != null) {
            transaction.remove(mWithdrawalFormFragment);
        }
        if (mWithdrawalResultFragment != null) {
            transaction.remove(mWithdrawalResultFragment);
        }
        transaction.commit();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        mWithdrawalFormFragment = null;
        mWithdrawalResultFragment = null;

        mBackButton.setEnabled(true);
        mBackButton.setClickable(true);
        mInTransaction = false;
    }

    public View getCoordinatorLayout() {
        return mCoordinatorLayout;
    }
}