package com.agnet.leteApp.fragments.main.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.main.sales.AddCartFragment;
import com.agnet.leteApp.fragments.main.sales.ProductsFragment;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Product;
import com.agnet.leteApp.models.ProjectType;
import com.agnet.leteApp.service.Endpoint;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.vision.text.Line;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

/**
 * Created by alicephares on 8/5/16.
 */
public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private List<Product> products = Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private ProductsFragment fragment;
    private DecimalFormat _formatter;
    private Gson _gson;


    // Provide a suitable constructor (depends on the kind of dataset)
    public ProductsAdapter(Context c, List<Product> products, ProductsFragment fragment) {
        this.products = products;
        this.inflator = LayoutInflater.from(c);
        this.c = c;
        this.fragment = fragment;

        _formatter = new DecimalFormat("#,###,##0.00");
        _gson = new Gson();

        _preferences = c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = inflator.inflate(R.layout.card_product_item, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }

    int count = 0;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Product currentProduct = products.get(position);
        holder.mName.setText(currentProduct.getName());
        holder.mSku.setText(currentProduct.getSku());
        holder.mPrice.setText("TZS"+_formatter.format(currentProduct.getPrice()));

        Log.d("HEHEH",_gson.toJson(_formatter.format(currentProduct.getPrice())));

        holder.mWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _editor.putString("PRODUCT", _gson.toJson(currentProduct));
                _editor.commit();
                new FragmentHelper(c).replaceWithbackStack(new AddCartFragment(),"AddCartFragment", R.id.fragment_placeholder);
            }
        });

        Endpoint.setStorageUrl(currentProduct.getImgUrl());
        String url = Endpoint.getStorageUrl();

        Glide.with(c).load(url)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .error(R.drawable.ic_place_holder)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(holder.mImg);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper;
        public TextView mName, mPrice, mSku;
        private ImageView mImg;

        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.wrapper);
            mName = view.findViewById(R.id.name);
            mImg = view.findViewById(R.id.product_img);
            mPrice = view.findViewById(R.id.price);
            mSku = view.findViewById(R.id.sku);

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return products.size();
    }


}