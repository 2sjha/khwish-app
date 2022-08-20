package com.khwish.app.constants;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.khwish.app.BuildConfig;

import java.util.UUID;

public class AuthConstants {

    public static final String MAIN_SP_KEY = "khwish_prefs";
    public static final String SP_IS_LOGGED_IN_KEY = "is_logged_in";
    public static final String SP_USER_ID_KEY = "user_id";
    public static final String SP_USER_NAME_KEY = "user_name";
    public static final String SP_USER_IMAGE_BITMAP_KEY = "user_image_drawable";
    public static final String SP_USER_NOTIFICATION_TOKEN_KEY = "user_notification_token";
    public static final String SP_AUTH_TOKEN_KEY = "auth_token";
    public static final String SP_AUTH_TOKEN_EXPIRES_AT_KEY = "auth_token_expires_at";

    public static final String SP_ON_BOARD_INCOMPLETE_KEY = "on_board_incomplete";
    public static final String SP_ON_BOARD_BANK_DETAILS_INCOMPLETE_KEY = "on_board_bank_incomplete";
    public static final String SP_ON_BOARD_INCOMPLETE_REGISTERED_VIA_KEY = "on_board_incomplete_user_registered_via";
    public static final String SP_ON_BOARD_INCOMPLETE_USER_EMAIL_KEY = "on_board_incomplete_user_email";
    public static final String SP_ON_BOARD_INCOMPLETE_USER_PHONE_NUMBER_KEY = "on_board_incomplete_user_phone_number";

    public static String APP_VERSION = BuildConfig.VERSION_NAME;
    public static boolean IS_LOGGED_IN;
    public static UUID USER_ID;
    public static String USER_NAME;
    public static Bitmap USER_IMAGE_BITMAP;
    public static String USER_NOTIFICATION_TOKEN;
    public static String AUTH_TOKEN;
    public static Long AUTH_TOKEN_EXPIRES_AT;
    public static boolean ON_BOARD_BANK_INCOMPLETE;
}
