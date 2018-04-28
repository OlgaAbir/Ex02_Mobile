import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 28-Apr-18.
 */

// An object that represents a dare between 2 users.
public class Dare implements Parcelable {
    public Dare() {}

    private String mId; // Dare ID
    private String mCreaterID; // The id of the creating user
    private String mAttemptingUserID;
    private String mDescriptionImgURL; // The firebase storage url of the image that describes the dare
    private String mCompletionImgURL; // The firebase storage url of the image that is used as evidence of completion
    private float mBuyInCost; // The amount of money used to buy in to attempt the dare
    private Date mStartDate; // The date the dare was accepted
    private Date mEndDate; // The date the dare was succeeded
    private DareState mDareState = DareState.Available;

    protected Dare(Parcel in) {
        mId = in.readString();
        mCreaterID = in.readString();
        mAttemptingUserID = in.readString();
        mDescriptionImgURL = in.readString();
        mCompletionImgURL = in.readString();
        mBuyInCost = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mCreaterID);
        dest.writeString(mAttemptingUserID);
        dest.writeString(mDescriptionImgURL);
        dest.writeString(mCompletionImgURL);
        dest.writeFloat(mBuyInCost);
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

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

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
}
