package com.khwish.app.fragments.homepage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khwish.app.R;
import com.khwish.app.activities.HomeActivity;
import com.khwish.app.adapters.homepage.EventCardsAdapter;
import com.khwish.app.responses.HomePageResponse;

public class OpenEventsFragment extends Fragment {

    private HomeActivity mParentActivity;
    private HomePageResponse mHomePageResponse;
    private ProgressBar mLoadingProgress;
    private TextView mErrorText;
    private RecyclerView mEventsRecycler;
    private EventCardsAdapter mEventCardsAdapter;

    public static OpenEventsFragment newInstance() {
        return new OpenEventsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_home_events_open, container, false);

        if (fragment != null && getActivity() != null && getContext() != null) {

            mEventsRecycler = fragment.findViewById(R.id.events_recycler);
            mLoadingProgress = fragment.findViewById(R.id.loading_progress);
            mErrorText = fragment.findViewById(R.id.error_text);

            mEventsRecycler.setNestedScrollingEnabled(false);

            mParentActivity = (HomeActivity) getActivity();
            mHomePageResponse = mParentActivity.getHomePageResponse();

            if (mHomePageResponse != null && mHomePageResponse.getEvents() != null && !mHomePageResponse.getEvents().isEmpty()) {
                mLoadingProgress.setVisibility(View.GONE);
                mEventsRecycler.setVisibility(View.VISIBLE);

                mEventCardsAdapter = new EventCardsAdapter(mHomePageResponse.getEvents(),getContext());
                mEventsRecycler.setLayoutManager(new LinearLayoutManager(mParentActivity));
                mEventsRecycler.setAdapter(mEventCardsAdapter);
                mEventsRecycler.addItemDecoration(new DividerItemDecoration(mParentActivity, DividerItemDecoration.HORIZONTAL));
            }
            return fragment;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    public void stopLoading() {
        mParentActivity = (HomeActivity) getActivity();
        if (mParentActivity != null) {
            mHomePageResponse = mParentActivity.getHomePageResponse();
        }

        if (mHomePageResponse == null) {
            try {
                mLoadingProgress.setVisibility(View.GONE);
                mEventsRecycler.setVisibility(View.GONE);
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(mParentActivity.getString(R.string.oops));
            } catch (Exception e) {
                Log.e("OpenEventsFragments", e.toString(), e);
            }
        } else if (mHomePageResponse.getEvents() == null || mHomePageResponse.getEvents().isEmpty()) {
            mLoadingProgress.setVisibility(View.GONE);
            mEventsRecycler.setVisibility(View.GONE);
            mErrorText.setVisibility(View.VISIBLE);
            mErrorText.setText(mParentActivity.getString(R.string.no_events_yet));
        } else {
            mLoadingProgress.setVisibility(View.GONE);
            mErrorText.setVisibility(View.GONE);
            mEventsRecycler.setVisibility(View.VISIBLE);

            mEventCardsAdapter = new EventCardsAdapter(mHomePageResponse.getEvents(),getContext());
            mEventsRecycler.setLayoutManager(new LinearLayoutManager(mParentActivity));
            mEventsRecycler.setAdapter(mEventCardsAdapter);
            mEventsRecycler.addItemDecoration(new DividerItemDecoration(mParentActivity, DividerItemDecoration.HORIZONTAL));
        }
    }
}
