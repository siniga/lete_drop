package com.agnet.lete_drop.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.models.Company;

import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by alicephares on 8/5/16.
 */
public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> {

    private List<Company> companies = Collections.emptyList();
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
    public CompanyAdapter(Context c, List<Company> companies) {
        this.companies = companies;
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
        View v = inflator.inflate(R.layout.card_company, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }

    int count = 0;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Company currentCompany = companies.get(position);
        holder.mName.setText(currentCompany.getName());
        holder.mWrapper.setBackgroundColor(Color.parseColor(currentCompany.getImgUrl()));

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper;
        public TextView mName;
        public ImageView mImg;
        public LinearLayout mTransparentView;


        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.category_wrapper);
            mName = view.findViewById(R.id.category_name);
            mImg = view.findViewById(R.id.category_img);
            mTransparentView = view.findViewById(R.id.transparent_frontview);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return companies.size();
    }


}