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

    public static int mBalance = 600;

    public static List<String> DaresIDs = new ArrayList<>();

    public UserDetails() {}

    public int getBalance() {return mBalance;}

    public void setBalance(int mBalance) {this.mBalance = mBalance;}

    @Override
    public int describeContents() {
        return 0;
    }

    protected UserDetails(Parcel in) {
        mBalance = in.readInt();
    }
    public static void getDaresFromDB() {
//        Log.e(TAG, "getDaresFromDB() >>" );
        FirebaseUser mLoggedInUser;
        mLoggedInUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = mLoggedInUser.getUid();
        DatabaseReference mUserDatabaseRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(userID).child(userID);
        // Read from the database
        mUserDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                Log.e(TAG, "onDataChange() >>" );
                DaresIDs.clear();
                for (DataSnapshot dareSnapshot: dataSnapshot.getChildren()) {
                    Log.e("UserDetails", "#$#Dare: " + dareSnapshot.getKey()); // Print for debugging TODO: remove before assigning
                    DaresIDs.add(dareSnapshot.getKey());
                }
                Log.e("UserDetails", "onDataChange() <<" );
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
//        Log.e(TAG, "getDaresFromDB() <<" );
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mBalance);
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        @Override
        public UserDetails[] newArray(int size) {return new UserDetails[size];}
    };

    /*@Override
    public String toString() {
        return "UserDetails{" +
                "mText='" + mText + '\'' +
                ", mUserID='" + mUserID + '\'' +
                ", mUserName='" + mUserName +
                '}';
    }*/
}
