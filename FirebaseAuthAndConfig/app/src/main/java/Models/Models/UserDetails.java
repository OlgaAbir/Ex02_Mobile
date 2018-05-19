package Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDetails implements Parcelable {

    private int mBalance = 600;
    private List<String> mPurchasedDareIds = new ArrayList<>();
    private List<String> mCompletedDareIds = new ArrayList<>();

    public UserDetails() {}

    protected UserDetails(Parcel in) {
        mBalance = in.readInt();

        in.readStringList(mPurchasedDareIds);
        if(mPurchasedDareIds == null) {
            mPurchasedDareIds = new ArrayList<>();
        }

        in.readStringList(mCompletedDareIds);
        if(mCompletedDareIds == null) {
            mCompletedDareIds = new ArrayList<>();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mBalance);
        dest.writeStringList(mPurchasedDareIds);
        dest.writeStringList(mCompletedDareIds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };

    @Override
    public String toString() {
        return "UserDetails{" +
                "mBalance=" + mBalance +
                ", mPurchasedDareIds=" + mPurchasedDareIds +
                ", mCompletedDareIds=" + mCompletedDareIds +
                '}';
    }

    public int getBalance() {
        return mBalance;
    }

    public void setBalance(int mBalance) {
        this.mBalance = mBalance;
    }

    public List<String> getPurchasedDareIds() {
        return mPurchasedDareIds;
    }

    public void setPurchasedDareIds(List<String> mPurchasedDareIds) {
        this.mPurchasedDareIds = mPurchasedDareIds;
    }

    public List<String> getCompletedDareIds() {
        return mCompletedDareIds;
    }

    public void setCompletedDareIds(List<String> mCompletedDareIds) {
        this.mCompletedDareIds = mCompletedDareIds;
    }
}
