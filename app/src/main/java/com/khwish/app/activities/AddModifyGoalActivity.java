package com.khwish.app.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.khwish.app.R;
import com.khwish.app.constants.Constants;
import com.khwish.app.requests.AddModifyGoalRequest;
import com.khwish.app.responses.EventDetailsResponse;
import com.khwish.app.responses.GoalDetailsResponse;
import com.khwish.app.retrofit.ServerApi;
import com.khwish.app.firebase.AnalyticsService;
import com.khwish.app.utils.ParsingUtil;
import com.khwish.app.utils.SnackbarUtil;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddModifyGoalActivity extends AppCompatActivity {

    private String TAG = AddModifyGoalActivity.class.getSimpleName();
    private EditText mGoalNameText;
    private EditText mGoalDescText;
    private EditText mGoalAmountText;
    private TextInputLayout mGoalAmountParent;
    private EventDetailsResponse mEventDetails;
    private GoalDetailsResponse mGoalDetails;
    private boolean mModifyGoal;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modify_goal);

        mCoordinatorLayout = findViewById(R.id.coordinator);

        Intent intent = getIntent();
        mGoalNameText = findViewById(R.id.goal_name_form);
        mGoalAmountText = findViewById(R.id.goal_amount_form);
        mGoalDescText = findViewById(R.id.goal_desc_form);
        TextView goalWillBeText = findViewById(R.id.goal_will_be_text);
        final RelativeLayout saveButtonLayout = findViewById(R.id.save_goal_button_layout);
        ImageButton backButton = findViewById(R.id.back_button);
        final Switch goalAmountSwitch = findViewById(R.id.goal_amount_switch);
        mGoalAmountParent = findViewById(R.id.goal_amount_form_parent);

        try {
            mModifyGoal = intent.getBooleanExtra(Constants.GOAL_MODIFY_INTENT_KEY, false);
            if (mModifyGoal) {
                TextView headerText = findViewById(R.id.add_modify_goal_header_text);
                headerText.setText(R.string.action_modify_goal);
                mGoalDetails = (GoalDetailsResponse) intent.getSerializableExtra(Constants.GOAL_DETAILS_INTENT_KEY);
                if (mGoalDetails != null) {
                    mGoalNameText.setText(mGoalDetails.getName());
                    mGoalAmountText.setText(String.valueOf(mGoalDetails.getTotalAmount()));
                    mGoalDescText.setText(mGoalDetails.getDescription());
                }
            }

            mEventDetails = (EventDetailsResponse) intent.getSerializableExtra(Constants.EVENT_DETAILS_INTENT_KEY);
            if (mEventDetails != null) {
                SpannableStringBuilder styledGoalAddedToEventBuilder = new SpannableStringBuilder();
                if (mModifyGoal) {
                    styledGoalAddedToEventBuilder.append(getString(R.string.goal_will_be_modified_in),
                            new TextAppearanceSpan(this, R.style.goal_will_be), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    styledGoalAddedToEventBuilder.append(getString(R.string.goal_will_be_added_to),
                            new TextAppearanceSpan(this, R.style.goal_will_be), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                styledGoalAddedToEventBuilder.append(mEventDetails.getName(),
                        new TextAppearanceSpan(this, R.style.bold_event_name), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                goalWillBeText.setText(styledGoalAddedToEventBuilder, TextView.BufferType.SPANNABLE);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            SnackbarUtil.makeLongSnack(mCoordinatorLayout, getString(R.string.oops));
        }

        backButton.setOnClickListener(v -> onBackPressed());

        saveButtonLayout.setOnClickListener(v -> {

            String goalTitle = mGoalNameText.getText().toString();
            String goalDesc = mGoalDescText.getText().toString();
            String goalAmountStr = mGoalAmountText.getText().toString();
            int goalAmount = 0;
            try {
                goalAmount = Integer.parseInt(goalAmountStr);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
            final int finalGoalAmt = goalAmount;

            if (!TextUtils.isEmpty(goalTitle) && !TextUtils.isEmpty(goalDesc)) {
                AlertDialog exitDialog = new AlertDialog.Builder(AddModifyGoalActivity.this).setCancelable(false).create();
                if (mModifyGoal) {
                    exitDialog.setMessage(getString(R.string.confirm_modify_goal));
                } else {
                    exitDialog.setMessage(getString(R.string.confirm_goal));
                }
                exitDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
                    if (mModifyGoal) {
                        modifyGoal(new AddModifyGoalRequest(mEventDetails.getId(), mGoalDetails.getId(), goalTitle, goalDesc, finalGoalAmt));
                    } else {
                        addNewGoal(new AddModifyGoalRequest(mEventDetails.getId(), goalTitle, goalDesc, finalGoalAmt));
                    }
                });
                exitDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> exitDialog.dismiss());
                exitDialog.show();
            } else {
                if (TextUtils.isEmpty(goalTitle)) {
                    mGoalNameText.requestFocus();
                    mGoalNameText.setError(getString(R.string.cannot_be_empty));
                } else if (TextUtils.isEmpty(goalDesc)) {
                    mGoalDescText.requestFocus();
                    mGoalDescText.setError(getString(R.string.cannot_be_empty));
                }
            }
        });

        goalAmountSwitch.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                mGoalAmountParent.setVisibility(View.VISIBLE);
            } else {
                mGoalAmountParent.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog exitDialog = new AlertDialog.Builder(AddModifyGoalActivity.this).setCancelable(false).create();
        exitDialog.setMessage(getString(R.string.discard_goal));
        exitDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        exitDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> exitDialog.dismiss());
        exitDialog.show();
    }

    private void addNewGoal(AddModifyGoalRequest addGoalRequest) {
        ServerApi.addGoal(addGoalRequest).enqueue(new Callback<GoalDetailsResponse>() {
            @Override
            public void onResponse(@NonNull Call<GoalDetailsResponse> call, @NonNull Response<GoalDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GoalDetailsResponse resp = response.body();
                    SnackbarUtil.makeLongSnack(mCoordinatorLayout, resp.getMessage());
                    if (resp.isSuccess()) {
                        setResult(RESULT_OK);

                        AnalyticsService.log(getApplicationContext(), "NEW_GOAL_ADDED", "AddModifyGoalActivity");

                        finish();
                    }
                } else if (response.errorBody() != null) {

                    AnalyticsService.log(getApplicationContext(), "Failure", "AddModifyGoalActivity");

                    try {
                        GoalDetailsResponse resp = ParsingUtil.fromJson(response.errorBody().string(), GoalDetailsResponse.class);
                        SnackbarUtil.makeLongSnack(mCoordinatorLayout, resp.getMessage());
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GoalDetailsResponse> call, @NonNull Throwable t) {
                SnackbarUtil.makeLongClickableSnack(mCoordinatorLayout, getString(R.string.check_internet), getString(R.string.retry), v -> addNewGoal(addGoalRequest));
                Log.e(TAG, t.toString(), t);
            }
        });
    }

    private void modifyGoal(AddModifyGoalRequest modifyGoalRequest) {
        ServerApi.modifyGoal(modifyGoalRequest).enqueue(new Callback<GoalDetailsResponse>() {
            @Override
            public void onResponse(@NonNull Call<GoalDetailsResponse> call, @NonNull Response<GoalDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GoalDetailsResponse resp = response.body();
                    SnackbarUtil.makeLongSnack(mCoordinatorLayout, resp.getMessage());
                    if (resp.isSuccess()) {
                        setResult(RESULT_OK);

                        AnalyticsService.log(getApplicationContext(), "GOAL_MODIFIED", "AddModifyGoalActivity");

                        finish();
                    }
                } else if (response.errorBody() != null) {

                    AnalyticsService.log(getApplicationContext(), "Failure", "AddModifyGoalActivity");

                    try {
                        GoalDetailsResponse resp = ParsingUtil.fromJson(response.errorBody().string(), GoalDetailsResponse.class);
                        SnackbarUtil.makeLongSnack(mCoordinatorLayout, resp.getMessage());
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GoalDetailsResponse> call, @NonNull Throwable t) {
                SnackbarUtil.makeLongClickableSnack(mCoordinatorLayout, getString(R.string.check_internet), getString(R.string.retry), v -> modifyGoal(modifyGoalRequest));
                Log.e(TAG, t.toString(), t);
            }
        });
    }

}