package com.khwish.app.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class WalletActivitiesResponse extends BaseResponse {

    @SerializedName("wallet_amount")
    private Double walletAmount;

    @SerializedName("activities")
    private ArrayList<WalletActivity> activities;

    public WalletActivitiesResponse() {
    }

    public Double getWalletAmount() {
        return walletAmount;
    }

    public ArrayList<WalletActivity> getActivities() {
        return activities;
    }

    public void sort() {
        if (activities != null) {
            Collections.sort(activities, (o1, o2) -> (int) (o2.getTime() - o1.getTime()));
        }
    }

    public static class WalletActivity implements Serializable {

        // +1 for positive activity & -1 for negative activity
        @SerializedName("type")
        private int activityType;

        @SerializedName("amount")
        private Double amount;

        @SerializedName("time")
        private Long time;

        @SerializedName("message")
        private String message;

        public WalletActivity() {
        }

        public int getActivityType() {
            return activityType;
        }

        public Double getAmount() {
            return amount;
        }

        public Long getTime() {
            return time;
        }

        public String getMessage() {
            return message;
        }
    }

}
