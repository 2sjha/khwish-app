package com.khwish.app.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserProfileResponse extends BaseResponse {

    @SerializedName("user_details")
    @Expose
    private UserDetails userDetails;

    @SerializedName("bank_details")
    @Expose
    private BankDetails bankDetails;

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public BankDetails getBankDetails() {
        return bankDetails;
    }

    public static class UserDetails implements Serializable {

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

        @SerializedName("registered_via")
        @Expose
        private String registeredVia;

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public Long getDateOfBirth() {
            return dateOfBirth;
        }

        public String getRegisteredVia() {
            return registeredVia;
        }
    }

    public static class BankDetails implements Serializable {

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

        public String getUpiVpa() {
            return upiVpa;
        }

        public Long getAccountNumber() {
            return accountNumber;
        }

        public String getIfscCode() {
            return ifscCode;
        }

        public String getAccountHoldersName() {
            return accountHoldersName;
        }
    }
}
