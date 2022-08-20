package com.khwish.app.fragments.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.khwish.app.R;
import com.khwish.app.activities.LoginActivity;
import com.khwish.app.constants.Constants;

public class LoginSuccessFragment extends Fragment {

    private String TAG = this.getClass().getSimpleName();
    private Intent mTargetActivityIntent;
    private boolean fromLogin;

    public LoginSuccessFragment(Intent targetActivityIntent, boolean fromLogin) {
        this.mTargetActivityIntent = targetActivityIntent;
        this.fromLogin = fromLogin;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_login_success, container, false);
        if (fragment != null && getActivity() != null && getContext() != null && mTargetActivityIntent != null) {
            Context context = getContext();
            Activity activity = getActivity();
            if(!fromLogin) {
                fragment.setBackgroundColor(getResources().getColor(R.color.white));
            }
            if (mTargetActivityIntent != null) {
                Runnable runnable = () -> {
                    mTargetActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(mTargetActivityIntent);
                    activity.finish();
                };
                Handler handler = new Handler();
                handler.postDelayed(runnable, Constants.GO_TO_NEXT_SCREEN_DELAY);
            } else {
                Log.d(TAG, "onCreateView: target intent is " + null);
            }

            return fragment;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}
