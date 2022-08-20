package com.khwish.app.adapters.eventdetails;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.khwish.app.fragments.eventdetails.ContributorsFragment;
import com.khwish.app.fragments.eventdetails.GoalsFragment;

public class GoalsContributorsPagerAdapter extends FragmentStateAdapter {

    private GoalsFragment goalsFragment;
    private ContributorsFragment contributorsFragment;

    public GoalsContributorsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            if (contributorsFragment == null) {
                contributorsFragment = ContributorsFragment.newInstance();
            }
            return contributorsFragment;
        } else if (position == 1) {
            if (goalsFragment == null) {
                goalsFragment = GoalsFragment.newInstance();
            }
            return goalsFragment;
        } else {
            if (contributorsFragment == null) {
                contributorsFragment = ContributorsFragment.newInstance();
            }
            return contributorsFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public GoalsFragment getGoalsFragment() {
        return goalsFragment;
    }

    public ContributorsFragment getContributorsFragment() {
        return contributorsFragment;
    }
}
