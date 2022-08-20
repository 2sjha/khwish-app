package com.khwish.app.fragments.eventdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khwish.app.R;
import com.khwish.app.activities.EventDetailsActivity;
import com.khwish.app.adapters.eventdetails.ContributorsListAdapter;
import com.khwish.app.responses.EventContributorsResponse;

public class ContributorsFragment extends Fragment {

    private EventDetailsActivity mParentActivity;
    private ContributorsListAdapter mContributorsAdapter;
    private RecyclerView mContributorsRecycler;
    private TextView mErrorText;
    private ProgressBar mLoadingProgress;

    public static ContributorsFragment newInstance() {
        return new ContributorsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_event_details_contributors, container, false);
        if (fragment != null && getActivity() != null && getContext() != null) {
            mContributorsRecycler = fragment.findViewById(R.id.contributors_recycler);
            mParentActivity = (EventDetailsActivity) getActivity();
            EventContributorsResponse resp = mParentActivity.getContributors();
            mErrorText = fragment.findViewById(R.id.error_text);
            mLoadingProgress = fragment.findViewById(R.id.loading_progress);

            if (resp != null && resp.getContributions() != null && !resp.getContributions().isEmpty()) {
                mContributorsAdapter = new ContributorsListAdapter(resp);
                mContributorsRecycler.setLayoutManager(new LinearLayoutManager(mParentActivity));
                mContributorsRecycler.setAdapter(mContributorsAdapter);

                mLoadingProgress.setVisibility(View.GONE);
                mErrorText.setVisibility(View.GONE);
                mContributorsRecycler.setVisibility(View.VISIBLE);
            } else if (resp != null && (resp.getContributions() == null || resp.getContributions().isEmpty())) {
                mLoadingProgress.setVisibility(View.GONE);
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(R.string.no_contributions_yet);
                mContributorsRecycler.setVisibility(View.GONE);
            }
            return fragment;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mParentActivity.fetchEventContributors();
    }

    public void updateContributors() {
        EventContributorsResponse contributors = mParentActivity.getContributors();
        mLoadingProgress.setVisibility(View.GONE);
        if (contributors != null) {
            // Initialise Adapter if that was not initialised in onCreate
            if (mContributorsAdapter == null) {
                mContributorsAdapter = new ContributorsListAdapter(contributors);
                mContributorsRecycler.setLayoutManager(new LinearLayoutManager(mParentActivity));
                mContributorsRecycler.setAdapter(mContributorsAdapter);

                if (contributors.getContributions() == null || contributors.getContributions().isEmpty()) {
                    mErrorText.setText(R.string.no_contributions_yet);
                    mErrorText.setVisibility(View.VISIBLE);
                    mContributorsRecycler.setVisibility(View.GONE);
                } else {
                    mErrorText.setVisibility(View.GONE);
                    mContributorsRecycler.setVisibility(View.VISIBLE);
                }
            }
            mContributorsAdapter.updateContributors(contributors);
        } else {
            mErrorText.setVisibility(View.VISIBLE);
            mContributorsRecycler.setVisibility(View.GONE);
        }
    }
}
