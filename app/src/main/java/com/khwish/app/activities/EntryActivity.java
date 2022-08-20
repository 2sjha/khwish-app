package com.khwish.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.khwish.app.R;
import com.khwish.app.constants.AuthConstants;
import com.khwish.app.constants.Constants;

import java.util.UUID;

public class EntryActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        ProgressBar loadingProgressBar = findViewById(R.id.loading_progress);

        SharedPreferences sharedPrefs = getSharedPreferences(AuthConstants.MAIN_SP_KEY, MODE_PRIVATE);
        boolean isLoggedIn = sharedPrefs.getBoolean(AuthConstants.SP_IS_LOGGED_IN_KEY, false);
        String userIdStr = sharedPrefs.getString(AuthConstants.SP_USER_ID_KEY, "");
        String userName = sharedPrefs.getString(AuthConstants.SP_USER_NAME_KEY, null);
        String userImgBitmapStr = sharedPrefs.getString(AuthConstants.SP_USER_IMAGE_BITMAP_KEY, null);
        UUID userId;
        try {
            userId = UUID.fromString(userIdStr);
            AuthConstants.USER_ID = userId;
            AuthConstants.USER_NAME = userName;
            AuthConstants.AUTH_TOKEN = sharedPrefs.getString(AuthConstants.SP_AUTH_TOKEN_KEY, "");
            AuthConstants.USER_NOTIFICATION_TOKEN = sharedPrefs.getString(AuthConstants.SP_USER_NOTIFICATION_TOKEN_KEY, null);
            AuthConstants.AUTH_TOKEN_EXPIRES_AT = sharedPrefs.getLong(AuthConstants.SP_AUTH_TOKEN_EXPIRES_AT_KEY, 0);
            AuthConstants.ON_BOARD_BANK_INCOMPLETE = sharedPrefs.getBoolean(AuthConstants.SP_ON_BOARD_BANK_DETAILS_INCOMPLETE_KEY, false);
        } catch (Exception e) {
            isLoggedIn = false;
        }


        if (userImgBitmapStr != null) {
            byte[] b = Base64.decode(userImgBitmapStr, Base64.DEFAULT);
            AuthConstants.USER_IMAGE_BITMAP = BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            AuthConstants.USER_IMAGE_BITMAP = null;
        }

        Log.d(TAG, "User loggedIn status: " + isLoggedIn);
        if (isLoggedIn) {
            loadingProgressBar.setVisibility(View.GONE);

            boolean isSignupIncomplete = sharedPrefs.getBoolean(AuthConstants.SP_ON_BOARD_INCOMPLETE_KEY, false);
            String userRegisteredVia = sharedPrefs.getString(AuthConstants.SP_ON_BOARD_INCOMPLETE_REGISTERED_VIA_KEY, null);
            String userEmail = sharedPrefs.getString(AuthConstants.SP_ON_BOARD_INCOMPLETE_USER_EMAIL_KEY, null);
            String userPhoneNumber = sharedPrefs.getString(AuthConstants.SP_ON_BOARD_INCOMPLETE_USER_PHONE_NUMBER_KEY, null);

            if (isSignupIncomplete) {
                goToSignup(userRegisteredVia, userEmail, userPhoneNumber);
            } else {
                goToHome();
            }
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            goToLogin();
        }
    }

    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToSignup(String userRegisteredVia, String userEmail, String userPhoneNumber) {
        Intent intent = new Intent(this, OnBoardUserActivity.class);
        intent.putExtra(Constants.ON_BOARD_NEW_USER_INTENT_KEY, true);
        intent.putExtra(Constants.NEW_USER_ON_BOARD_USER_REGISTERED_VIA_INTENT_KEY, userRegisteredVia);
        intent.putExtra(Constants.NEW_USER_ON_BOARD_USER_EMAIL_INTENT_KEY, userEmail);
        intent.putExtra(Constants.NEW_USER_ON_BOARD_USER_PHONE_NUMBER_INTENT_KEY, userPhoneNumber);
        startActivity(intent);
        finish();
    }
}