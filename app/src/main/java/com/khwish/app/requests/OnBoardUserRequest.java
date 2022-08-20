package com.khwish.app.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class OnBoardUserRequest {

    @SerializedName("user_id")
    @Expose
    private UUID userId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;

    @SerializedName("date_of_birth")
    @Expose
    private Long dateOfBirth;

    @SerializedName("bank_details")
    @Expose
    private BankDetails bankDetails;

    public OnBoardUserRequest(UUID userId, String name, String email, String phoneNumber, Long dateOfBirth, BankDetails bankDetails) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.bankDetails = bankDetails;
    }

    public static class BankDetails {

        @SerializedName("upi_vpa")
        @Expose
        private String upiVpa;

        @SerializedName("account_number")
        @Expose
        private Long accountNumber;

        @SerializedName("ifsc_code")
        @Expose
        private String ifscCode;

        @SerializedName("account_holders_name")
        @Expose
        private String accountHoldersName;

        public BankDetails(String upiVpa, Long accountNumber, String ifscCode, String accountHoldersName) {
            this.upiVpa = upiVpa;
            this.accountNumber = accountNumber;
            this.ifscCode = ifscCode;
            this.accountHoldersName = accountHoldersName;
        }
    }
}
