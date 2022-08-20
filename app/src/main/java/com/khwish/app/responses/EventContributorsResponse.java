package com.khwish.app.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;

public class EventContributorsResponse extends BaseResponse {

    @SerializedName("contributions")
    @Expose
    private ArrayList<ContributionResponse> contributions;

    public EventContributorsResponse() {
    }

    public ArrayList<ContributionResponse> getContributions() {
        return contributions;
    }

    public void sort() {
        if (contributions != null) {
            Collections.sort(contributions, (o1, o2) -> (int) (o2.getCreatedAt() - o1.getCreatedAt()));
        }
    }

    public static class ContributionResponse {

        @SerializedName("created_at")
        @Expose
        private Long createdAt;

        @SerializedName("contributor_name")
        @Expose
        private String contributorName;

        @SerializedName("contribution_amount")
        @Expose
        private Double contributionAmount;

        @SerializedName("goal_name")
        @Expose
        private String goalName;

        public ContributionResponse() {
        }

        public String getContributorName() {
            return contributorName;
        }

        public Double getContributionAmount() {
            return contributionAmount;
        }

        public String getGoalName() {
            return goalName;
        }

        public Long getCreatedAt() {
            return createdAt;
        }
    }
}