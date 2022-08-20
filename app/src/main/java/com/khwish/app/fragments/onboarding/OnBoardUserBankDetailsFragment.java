package com.khwish.app.fragments.onboarding;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.khwish.app.R;
import com.khwish.app.activities.OnBoardUserActivity;
import com.khwish.app.requests.OnBoardUserRequest;
import com.khwish.app.responses.UserProfileResponse;
import com.khwish.app.utils.SnackbarUtil;

import java.util.regex.Pattern;

import static android.view.View.GONE;

public class OnBoardUserBankDetailsFragment extends Fragment {

    public static final String USER_BANK_DETAILS = "user_bank_details";
    private String TAG = this.getClass().getSimpleName();
    private OnBoardUserActivity mParentActivity;
    private EditText mBankAccountNumberEditText;
    private RadioButton mUpiChosenRadioButton;
    private RadioButton mAccountChosenRadioButton;
    private EditText mUpiVpaEditText;
    private EditText mReEnterBankAccountNumberEditText;
    private EditText mIfscCoderEditText;
    private EditText mAccountHoldersNameEditText;
    private Boolean isNewUser;

    public OnBoardUserBankDetailsFragment(boolean isNewUser) {
        this.isNewUser = isNewUser;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_onboard_bank, container, false);
        if (fragment != null && getActivity() != null && getContext() != null) {
            mParentActivity = (OnBoardUserActivity) getActivity();

            mBankAccountNumberEditText = fragment.findViewById(R.id.account_number_form);
            mUpiVpaEditText = fragment.findViewById(R.id.upi_vpa_form);
            mReEnterBankAccountNumberEditText = fragment.findViewById(R.id.reenter_account_number_form);
            mIfscCoderEditText = fragment.findViewById(R.id.ifsc_form);
            mAccountHoldersNameEditText = fragment.findViewById(R.id.account_holder_name_form);
            mUpiChosenRadioButton = fragment.findViewById(R.id.upi_chosen_radio_button);
            mAccountChosenRadioButton = fragment.findViewById(R.id.account_chosen_radio_button);
            final RelativeLayout finishButtonLayout = fragment.findViewById(R.id.finish_button_layout);
            final RelativeLayout skipButtonLayout = fragment.findViewById(R.id.skip_button_layout);

            // Setting RadioButton Listeners to alternate between them.
            mUpiChosenRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    mAccountChosenRadioButton.setChecked(false);
                }
            });

            mAccountChosenRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    mUpiChosenRadioButton.setChecked(false);
                }
            });

            Bundle bundleInfo = getArguments();
            if (bundleInfo != null) {
                UserProfileResponse.BankDetails userBankDetails = (UserProfileResponse.BankDetails) bundleInfo.getSerializable(USER_BANK_DETAILS);
                if (userBankDetails != null) {
                    Long accountNumber = userBankDetails.getAccountNumber();
                    String upiVpa = userBankDetails.getUpiVpa();
                    String ifscCode = userBankDetails.getIfscCode();
                    String accountHoldersName = userBankDetails.getAccountHoldersName();

                    if (mUpiVpaEditText != null && !TextUtils.isEmpty(upiVpa)) {
                        mUpiVpaEditText.setText(upiVpa);
                        mUpiChosenRadioButton.setChecked(true);
                    } else {
                        mAccountChosenRadioButton.setChecked(true);
                    }

                    if (mBankAccountNumberEditText != null && mReEnterBankAccountNumberEditText != null && accountNumber != null) {
                        mBankAccountNumberEditText.setText(String.valueOf(accountNumber));
                        mReEnterBankAccountNumberEditText.setText(String.valueOf(accountNumber));
                    }

                    if (mIfscCoderEditText != null && ifscCode != null) {
                        mIfscCoderEditText.setText(ifscCode);
                    }

                    if (mAccountHoldersNameEditText != null && accountHoldersName != null) {
                        mAccountHoldersNameEditText.setText(accountHoldersName);
                    }
                } else {
                    mUpiChosenRadioButton.setChecked(true);
                }
            } else {
                mUpiChosenRadioButton.setChecked(true);
            }

            // Setting RadioButton checks if text fields are clicked
            mUpiVpaEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!mUpiChosenRadioButton.isChecked()) {
                    mUpiChosenRadioButton.setChecked(true);
                }
            });

            mBankAccountNumberEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!mAccountChosenRadioButton.isChecked()) {
                    mAccountChosenRadioButton.setChecked(true);
                }
            });
            mReEnterBankAccountNumberEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!mAccountChosenRadioButton.isChecked()) {
                    mAccountChosenRadioButton.setChecked(true);
                }
            });
            mIfscCoderEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!mAccountChosenRadioButton.isChecked()) {
                    mAccountChosenRadioButton.setChecked(true);
                }
            });
            mAccountHoldersNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!mAccountChosenRadioButton.isChecked()) {
                    mAccountChosenRadioButton.setChecked(true);
                }
            });

            if (!isNewUser) {
                skipButtonLayout.setVisibility(GONE);

                TextView finishButtonText = fragment.findViewById(R.id.finish_button_text);
                finishButtonText.setText(mParentActivity.getString(R.string.action_submit));
            } else {
                skipButtonLayout.setOnClickListener(v -> mParentActivity.onBoardUser());
            }

            finishButtonLayout.setOnClickListener(v -> validateDataAndOnBoardUser());
            return fragment;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    private void validateDataAndOnBoardUser() {
        String upiVpa = mUpiVpaEditText.getText().toString();
        String bankAccountNumberStr = mBankAccountNumberEditText.getText().toString();
        String reEnterBackAccountNumberStr = mReEnterBankAccountNumberEditText.getText().toString();
        String ifscCode = mIfscCoderEditText.getText().toString();
        String accountHoldersName = mAccountHoldersNameEditText.getText().toString();
        Long bankAccountNumber = null;

        try {
            bankAccountNumber = Long.parseLong(bankAccountNumberStr);
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            if (mAccountChosenRadioButton.isChecked()) {
                mReEnterBankAccountNumberEditText.requestFocus();
                mReEnterBankAccountNumberEditText.setError(getString(R.string.please_check_account_number));
            }
        }

        if (mUpiChosenRadioButton.isChecked()) {
            if (validateUpi(upiVpa)) {
                onBoardUser(upiVpa, bankAccountNumber, ifscCode, accountHoldersName);
            }
        } else if (mAccountChosenRadioButton.isChecked()) {
            if (validateBankAccountDetails(bankAccountNumberStr, reEnterBackAccountNumberStr, ifscCode, accountHoldersName)) {
                onBoardUser(upiVpa, bankAccountNumber, ifscCode, accountHoldersName);
            }
        } else {
            SnackbarUtil.makeLongSnack(mParentActivity.getCoordinatorLayout(), getString(R.string.oops));
        }
    }

    private boolean validateBankAccountDetails(String bankAccountNumberStr, String reEnterBackAccountNumberStr, String ifscCode, String accountHoldersName) {
        if (TextUtils.isEmpty(bankAccountNumberStr)) {
            mBankAccountNumberEditText.requestFocus();
            mBankAccountNumberEditText.setError(getString(R.string.cannot_be_empty));
            return false;
        } else if (TextUtils.isEmpty(reEnterBackAccountNumberStr)) {
            mReEnterBankAccountNumberEditText.requestFocus();
            mReEnterBankAccountNumberEditText.setError(getString(R.string.cannot_be_empty));
            return false;
        } else if (!bankAccountNumberStr.equals(reEnterBackAccountNumberStr)) {
            mReEnterBankAccountNumberEditText.requestFocus();
            mReEnterBankAccountNumberEditText.setError(getString(R.string.account_numbers_dont_match));
            return false;
        } else if (bankAccountNumberStr.length() < 10) {
            mBankAccountNumberEditText.requestFocus();
            mBankAccountNumberEditText.setError(getString(R.string.please_check_account_number));
            return false;
        } else if (TextUtils.isEmpty(ifscCode)) {
            mIfscCoderEditText.requestFocus();
            mIfscCoderEditText.setError(getString(R.string.cannot_be_empty));
            return false;
        } else if (ifscCode.length() < 10) {
            mIfscCoderEditText.requestFocus();
            mIfscCoderEditText.setError(getString(R.string.invalid_ifsc));
            return false;
        } else if (TextUtils.isEmpty(accountHoldersName)) {
            mAccountHoldersNameEditText.requestFocus();
            mAccountHoldersNameEditText.setError(getString(R.string.cannot_be_empty));
            return false;
        } else {
            return true;
        }
    }

    private boolean validateUpi(String upiId) {
        if (TextUtils.isEmpty(upiId)) {
            mUpiVpaEditText.requestFocus();
            mUpiVpaEditText.setError(getString(R.string.cannot_be_empty));
            return false;
        } else if (!Pattern.compile("^\\w+@\\w+$").matcher(upiId).matches()) {
            mUpiVpaEditText.requestFocus();
            mUpiVpaEditText.setError(getString(R.string.invalid_upi_id));
            return false;
        } else {
            return true;
        }
    }

    private void onBoardUser(String upiVpa, Long bankAccountNumber, String ifscCode, String accountHoldersName) {
        OnBoardUserRequest.BankDetails bankDetails = new OnBoardUserRequest.BankDetails(upiVpa, bankAccountNumber, ifscCode, accountHoldersName);
        mParentActivity.setUserBankDetails(bankDetails);
        mParentActivity.onBoardUser();
    }
}
