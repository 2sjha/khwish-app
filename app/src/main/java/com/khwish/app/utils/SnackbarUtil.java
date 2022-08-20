package com.khwish.app.utils;

import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.khwish.app.R;

public class SnackbarUtil {

    public static void makeLongSnack(View view, String text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    public static void makeLongSnack(CoordinatorLayout coordinatorLayout, String text) {
        if (text.equals(String.valueOf(R.string.check_internet))) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public static void makeLongClickableSnack(CoordinatorLayout coordinatorLayout, String snackText, String clickableText, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackText, Snackbar.LENGTH_INDEFINITE)
                .setAction(clickableText, listener);
        snackbar.show();
    }
    public static void makeLongClickableSnack(View coordinatorLayout, String snackText, String clickableText, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackText, Snackbar.LENGTH_INDEFINITE)
                .setAction(clickableText, listener);
        snackbar.show();
    }
}
