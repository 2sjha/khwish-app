package com.khwish.app.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;

public class LogoutUserRequest implements Serializable {

    @SerializedName("user_id")
    @Expose
    private UUID userId;

    public LogoutUserRequest(UUID userId) {
        this.userId = userId;
    }
}