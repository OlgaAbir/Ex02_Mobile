package Models;


public class AdvancedNotificationData {
    private static AdvancedNotificationData mInstance;
    private int mSale; // discount in percents
    private int mReviewBonus; // bonus for review writing
    private String mFilterDares;

    private AdvancedNotificationData()
    {
    }

    public static AdvancedNotificationData getInstance()
    {
        if(mInstance == null)
        {
            mInstance = new AdvancedNotificationData();
        }

        return mInstance;
    }

    public int getSale()
    {
        return mSale;
    }

    public void setSale(int sale)
    {
        if(sale >= 0 && sale <= 100) {
            mSale = sale;
        }
    }

    public int getReviewBonus()
    {
        return mReviewBonus;
    }

    public void setReviewBonus(int bonusAmount)
    {
        mReviewBonus = bonusAmount;
    }

    public String getFilterDares()
    {
        return mFilterDares;
    }

    public void setFilterDares(String filter)
    {
        mFilterDares = filter;
    }
}
