package com.khwish.app.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.khwish.app.R;
import com.khwish.app.activities.AddModifyEventActivity;
import com.khwish.app.activities.EventDetailsActivity;
import com.khwish.app.activities.HomeActivity;
import com.khwish.app.activities.OnBoardUserActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtil {

    private static final String TAG = DateTimeUtil.class.getSimpleName();
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ROOT);
    private static SimpleDateFormat sdfDt = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ROOT);

    static {
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        sdfDt.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
    }

    public static String getCurrentReadableDate() {
        Date date = new Date();
        return sdf.format(date);
    }

    public static String getReadableDateFromEpoch(Context context, Long epoch) {
        if (epoch == null) {
            if (EventDetailsActivity.class.equals(context.getClass())
                    || AddModifyEventActivity.class.equals(context.getClass())) {
                return context.getString(R.string.event_date_not_set);
            } else if (OnBoardUserActivity.class.equals(context.getClass())) {
                return context.getString(R.string.dob_not_set);
            } else {
                return null;
            }

        } else {
            Date date = new Date(epoch * 1000);
            return sdf.format(date);
        }
    }

    public static String getReadableDateTimeFromEpoch(Long epoch) {
        if (epoch == null) {
            return null;
        } else {
            Date date = new Date(epoch * 1000);
            return sdfDt.format(date);
        }
    }

    public static String formatDateFromYearMonthDay(int year, int month, int dayOfMonth) {
        String dayStr = String.valueOf(dayOfMonth);
        String monthStr = String.valueOf(month);
        if (dayOfMonth < 10) {
            dayStr = "0" + dayOfMonth;
        }

        if (month < 10) {
            monthStr = "0" + month;
        }

        return dayStr + "-" + monthStr + "-" + year;
    }

    public static Long getEpochFromReadableDate(Context context, String dateStr) {
        if (TextUtils.isEmpty(dateStr)
                || context.getString(R.string.prompt_dob).equals(dateStr)
                || context.getString(R.string.event_date_not_set).equals(dateStr)
                || context.getString(R.string.dob_not_set).equals(dateStr)) {
            return null;
        }

        try {
            Date parsedDate = sdf.parse(dateStr);
            if (parsedDate == null) {
                return getEpochFromDate(new Date());
            } else {
                return getEpochFromDate(parsedDate);
            }
        } catch (ParseException e) {
            Log.e(TAG, e.toString(), e);
            return null;
        }
    }

    private static Long getEpochFromDate(Date date) {
        return date.getTime() / 1000;
    }
}
