package com.khwish.app.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.khwish.app.R;
import com.khwish.app.constants.Constants;
import com.khwish.app.requests.AddModifyEventRequest;
import com.khwish.app.responses.EventDetailsResponse;
import com.khwish.app.retrofit.ServerApi;
import com.khwish.app.firebase.AnalyticsService;
import com.khwish.app.utils.DateTimeUtil;
import com.khwish.app.utils.ParsingUtil;
import com.khwish.app.utils.SnackbarUtil;

import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddModifyEventActivity extends AppCompatActivity {

    private String TAG = AddModifyEventActivity.class.getSimpleName();
    private EditText mEventNameText;
    private EditText mEventDescText;
    private TextView mEventDateText;
    private boolean mModifyEvent;
    private EventDetailsResponse mEventDetails;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modify_event);

        mCoordinatorLayout = findViewById(R.id.coordinator);

        Intent intent = getIntent();
        mEventNameText = findViewById(R.id.event_name_form);
        mEventDateText = findViewById(R.id.event_date_text);
        mEventDescText = findViewById(R.id.event_desc_form);

        ImageButton backButton = findViewById(R.id.back_button);
        final RelativeLayout saveButtonLayout = findViewById(R.id.save_event_button_layout);
        final Switch eventDateSwitch = findViewById(R.id.event_date_switch);

        try {
            mModifyEvent = intent.getBooleanExtra(Constants.EVENT_MODIFY_INTENT_KEY, false);
            if (mModifyEvent) {
                TextView headerText = findViewById(R.id.add_modify_event_header_text);
                headerText.setText(getString(R.string.action_modify_event));

                mEventDetails = (EventDetailsResponse) intent.getSerializableExtra(Constants.EVENT_DETAILS_INTENT_KEY);
                if (mEventDetails != null) {
                    mEventNameText.setText(mEventDetails.getName());
                    mEventDescText.setText(mEventDetails.getDescription());
                    if (mEventDetails.getEventDate() != null) {
                        String eventDate = DateTimeUtil.getReadableDateFromEpoch(AddModifyEventActivity.this, mEventDetails.getEventDate());
                        mEventDateText.setText(eventDate);
                    } else {
                        mEventDateText.setText(R.string.event_date_not_set);

                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            SnackbarUtil.makeLongSnack(mCoordinatorLayout, getString(R.string.oops));
        }

        Calendar c = Calendar.getInstance();
        int cYear = c.get(Calendar.YEAR);
        int cMonth = c.get(Calendar.MONTH);
        int cDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, month, dayOfMonth) -> {
            int monthCorr = view.getMonth();
            String endDate = DateTimeUtil.formatDateFromYearMonthDay(year, (monthCorr + 1), dayOfMonth);
            mEventDateText.setText(endDate);
            mEventDateText.setError(null);
        };
        mEventDateText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddModifyEventActivity.this,
                    onDateSetListener, cYear, cMonth, cDay);
            datePickerDialog.show();
        });

        backButton.setOnClickListener(v -> onBackPressed());

        saveButtonLayout.setOnClickListener(v -> {

            String eventTitle = mEventNameText.getText().toString();
            String eventDesc = mEventDescText.getText().toString();
            String eventEndDateStr = mEventDateText.getText().toString();

            Long eventEndDate = DateTimeUtil.getEpochFromReadableDate(AddModifyEventActivity.this, eventEndDateStr);

            if (TextUtils.isEmpty(eventTitle)) {
                mEventNameText.requestFocus();
                mEventNameText.setError(getString(R.string.cannot_be_empty));
            } else if (TextUtils.isEmpty(eventDesc)) {
                mEventDescText.requestFocus();
                mEventDescText.setError(getString(R.string.cannot_be_empty));
            } else if (mEventDateText.getVisibility() == View.VISIBLE && eventEndDate == null) {
                mEventDateText.requestFocus();
                mEventDateText.setError(getString(R.string.event_date_not_set));
            } else {
                AlertDialog exitDialog = new AlertDialog.Builder(AddModifyEventActivity.this).setCancelable(false).create();
                if (mModifyEvent) {
                    exitDialog.setMessage(getString(R.string.confirm_modify_event));
                } else {
                    exitDialog.setMessage(getString(R.string.confirm_event));
                }
                exitDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
                    if (mModifyEvent) {
                        modifyEvent(new AddModifyEventRequest(mEventDetails.getId(), eventTitle,
                                eventDesc, eventEndDate, 0.0F, 0.0F));
                    } else {
                        addNewEvent(new AddModifyEventRequest(eventTitle, eventDesc, eventEndDate,
                                0.0F, 0.0F));
                    }
                });
                exitDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> exitDialog.dismiss());
                exitDialog.show();
            }

        });

        eventDateSwitch.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                mEventDateText.setVisibility(View.VISIBLE);
            } else {
                mEventDateText.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog exitDialog = new AlertDialog.Builder(AddModifyEventActivity.this).setCancelable(false).create();
        exitDialog.setMessage(getString(R.string.discard_event));
        exitDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        exitDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> exitDialog.dismiss());
        exitDialog.show();
    }

    private void addNewEvent(AddModifyEventRequest addEventRequest) {

        ServerApi.addEvent(addEventRequest).enqueue(new Callback<EventDetailsResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventDetailsResponse> call, @NonNull Response<EventDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EventDetailsResponse resp = response.body();
                    SnackbarUtil.makeLongSnack(mCoordinatorLayout, resp.getMessage());
                    if (resp.isSuccess()) {
                        setResult(RESULT_OK);

                        AnalyticsService.log(getApplicationContext(), "NEW_EVENT_ADDED", "AddModifyEventActivity");

                        if (!mModifyEvent) {
                            AlertDialog newGoalDialog = new AlertDialog.Builder(AddModifyEventActivity.this).setCancelable(false).create();
                            newGoalDialog.setMessage(getString(R.string.ask_add_goal));
                            newGoalDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) ->
                                    openAddGoalIntent(resp, AddModifyEventActivity.this)
                            );
                            newGoalDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
                                newGoalDialog.dismiss();
                                finish();
                            });
                            newGoalDialog.show();
                        }
                    }

                } else if (response.errorBody() != null) {
                    AnalyticsService.log(getApplicationContext(), "Failure", "AddModifyEventActivity");
                    try {
                        EventDetailsResponse resp = ParsingUtil.fromJson(response.errorBody().string(), EventDetailsResponse.class);
                        SnackbarUtil.makeLongSnack(mCoordinatorLayout, resp.getMessage());
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventDetailsResponse> call, @NonNull Throwable t) {
                SnackbarUtil.makeLongClickableSnack(mCoordinatorLayout, getString(R.string.check_internet), getString(R.string.retry), v -> addNewEvent(addEventRequest));
                Log.e(TAG, t.toString(), t);
            }
        });
    }

    private void modifyEvent(AddModifyEventRequest modifyEventRequest) {

        ServerApi.modifyEvent(modifyEventRequest).enqueue(new Callback<EventDetailsResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventDetailsResponse> call, @NonNull Response<EventDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EventDetailsResponse resp = response.body();
                    SnackbarUtil.makeLongSnack(mCoordinatorLayout, resp.getMessage());
                    if (resp.isSuccess()) {
                        setResult(RESULT_OK);

                        AnalyticsService.log(getApplicationContext(), "EVENT_MODIFIED", "AddModifyEventActivity");

                        finish();
                    }
                } else if (response.errorBody() != null) {

                    AnalyticsService.log(getApplicationContext(), "Failure", "AddModifyEventActivity");

                    try {
                        EventDetailsResponse resp = ParsingUtil.fromJson(response.errorBody().string(), EventDetailsResponse.class);
                        SnackbarUtil.makeLongSnack(mCoordinatorLayout, resp.getMessage());
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventDetailsResponse> call, @NonNull Throwable t) {
                SnackbarUtil.makeLongClickableSnack(mCoordinatorLayout, getString(R.string.check_internet), getString(R.string.retry), v -> modifyEvent(modifyEventRequest));
                Log.e(TAG, t.toString(), t);
            }
        });
    }

    private void openAddGoalIntent(EventDetailsResponse event, Context context) {
        Intent sendIntent = new Intent(context, AddModifyGoalActivity.class);
        sendIntent.putExtra(Constants.EVENT_DETAILS_INTENT_KEY, event);
        context.startActivity(sendIntent);
        finish();
    }
}