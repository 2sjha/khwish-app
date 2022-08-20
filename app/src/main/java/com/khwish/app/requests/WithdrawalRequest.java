package com.khwish.app.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WithdrawalRequest {

    @SerializedName("amount")
    @Expose
    private double amount;

    @SerializedName("method")
    @Expose
    private String method;

    public WithdrawalRequest(double amount, String method) {
        this.amount = amount;
        this.method = method;
    }
}
