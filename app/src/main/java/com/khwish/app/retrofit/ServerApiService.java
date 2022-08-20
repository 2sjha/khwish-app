package com.khwish.app.retrofit;

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
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerApiService {

    @POST("/user/login")
    Call<LoginResponse> loginUser(@Body LoginUserRequest loginUserRequest);

    @POST("/user/on-board")
    Call<BaseResponse> onBoardUser(@Header("user-id") UUID userId, @Header("auth-token") String authToken,
                                   @Header("app-version") String appVersion, @Body OnBoardUserRequest onBoardUserRequest);

    @GET("/user/profile")
    Call<UserProfileResponse> getUserProfile(@Header("user-id") UUID userId, @Header("auth-token") String authToken,
                                             @Header("app-version") String appVersion);

    @GET("/user/home")
    Call<HomePageResponse> getHomepage(@Header("user-id") UUID userId, @Header("auth-token") String authToken,
                                       @Header("app-version") String appVersion);

    @POST("/user/notification-token")
    Call<BaseResponse> sendNotificationToken(@Header("user-id") UUID userId, @Header("auth-token") String authToken,
                                             @Header("app-version") String appVersion,
                                             @Body UserNotificationTokenRequest userNotificationTokenRequest);

    @POST("/user/withdraw")
    Call<BaseResponse> withdraw(@Header("user-id") UUID userId, @Header("auth-token") String authToken,
                                @Header("app-version") String appVersion, @Body WithdrawalRequest withdrawalRequest);

    @GET("/user/wallet-activities")
    Call<WalletActivitiesResponse> getWalletActivities(@Header("user-id") UUID userId, @Header("auth-token") String authToken,
                                                       @Header("app-version") String appVersion);

    @POST("/user/logout")
    Call<LoginResponse> logoutUser(@Header("user-id") UUID userId, @Header("auth-token") String authToken,
                                   @Header("app-version") String appVersion, @Body LogoutUserRequest logoutUserRequest);

    @GET("/events/details")
    Call<EventDetailsResponse> getEventDetails(@Header("user-id") UUID userId, @Header("auth-token") String authToken,
                                               @Header("app-version") String appVersion, @Query("event-id") UUID eventId);

    @POST("/events/add")
    Call<EventDetailsResponse> addEvent(@Header("user-id") UUID userId, @Header("auth-token") String authToken,
                                        @Header("app-version") String appVersion, @Body AddModifyEventRequest addEventRequest);

    @POST("/events/modify")
    Call<EventDetailsResponse> modifyEvent(@Header("user-id") UUID userId, @Header("auth-token") String authToken,
                                           @Header("app-version") String appVersion,
                                           @Body AddModifyEventRequest modifyEventRequest);

    @GET("/events/contributors")
    Call<EventContributorsResponse> getEventContributors(@Header("user-id") UUID userId, @Header("auth-token") String authToken,
                                                         @Header("app-version") String appVersion,
                                                         @Query("event-id") UUID eventId);

    @POST("/goals/add")
    Call<GoalDetailsResponse> addGoal(@Header("user-id") UUID userId, @Header("auth-token") String authToken,
                                      @Header("app-version") String appVersion, @Body AddModifyGoalRequest addGoalRequest);

    @POST("/goals/modify")
    Call<GoalDetailsResponse> modifyGoal(@Header("user-id") UUID userId, @Header("auth-token") String authToken,
                                         @Header("app-version") String appVersion, @Body AddModifyGoalRequest modifyGoalRequest);
}
