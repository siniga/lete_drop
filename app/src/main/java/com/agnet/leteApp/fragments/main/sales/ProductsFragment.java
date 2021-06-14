package com.agnet.leteApp.fragments.main.sales;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.agnet.leteApp.fragments.auth.LoginFragment;
import com.agnet.leteApp.fragments.main.adapters.CategoryAdapter;
import com.agnet.leteApp.fragments.main.adapters.ProductsAdapter;
import com.agnet.leteApp.fragments.main.adapters.ProjectAdapter;
import com.agnet.leteApp.fragments.main.adapters.ProjectTypeAdapter;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Category;
import com.agnet.leteApp.models.ProjectType;
import com.agnet.leteApp.models.ResponseData;
import com.agnet.leteApp.models.User;
import com.agnet.leteApp.service.Endpoint;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductsFragment extends Fragment {

    private FragmentActivity _c;
    private RecyclerView _productsList, _categorytList;
    private LinearLayoutManager _productsLayoutManager, _categoryLayoutManager;
    private String Token;
    private SharedPreferences.Editor _editor;
    private SharedPreferences _preferences;
    private Gson _gson;
    private ShimmerFrameLayout _shimmerLoader;
    private User _user;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        _c = getActivity();

        _preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
        _gson = new Gson();

        TextView username = view.findViewById(R.id.user_name);
        _categorytList= view.findViewById(R.id.category_list);
        _productsList= view.findViewById(R.id.product_list);
        _shimmerLoader = view.findViewById(R.id.shimmer_view_container);
        LinearLayout userAcc = view.findViewById(R.id.view_user_account_btn);


        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);
            String projectName = _preferences.getString("PROJECT_NAME", null);
            username.setText(projectName);

        } catch (NullPointerException e) {

        }

        _categoryLayoutManager= new LinearLayoutManager(_c, RecyclerView.HORIZONTAL, false);
        _categorytList.setLayoutManager(_categoryLayoutManager);


        _productsLayoutManager = new GridLayoutManager(_c, 2);
        _productsList.setLayoutManager(_productsLayoutManager);



        getCategories();

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        _shimmerLoader.setVisibility(View.GONE);
        _shimmerLoader.stopShimmerAnimation();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    _c.finish();
                    //Toast.makeText(_c, "Here we a", Toast.LENGTH_SHORT).show();

                    return true;
                }
            }
            return false;
        });
    }

    public void getCategories() {

        _shimmerLoader.setVisibility(View.VISIBLE);
        _shimmerLoader.startShimmerAnimation();

        Endpoint.setUrl("categories/client/3");
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    ResponseData res = _gson.fromJson(response, ResponseData.class);


                    CategoryAdapter productsAdapter = new CategoryAdapter(_c,res.getCategories(), this);
                    _categorytList.setAdapter(productsAdapter);

                    _shimmerLoader.setVisibility(View.GONE);
                    _shimmerLoader.stopShimmerAnimation();

                    getProducts();
                },
                error -> {
                    error.printStackTrace();


                    _shimmerLoader.setVisibility(View.GONE);
                    _shimmerLoader.stopShimmerAnimation();

                    NetworkResponse response = error.networkResponse;
                    String errorMsg = "";
                    if (response != null && response.data != null) {
                        String errorString = new String(response.data);
                        Log.i("log error", errorString);
                        //TODO: display errors based on the message from the server
                        Toast.makeText(_c, "Kuna tatizo, angalia mtandao alafu jaribu tena", Toast.LENGTH_SHORT).show();
                    }


                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + "" + Token);
                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);

        postRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }


    public void getProducts() {


        Endpoint.setUrl("products/category/1");
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                response -> {


                    ResponseData res = _gson.fromJson(response, ResponseData.class);

                    ProductsAdapter productsAdapter = new ProductsAdapter(_c,res.getProducts(), this);
                    _productsList.setAdapter(productsAdapter);

                    Log.d("RESPONSEHERE", _gson.toJson(res.getProducts()));

                },
                error -> {
                    error.printStackTrace();


                    _shimmerLoader.setVisibility(View.GONE);
                    _shimmerLoader.stopShimmerAnimation();

                    NetworkResponse response = error.networkResponse;
                    String errorMsg = "";
                    if (response != null && response.data != null) {
                        String errorString = new String(response.data);
                        Log.i("log error", errorString);
                        //TODO: display errors based on the message from the server
                        Toast.makeText(_c, "Kuna tatizo, angalia mtandao alafu jaribu tena", Toast.LENGTH_SHORT).show();
                    }


                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + "" + Token);
                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);

        postRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }


}
