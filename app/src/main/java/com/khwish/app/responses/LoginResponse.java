package com.khwish.app.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class LoginResponse extends BaseResponse {

    @SerializedName("user_id")
    @Expose
    private UUID userId;

    @SerializedName("user_name")
    @Expose
    private String userName;

    @SerializedName("is_new_user")
    @Expose
    private Boolean isNewUser;

    @SerializedName("auth_token")
    @Expose
    private String authToken;

    @SerializedName("auth_token_expires_at")
    @Expose
    private Long authTokenExpiresAt;

    public UUID getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isNewUser() {
        return isNewUser != null ? isNewUser : false;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Long getAuthTokenExpiresAt() {
        return authTokenExpiresAt;
    }

}