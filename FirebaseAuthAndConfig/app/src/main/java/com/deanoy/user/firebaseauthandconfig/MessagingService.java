package com.deanoy.user.firebaseauthandconfig;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import Models.AdvancedNotificationData;
import Models.MessagingData;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "PushNotificationService";
    private static MessagingData mMessagingData = new MessagingData();

    public MessagingService() {

    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "onMessageReceived() >>");
        String title;
        String body;

        RemoteMessage.Notification notification;

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
        buildNotification();

        Log.e(TAG, "onMessageReceived() <<");
    }

    private void parseData(RemoteMessage remoteMessage) {
        Log.e(TAG, "parseData() >>");

        //parse the data
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

        value = mMessagingData.getData().get("large_icon");
        if (value != null  && value.equals("icon")) {
            mMessagingData.setIcon(R.drawable.notification);
        }
        value = mMessagingData.getData().get("sound");
        if (value != null) {
            if (value.equals("alert")) {
                mMessagingData.setSoundRri(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            } else if (value.equals("ringtone")) {
                mMessagingData.setSoundRri(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
            } else{
                mMessagingData.setSoundRri(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mMessagingData.setPendingIntent(PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT));

        value = mMessagingData.getData().get("action");
        if (value != null) {
            if (value.contains("go to dares")) {
                intent = new Intent(this, AllProductsActivity.class);

                if(mMessagingData.getData().containsKey("sale"))
                {
                    AdvancedNotificationData.getInstance().setSale(Integer.parseInt(mMessagingData.getData().get("sale").toString()));
                }

                if(mMessagingData.getData().containsKey("reviewBonus"))
                {
                    AdvancedNotificationData.getInstance().setReviewBonus(Integer.parseInt(mMessagingData.getData().get("reviewBonus")));
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mMessagingData.setPendingIntent(PendingIntent.getActivity(this, 0 , intent,
                        PendingIntent.FLAG_ONE_SHOT));
                mMessagingData.addAction(new NotificationCompat.Action(R.drawable.moneygun,"Go to dare!", mMessagingData.getPendingIntent()));
            }
        }
        Log.e(TAG, "parseData() <<");
    }

    private void buildNotification() {
        Log.e(TAG, "buildNotification() >>");
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, null)
                        .setContentTitle(mMessagingData.getTitle())
                        .setContentText(mMessagingData.getBody())
                        .setContentIntent(mMessagingData.getPendingIntent())
                        .setSmallIcon(R.drawable.small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),mMessagingData.getIcon()))
                        .setSound(mMessagingData.getSoundRri())
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setOngoing(false);

        for(NotificationCompat.Action action: mMessagingData.getActions()) {
            notificationBuilder.addAction(action);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 , notificationBuilder.build());
        mMessagingData.clearActions(); // Clear actions for next notification
        Log.e(TAG, "buildNotification() <<");
    }
}
