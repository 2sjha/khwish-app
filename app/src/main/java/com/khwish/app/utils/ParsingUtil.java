package com.khwish.app.utils;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

public class ParsingUtil {

    private static final String TAG = ParsingUtil.class.getSimpleName();
    private static Gson gson = new GsonBuilder().setLenient().create();

    public static <T> String toJson(T object) {
        if (object != null) {
            return gson.toJson(object);
        } else {
            return null;
        }
    }

    public static <T> T fromJson(String jsonStr, Class<T> classType) {
        if (jsonStr != null) {
            try {
                return gson.fromJson(jsonStr, classType);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
                return null;
            }
        } else {
            return null;
        }
    }

    private static double roundDouble(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        try {
            BigDecimal bd = BigDecimal.valueOf(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public static double getProgress(double collectedAmount, double totalAmount) {
        if (collectedAmount == 0 && totalAmount == 0) {
            return 0.0;
        } else if (totalAmount == 0) {
            return 100.0;
        } else if (collectedAmount == 0) {
            return 0.0;
        } else {
            return roundDouble((collectedAmount / totalAmount) * 100, 2);
        }
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        String phoneNumberRegex = "^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$";
        boolean regexMatch = Pattern.compile(phoneNumberRegex).matcher(phoneNumber).matches();
        return !TextUtils.isEmpty(phoneNumber) && phoneNumber.length() >= 10 && regexMatch;
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String getPhoneNumber(String phoneNumberInput) {
        //TODO - Handle Other country codes as well, later on
        if (phoneNumberInput.length() == 10 && !phoneNumberInput.startsWith("+")) {
            return "+91" + phoneNumberInput;
        } else if (phoneNumberInput.length() == 13  && phoneNumberInput.startsWith("+91")) {
            return phoneNumberInput;
        } else {
            return phoneNumberInput;
        }
    }
}
