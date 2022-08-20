package com.khwish.app.fragments.wallet;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.khwish.app.R;
import com.khwish.app.activities.WalletActivity;
import com.khwish.app.requests.WithdrawalRequest;
import com.khwish.app.utils.SnackbarUtil;

public class WithdrawalFormFragment extends Fragment {

    public static final String WALLET_AMOUNT = "wallet_amount";
    private final String TAG = this.getClass().getSimpleName();
    private double mWalletAmount;
    private Button mWithdrawButton;
    private ProgressBar mWithdrawalProgressbar;
    private EditText mWithdrawalAmountForm;
    private Spinner mWithdrawMethodSpinner;
    private WalletActivity mParentActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View fragment = inflater.inflate(R.layout.fragment_withdrawal_form, container, false);
        if (fragment != null && getActivity() != null && getContext() != null) {
            mParentActivity = (WalletActivity) getActivity();
            Bundle walletInfo = getArguments();

            if (walletInfo == null) {
                SnackbarUtil.makeLongSnack(mParentActivity.getCoordinatorLayout(), mParentActivity.getString(R.string.oops));
                mParentActivity.resetWithdrawalFragments();
                return fragment;
            }

            mWalletAmount = walletInfo.getDouble(WALLET_AMOUNT);
            mWithdrawButton = fragment.findViewById(R.id.withdraw_button);
            mWithdrawalProgressbar = fragment.findViewById(R.id.withdrawal_progressbar);
            mWithdrawalAmountForm = fragment.findViewById(R.id.withdrawal_amount_form);
            mWithdrawMethodSpinner = fragment.findViewById(R.id.transfer_method_spinner);

            mWithdrawButton.setOnClickListener(v -> {
                String withdrawalAmountStr = mWithdrawalAmountForm.getText().toString();
                String transferMethod = mWithdrawMethodSpinner.getSelectedItem().toString();

                double withdrawalAmount = validateAmountAndReturn(withdrawalAmountStr);
                if (withdrawalAmount != -1 && isValidTransferMethod(transferMethod)) {
                    mWithdrawButton.setEnabled(false);
                    mWithdrawButton.setClickable(false);

                    mParentActivity.makeWithdrawalRequest(new WithdrawalRequest(withdrawalAmount, transferMethod));
                }
            });

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mParentActivity,
                    R.array.transfer_methods, R.layout.spinner_listitem);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mWithdrawMethodSpinner.setAdapter(adapter);
            mWithdrawMethodSpinner.setSelection(0, false);
            return fragment;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    private double validateAmountAndReturn(String withdrawalAmountStr) {
        double withdrawalAmount;
        try {
            withdrawalAmount = Double.parseDouble(withdrawalAmountStr);
        } catch (Exception e) {
            mWithdrawalAmountForm.setError(mParentActivity.getString(R.string.invalid_withdrawal_amount));
            mWithdrawalAmountForm.requestFocus();
            return -1;
        }

        if (withdrawalAmount > mWalletAmount) {
            mWithdrawalAmountForm.setError(mParentActivity.getString(R.string.invalid_withdrawal_amount));
            mWithdrawalAmountForm.requestFocus();
            return -1;
        }
        return withdrawalAmount;
    }

    private boolean isValidTransferMethod(String transferMethod) {
        String[] transferMethods = mParentActivity.getResources().getStringArray(R.array.transfer_methods);

        if (!TextUtils.isEmpty(transferMethod) && !transferMethods[0].equals(transferMethod)) {
            return true;
        } else {
            mWithdrawMethodSpinner.requestFocus();
            TextView errorText = (TextView) mWithdrawMethodSpinner.getSelectedView();
            errorText.setError("");
            errorText.setText(mParentActivity.getString(R.string.please_select_transfer_method));
            errorText.setTextColor(Color.RED);
            return false;
        }
    }

    public void enableWithdrawalProgress() {
        mWithdrawalProgressbar.setVisibility(View.VISIBLE);
    }

    public void disableWithdrawalProgress() {
        mWithdrawalProgressbar.setVisibility(View.GONE);
    }
}
