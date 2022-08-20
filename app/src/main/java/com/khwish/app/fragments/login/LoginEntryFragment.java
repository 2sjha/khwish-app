package com.khwish.app.fragments.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.khwish.app.R;
import com.khwish.app.activities.LoginActivity;
import com.khwish.app.utils.DialogUtil;
import com.khwish.app.utils.ParsingUtil;
import com.khwish.app.utils.SnackbarUtil;

import java.util.Arrays;

public class LoginEntryFragment extends Fragment {

    private String TAG = this.getClass().getSimpleName();
    private CallbackManager mFbCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private LoginActivity mParentActivity;
    private EditText mPhoneNumberEditText;

    public LoginEntryFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_login_entry, container, false);
        if (fragment != null && getActivity() != null && getContext() != null) {
            mParentActivity = (LoginActivity) getActivity();

            final Button receiveOtpButton = fragment.findViewById(R.id.receive_otp_button);
            final RelativeLayout fbLoginButtonLayout = fragment.findViewById(R.id.fb_login_button_layout);
            final SignInButton googleLoginButton = fragment.findViewById(R.id.google_login_button);
            mPhoneNumberEditText = fragment.findViewById(R.id.number_form);

            FacebookSdk.sdkInitialize(mParentActivity);
            mFbCallbackManager = CallbackManager.Factory.create();

            fbLoginButtonLayout.setOnClickListener(v -> LoginManager.getInstance()
                    .logInWithReadPermissions(this, Arrays.asList("email", "public_profile")));

            // Callback for Fb OAuth
            LoginManager.getInstance().registerCallback(mFbCallbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.d(TAG, "facebook:onSuccess:" + loginResult);
                            firebaseAuthWithFacebookToken(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                            DialogUtil.dismissLoadingDialog(mParentActivity);
                            Log.w(TAG, "facebook login cancelled");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            DialogUtil.dismissLoadingDialog(mParentActivity);
                            Log.e(TAG, "facebook login has encountered error. " + exception.toString());
                        }
                    });

            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(mParentActivity, gso);
            googleLoginButton.setSize(SignInButton.SIZE_STANDARD);
            setGoogleLoginButtonText(googleLoginButton, getString(R.string.google));
            googleLoginButton.setOnClickListener(v -> {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, LoginActivity.RC_SIGN_IN);
            });

            receiveOtpButton.setOnClickListener(v -> {
                String phoneNumber = mPhoneNumberEditText.getText().toString();
                if (ParsingUtil.isValidPhoneNumber(phoneNumber)) {
                    String userPhoneNumber = ParsingUtil.getPhoneNumber(phoneNumber);
                    mParentActivity.goToLoginOtp(userPhoneNumber);
                } else {
                    mPhoneNumberEditText.requestFocus();
                    mPhoneNumberEditText.setError(getString(R.string.invalid_phone_number));
                }
            });

            return fragment;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle Google Login Intent Result here
        if (requestCode == LoginActivity.RC_SIGN_IN) {
            DialogUtil.showLoadingDialog(mParentActivity);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                } else {
                    Log.w(TAG, "Google sign in failed");
                    SnackbarUtil.makeLongSnack(mParentActivity.getCoordinatorLayout(), getString(R.string.google_login_failed));
                    DialogUtil.dismissLoadingDialog(mParentActivity);
                }
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                SnackbarUtil.makeLongSnack(mParentActivity.getCoordinatorLayout(), getString(R.string.google_login_failed));
                DialogUtil.dismissLoadingDialog(mParentActivity);
            }
        } else {
            DialogUtil.showLoadingDialog(mParentActivity);
            this.getFbCallbackManager().onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mParentActivity.getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(mParentActivity, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mParentActivity.getFirebaseAuth().getCurrentUser();
                        if (user != null) {
                            mParentActivity.verifyTokenFromServer(user);
                        } else {
                            DialogUtil.dismissLoadingDialog(mParentActivity);
                            Log.e(TAG, "Couldn\'t get user object.");
                            SnackbarUtil.makeLongSnack(mParentActivity.getCoordinatorLayout(), getString(R.string.oops));
                        }
                    } else {
                        DialogUtil.dismissLoadingDialog(mParentActivity);
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        SnackbarUtil.makeLongSnack(mParentActivity.getCoordinatorLayout(), getString(R.string.google_login_failed));
                    }
                });
    }

    private void firebaseAuthWithFacebookToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mParentActivity.getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(mParentActivity, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mParentActivity.getFirebaseAuth().getCurrentUser();
                        if (user != null) {
                            mParentActivity.verifyTokenFromServer(user);
                        } else {
                            DialogUtil.dismissLoadingDialog(mParentActivity);
                            Log.e(TAG, "signInWithCredential: Couldn\'t get user object.");
                            SnackbarUtil.makeLongSnack(mParentActivity.getCoordinatorLayout(), getString(R.string.oops));
                        }
                    } else {
                        DialogUtil.dismissLoadingDialog(mParentActivity);
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        SnackbarUtil.makeLongSnack(mParentActivity.getCoordinatorLayout(), getString(R.string.fb_login_failed));
                    }
                });
    }

    private void setGoogleLoginButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    private CallbackManager getFbCallbackManager() {
        return mFbCallbackManager;
    }

    public void updateUserPhoneNumber(String userPhoneNumber) {
        mPhoneNumberEditText.setText(userPhoneNumber);
    }
}