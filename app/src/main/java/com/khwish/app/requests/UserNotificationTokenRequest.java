package com.khwish.app.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserNotificationTokenRequest {

    @SerializedName("notification_token")
    @Expose
    private String notificationToken;

    public UserNotificationTokenRequest(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    public String getNotificationToken() {
        return notificationToken;
    }
}
