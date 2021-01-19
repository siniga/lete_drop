package com.agnet.lete_drop.adapters;

import android.content.Context;
import android.content.SharedPreferences;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.fragments.ProductsFragment;
import com.agnet.lete_drop.helpers.FragmentHelper;
import com.agnet.lete_drop.models.Category;
import com.agnet.lete_drop.service.Endpoint;
import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.mWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _editor.putInt("CATEGORY_ID", currentCategory.getId());
                _editor.putInt("CATEGORY_POSITION", position);
                _editor.commit();

//                Intent i = new Intent(c, OrderActivity.class);
//                c.startActivity(i);

                new FragmentHelper(c).replaceWithbackStack(new ProductsFragment(), " ProductsFragment", R.id.fragment_placeholder);



//                ((HomeFragment) fragment).getProductsByCategory(currentCategory.getServerId());

                index = position;
                notifyDataSetChanged();

            }
        });

        Endpoint.setStorageUrl(currentCategory.getImgUrl());
        String url = Endpoint.getStorageUrl();

        try {
            Glide.with(c)
                    .load(new URL(url))
                    .into(holder.mImg);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (index == -1) {
            index = position;

        }

       /* Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.mTransparentView.setBackgroundColor(color);*/
/*
        if (index == position) {

         *//*   ((ProductsFragment) fragment).getProductsByCategory(currentCategory.getServerId());*//*

//            holder.mName.setBackgroundResource(c.getResources().getIdentifier("round_corners_with_stroke_primary", "drawable", c.getPackageName()));
//            holder.mName.setTextColor(Color.parseColor("#008577"));
        } else {
//            holder.mName.setBackgroundResource(c.getResources().getIdentifier("round_corners_with_stroke_grey", "drawable", c.getPackageName()));
//            holder.mName.setTextColor(Color.parseColor("#666666"));
        }*/

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
        return categories.size();
    }


}