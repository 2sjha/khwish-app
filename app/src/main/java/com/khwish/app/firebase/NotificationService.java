package com.khwish.app.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.khwish.app.R;
import com.khwish.app.activities.HomeActivity;
import com.khwish.app.constants.AuthConstants;
import com.khwish.app.requests.UserNotificationTokenRequest;
import com.khwish.app.responses.BaseResponse;
import com.khwish.app.retrofit.ServerApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = NotificationService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        sendNotificationTokenToServer(new UserNotificationTokenRequest(s));
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            sendNotification(notification.getTitle(), notification.getBody());
        }
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.contributions_received_notification_channel);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private static void sendNotificationTokenToServer(UserNotificationTokenRequest userNotificationTokenRequest) {
        sendNotificationTokenToServer(null, userNotificationTokenRequest);
    }

    public static void sendNotificationTokenToServer(Context context, UserNotificationTokenRequest userNotificationTokenRequest) {
        if (userNotificationTokenRequest == null || userNotificationTokenRequest.getNotificationToken() == null) {
            Log.e(TAG, "Empty Token for User notification");
            return;
        }

        ServerApi.sendNotificationToken(userNotificationTokenRequest).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, "User Notification Token: " + userNotificationTokenRequest.getNotificationToken() + " sent to server.");

                    if (context != null) {
                        AuthConstants.USER_NOTIFICATION_TOKEN = userNotificationTokenRequest.getNotificationToken();
                        SharedPreferences.Editor spEditor = context.getSharedPreferences(AuthConstants.MAIN_SP_KEY, MODE_PRIVATE).edit();
                        spEditor.putString(AuthConstants.SP_USER_NOTIFICATION_TOKEN_KEY, userNotificationTokenRequest.getNotificationToken());
                        spEditor.apply();
                    }
                } else {
                    Log.e(TAG, "Couldn't send User Notification Token to server.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                Log.e(TAG, t.toString(), t);
            }
        });
    }
}