package com.agnet.leteApp.fragments.main.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.main.sales.CartFragment;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.models.Cart;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alicephares on 8/5/16.
 */
public class ReceiptProductsAdapter extends RecyclerView.Adapter<ReceiptProductsAdapter.ViewHolder> {

    private List<Cart> products = Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private int locateId;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private List productlist = new ArrayList();
    private int cartItemCounts = 0;
    private int index = -1;
    private DatabaseHandler _dbHandler;
    private CartFragment cartFragment;
    private static int SPLASH_TIME_OUT = 5000;



    // Provide a suitable constructor (depends on the kind of dataset)
    public ReceiptProductsAdapter(Context c, List<Cart> products, CartFragment cartFragment) {
        this.products = products;
        this.inflator = LayoutInflater.from(c);
        this.c = c;
        this.cartFragment = cartFragment;

        _preferences = c.getSharedPreferences("SharedProductsData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        _dbHandler = new DatabaseHandler(c);

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = inflator.inflate(R.layout.card_receipt_product, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Cart currentProduct = products.get(position);

        final DecimalFormat formatter =  new DecimalFormat("#,###,##0.00");

        holder.mName.setText(currentProduct.getName());
        holder.mPrice.setText("TZS:" +formatter.format(currentProduct.getAmount()));
        holder.mQnty.setText("" + currentProduct.getQuantity());


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper;
        public RelativeLayout mQntyChangeBtn;
        public TextView mName, mPrice;
        public ImageView mImg, mRemoveCartProductBtn;
        public TextView mQnty, mSku;
        public Button mDecrementBtn, mIncrementBtn;


        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.shop_wrapper);
            mName = view.findViewById(R.id.name);
            mPrice = view.findViewById(R.id.price);
            mQnty = view.findViewById(R.id.quantity);
        }

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void filterList(List<Cart> filterdProducts) {
        this.products = filterdProducts;
        notifyDataSetChanged();

    }

    public int getImage(String imageName) {

        int drawableResourceId = c.getResources().getIdentifier(imageName, "drawable", c.getPackageName());

        return drawableResourceId;
    }

    public void removeAt(int position) {
        products.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, products.size());
    }


}