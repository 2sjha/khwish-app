package com.khwish.app.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class HomePageResponse extends BaseResponse {

    @SerializedName("wallet_amount")
    @Expose
    private Double walletAmount;

    @SerializedName("events")
    @Expose
    private ArrayList<EventDetailsResponse> events;

    public HomePageResponse() {
    }

    public Double getWalletAmount() {
        return walletAmount;
    }

    public ArrayList<EventDetailsResponse> getEvents() {
        return events;
    }
}
