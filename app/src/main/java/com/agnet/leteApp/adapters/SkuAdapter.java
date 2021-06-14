package com.agnet.leteApp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.ProductsFragment;
import com.agnet.leteApp.models.Sku;

import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by alicephares on 8/5/16.
 */
public class SkuAdapter extends RecyclerView.Adapter<SkuAdapter.ViewHolder> {

    private List<Sku> skus = Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private int locateId;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int index = -1;
    private ProductsFragment fragment;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;


    // Provide a suitable constructor (depends on the kind of dataset)
    public SkuAdapter(Context c, List<Sku> skus) {
        this.skus = skus;
        this.inflator = LayoutInflater.from(c);
        this.fragment = fragment;
        this.c = c;

        _preferences = c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = inflator.inflate(R.layout.card_category, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }

    int count = 0;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Sku currentSku = skus.get(position);

        holder.mName.setText(currentSku.getName());

        holder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                index = position;
                notifyDataSetChanged();

            }
        });

        if (index == -1) {
            index = position;

        }

        if (index == position) {

            holder.mName.setBackgroundResource(c.getResources().getIdentifier("round_corners_with_stroke_primary", "drawable", c.getPackageName()));
            holder.mName.setTextColor(Color.parseColor("#666666"));
            holder.mName.setPadding(12,0,12,0);
        } else {
            holder.mName.setBackgroundResource(c.getResources().getIdentifier("round_corners_with_stroke_grey", "drawable", c.getPackageName()));
            holder.mName.setTextColor(Color.parseColor("#666666"));
            holder.mName.setPadding(12,0,12,0);
        }

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mWrapper;
        public Button mName;


        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.card);
            mName = view.findViewById(R.id.name);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return skus.size();
    }


}