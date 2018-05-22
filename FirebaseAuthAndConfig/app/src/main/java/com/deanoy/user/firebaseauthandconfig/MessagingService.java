package com.deanoy.user.firebaseauthandconfig;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import Models.MessagingData;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "PushNotificationService";
    private static MessagingData mMessagingData = new MessagingData();

    public MessagingService() {

    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "onMessageReceived() >>");
        String title = "title";
        String body = "body";

        //int icon = R.drawable.ic_notifications_black_24dp;
        RemoteMessage.Notification notification;


        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());


        if (remoteMessage.getNotification() == null) {
            Log.e(TAG, "onMessageReceived() >> Notification is empty");
        } else {
            notification = remoteMessage.getNotification();
            title = notification.getTitle();
            body = notification.getBody();
            Log.e(TAG, "onMessageReceived() >> title: " + title + " , body="+body);
        }

        parseData(remoteMessage);

        Log.e(TAG, "onMessageReceived() <<");
    }

    private void parseData(RemoteMessage remoteMessage) {
        //parse the data
        mMessagingData.setSoundRri(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mMessagingData.setData(remoteMessage.getData());
        Log.e(TAG, "Message data : " + mMessagingData.getData());

        String value = mMessagingData.getData().get("title");
        if (value != null) {
            mMessagingData.setTitle(value);
        }

        value = mMessagingData.getData().get("body");
        if (value != null) {
            mMessagingData.setBody(value);
        }

        value = mMessagingData.getData().get("small_icon");
        if (value != null  && value.equals("alarm")) {
            mMessagingData.setIcon(R.drawable.ic_filter_24dp); //TODO: change image to a custom one
        }
        value = mMessagingData.getData().get("sound");
        if (value != null) {
            if (value.equals("alert")) {
                mMessagingData.setSoundRri(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            } else if (value.equals("ringtone")) {
                mMessagingData.setSoundRri(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
            }
        }

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, null)
                        .setContentTitle(mMessagingData.getTitle())
                        .setContentText(mMessagingData.getBody())
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(mMessagingData.getIcon())
                        .setSound(mMessagingData.getSoundRri());

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 , notificationBuilder.build());

        Log.e(TAG, "onMessageReceived() <<");
    }
}
