package com.khwish.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.khwish.app.R;
import com.khwish.app.constants.AuthConstants;
import com.khwish.app.constants.Constants;
import com.khwish.app.fragments.common.ErrorFragment;
import com.khwish.app.fragments.common.LoginSuccessFragment;
import com.khwish.app.fragments.login.LoginEntryFragment;
import com.khwish.app.fragments.login.LoginOtpFragment;
import com.khwish.app.requests.LoginUserRequest;
import com.khwish.app.requests.UserNotificationTokenRequest;
import com.khwish.app.responses.LoginResponse;
import com.khwish.app.retrofit.ServerApi;
import com.khwish.app.firebase.NotificationService;
import com.khwish.app.firebase.AnalyticsService;
import com.khwish.app.utils.DialogUtil;
import com.khwish.app.utils.SnackbarUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 908;
    private String TAG = this.getClass().getSimpleName();
    private FirebaseAuth mFirebaseAuth;
    private FragmentManager mFragmentManager;
    private LoginEntryFragment mLoginEntryFragment;
    private LoginOtpFragment mLoginOtpFragment;
    private String mUserRegisteredVia;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFragmentManager = getSupportFragmentManager();

        mCoordinatorLayout = findViewById(R.id.coordinator);

        FrameLayout loginFragmentsContainer = findViewById(R.id.onboarding_fragments_container);
        if (loginFragmentsContainer != null) {
            if (savedInstanceState != null) {
                return;
            }

            mLoginEntryFragment = new LoginEntryFragment();
            mFragmentManager.beginTransaction().add(R.id.onboarding_fragments_container, mLoginEntryFragment).commit();
        }
    }

    public void verifyTokenFromServer(FirebaseUser user) {
        //Make HTTPS call and verify user-token and if everything goes ok, goto next screen
        user.getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String idToken = task.getResult().getToken();

                loginUserUsingToken(idToken, user.getDisplayName(), user.getPhoneNumber(), user.getEmail(), user.getPhotoUrl());
            } else {
                Log.e(TAG, "Couldn't fetch id token");
            }
        });
    }

    private void loginUserUsingToken(String idToken, String userName, String phoneNumber, String email, Uri userImgUri) {
        if (phoneNumber == null && email == null) {
            SnackbarUtil.makeLongSnack(mCoordinatorLayout, getString(R.string.oops));
            DialogUtil.dismissLoadingDialog(LoginActivity.this);
            return;
        }

        if (phoneNumber != null) {
            mUserRegisteredVia = Constants.REGISTERED_VIA_PHONE_NUMBER;
        } else {
            mUserRegisteredVia = Constants.REGISTERED_VIA_EMAIL;
        }

        final SharedPreferences.Editor sharedPrefsEditor = getSharedPreferences(AuthConstants.MAIN_SP_KEY, MODE_PRIVATE).edit();
        LoginUserRequest loginUserRequest = new LoginUserRequest(idToken, userName, phoneNumber, email);

        ServerApi.loginUser(loginUserRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, loginResponse.toString());

                    if (!loginResponse.isSuccess()) {
                        SnackbarUtil.makeLongSnack(mCoordinatorLayout, getString(R.string.oops));
                        DialogUtil.dismissLoadingDialog(LoginActivity.this);
                        goToErrorFragment();
                        return;
                    }

                    AuthConstants.IS_LOGGED_IN = true;
                    AuthConstants.USER_ID = loginResponse.getUserId();
                    if (loginResponse.getUserName() != null) {
                        AuthConstants.USER_NAME = loginResponse.getUserName();
                    } else {
                        AuthConstants.USER_NAME = userName;
                    }
                    AuthConstants.AUTH_TOKEN = loginResponse.getAuthToken();
                    AuthConstants.AUTH_TOKEN_EXPIRES_AT = loginResponse.getAuthTokenExpiresAt();

                    sharedPrefsEditor.putBoolean(AuthConstants.SP_IS_LOGGED_IN_KEY, AuthConstants.IS_LOGGED_IN);
                    sharedPrefsEditor.putString(AuthConstants.SP_USER_ID_KEY, AuthConstants.USER_ID.toString());
                    sharedPrefsEditor.putString(AuthConstants.SP_USER_NAME_KEY, AuthConstants.USER_NAME);
                    sharedPrefsEditor.putString(AuthConstants.SP_AUTH_TOKEN_KEY, AuthConstants.AUTH_TOKEN);
                    sharedPrefsEditor.putLong(AuthConstants.SP_AUTH_TOKEN_EXPIRES_AT_KEY, AuthConstants.AUTH_TOKEN_EXPIRES_AT);
                    sharedPrefsEditor.apply();

                    //Fetch and Send User Notification Token to Server
                    sendUserNotificationToServer();
                    DialogUtil.dismissLoadingDialog(LoginActivity.this);

                    Intent intent;
                    if (loginResponse.isNewUser()) {

                        AnalyticsService.log(getApplicationContext(), "NEW_USER_LOGIN", "LoginActivity");

                        intent = new Intent(LoginActivity.this, OnBoardUserActivity.class);
                        intent.putExtra(Constants.ON_BOARD_NEW_USER_INTENT_KEY, true);
                        intent.putExtra(Constants.NEW_USER_ON_BOARD_USER_NAME_INTENT_KEY, userName);
                        intent.putExtra(Constants.NEW_USER_ON_BOARD_USER_REGISTERED_VIA_INTENT_KEY, mUserRegisteredVia);
                        intent.putExtra(Constants.NEW_USER_ON_BOARD_USER_EMAIL_INTENT_KEY, email);
                        intent.putExtra(Constants.NEW_USER_ON_BOARD_USER_PHONE_NUMBER_INTENT_KEY, phoneNumber);

                        sharedPrefsEditor.putBoolean(AuthConstants.SP_ON_BOARD_INCOMPLETE_KEY, true);
                        sharedPrefsEditor.putString(AuthConstants.SP_ON_BOARD_INCOMPLETE_REGISTERED_VIA_KEY, mUserRegisteredVia);
                        sharedPrefsEditor.putString(AuthConstants.SP_ON_BOARD_INCOMPLETE_USER_EMAIL_KEY, email);
                        sharedPrefsEditor.putString(AuthConstants.SP_ON_BOARD_INCOMPLETE_USER_PHONE_NUMBER_KEY, phoneNumber);

                        // Handling Bank Info Not Present, as that would be the default for new user.
                        // For old users, who dont have bank details, that is handled on server side
                        sharedPrefsEditor.putBoolean(AuthConstants.SP_ON_BOARD_BANK_DETAILS_INCOMPLETE_KEY, true);
                        AuthConstants.ON_BOARD_BANK_INCOMPLETE = true;

                        sharedPrefsEditor.apply();
                    } else {

                        AnalyticsService.log(getApplicationContext(), "OLD_USER_LOGIN", "LoginActivity");
                        intent = new Intent(LoginActivity.this, HomeActivity.class);
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    gotToLoginSuccess(intent);
                } else if (response.errorBody() != null) {

                    AnalyticsService.log(getApplicationContext(), "Failure", "LoginActivity");

                    String loginResponse = response.errorBody().toString();
                    DialogUtil.dismissLoadingDialog(LoginActivity.this);
                    goToErrorFragment();
                    SnackbarUtil.makeLongSnack(mCoordinatorLayout, getString(R.string.oops));
                    Log.d(TAG, loginResponse);
                } else {
                    AnalyticsService.log(getApplicationContext(), "Failure", "LoginActivity");
                    DialogUtil.dismissLoadingDialog(LoginActivity.this);
                    goToErrorFragment();
                    SnackbarUtil.makeLongSnack(mCoordinatorLayout, getString(R.string.oops));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                AnalyticsService.log(getApplicationContext(), "Failure", "LoginActivity");
                DialogUtil.dismissLoadingDialog(LoginActivity.this);
                goToErrorFragment();
                SnackbarUtil.makeLongClickableSnack(mCoordinatorLayout, getString(R.string.check_internet),
                        getString(R.string.retry), v -> loginUserUsingToken(idToken, userName, phoneNumber, email, userImgUri));
                Log.e(TAG, t.toString());
            }
        });

        AsyncTask.execute(() -> {
            try {
                if (userImgUri != null) {
                    Bitmap bmp = BitmapFactory.decodeStream(new URL(userImgUri.toString()).openConnection().getInputStream());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();

                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                    AuthConstants.USER_IMAGE_BITMAP = bmp;
                    sharedPrefsEditor.putString(AuthConstants.SP_USER_IMAGE_BITMAP_KEY, encodedImage);
                    sharedPrefsEditor.apply();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void sendUserNotificationToServer() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "getInstanceId failed", task.getException());
                return;
            }

            if (task.getResult() != null) {
                String token = task.getResult().getToken();
                NotificationService.sendNotificationTokenToServer(this, new UserNotificationTokenRequest(token));
                SnackbarUtil.makeLongSnack(mCoordinatorLayout, getString(R.string.user_notification_token_sent));
            }
        });
    }

    public FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }

    public void goToLoginOtp(String userPhoneNumber) {
        if (mLoginOtpFragment == null) {
            mLoginOtpFragment = new LoginOtpFragment(userPhoneNumber);
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.onboarding_fragments_container, mLoginOtpFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void goToLoginEntry(String userPhoneNumber) {
        if (mLoginEntryFragment == null) {
            mLoginEntryFragment = new LoginEntryFragment();
        }
        mLoginEntryFragment.updateUserPhoneNumber(userPhoneNumber);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.onboarding_fragments_container, mLoginEntryFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private void gotToLoginSuccess(Intent intent) {

        AnalyticsService.log(getApplicationContext(), "LOGIN_SUCCESS", "LoginActivity");

        LoginSuccessFragment loginSuccessFragment = new LoginSuccessFragment(intent, true);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.onboarding_fragments_container, loginSuccessFragment);
        transaction.commit();
    }

    public void goToErrorFragment() {
        ErrorFragment errorFragment = new ErrorFragment(true);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.onboarding_fragments_container, errorFragment);
        transaction.commit();
    }

    public CoordinatorLayout getCoordinatorLayout() {
        return mCoordinatorLayout;
    }
}
