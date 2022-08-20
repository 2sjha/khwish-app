package com.khwish.app.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.khwish.app.R;

public class DialogUtil {

    private static ProgressDialog dialog;

    public static void showLoadingDialog(Context context) {
        if (!((Activity) context).isFinishing()) {
            dialog = new ProgressDialog(context, R.style.LoadingDialog);
            dialog.setTitle("");
            dialog.setMessage(context.getString(R.string.please_wait));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public static void dismissLoadingDialog(Context context) {
        if (!((Activity) context).isFinishing()) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
