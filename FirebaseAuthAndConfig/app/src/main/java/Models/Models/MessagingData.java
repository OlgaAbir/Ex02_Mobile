package Models;

import android.media.RingtoneManager;
import android.net.Uri;

import java.util.Map;

public class MessagingData {
    private String mTitle;
    private String mBody;
    private int mIconId;
    private Uri mSoundRri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private Map<String, String> mData;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        this.mBody = body;
    }

    public int getIcon() {
        return mIconId;
    }

    public void setIcon(int icon) {
        this.mIconId= icon;
    }

    public Uri getSoundRri() {
        return mSoundRri;
    }

    public void setSoundRri(Uri soundRri) {
        this.mSoundRri = soundRri;
    }

    public Map<String, String> getData() {
        return mData;
    }

    public void setData(Map<String, String> data) {
        this.mData= data;
    }
}
