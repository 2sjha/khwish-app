package com.khwish.app.fragments.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.khwish.app.R;

public class ClosedEventsFragment extends Fragment {

    public static ClosedEventsFragment newInstance() {
        return new ClosedEventsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_home_events_closed, container, false);
        if (fragment != null && getActivity() != null && getContext() != null) {
            return fragment;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}
