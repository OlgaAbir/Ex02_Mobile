package Models;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.HashMap;
import java.util.Map;


public class AnalyticsManager {
    private static String TAG = "AnalyticsManager";
    private static AnalyticsManager mInstance = null;
    public enum eDareEventType { DareSelected, DarePurchase, ImageSelection, ImageUpload }

    private FirebaseAnalytics mFirebaseAnalytics;

    private AnalyticsManager() {

    }

    public static AnalyticsManager getInstance() {

        if (mInstance == null) {
            mInstance = new AnalyticsManager();
        }
        return (mInstance);
    }

    public void init(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void setUserID(String id) {
        mFirebaseAnalytics.setUserId(id);
    }


    public void setUserProperty(String name, String value) {
        Log.e(TAG, "Setting user property " + name + ": " + value);
        mFirebaseAnalytics.setUserProperty(name, value);
    }

    public void trackSignInEvent(String signInMethod) {
        Log.e(TAG, "Tracking sign in event with sign in method: " + signInMethod);
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.METHOD, signInMethod);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, params);
    }

    public void trackFilterMethod(String method, String value) {
        Log.e(TAG, "Tracking filter method: " + method + ". With value: " + value);
        Bundle params = new Bundle();
        params.putString("filter_method", method);
        params.putString("filter_value", value);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, params);
    }

    public void trackSortingMethod(String method) {
        Log.e(TAG, "Tracking sorting method: " + method);
        Bundle params = new Bundle();
        params.putString("sorting_method", method);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, params);
    }

    public void trackDareEvents(eDareEventType dareEventType, Dare dare) {
        Log.e(TAG, "Tracking dare event for dare: " + dare.toString());
        Bundle params = new Bundle();

        params.putString("dare_name", dare.getDareName());
        params.putString("dare_description", dare.getDescription());
        params.putString("creator_name", dare.getCreaterName());
        params.putString("attempting_users_count", Integer.toString(dare.getAttemptingUserID().size()));
        params.putString("completed_users_count", Integer.toString(dare.getCompletedUserIds().size()));
        params.putString("buy_in_cost", Integer.toString(dare.getBuyInCost()));
        params.putString("profit", Integer.toString(dare.getProfit()));


        switch (dareEventType) {
            case DareSelected:
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
                break;
            case DarePurchase:
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, params);
                break;
            case ImageSelection:
                mFirebaseAnalytics.logEvent("image_selected", params);
                break;
            case ImageUpload:
                mFirebaseAnalytics.logEvent("image_uploaded", params);
                break;
        }
    }
}
