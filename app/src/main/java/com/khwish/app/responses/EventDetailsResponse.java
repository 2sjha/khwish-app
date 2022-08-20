package com.khwish.app.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class EventDetailsResponse extends BaseResponse {

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

    @SerializedName("event_date")
    @Expose
    private Long eventDate;

    @SerializedName("location_lat")
    @Expose
    private Float locationLat;

    @SerializedName("location_long")
    @Expose
    private Float locationLong;

    @SerializedName("collected_amount")
    @Expose
    private Double collectedAmount;

    @SerializedName("total_amount")
    @Expose
    private Double totalAmount;

    @SerializedName("invite_message")
    @Expose
    private String inviteMessage;

    @SerializedName("goals_details")
    @Expose
    private ArrayList<GoalDetailsResponse> goalDetails;

    public EventDetailsResponse() {
    }

    public void sort() {
        if (goalDetails != null) {
            Collections.sort(goalDetails, (o1, o2) -> (int) (o2.getCreatedAt() - o1.getCreatedAt()));
        }
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

    public Long getEventDate() {
        return eventDate;
    }

    public Float getLocationLat() {
        return locationLat;
    }

    public Float getLocationLong() {
        return locationLong;
    }

    public Double getCollectedAmount() {
        return collectedAmount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public String getInviteMessage() {
        return inviteMessage;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public ArrayList<GoalDetailsResponse> getGoalDetails() {
        return goalDetails;
    }
}