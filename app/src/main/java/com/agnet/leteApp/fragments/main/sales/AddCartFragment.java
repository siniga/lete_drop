package com.agnet.leteApp.fragments.main.sales;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.main.adapters.CategoryAdapter;
import com.agnet.leteApp.fragments.main.adapters.ProductsAdapter;
import com.agnet.leteApp.helpers.AndroidDatabaseManager;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Cart;
import com.agnet.leteApp.models.Category;
import com.agnet.leteApp.models.Product;
import com.agnet.leteApp.models.ResponseData;
import com.agnet.leteApp.models.User;
import com.agnet.leteApp.service.Endpoint;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCartFragment extends Fragment {

    private FragmentActivity _c;
    private RecyclerView _productsList, _categorytList;
    private LinearLayoutManager _productsLayoutManager, _categoryLayoutManager;
    private String Token;
    private SharedPreferences.Editor _editor;
    private SharedPreferences _preferences;
    private Gson _gson;
    private ShimmerFrameLayout _shimmerLoader;
    private User _user;
    private Product _product;
    private DecimalFormat _formatter;
    private int count = 1;
    private DatabaseHandler _dbHandler;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_to_cart, container, false);
        _c = getActivity();

        _preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _formatter =  new DecimalFormat("#,###,##0.00");
        _dbHandler = new DatabaseHandler(_c);
        _editor = _preferences.edit();
        _gson = new Gson();

        TextView productheader = view.findViewById(R.id.product_header);
        ImageView productImg = view.findViewById(R.id.product_img);
        TextView productPrice = view.findViewById(R.id.product_price);
        TextView productName = view.findViewById(R.id.product_name);
        TextView productSku = view.findViewById(R.id.product_sku);
        Button addQnty = view.findViewById(R.id.quantity_view_add);
        Button removeQnty = view.findViewById(R.id.quantity_view_remove);
        EditText quantity = view.findViewById(R.id.quantity);
        Button addToCartBtn = view.findViewById(R.id.add_to_cart_btn);

        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);
            String results  = _preferences.getString("PRODUCT", null);
            _product = _gson.fromJson(results, Product.class);
            productheader.setText(_product.getName());

            productName.setText(_product.getName());
            productPrice.setText("TZS"+_formatter.format(_product.getPrice()));
            productSku.setText(_product.getSku());

        } catch (NullPointerException e) {

        }

        if(_dbHandler.isColumnAvailable("carts","product_id",""+_product.getId())){
            count = _dbHandler.getCartItemQnty(_product.getId());
            quantity.setText(""+count);
        }

        Endpoint.setStorageUrl(_product.getImgUrl());
        String url = Endpoint.getStorageUrl();

        Glide.with(_c).load(url)
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
                }).into(productImg);

        addQnty.setOnClickListener(view13 -> {
            count++;
            quantity.setText(""+count );

        });

        removeQnty.setOnClickListener(view12 -> {
            if(count > 1)
            count--;
            quantity.setText(""+count);

        });

        addToCartBtn.setOnClickListener(view1 -> {
            int Qnty =Integer.parseInt(quantity.getText().toString());
            Double amount  = Qnty * _product.getPrice();

            Log.d("HEHEHu", ""+amount);

            if(_dbHandler.isColumnAvailable("carts","product_id",""+_product.getId())){
                _dbHandler.updateCart(new Cart(0,_product.getName(), amount,_product.getId(),Qnty,_product.getPrice()));
            }else {
                _dbHandler.createCart(new Cart(0,_product.getName(), amount,_product.getId(), Qnty,_product.getPrice()));
            }



            new FragmentHelper(_c).replace(new ProductsFragment(),"ProductsFragment", R.id.fragment_placeholder);
        });

        productImg.setOnClickListener(view14 -> {
            Intent intent = new Intent(_c, AndroidDatabaseManager.class);
            _c.startActivity(intent);
        });
        return view;
    }




    @Override
    public void onPause() {
        super.onPause();
    }
    



}
