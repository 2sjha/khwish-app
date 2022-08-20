package com.khwish.app.fragments.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.khwish.app.R;
import com.khwish.app.activities.WalletActivity;

public class WithdrawalResultFragment extends Fragment {

    private WalletActivity mParentActivity;
    private String mResultText;

    public WithdrawalResultFragment(String mResultText) {
        this.mResultText = mResultText;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_withdrawal_result, container, false);
        if (fragment != null && getActivity() != null) {
            mParentActivity = (WalletActivity) getActivity();
            TextView resultTextView = fragment.findViewById(R.id.result_text);
            resultTextView.setText(mResultText);
            Button okButton = fragment.findViewById(R.id.ok_button);
            okButton.setOnClickListener(v -> mParentActivity.resetWithdrawalFragments());
            return fragment;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}
