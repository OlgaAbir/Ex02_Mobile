package Models;

import android.os.Parcel;
import android.os.Parcelable;

public class DareCoins implements Parcelable {

    private String mAmount;
    private String mPrice;

    public DareCoins(){}

    protected DareCoins(Parcel in) {
        mAmount = in.readString();
        mPrice = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAmount);
        dest.writeString(mPrice);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DareCoins> CREATOR = new Creator<DareCoins>() {
        @Override
        public DareCoins createFromParcel(Parcel in) {
            return new DareCoins(in);
        }

        @Override
        public DareCoins[] newArray(int size) {
            return new DareCoins[size];
        }
    };

    public String getAmount()
    {
        return mAmount;
    }

    public String getPrice()
    {
        return mPrice;
    }

    public void setAmount(String amount)
    {
        mAmount = amount;
    }

    public void setPrice(String price)
    {
        mPrice = price;
    }

    @Override
    public String toString() {
        return "DareCoins{" +
                "Amount='" + mAmount + '\'' +
                ", Price='" + mPrice + '\'' +
                '}';
    }
}
