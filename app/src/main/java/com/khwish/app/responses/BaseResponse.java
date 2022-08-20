package com.khwish.app.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseResponse implements Serializable {

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("message")
    @Expose
    private String message;

    BaseResponse() {
    }

    public boolean isSuccess() {
        return success != null ? success : false;
    }

    public String getMessage() {
        return message;
    }
}