package Models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by user on 28-Apr-18.
 */

// An object that represents a dare between 2 users.
public class Dare implements Parcelable {
    public Dare() {}

    // TODO: add Dare name implementation
    private String mDareName;
    private String mCreaterID; // The id of the creating user
    private String mAttemptingUserID;
    private String mDescriptionImgURL; // The firebase storage url of the image that describes the dare
    private String mCompletionImgURL; // The firebase storage url of the image that is used as evidence of completion
    private float mBuyInCost; // The amount of money used to buy in to attempt the dare
    private Date mStartDate; // The date the dare was accepted
    private Date mEndDate; // The date the dare was succeeded
    private DareState mDareState = DareState.Available;
    private Review[] mReviews = new Review[] {};

    protected Dare(Parcel in) {
        mCreaterID = in.readString();
        mAttemptingUserID = in.readString();
        mDescriptionImgURL = in.readString();
        mCompletionImgURL = in.readString();
        mBuyInCost = in.readFloat();
        mReviews = (Review[])in.readParcelableArray(getClass().getClassLoader());
        long tempDate = in.readLong();
        mStartDate = tempDate == -1 ? null : new Date(tempDate);
        tempDate = in.readLong();
        mEndDate = tempDate == -1 ? null : new Date(tempDate);
        mDareState = DareState.Available;  //TODO: temp, mDareState = (darestate)in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCreaterID);
        dest.writeString(mAttemptingUserID);
        dest.writeString(mDescriptionImgURL);
        dest.writeString(mCompletionImgURL);
        dest.writeFloat(mBuyInCost);
        dest.writeParcelableArray(mReviews, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeLong(mStartDate != null ? mStartDate.getTime() : -1);
        dest.writeLong(mEndDate != null ? mEndDate.getTime() : -1);
        dest.writeSerializable(mDareState);
    }

    @Override
    public int describeContents() {
        return 0;
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


    public String getmDareName(){return mDareName;}
    public String getmCreaterID() {
        return mCreaterID;
    }

    public void setmCreaterID(String mCreaterID) {
        this.mCreaterID = mCreaterID;
    }

    public String getmAttemptingUserID() {
        return mAttemptingUserID;
    }

    public void setmAttemptingUserID(String mAttemptingUserID) {
        this.mAttemptingUserID = mAttemptingUserID;
    }

    public String getmDescriptionImgURL() {
        return mDescriptionImgURL;
    }

    public void setmDescriptionImgURL(String mDescriptionImgURL) {
        this.mDescriptionImgURL = mDescriptionImgURL;
    }

    public String getmCompletionImgURL() {
        return mCompletionImgURL;
    }

    public void setmCompletionImgURL(String mCompletionImgURL) {
        this.mCompletionImgURL = mCompletionImgURL;
    }

    public float getmBuyInCost() {
        return mBuyInCost;
    }

    public void setmBuyInCost(float mBuyInCost) {
        this.mBuyInCost = mBuyInCost;
    }

    public Date getmStartDate() {
        return mStartDate;
    }

    public void setmStartDate(Date mStartDate) {
        this.mStartDate = mStartDate;
    }

    public Date getmEndDate() {
        return mEndDate;
    }

    public void setmEndDate(Date mEndDate) {
        this.mEndDate = mEndDate;
    }

    public DareState getmDareState() {
        return mDareState;
    }

    public void setmDareState(DareState mDareState) {
        this.mDareState = mDareState;
    }

    @Override
    public String toString() {
        return "Dare{" +
                ", mCreaterID='" + mCreaterID + '\'' +
                ", mAttemptingUserID='" + mAttemptingUserID + '\'' +
                ", mDescriptionImgURL='" + mDescriptionImgURL + '\'' +
                ", mCompletionImgURL='" + mCompletionImgURL + '\'' +
                ", mBuyInCost=" + mBuyInCost +
                ", mStartDate=" + mStartDate +
                ", mEndDate=" + mEndDate +
                ", mDareState=" + mDareState +
                ", mReviews=" + Arrays.toString(mReviews) +
                '}';
    }
}
