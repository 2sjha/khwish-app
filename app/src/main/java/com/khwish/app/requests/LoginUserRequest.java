package com.khwish.app.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginUserRequest implements Serializable {

    @SerializedName("id_token")
    @Expose
    private String idToken;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;

    @SerializedName("email")
    @Expose
    private String email;

    public LoginUserRequest(String idToken, String name, String phoneNumber, String email) {
        this.idToken = idToken;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}
