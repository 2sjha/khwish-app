package com.khwish.app.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class GoalDetailsResponse extends BaseResponse {

    @SerializedName("created_at")
    @Expose
    private Long createdAt;

    @SerializedName("id")
    @Expose
    private UUID id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("collected_amount")
    @Expose
    private double collectedAmount;

    @SerializedName("total_amount")
    @Expose
    private double totalAmount;

    public GoalDetailsResponse() {
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getCollectedAmount() {
        return collectedAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Long getCreatedAt() {
        return createdAt;
    }
}