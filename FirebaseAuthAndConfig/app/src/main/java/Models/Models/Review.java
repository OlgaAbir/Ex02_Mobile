package Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by user on 28-Apr-18.
 */

public class Review implements Parcelable {

    private String mText;
    private String mWriterID;
    private String mWriterName;
    private Date mCreationDate;

    public Review() {}

    public String getWriterName()
    {
        return mWriterName;
    }

    public void setWriterName(String writerName)
    {
        mWriterName = writerName;
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String getWriterID() {
        return mWriterID;
    }

    public void setWriterID(String mWriterID) {
        this.mWriterID = mWriterID;
    }

    public Date getCreationDate() {
        return mCreationDate;
    }

    public void setCreationDate(Date mCreationDate) {
        this.mCreationDate = mCreationDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Review(Parcel in) {
        mText = in.readString();
        mWriterID = in.readString();
        long tempDate = in.readLong();
        mCreationDate = tempDate == -1 ? null : new Date(tempDate);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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

    @Override
    public String toString() {
        return "Review{" +
                "mText='" + mText + '\'' +
                ", mWriterID='" + mWriterID + '\'' +
                ", mWriterName='" + mWriterName + '\'' +
                ", mCreationDate=" + mCreationDate +
                '}';
    }
}
