package Models;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

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
    private ArrayList<String> mCompletedUserIds;
    private int mBuyInCost; // The amount of money used to buy in to attempt the dare

    //Dare progress
    private Bitmap mDescriptionImgBitmap; // The firebase storage url of the image that describes the dare

    protected Dare(Parcel in) {
        mDareId = in.readString();
        mCreaterID = in.readString();
        mCreaterEmail = in.readString();
        mCreaterName = in.readString();
        mDareName = in.readString();
        mDescription = in.readString();
        mBuyInCost = in.readInt();

        mAttemptingUserID = new ArrayList<>();
        in.readStringList(mAttemptingUserID);
        mCompletedUserIds = new ArrayList<>();
        in.readStringList(mCompletedUserIds);
    }

    //TODO: remove this before handing assignment
    public Dare(String createrID, String createrEmail, String createrName, String dareName, String description, int buyInCost) {
        this.mCreaterID = createrID;
        this.mCreaterEmail = createrEmail;
        this.mCreaterName = createrName;
        this.mDareName = dareName;
        this.mDescription = description;
        this.mAttemptingUserID = null;
        this.mCompletedUserIds = null;
        this.mBuyInCost = buyInCost;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDareId);
        dest.writeString(mCreaterID);
        dest.writeString(mCreaterEmail);
        dest.writeString(mCreaterName);
        dest.writeString(mDareName);
        dest.writeString(mDescription);
        dest.writeInt(mBuyInCost);
        dest.writeStringList(mAttemptingUserID);
        dest.writeStringList(mCompletedUserIds);
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
                '}';
    }

    public String getCreaterID() {
        return mCreaterID;
    }

    public void setCreaterID(String mCreaterID) {
        this.mCreaterID = mCreaterID;
    }

    public int getBuyInCost() {
        return mBuyInCost;
    }

    public void setBuyInCost(int mBuyInCost) {
        this.mBuyInCost = mBuyInCost;
    }

    public int getProfit() {
        return 2 * mBuyInCost;
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

    public void setCompletedUserIds(ArrayList<String> mCompletedUserIds) {
        this.mCompletedUserIds = mCompletedUserIds;
    }

    public ArrayList<String> getCompletedUserIds() {
        return mCompletedUserIds;
    }

    public String getDareId() {
        return mDareId;
    }

    public void setDareId(String mDareId) {
        this.mDareId = mDareId;
    }

    public Bitmap getDescriptionImgBitmap() {
        return mDescriptionImgBitmap;
    }

    public void setDescriptionImgBitmap(Bitmap descriptionImgBitmap) {
        this.mDescriptionImgBitmap = descriptionImgBitmap;
    }
}
