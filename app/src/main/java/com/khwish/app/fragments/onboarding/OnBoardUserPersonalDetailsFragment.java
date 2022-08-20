package com.khwish.app.fragments.onboarding;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.khwish.app.R;
import com.khwish.app.activities.OnBoardUserActivity;
import com.khwish.app.constants.Constants;
import com.khwish.app.utils.DateTimeUtil;
import com.khwish.app.utils.ParsingUtil;
import com.khwish.app.utils.SnackbarUtil;

import java.util.Calendar;

public class OnBoardUserPersonalDetailsFragment extends Fragment {

    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_PHONE_NUMBER = "user_phone_number";
    public static final String USER_DOB = "user_dob";
    public static final String USER_REGISTERED_VIA = "user_registered_via";
    private String TAG = this.getClass().getSimpleName();
    private OnBoardUserActivity mParentActivity;
    private boolean mIsNewUser;
    private EditText mFullNameEditText;
    private String mUserFullName;
    private RelativeLayout mEmailLayout;
    private EditText mEmailEditText;
    private String mUserEmail;
    private RelativeLayout mPhoneNumberLayout;
    private EditText mPhoneNumberEditText;
    private String mUserPhoneNumber;
    private TextView mDobEditableText;
    private String mUserDobStr;
    private CheckBox mTncCheckbox;

    public OnBoardUserPersonalDetailsFragment(boolean isNewUser) {
        this.mIsNewUser = isNewUser;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_onboard_personal, container, false);
        if (fragment != null && getActivity() != null && getContext() != null) {
            mParentActivity = (OnBoardUserActivity) getActivity();

            mFullNameEditText = fragment.findViewById(R.id.full_name_form);
            mPhoneNumberEditText = fragment.findViewById(R.id.phone_number_form);
            mEmailEditText = fragment.findViewById(R.id.email_form);
            mDobEditableText = fragment.findViewById(R.id.dob_editable_text);
            mTncCheckbox = fragment.findViewById(R.id.tnc_checkbox);
            mEmailLayout = fragment.findViewById(R.id.email_layout);
            mPhoneNumberLayout = fragment.findViewById(R.id.phone_number_layout);
            final LinearLayout tncLayout = fragment.findViewById(R.id.tnc_check_layout);
            final RelativeLayout nextButtonLayout = fragment.findViewById(R.id.next_button_layout);
            final Switch dobSwitch = fragment.findViewById(R.id.dob_switch);
            final TextView tncClickableText = fragment.findViewById(R.id.tnc_clickable_text);

            Bundle userDetailsInfo = getArguments();
            if (userDetailsInfo != null) {
                String userName = userDetailsInfo.getString(USER_NAME);
                String userEmail = userDetailsInfo.getString(USER_EMAIL);
                String userPhoneNumber = userDetailsInfo.getString(USER_PHONE_NUMBER);
                String userRegisteredVia = userDetailsInfo.getString(USER_REGISTERED_VIA);
                long dobEpoch = userDetailsInfo.getLong(USER_DOB);

                // Setting Data sent from different activities
                mFullNameEditText.setText(userName);
                mEmailEditText.setText(userEmail);
                mPhoneNumberEditText.setText(userPhoneNumber);
                if (dobEpoch == 0L) {
                    mDobEditableText.setText(mParentActivity.getString(R.string.prompt_dob));
                } else {
                    mDobEditableText.setText(DateTimeUtil.getReadableDateFromEpoch(mParentActivity, dobEpoch));
                }

                // User Registered Via Layout will be invisible, but data is set & validated
                if (Constants.REGISTERED_VIA_EMAIL.equals(userRegisteredVia)) {
                    mEmailLayout.setVisibility(View.GONE);
                } else if (Constants.REGISTERED_VIA_PHONE_NUMBER.equals(userRegisteredVia)) {
                    mPhoneNumberLayout.setVisibility(View.GONE);
                }
            }

            DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, month, dayOfMonth) -> {
                int monthCorr = view.getMonth();
                String dob = DateTimeUtil.formatDateFromYearMonthDay(year, monthCorr + 1, dayOfMonth);
                mDobEditableText.setText(dob);
                mDobEditableText.setError(null);
                mParentActivity.setDobEpoch(DateTimeUtil.getEpochFromReadableDate(mParentActivity, dob));
            };

            Calendar c = Calendar.getInstance();
            int cYear = c.get(Calendar.YEAR);
            int cMonth = c.get(Calendar.MONTH);
            int cDay = c.get(Calendar.DAY_OF_MONTH);
            mDobEditableText.setOnClickListener(v -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        mParentActivity, onDateSetListener, cYear, cMonth, cDay);
                datePickerDialog.show();
            });

            View.OnClickListener newUserClickListener = v -> {
                if (validateAndReturnErrorView()) {
                    mParentActivity.setFullName(mUserFullName);
                    mParentActivity.setPhoneNumber(mUserPhoneNumber);
                    mParentActivity.setEmail(mUserEmail);
                    Long userDobEpoch = DateTimeUtil.getEpochFromReadableDate(mParentActivity, mUserDobStr);
                    mParentActivity.setDobEpoch(userDobEpoch);

                    mParentActivity.gotToBankDetails();
                }
            };

            View.OnClickListener profileEditClickListener = v -> {
                if (validateAndReturnErrorView()) {
                    mParentActivity.setFullName(mUserFullName);
                    mParentActivity.setPhoneNumber(mUserPhoneNumber);
                    mParentActivity.setEmail(mUserEmail);
                    Long userDobEpoch = DateTimeUtil.getEpochFromReadableDate(mParentActivity, mUserDobStr);
                    mParentActivity.setDobEpoch(userDobEpoch);

                    mParentActivity.onBoardUser();
                }
            };

            dobSwitch.setOnCheckedChangeListener((v, isChecked) -> {
                if (isChecked) {
                    mDobEditableText.setVisibility(View.VISIBLE);
                } else {
                    mDobEditableText.setVisibility(View.GONE);
                }
            });

            if (mIsNewUser) {
                SpannableStringBuilder styledTnCTextBuilder = new SpannableStringBuilder();
                styledTnCTextBuilder.append(getString(R.string.tnc_agree));
                styledTnCTextBuilder.append(" ");
                ClickableSpan tnClickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View textView) {
                        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(Constants.TNC_URL));
                        startActivity(browserIntent);
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(true);
                        ds.setColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                };

                styledTnCTextBuilder.append(getString(R.string.tnc), tnClickableSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                tncClickableText.setMovementMethod(LinkMovementMethod.getInstance());
                tncClickableText.setText(styledTnCTextBuilder, TextView.BufferType.SPANNABLE);

                nextButtonLayout.setOnClickListener(newUserClickListener);
            } else {
                tncLayout.setVisibility(View.GONE);
                mTncCheckbox.setChecked(true);
                TextView nextButtonText = fragment.findViewById(R.id.next_button_text);
                nextButtonText.setText(mParentActivity.getString(R.string.action_submit));
                nextButtonLayout.setOnClickListener(profileEditClickListener);
            }
            return fragment;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    private boolean validateAndReturnErrorView() {
        getInputValues();

        if (TextUtils.isEmpty(mUserFullName)) {
            mFullNameEditText.requestFocus();
            mFullNameEditText.setError(getString(R.string.cannot_be_empty));
            return false;
        } else if (!ParsingUtil.isValidEmail(mUserEmail) && mEmailLayout.getVisibility() == View.VISIBLE) {
            mEmailEditText.requestFocus();
            mEmailEditText.setError(getString(R.string.invalid_email));
            return false;
        } else if (!ParsingUtil.isValidPhoneNumber(mUserPhoneNumber)
                && mPhoneNumberLayout.getVisibility() == View.VISIBLE) {
            mPhoneNumberEditText.requestFocus();
            mPhoneNumberEditText.setError(getString(R.string.invalid_phone_number));
            return false;
        } else if (!mTncCheckbox.isChecked()) {
            SnackbarUtil.makeLongSnack(mParentActivity.getCoordinatorLayout(), getString(R.string.please_agree_tnc));
            return false;
        } else if (mDobEditableText.getVisibility() == View.VISIBLE) {
            Long userDobEpoch = DateTimeUtil.getEpochFromReadableDate(mParentActivity, mUserDobStr);
            mParentActivity.setDobEpoch(userDobEpoch);
            if (userDobEpoch == null) {
                mDobEditableText.requestFocus();
                mDobEditableText.setError(getString(R.string.dob_not_set));
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void getInputValues() {
        mUserFullName = mFullNameEditText.getText().toString();
        mUserEmail = mEmailEditText.getText().toString();
        mUserPhoneNumber = ParsingUtil.getPhoneNumber(mPhoneNumberEditText.getText().toString());
        mUserDobStr = mDobEditableText.getText().toString();
    }

}

