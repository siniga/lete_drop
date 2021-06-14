package com.agnet.leteApp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.models.HistoryProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by alicephares on 8/5/16.
 */
public class HistoryProductAdapter extends RecyclerView.Adapter<HistoryProductAdapter.ViewHolder> {

    private List<HistoryProduct> products = Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private int locateId;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private List productlist = new ArrayList();
    private int cartItemCounts = 0;
    private int index = -1;



    // Provide a suitable constructor (depends on the kind of dataset)
    public HistoryProductAdapter(Context c, List<HistoryProduct> products) {
        this.products = products;
        this.inflator = LayoutInflater.from(c);
        this.c = c;

        _preferences = c.getSharedPreferences("SharedProductsData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = inflator.inflate(R.layout.card_history_product, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final HistoryProduct currentProduct = products.get(position);


//        holder.setIsRecyclable(false);//dissallow recyclerview from recycling to avoid losing previously inserted items

        holder.mName.setText(currentProduct.getName());
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper;
        public TextView mName, mPrice;



        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.shop_wrapper);
            mName = view.findViewById(R.id.name);

        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


}