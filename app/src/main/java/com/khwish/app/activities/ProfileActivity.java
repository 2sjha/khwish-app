package com.khwish.app.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.khwish.app.R;
import com.khwish.app.constants.AuthConstants;
import com.khwish.app.constants.Constants;
import com.khwish.app.responses.UserProfileResponse;
import com.khwish.app.retrofit.ServerApi;
import com.khwish.app.utils.SnackbarUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    private UserProfileResponse mProfileDetails;
    private RelativeLayout mPersonalInfoParentLayout;
    private RelativeLayout mBankInfoParentLayout;
    private RelativeLayout mUpiDatalayout;
    private RelativeLayout mBankAccountDatalayout;
    private TextView mNoBankInfoText;
    private ProgressBar mLoadingProgress;
    private TextView mErrorText;
    private TextView mFullNameText;
    private TextView mEmailText;
    private TextView mPhoneNumberText;
    private TextView mUpiVpaText;
    private TextView mAccountNumberText;
    private TextView mIfscText;
    private TextView mAccountHoldersNameText;
    private TextView mLogOutTextButton;
    private View mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fetchProfileDetails();
        mCoordinatorLayout = findViewById(R.id.coordinator);

        ImageButton backButton = findViewById(R.id.back_button);
        mPersonalInfoParentLayout = findViewById(R.id.personal_details_layout);
        mBankInfoParentLayout = findViewById(R.id.bank_details_layout);
        mNoBankInfoText = findViewById(R.id.no_bank_info_text);
        mUpiDatalayout = findViewById(R.id.upi_data_layout);
        mBankAccountDatalayout = findViewById(R.id.bank_account_data_layout);
        mLoadingProgress = findViewById(R.id.loading_progress);
        mErrorText = findViewById(R.id.error_text);
        mFullNameText = findViewById(R.id.name_value_text);
        mEmailText = findViewById(R.id.email_value_text);
        mPhoneNumberText = findViewById(R.id.phone_number_value_text);
        mUpiVpaText = findViewById(R.id.upi_vpa_value_text);
        mAccountNumberText = findViewById(R.id.account_number_value_text);
        mIfscText = findViewById(R.id.ifsc_value_text);
        mAccountHoldersNameText = findViewById(R.id.account_holders_name_value_text);

        ImageView profileImgView = findViewById(R.id.profile_picture_img);
        ImageButton editPersonalInfoButton = findViewById(R.id.edit_personal_info_button);
        ImageButton editBankInfoButton = findViewById(R.id.edit_bank_info_button);
        TextView emailUsText = findViewById((R.id.email_us_at));
        TextView contactUsText = findViewById((R.id.contact_us_at));
        TextView termsAndConditionsText = findViewById((R.id.terms_and_conditions));
        mLogOutTextButton = findViewById(R.id.log_out_text_button);

        if (AuthConstants.USER_IMAGE_BITMAP != null) {
            Bitmap circleUserImgBmp = getRoundedCroppedBitmap(AuthConstants.USER_IMAGE_BITMAP);
            profileImgView.setImageBitmap(circleUserImgBmp);
        } else {
            profileImgView.setBackground(getDrawable(R.drawable.ic_profile_filled));
            profileImgView.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        }

        editPersonalInfoButton.setOnClickListener(v -> {
            openEditPersonalInfoIntent();
        });
        editBankInfoButton.setOnClickListener(v -> {
            openEditBankInfoIntent();
        });
        mLogOutTextButton.setOnClickListener(v -> {
            logoutUser();
        });
        backButton.setOnClickListener(v -> onNavigateUp());
        termsAndConditionsText.setOnClickListener(v -> openTermsAndConditionsPage());

        SpannableStringBuilder styledEmailUsTextBuilder = new SpannableStringBuilder();
        styledEmailUsTextBuilder.append(getString(R.string.email_us_at));
        styledEmailUsTextBuilder.append(" ");
        ClickableSpan emailClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.support_email)});
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        };

        styledEmailUsTextBuilder.append(getString(R.string.support_email), emailClickableSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        emailUsText.setText(styledEmailUsTextBuilder, TextView.BufferType.SPANNABLE);
        emailUsText.setMovementMethod(new LinkMovementMethod());

        SpannableStringBuilder styledContactUsTextBuilder = new SpannableStringBuilder();
        styledContactUsTextBuilder.append(getString(R.string.contact_us_at));
        styledContactUsTextBuilder.append(" ");
        ClickableSpan contactClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {

                Uri supportPhoneNumberUri = Uri.parse("tel:" + getString(R.string.support_phone_number));
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, supportPhoneNumberUri);
                startActivity(dialIntent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        };

        styledContactUsTextBuilder.append(getString(R.string.support_phone_number), contactClickableSpan,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        contactUsText.setText(styledContactUsTextBuilder, TextView.BufferType.SPANNABLE);
        contactUsText.setMovementMethod(new LinkMovementMethod());
    }

    private void fetchProfileDetails() {
        ServerApi.getUserProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                mLoadingProgress.setVisibility(View.GONE);
                mLogOutTextButton.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        mProfileDetails = response.body();

                        if (mProfileDetails.getUserDetails() == null) {
                            mErrorText.setVisibility(View.VISIBLE);
                        } else {
                            mErrorText.setVisibility(View.GONE);
                            mPersonalInfoParentLayout.setVisibility(View.VISIBLE);

                            mFullNameText.setText(mProfileDetails.getUserDetails().getName());
                            mEmailText.setText(mProfileDetails.getUserDetails().getEmail());
                            mPhoneNumberText.setText(mProfileDetails.getUserDetails().getPhoneNumber());
                        }

                        mBankInfoParentLayout.setVisibility(View.VISIBLE);
                        if (mProfileDetails.getBankDetails() == null) {
                            mNoBankInfoText.setVisibility(View.VISIBLE);
                            mUpiDatalayout.setVisibility(View.GONE);
                            mBankAccountDatalayout.setVisibility(View.GONE);
                        } else {
                            mNoBankInfoText.setVisibility(View.GONE);

                            if (!TextUtils.isEmpty(mProfileDetails.getBankDetails().getUpiVpa())) {
                                mUpiDatalayout.setVisibility(View.VISIBLE);
                                mUpiVpaText.setText(mProfileDetails.getBankDetails().getUpiVpa());
                            }
                            if (mProfileDetails.getBankDetails().getAccountNumber() != null) {
                                mBankAccountDatalayout.setVisibility(View.VISIBLE);
                                mAccountNumberText.setText(String.valueOf(mProfileDetails.getBankDetails().getAccountNumber()));
                            }
                            mIfscText.setText(mProfileDetails.getBankDetails().getIfscCode());
                            mAccountHoldersNameText.setText(mProfileDetails.getBankDetails().getAccountHoldersName());
                        }
                    } else {
                        mErrorText.setVisibility(View.VISIBLE);
                    }
                } else {
                    mErrorText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                Log.e(TAG, t.toString(), t);
                mLoadingProgress.setVisibility(View.GONE);
                mErrorText.setVisibility(View.VISIBLE);
                mLogOutTextButton.setVisibility(View.VISIBLE);
                SnackbarUtil.makeLongClickableSnack(mCoordinatorLayout, getString(R.string.check_internet),
                        getString(R.string.retry), v -> fetchProfileDetails());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.MODIFY_PROFILE_INTENT_CODE && resultCode == RESULT_OK) {
            fetchProfileDetails();
        }
    }

    private void openEditPersonalInfoIntent() {
        Intent editPersonalInfoIntent = new Intent(ProfileActivity.this, OnBoardUserActivity.class);
        editPersonalInfoIntent.putExtra(Constants.PROFILE_DETAILS_INTENT_KEY, mProfileDetails);
        editPersonalInfoIntent.putExtra(Constants.PROFILE_MODIFY_PERSONAL_DETAILS_INTENT_KEY, true);
        startActivityForResult(editPersonalInfoIntent, Constants.MODIFY_PROFILE_INTENT_CODE);
    }

    private void openEditBankInfoIntent() {
        Intent editBankInfoIntent = new Intent(ProfileActivity.this, OnBoardUserActivity.class);
        editBankInfoIntent.putExtra(Constants.PROFILE_DETAILS_INTENT_KEY, mProfileDetails);
        editBankInfoIntent.putExtra(Constants.PROFILE_MODIFY_BANK_DETAILS_INTENT_KEY, true);
        startActivityForResult(editBankInfoIntent, Constants.MODIFY_PROFILE_INTENT_CODE);
    }

    private void openTermsAndConditionsPage() {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(Constants.TNC_URL));
        startActivity(viewIntent);
    }

    private void logoutUser() {

        AlertDialog exitDialog = new AlertDialog.Builder(ProfileActivity.this).setCancelable(false).create();
        exitDialog.setMessage(getString(R.string.logout));
        exitDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> {
            SharedPreferences.Editor spEditor = getSharedPreferences(AuthConstants.MAIN_SP_KEY, MODE_PRIVATE).edit();
            spEditor.clear();
            spEditor.apply();

            Intent logoutIntent = new Intent(ProfileActivity.this, EntryActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
        });
        exitDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
            exitDialog.dismiss();
        });
        exitDialog.show();
    }

    private Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
        int widthLight = bitmap.getWidth();
        int heightLight = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);

        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));

        canvas.drawRoundRect(rectF, widthLight / 2, heightLight / 2, paintColor);

        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, 0, 0, paintImage);

        return output;
    }
}
