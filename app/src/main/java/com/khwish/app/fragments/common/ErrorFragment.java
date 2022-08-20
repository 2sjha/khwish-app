package com.khwish.app.fragments.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.khwish.app.R;

public class ErrorFragment extends Fragment {

    private boolean fromLogin;

    public ErrorFragment(boolean fromLogin) {
        super();
        this.fromLogin = fromLogin;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_error, container, false);
        if(!fromLogin) {
            fragment.setBackgroundColor(getResources().getColor(R.color.white));
        }

        TextView backButtonText = fragment.findViewById(R.id.back_button);
        backButtonText.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        return fragment;
    }
}