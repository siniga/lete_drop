package com.agnet.leteApp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.models.Cart;


import java.util.Collections;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by alicephares on 8/5/16.
 */
public class ConfirmCartAdapter extends RecyclerView.Adapter<ConfirmCartAdapter.ViewHolder> {

    private List<Cart> products = Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private int locateId;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int index = -1;
    private Fragment fragment;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;


    // Provide a suitable constructor (depends on the kind of dataset)
    public ConfirmCartAdapter(Context c, List<Cart> products, Fragment fragment) {
        this.products = products;
        this.inflator = LayoutInflater.from(c);
        this.fragment = fragment;
        this.c = c;


        _preferences = c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == -1)
            return VIEW_TYPES.tabHeader;
        else
            return VIEW_TYPES.tabContent;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view = null;

        switch (viewType) {
            case VIEW_TYPES.tabHeader:
                view = inflator.inflate(R.layout.card_confirm_cart_header, parent, false);
                break;
            case VIEW_TYPES.tabContent:
                view = inflator.inflate(R.layout.card_confirm_cart_content, parent, false);
                break;
            default:
                break;
        }
        return new ViewHolder(c, view);
    }

    int count = 0;

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Cart currentProduct = products.get(position);

        int viewType = getItemViewType(position);

        switch (viewType) {

            case VIEW_TYPES.tabHeader:
                break;
            case VIEW_TYPES.tabContent:
                handleContent(holder, currentProduct);
                break;

        }

    }

    private void handleContent(ViewHolder holder, Cart currentProduct) {
        holder.mProuctName.setText(currentProduct.getName());
        holder.mPrice.setText(""+currentProduct.getTotalPrice());
        holder.mQnty.setText("" + currentProduct.getQuantity());
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mProuctName;
        public TextView mPrice, mQnty;

        public ViewHolder(Context context, View view) {
            super(view);

            mProuctName = view.findViewById(R.id.product_name);
            mQnty = view.findViewById(R.id.quantity);
            mPrice = view.findViewById(R.id.price);

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return products.size();
    }

    public class VIEW_TYPES {
        public static final int tabHeader = 1;
        public static final int tabContent = 2;
    }

}

