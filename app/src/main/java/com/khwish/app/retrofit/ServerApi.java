package com.khwish.app.retrofit;

import com.khwish.app.constants.AuthConstants;
import com.khwish.app.requests.AddModifyEventRequest;
import com.khwish.app.requests.AddModifyGoalRequest;
import com.khwish.app.requests.LoginUserRequest;
import com.khwish.app.requests.LogoutUserRequest;
import com.khwish.app.requests.OnBoardUserRequest;
import com.khwish.app.requests.UserNotificationTokenRequest;
import com.khwish.app.requests.WithdrawalRequest;
import com.khwish.app.responses.BaseResponse;
import com.khwish.app.responses.EventContributorsResponse;
import com.khwish.app.responses.EventDetailsResponse;
import com.khwish.app.responses.GoalDetailsResponse;
import com.khwish.app.responses.HomePageResponse;
import com.khwish.app.responses.LoginResponse;
import com.khwish.app.responses.UserProfileResponse;
import com.khwish.app.responses.WalletActivitiesResponse;

import java.util.UUID;

import retrofit2.Call;

public class ServerApi {

    public static Call<LoginResponse> loginUser(LoginUserRequest loginUserRequest) {
        return ServerAPIBuilder.getApiService().loginUser(loginUserRequest);
    }

    public static Call<LoginResponse> logoutUser(LogoutUserRequest logoutUserRequest) {
        return ServerAPIBuilder.getApiService().logoutUser(AuthConstants.USER_ID, AuthConstants.AUTH_TOKEN,
                AuthConstants.APP_VERSION, logoutUserRequest);
    }

    public static Call<BaseResponse> onBoardUser(OnBoardUserRequest onBoardUserRequest) {
        return ServerAPIBuilder.getApiService().onBoardUser(AuthConstants.USER_ID, AuthConstants.AUTH_TOKEN,
                AuthConstants.APP_VERSION, onBoardUserRequest);
    }

    public static Call<BaseResponse> sendNotificationToken(UserNotificationTokenRequest userNotificationTokenRequest) {
        return ServerAPIBuilder.getApiService().sendNotificationToken(AuthConstants.USER_ID, AuthConstants.AUTH_TOKEN,
                AuthConstants.APP_VERSION, userNotificationTokenRequest);
    }

    public static Call<UserProfileResponse> getUserProfile() {
        return ServerAPIBuilder.getApiService().getUserProfile(AuthConstants.USER_ID, AuthConstants.AUTH_TOKEN,
                AuthConstants.APP_VERSION);
    }

    public static Call<HomePageResponse> getHomePage() {
        return ServerAPIBuilder.getApiService().getHomepage(AuthConstants.USER_ID, AuthConstants.AUTH_TOKEN,
                AuthConstants.APP_VERSION);
    }

    public static Call<BaseResponse> withdraw(WithdrawalRequest withdrawalRequest) {
        return ServerAPIBuilder.getApiService().withdraw(AuthConstants.USER_ID, AuthConstants.AUTH_TOKEN,
                AuthConstants.APP_VERSION, withdrawalRequest);
    }

    public static Call<WalletActivitiesResponse> getWalletActivities() {
        return ServerAPIBuilder.getApiService().getWalletActivities(AuthConstants.USER_ID, AuthConstants.AUTH_TOKEN,
                AuthConstants.APP_VERSION);
    }

    public static Call<EventDetailsResponse> getEventDetails(UUID eventId) {
        return ServerAPIBuilder.getApiService().getEventDetails(AuthConstants.USER_ID, AuthConstants.AUTH_TOKEN,
                AuthConstants.APP_VERSION, eventId);
    }

    public static Call<EventDetailsResponse> addEvent(AddModifyEventRequest addEventRequest) {
        return ServerAPIBuilder.getApiService().addEvent(AuthConstants.USER_ID, AuthConstants.AUTH_TOKEN,
                AuthConstants.APP_VERSION, addEventRequest);
    }

    public static Call<EventDetailsResponse> modifyEvent(AddModifyEventRequest modifyEventRequest) {
        return ServerAPIBuilder.getApiService().modifyEvent(AuthConstants.USER_ID, AuthConstants.AUTH_TOKEN,
                AuthConstants.APP_VERSION, modifyEventRequest);
    }

    public static Call<EventContributorsResponse> getEventContributors(UUID eventId) {
        return ServerAPIBuilder.getApiService().getEventContributors(AuthConstants.USER_ID, AuthConstants.AUTH_TOKEN,
                AuthConstants.APP_VERSION, eventId);
    }

    public static Call<GoalDetailsResponse> addGoal(AddModifyGoalRequest addGoalRequest) {
        return ServerAPIBuilder.getApiService().addGoal(AuthConstants.USER_ID, AuthConstants.AUTH_TOKEN,
                AuthConstants.APP_VERSION, addGoalRequest);
    }

    public static Call<GoalDetailsResponse> modifyGoal(AddModifyGoalRequest modifyGoalRequest) {
        return ServerAPIBuilder.getApiService().modifyGoal(AuthConstants.USER_ID, AuthConstants.AUTH_TOKEN,
                AuthConstants.APP_VERSION, modifyGoalRequest);
    }
}
