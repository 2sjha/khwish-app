package com.khwish.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.khwish.app.R;
import com.khwish.app.constants.AuthConstants;
import com.khwish.app.constants.Constants;
import com.khwish.app.fragments.common.ErrorFragment;
import com.khwish.app.fragments.common.LoginSuccessFragment;
import com.khwish.app.fragments.onboarding.OnBoardUserBankDetailsFragment;
import com.khwish.app.fragments.onboarding.OnBoardUserPersonalDetailsFragment;
import com.khwish.app.requests.OnBoardUserRequest;
import com.khwish.app.responses.BaseResponse;
import com.khwish.app.responses.UserProfileResponse;
import com.khwish.app.retrofit.ServerApi;
import com.khwish.app.utils.DialogUtil;
import com.khwish.app.utils.SnackbarUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnBoardUserActivity extends AppCompatActivity {

    private final static String TAG = OnBoardUserActivity.class.getSimpleName();
    private FragmentManager mFragmentManager;
    private OnBoardUserPersonalDetailsFragment mOnBoardUserPersonalDetailsFragment;
    private OnBoardUserBankDetailsFragment mOnBoardUserBankDetailsFragment;
    private ImageButton mBackButton;
    private String mFullName;
    private String mEmail;
    private String mPhoneNumber;
    private Long mDobEpoch;
    private OnBoardUserRequest.BankDetails mUserBankDetails;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard_user);
        mFragmentManager = getSupportFragmentManager();

        mBackButton = findViewById(R.id.back_button);
        mBackButton.setOnClickListener(v -> onBackPressed());

        mCoordinatorLayout = findViewById(R.id.coordinator);

        FrameLayout loginFragmentsContainer = findViewById(R.id.onboarding_fragments_container);
        if (loginFragmentsContainer != null) {
            if (savedInstanceState != null) {
                return;
            }
        }

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundleInfo = intent.getExtras();
            if (bundleInfo != null) {
                boolean newUserOnBoardingFlow = bundleInfo.getBoolean(Constants.ON_BOARD_NEW_USER_INTENT_KEY, false);
                boolean editPersonalDetailsFlow = bundleInfo.getBoolean(Constants.PROFILE_MODIFY_PERSONAL_DETAILS_INTENT_KEY, false);
                boolean editBankDetailsFlow = bundleInfo.getBoolean(Constants.PROFILE_MODIFY_BANK_DETAILS_INTENT_KEY, false);

                // Normal OnBoarding Flow being called from Login Activity, for new users
                if (newUserOnBoardingFlow) {


                    String userRegisteredVia = bundleInfo.getString(Constants.NEW_USER_ON_BOARD_USER_REGISTERED_VIA_INTENT_KEY, null);
                    String userName = bundleInfo.getString(Constants.NEW_USER_ON_BOARD_USER_NAME_INTENT_KEY, null);
                    String userEmail = bundleInfo.getString(Constants.NEW_USER_ON_BOARD_USER_EMAIL_INTENT_KEY, null);
                    String userPhoneNumber = bundleInfo.getString(Constants.NEW_USER_ON_BOARD_USER_PHONE_NUMBER_INTENT_KEY, null);

                    mOnBoardUserPersonalDetailsFragment = new OnBoardUserPersonalDetailsFragment(true);
                    Bundle args = new Bundle();
                    args.putString(OnBoardUserPersonalDetailsFragment.USER_REGISTERED_VIA, userRegisteredVia);
                    args.putString(OnBoardUserPersonalDetailsFragment.USER_NAME, userName);
                    args.putString(OnBoardUserPersonalDetailsFragment.USER_EMAIL, userEmail);
                    args.putString(OnBoardUserPersonalDetailsFragment.USER_PHONE_NUMBER, userPhoneNumber);
                    mOnBoardUserPersonalDetailsFragment.setArguments(args);

                    mFragmentManager.beginTransaction().add(R.id.onboarding_fragments_container, mOnBoardUserPersonalDetailsFragment).commit();
                } else if (editPersonalDetailsFlow || editBankDetailsFlow) {
                    UserProfileResponse profileDetails = (UserProfileResponse) intent.getSerializableExtra(Constants.PROFILE_DETAILS_INTENT_KEY);

                    if (profileDetails != null) {
                        if (editPersonalDetailsFlow && profileDetails.getUserDetails() != null) {
                            UserProfileResponse.UserDetails userDetails = profileDetails.getUserDetails();

                            mOnBoardUserPersonalDetailsFragment = new OnBoardUserPersonalDetailsFragment(false);
                            Bundle args = new Bundle();
                            args.putString(OnBoardUserPersonalDetailsFragment.USER_NAME, userDetails.getName());
                            args.putString(OnBoardUserPersonalDetailsFragment.USER_EMAIL, userDetails.getEmail());
                            args.putString(OnBoardUserPersonalDetailsFragment.USER_PHONE_NUMBER, userDetails.getPhoneNumber());
                            args.putString(OnBoardUserPersonalDetailsFragment.USER_REGISTERED_VIA, userDetails.getRegisteredVia());
                            if (userDetails.getDateOfBirth() != null) {
                                args.putLong(OnBoardUserPersonalDetailsFragment.USER_DOB, userDetails.getDateOfBirth());
                            }
                            mOnBoardUserPersonalDetailsFragment.setArguments(args);

                            mFragmentManager.beginTransaction().add(R.id.onboarding_fragments_container, mOnBoardUserPersonalDetailsFragment).commit();
                        } else if (editBankDetailsFlow) {
                            mOnBoardUserBankDetailsFragment = new OnBoardUserBankDetailsFragment(false);
                            Bundle args = new Bundle();
                            args.putSerializable(OnBoardUserBankDetailsFragment.USER_BANK_DETAILS, profileDetails.getBankDetails());
                            mOnBoardUserBankDetailsFragment.setArguments(args);

                            mFragmentManager.beginTransaction().add(R.id.onboarding_fragments_container, mOnBoardUserBankDetailsFragment).commit();
                        } else {
                            showErrorFragment();
                        }
                    } else {
                        showErrorFragment();
                    }
                } else {
                    showErrorFragment();
                }
            } else {
                mOnBoardUserPersonalDetailsFragment = new OnBoardUserPersonalDetailsFragment(true);
                mFragmentManager.beginTransaction().add(R.id.onboarding_fragments_container, mOnBoardUserPersonalDetailsFragment).commit();
            }
        }
    }

    public void onBoardUser() {
        DialogUtil.showLoadingDialog(OnBoardUserActivity.this);
        ServerApi.onBoardUser(new OnBoardUserRequest(AuthConstants.USER_ID, mFullName, mEmail,
                mPhoneNumber, mDobEpoch, mUserBankDetails)).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                DialogUtil.dismissLoadingDialog(OnBoardUserActivity.this);
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse resp = response.body();
                    if (resp.isSuccess()) {
                        showSuccessAndProceed();
                    } else {
                        SnackbarUtil.makeLongSnack(mCoordinatorLayout, getString(R.string.oops));
                    }
                } else {
                    SnackbarUtil.makeLongSnack(mCoordinatorLayout, getString(R.string.oops));
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                DialogUtil.dismissLoadingDialog(OnBoardUserActivity.this);
                Log.e(TAG, t.toString(), t);
                SnackbarUtil.makeLongClickableSnack(mCoordinatorLayout, getString(R.string.check_internet), getString(R.string.retry), v -> onBoardUser());
            }
        });
    }

    private void showSuccessAndProceed() {
        SharedPreferences.Editor spEditor = getSharedPreferences(AuthConstants.MAIN_SP_KEY, MODE_PRIVATE).edit();
        spEditor.putBoolean(AuthConstants.SP_IS_LOGGED_IN_KEY, true);
        spEditor.remove(AuthConstants.SP_ON_BOARD_INCOMPLETE_KEY);
        spEditor.remove(AuthConstants.SP_ON_BOARD_INCOMPLETE_REGISTERED_VIA_KEY);
        spEditor.remove(AuthConstants.SP_ON_BOARD_INCOMPLETE_USER_EMAIL_KEY);
        spEditor.remove(AuthConstants.SP_ON_BOARD_INCOMPLETE_USER_PHONE_NUMBER_KEY);
        if (getFullName() != null) {
            spEditor.putString(AuthConstants.SP_USER_NAME_KEY, getFullName());
        }
        if (mUserBankDetails != null) {
            AuthConstants.ON_BOARD_BANK_INCOMPLETE = false;
            spEditor.remove(AuthConstants.SP_ON_BOARD_BANK_DETAILS_INCOMPLETE_KEY);
        }
        spEditor.apply();

        if (getFullName() != null) {
            AuthConstants.USER_NAME = getFullName();
        }
        AuthConstants.IS_LOGGED_IN = true;

        Intent intent = new Intent(OnBoardUserActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        goToOnBoardingSuccess(intent);
    }


    public void gotToBankDetails() {
        if (mOnBoardUserBankDetailsFragment == null) {
            mOnBoardUserBankDetailsFragment = new OnBoardUserBankDetailsFragment(true);
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.onboarding_fragments_container, mOnBoardUserBankDetailsFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private void goToOnBoardingSuccess(Intent intent) {
        mBackButton.setEnabled(false);
        LoginSuccessFragment loginSuccessFragment = new LoginSuccessFragment(intent, false);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.onboarding_fragments_container, loginSuccessFragment);
        transaction.commit();
    }

    private void showErrorFragment() {
        ErrorFragment errorFragment = new ErrorFragment(false);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.onboarding_fragments_container, errorFragment);
        transaction.commit();
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        this.mFullName = fullName;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }

    public void setDobEpoch(Long dobEpoch) {
        this.mDobEpoch = dobEpoch;
    }

    public void setUserBankDetails(OnBoardUserRequest.BankDetails mUserBankDetails) {
        this.mUserBankDetails = mUserBankDetails;
    }

    public CoordinatorLayout getCoordinatorLayout() {
        return mCoordinatorLayout;
    }
}