package Models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 28-Apr-18.
 */

// An object that represents a dare between 2 users.
public class Dare implements Parcelable {
    public Dare() {}

    //Creator details
    private String mCreaterID;
    private String mCreaterEmail;
    private String mCreaterName;

    //Dare details
    private String mDareId;
    private String mDareName;
    private String mDescription;
    private ArrayList<String> mAttemptingUserID;
    private float mBuyInCost; // The amount of money used to buy in to attempt the dare

    //Dare progress
    private String mDescriptionImgURL; // The firebase storage url of the image that describes the dare
    private String mCompletionImgURL; // The firebase storage url of the image that is used as evidence of completion
    private DareState mDareState = DareState.Available;

    protected Dare(Parcel in) {
        mDareId = in.readString();
        mCreaterID = in.readString();
        mCreaterEmail = in.readString();
        mCreaterName = in.readString();
        mDareName = in.readString();
        mDescription = in.readString();
        mAttemptingUserID = in.readArrayList(getClass().getClassLoader());
        mDescriptionImgURL = in.readString();
        mCompletionImgURL = in.readString();
        mBuyInCost = in.readFloat();
        mDareState = (DareState)in.readSerializable();
        //mDareState = DareState.Available TODO: remove this before handing assignment
    }

    //TODO: remove this before handing assignment
    public Dare(String createrID, String createrEmail, String createrName, String dareName, String description, float buyInCost, int hoursToFinish, String descriptionImgURL) {
        this.mCreaterID = createrID;
        this.mCreaterEmail = createrEmail;
        this.mCreaterName = createrName;
        this.mDareName = dareName;
        this.mDescription = description;
        this.mAttemptingUserID = null;
        this.mBuyInCost = buyInCost;
        this.mDescriptionImgURL = descriptionImgURL;
        this.mCompletionImgURL = null;
        this.mDareState = DareState.Available;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDareId);
        dest.writeString(mCreaterID);
        dest.writeString(mCreaterEmail);
        dest.writeString(mCreaterName);
        dest.writeString(mDareName);
        dest.writeString(mDescription);
        dest.writeStringList(mAttemptingUserID);
        dest.writeString(mDescriptionImgURL);
        dest.writeString(mCompletionImgURL);
        dest.writeFloat(mBuyInCost);
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

    //toString function - Mostly for debugging
    @Override
    public String toString() {
        return "Dare{" +
                "mDareID='" + mDareId + '\'' +
                "mCreaterID='" + mCreaterID + '\'' +
                ", mCreaterEmail='" + mCreaterEmail + '\'' +
                ", mCreaterName='" + mCreaterName + '\'' +
                ", mDareName='" + mDareName + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mAttemptingUserID=" + mAttemptingUserID +
                ", mBuyInCost=" + mBuyInCost +
                ", mDescriptionImgURL='" + mDescriptionImgURL + '\'' +
                ", mCompletionImgURL='" + mCompletionImgURL + '\'' +
                ", mDareState=" + mDareState +
                '}';
    }

    public String getCreaterID() {
        return mCreaterID;
    }

    public void setCreaterID(String mCreaterID) {
        this.mCreaterID = mCreaterID;
    }

    public String getDescriptionImgURL() {
        return mDescriptionImgURL;
    }

    public void setDescriptionImgURL(String mDescriptionImgURL) {
        this.mDescriptionImgURL = mDescriptionImgURL;
    }

    public String getCompletionImgURL() {
        return mCompletionImgURL;
    }

    public void setCompletionImgURL(String mCompletionImgURL) {
        this.mCompletionImgURL = mCompletionImgURL;
    }

    public float getBuyInCost() {
        return mBuyInCost;
    }

    public void setBuyInCost(float mBuyInCost) {
        this.mBuyInCost = mBuyInCost;
    }

    public float getProfit() {
        return 2 * mBuyInCost;
    }

    public DareState getDareState() {
        return mDareState;
    }

    public void setDareState(DareState mDareState) {
        this.mDareState = mDareState;
    }

    public String getCreaterEmail() {
        return mCreaterEmail;
    }

    public void setCreaterEmail(String mCreaterEmail) {
        this.mCreaterEmail = mCreaterEmail;
    }

    public String getCreaterName() {
        return mCreaterName;
    }

    public void setCreaterName(String mCreaterName) {
        this.mCreaterName = mCreaterName;
    }

    public String getDareName() {
        return mDareName;
    }

    public void setDareName(String mDareName) {
        this.mDareName = mDareName;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public ArrayList<String> getAttemptingUserID() {
        return mAttemptingUserID;
    }

    public void setAttemptingUserID(ArrayList<String> mAttemptingUserID) {
        this.mAttemptingUserID = mAttemptingUserID;
    }

    public String getDareId() {
        return mDareId;
    }

    public void setDareId(String mDareId) {
        this.mDareId = mDareId;
    }
}
