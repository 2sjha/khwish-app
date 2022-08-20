package com.khwish.app.adapters.wallet;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khwish.app.R;
import com.khwish.app.constants.Constants;
import com.khwish.app.responses.WalletActivitiesResponse;
import com.khwish.app.utils.DateTimeUtil;

public class WalletActivitiesAdapter extends RecyclerView.Adapter<WalletActivitiesAdapter.WalletActivityViewHolder> {

    private WalletActivitiesResponse mWalletDetails;
    private String INR_SYMBOL;
    private int MONEY_GREEN_COLOR;
    private int MONEY_RED_COLOR;

    public WalletActivitiesAdapter(WalletActivitiesResponse walletDetails) {
        walletDetails.sort();
        this.mWalletDetails = walletDetails;
    }

    @NonNull
    @Override
    public WalletActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout walletActivityLayout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_wallet_activity, parent, false);
        TextView amount = walletActivityLayout.findViewById(R.id.amount);
        TextView message = walletActivityLayout.findViewById(R.id.message);
        TextView time = walletActivityLayout.findViewById(R.id.time);
        INR_SYMBOL = parent.getContext().getString(R.string.inr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MONEY_GREEN_COLOR = parent.getContext().getColor(R.color.money_green);
            MONEY_RED_COLOR = parent.getContext().getColor(R.color.money_red);
        }

        return new WalletActivityViewHolder(walletActivityLayout, amount, message, time);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletActivityViewHolder holder, int position) {
        if (mWalletDetails != null && mWalletDetails.getActivities() != null && !mWalletDetails.getActivities().isEmpty()) {
            WalletActivitiesResponse.WalletActivity walletActivity = mWalletDetails.getActivities().get(position);

            if (walletActivity.getMessage() != null) {
                holder.message.setText(walletActivity.getMessage());
            } else {
                holder.message.setText(R.string.withdrawal);
            }
            holder.time.setText(DateTimeUtil.getReadableDateTimeFromEpoch(walletActivity.getTime()));
            String amount;
            if (walletActivity.getActivityType() == Constants.POSITIVE) {
                amount = "+ " + INR_SYMBOL + " " + walletActivity.getAmount();
                holder.amount.setText(amount);
                holder.amount.setTextColor(MONEY_GREEN_COLOR);
            } else if (walletActivity.getActivityType() == Constants.NEGATIVE) {
                amount = "- " + INR_SYMBOL + " " + walletActivity.getAmount();
                holder.amount.setText(amount);
                holder.amount.setTextColor(MONEY_RED_COLOR);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mWalletDetails != null && mWalletDetails.getActivities() != null) {
            return mWalletDetails.getActivities().size();
        } else {
            return 0;
        }
    }

    public void updateActivities(WalletActivitiesResponse updatedWalletDetails) {
        this.mWalletDetails = updatedWalletDetails;
        this.notifyDataSetChanged();
    }

    static class WalletActivityViewHolder extends RecyclerView.ViewHolder {
        private TextView amount;
        private TextView message;
        private TextView time;


        WalletActivityViewHolder(@NonNull RelativeLayout walletActivityLayout, TextView amount,
                                 TextView message, TextView time) {
            super(walletActivityLayout);
            this.amount = amount;
            this.message = message;
            this.time = time;
        }
    }
}
