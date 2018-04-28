import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by user on 28-Apr-18.
 */

public class Review implements Parcelable {
    public Review() {}

    private String mId;
    private String mText;
    private String mWriterID;
    private Date mCreationDate;

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmText() {
        return mText;
    }

    public void setmText(String mText) {
        this.mText = mText;
    }

    public String getmWriterID() {
        return mWriterID;
    }

    public void setmWriterID(String mWriterID) {
        this.mWriterID = mWriterID;
    }

    public Date getmCreationDate() {
        return mCreationDate;
    }

    public void setmCreationDate(Date mCreationDate) {
        this.mCreationDate = mCreationDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //TODO
    }

    public static final Creator<Dare> CREATOR = new Creator<Dare>() {
        @Override
        public Dare createFromParcel(Parcel in) {
            return new Dare(in);
        }

        @Override
        public Dare[] newArray(int size) {
            return new Dare[size];
        }
    };
}
