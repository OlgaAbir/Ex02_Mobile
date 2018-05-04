package Models;

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

    protected Review(Parcel in) {
        mId = in.readString();
        mText = in.readString();
        mWriterID = in.readString();
        long tempDate = in.readLong();
        mCreationDate = tempDate == -1 ? null : new Date(tempDate);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mText);
        dest.writeString(mWriterID);
        dest.writeLong(mCreationDate != null ? mCreationDate.getTime() : -1);
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
