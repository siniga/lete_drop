package com.agnet.leteApp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.ProductsFragment;
import com.agnet.leteApp.models.Category;

import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by alicephares on 8/5/16.
 */
public class CategoryProductAdapter extends RecyclerView.Adapter<CategoryProductAdapter.ViewHolder> {

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

    public interface OnItemClickListener {
        void onItemClick(Category item);
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public CategoryProductAdapter(Context c, List<Category> categories, Fragment fragment) {
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
        View v = inflator.inflate(R.layout.card_category_product, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }

    int count = 0;

    // Replace the contents of a
    //
    // view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Category currentCategory = categories.get(position);


        holder.mName.setText(currentCategory.getName());
        holder.mWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((ProductsFragment) fragment).getProductsByCategory(currentCategory.getServerId());

                index = position;
                notifyDataSetChanged();

                _editor.putInt("CATEGORY_POSITION", 0);
                _editor.commit();

            }
        });


        if (index == -1) {
            index = _preferences.getInt("CATEGORY_POSITION", 0);
//            ((ProductFragment) fragment).filterBycategory(currentCategory.getServerId());
//            Log.d("CAtet",""+currentCategory.getName());

        }


        if (index == position) {
            holder.mBorderBtm.setBackgroundColor(Color.parseColor("#000000"));
            holder.mName.setTextColor(Color.parseColor("#000000"));
        } else {
            holder.mBorderBtm.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.mName.setTextColor(Color.parseColor("#666666"));
        }


    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mWrapper;
        public TextView mName;
        public View mBorderBtm;

        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.card);
            mName = view.findViewById(R.id.name);
            mBorderBtm = view.findViewById(R.id.border_bottom);


        }

        public void bind(final Category item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return categories.size();
    }


}