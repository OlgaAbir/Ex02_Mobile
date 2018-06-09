package Models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.deanoy.user.firebaseauthandconfig.DareCoinsStoreActivity;
import com.deanoy.user.firebaseauthandconfig.R;

public class DareCoinsViewHolder extends RecyclerView.ViewHolder {

    private Button mbtnBuy;
    private TextView mtvAmount;
    private TextView mtvPrice;

    public DareCoinsViewHolder(Context context, View view) {

        super(view);

        mbtnBuy = view.findViewById(R.id.btnBuyCoins);
        mtvAmount = view.findViewById(R.id.tvAmount);
        mtvPrice = view.findViewById(R.id.tvCoinsPrice);

        mbtnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               DareCoinsStoreActivity.BuyCoins(mtvPrice.getText().toString());
            }
        });
    }

    public TextView getAmount() {
        return mtvAmount;
    }

    public TextView getPrice()
    {
        return mtvPrice;
    }

    public void setAmount(String amount)
    {
        mtvAmount.setText("Amount: " + amount);
    }

    public void setPrice(String price)
    {
        mtvPrice.setText("Price: " + price + " NIS");
    }
}
