package com.deanoy.user.firebaseauthandconfig;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;


public class InstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "PushTokenHandler";
    private static String deviceToken;

    @Override
    public void onTokenRefresh() {

        Log.e(TAG, "onTokenRefresh() >>");
        // Get updated InstanceID token.
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("dare_app");

        Log.e(TAG, "onTokenRefresh() << deviceToken="+deviceToken);
    }
}
