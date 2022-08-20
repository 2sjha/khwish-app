package com.khwish.app.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;

public class AddModifyGoalRequest implements Serializable {

    @SerializedName("event_id")
    @Expose
    private UUID eventId;

    @SerializedName("goal_id")
    @Expose
    private UUID goalId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("total_amount")
    @Expose
    private Integer totalAmount;

    public AddModifyGoalRequest() {
    }

    public AddModifyGoalRequest(UUID eventId, String name, String description, Integer totalAmount) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.totalAmount = totalAmount;
    }

    public AddModifyGoalRequest(UUID eventId, UUID goalId, String name, String description, Integer totalAmount) {
        this.eventId = eventId;
        this.goalId = goalId;
        this.name = name;
        this.description = description;
        this.totalAmount = totalAmount;
    }
}
