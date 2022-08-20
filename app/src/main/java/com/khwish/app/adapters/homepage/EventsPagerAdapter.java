package com.khwish.app.adapters.homepage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.khwish.app.fragments.homepage.OpenEventsFragment;

public class EventsPagerAdapter extends FragmentStateAdapter {

    private OpenEventsFragment mOpenEventsFragment;

    public EventsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.mOpenEventsFragment = OpenEventsFragment.newInstance();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return mOpenEventsFragment;
//            TODO - CleanupCode and fix after closed events is decided, implemented in backend
//        } else if (position == 1) {
//            return ClosedEventsFragment.newInstance();
        } else {
            return mOpenEventsFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 1;
//        TODO - fix after closed events is implemented in backend
//        return 3;
    }

    public OpenEventsFragment getOpenEventsFragment() {
        return mOpenEventsFragment;
    }
}
