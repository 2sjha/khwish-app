package com.khwish.app.fragments.login;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.khwish.app.R;
import com.khwish.app.activities.LoginActivity;
import com.khwish.app.utils.SnackbarUtil;

import java.util.concurrent.TimeUnit;

public class LoginOtpFragment extends Fragment {

    private String TAG = this.getClass().getSimpleName();
    private EditText mOtpEditText;
    private String mUserPhoneNumber;
    private String mGooglePhoneAuthVerificationId;
    private PhoneAuthProvider.ForceResendingToken mGooglePhoneAuthResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mPhoneAuthCallbacks;
    private LoginActivity mParentActivity;

    public LoginOtpFragment(String mUserPhoneNumber) {
        super();
        this.mUserPhoneNumber = mUserPhoneNumber;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_login_otp, container, false);
        if (fragment != null && getActivity() != null && getContext() != null && mUserPhoneNumber != null) {
            mParentActivity = (LoginActivity) getActivity();

            mOtpEditText = fragment.findViewById(R.id.otp_form);
            final Button enterOtpManuallyButton = fragment.findViewById(R.id.enter_manually_button);
            final TextView editPhoneNumberClickableText = fragment.findViewById(R.id.edit_phone_number_text);
            final TextView resendOtpClickableText = fragment.findViewById(R.id.resend_otp_text);
            final TextView userPhoneNumberText = fragment.findViewById(R.id.user_phone_number_text);
            userPhoneNumberText.setText(mUserPhoneNumber);

            editPhoneNumberClickableText.setOnClickListener(v -> {
                mParentActivity.goToLoginEntry(mUserPhoneNumber);
                mOtpEditText.setText(null);
            });

            enterOtpManuallyButton.setOnClickListener(v -> {
                mOtpEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) mParentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(mOtpEditText, InputMethodManager.SHOW_FORCED);
                }
            });

            resendOtpClickableText.setOnClickListener(v -> requestResendOtp(mUserPhoneNumber));

            mOtpEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    int otpLength = -1;

                    try {
                        otpLength = s.length();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString(), e);
                    }

                    if (otpLength == 6) {
                        String code = s.toString();
                        try {
                            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mGooglePhoneAuthVerificationId, code);
                            firebaseAuthWithPhoneCredentials(credential);
                        }catch (Exception e) {
                            Log.e(TAG, e.toString(),e);
                        }
                    } else if (otpLength > 0) {
                        mOtpEditText.requestFocus();
                        mOtpEditText.setError(getString(R.string.invalid_otp));
                    }
                }
            });

            // Callback for Phone number OAuth
            mPhoneAuthCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    Log.d(TAG, "onVerificationCompleted:" + credential);
                    String otp = credential.getSmsCode();
                    mOtpEditText.setText(otp);
                    mOtpEditText.setError(null);
                    firebaseAuthWithPhoneCredentials(credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Log.w(TAG, "onVerificationFailed", e);

                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        mOtpEditText.setError(getString(R.string.login_failed_invalid_creds));
                        mOtpEditText.requestFocus();
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        mOtpEditText.setError(getString(R.string.login_failed_too_many_requests));
                        mOtpEditText.requestFocus();
                    } else if (e instanceof FirebaseNetworkException) {
                        mParentActivity.goToErrorFragment();
                    }
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    Log.d(TAG, "onCodeSent:" + verificationId);

                    // Save verification ID and resending token so we can use them later
                    mGooglePhoneAuthVerificationId = verificationId;
                    mGooglePhoneAuthResendToken = token;
                }
            };

            requestOtpAndVerify(mUserPhoneNumber);
            return fragment;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    private void firebaseAuthWithPhoneCredentials(PhoneAuthCredential credential) {
        mParentActivity.getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(mParentActivity, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");

                        if (task.getResult() != null) {
                            FirebaseUser user = task.getResult().getUser();

                            if (user != null) {
                                //Verify User Token with server and on-board user
                                mParentActivity.verifyTokenFromServer(user);
                            } else {
                                SnackbarUtil.makeLongSnack(mParentActivity.getCoordinatorLayout(), getString(R.string.oops));
                            }
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            String errorCode = ((FirebaseAuthInvalidCredentialsException) task.getException()).getErrorCode();

                            if (!"ERROR_SESSION_EXPIRED".equals(errorCode)) {
                                mOtpEditText.setError(getString(R.string.invalid_otp));
                                mOtpEditText.requestFocus();
                            }
                        }
                    }
                });
    }

    private void requestOtpAndVerify(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS,
                mParentActivity, mPhoneAuthCallbacks);
    }


    private void requestResendOtp(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS,
                mParentActivity, mPhoneAuthCallbacks, mGooglePhoneAuthResendToken);
    }
}
