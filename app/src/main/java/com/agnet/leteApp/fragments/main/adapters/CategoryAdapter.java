package com.agnet.leteApp.fragments.main.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.ProductsFragment;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Category;
import com.agnet.leteApp.service.Endpoint;
import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Created by alicephares on 8/5/16.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories = Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private int locateId;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int index = -1;
    private Fragment fragment;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    int selected_position = 0;


    // Provide a suitable constructor (depends on the kind of dataset)
    public CategoryAdapter(Context c, List<Category> categories, Fragment fragment) {
        this.categories = categories;
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
        final Category currentCategory = categories.get(position);

        holder.mName.setText(currentCategory.getName());


        if(selected_position == position){
            holder.mWrapper.setBackgroundResource(R.drawable.round_corners_blue);
            holder.mName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.mWrapper.setPadding(20,5,20,5);

        }else {
            holder.mWrapper.setBackgroundResource(R.drawable.round_corners_white);
            holder.mName.setTextColor(Color.parseColor("#666666"));
        }

        holder.mWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) return;

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = holder.getAdapterPosition();
                notifyItemChanged(selected_position);

            }
        });


    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout  mWrapper;
        public TextView mName;
        public ImageView mImg;



        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.wrapper);
            mName = view.findViewById(R.id.name);


        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return categories.size();
    }


}