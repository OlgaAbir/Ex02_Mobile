package Models;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.HashMap;
import java.util.Map;
import com.appsee.Appsee;


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
        Appsee.start();
    }

    public void setUserID(String id) {
        mFirebaseAnalytics.setUserId(id);
        Appsee.setUserId(id);
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

        Map<String, Object> props = new HashMap<String, Object>();
        props.put(FirebaseAnalytics.Param.METHOD, signInMethod);
        Appsee.addEvent(FirebaseAnalytics.Event.SIGN_UP, props);
    }

    public void trackFilterMethod(String method, String value) {
        Log.e(TAG, "Tracking filter method: " + method + ". With value: " + value);
        Bundle params = new Bundle();
        params.putString("filter_method", method);
        params.putString("filter_value", value);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, params);

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("filter_method", method);
        props.put("filter_value", value);
        Appsee.addEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, props);
    }

    public void trackSortingMethod(String method) {
        Log.e(TAG, "Tracking sorting method: " + method);
        Bundle params = new Bundle();
        params.putString("sorting_method", method);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, params);

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("sorting_method", method);
        Appsee.addEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, props);
    }

    public void trackDareEvents(eDareEventType dareEventType, Dare dare) {
        Log.e(TAG, "Tracking dare event: " + getDareTypeStr(dareEventType) + "for dare: " + dare.toString());
        Bundle params = new Bundle();
        Map<String, Object> props = new HashMap<String, Object>();

        params.putString("dare_name", dare.getDareName());
        params.putString("dare_description", dare.getDescription());
        params.putString("creator_name", dare.getCreaterName());
        params.putString("attempting_users_count", Integer.toString(dare.getAttemptingUserID().size()));
        params.putString("completed_users_count", Integer.toString(dare.getCompletedUserIds().size()));
        params.putString("buy_in_cost", Integer.toString(dare.getBuyInCost()));
        params.putString("profit", Integer.toString(dare.getProfit()));

        props.put("dare_name", dare.getDareName());
        props.put("dare_description", dare.getDescription());
        props.put("creator_name", dare.getCreaterName());
        props.put("attempting_users_count", Integer.toString(dare.getAttemptingUserID().size()));
        props.put("completed_users_count", Integer.toString(dare.getCompletedUserIds().size()));
        props.put("buy_in_cost", Integer.toString(dare.getBuyInCost()));
        props.put("profit", Integer.toString(dare.getProfit()));

        switch (dareEventType) {
            case DareSelected:
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
                Appsee.addEvent(FirebaseAnalytics.Event.SELECT_CONTENT, props);
                break;
            case DarePurchase:
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, params);
                Appsee.addEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, props);
                break;
            case ImageSelection:
                mFirebaseAnalytics.logEvent("image_selected", params);
                Appsee.addEvent("image_selected", props);
                break;
            case ImageUpload:
                mFirebaseAnalytics.logEvent("image_uploaded", params);
                Appsee.addEvent("image_uploaded", props);
                break;
        }
    }

    public void trackAddReview(Review review) {
        Log.e(TAG, "Tracking add review: " + review);
        Bundle params = new Bundle();
        params.putString("text", review.getText());
        params.putString("writer_name", review.getWriterName());
        mFirebaseAnalytics.logEvent("add_review", params);

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("text", review.getText());
        props.put("writer_name", review.getWriterName());
        Appsee.addEvent("add_review", props);
    }

    // Mostly for debugging
    private String getDareTypeStr(eDareEventType dareEventType) {
        String dareTypeStr = "";

        switch (dareEventType) {
            case DareSelected:
                dareTypeStr = "DareSelected";
                break;
            case DarePurchase:
                dareTypeStr = "DarePurchased";
                break;
            case ImageSelection:
                dareTypeStr = "ImageSelection";
                break;
            case ImageUpload:
                dareTypeStr = "ImageUpload";
                break;
        }

        return dareTypeStr;
    }
}
