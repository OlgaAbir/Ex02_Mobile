package Models;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.deanoy.user.firebaseauthandconfig.DareCoinsStoreActivity;
import com.deanoy.user.firebaseauthandconfig.MainActivity;
import com.deanoy.user.firebaseauthandconfig.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

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
                if(FirebaseAuth.getInstance().getCurrentUser().isAnonymous())
                {
                    Toast.makeText(view.getContext(), "Please sign in/sign up to continue", Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    Intent i = new Intent(view.getContext() ,MainActivity.class);
                    view.getContext().startActivity(i);
                }
                else {
                    DareCoinsStoreActivity.BuyCoins(mtvPrice.getText().toString());
                }
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
