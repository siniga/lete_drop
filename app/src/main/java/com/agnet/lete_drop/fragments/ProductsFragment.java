package com.agnet.lete_drop.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.activities.MainActivity;
import com.agnet.lete_drop.adapters.CategoryProductAdapter;
import com.agnet.lete_drop.adapters.ProductAdapter;
import com.agnet.lete_drop.application.mSingleton;
import com.agnet.lete_drop.helpers.AppManager;
import com.agnet.lete_drop.helpers.CustomDivider;
import com.agnet.lete_drop.helpers.DatabaseHandler;
import com.agnet.lete_drop.models.Category;
import com.agnet.lete_drop.models.Product;
import com.agnet.lete_drop.models.ResponseData;
import com.agnet.lete_drop.service.Endpoint;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ProductsFragment extends Fragment {


    private FragmentActivity _c;
    private RecyclerView _productList, _categoryList;
    private LinearLayoutManager _layoutManager, _categoryLayoutManager;
    private ProductAdapter _productAdapter;
    private CategoryProductAdapter _categoryAdapter;
    private TextView _itemCounter;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private Gson _gson;
    private List<Category> _categories;
    private DatabaseHandler _dbHandler;
    private LinearLayout _errorMsg;
    private ShimmerFrameLayout _shimmerFrameLayout;
    private BottomNavigationView _navigation;
    private LinearLayout _btnHome;
    private RelativeLayout _openCartBtm;

    @SuppressLint({"RestrictedApi", "WrongConstant"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        _c = getActivity();

        //initialize
        _preferences = _c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
        _dbHandler = new DatabaseHandler(_c);
        _gson = new Gson();

        //binding
        _shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        _errorMsg = view.findViewById(R.id.error_msg);
        _navigation = _c.findViewById(R.id.bottom_navigation);
        _btnHome = _c.findViewById(R.id.home_btn);
        _openCartBtm = _c.findViewById(R.id.open_cart_wrapper);

        //methods
        _categories = _dbHandler.getCategories();
        _navigation.setVisibility(View.GONE);
        _btnHome.setVisibility(View.GONE);
        _openCartBtm.setVisibility(View.VISIBLE);

        _categoryList = view.findViewById(R.id.category_list);
        _categoryList.setHasFixedSize(true);

        _categoryLayoutManager = new LinearLayoutManager(_c, LinearLayoutManager.HORIZONTAL, false);
        _categoryList.setLayoutManager(_categoryLayoutManager);

        _categoryAdapter = new CategoryProductAdapter(_c, _categories, ProductsFragment.this);

        _categoryList.setAdapter(_categoryAdapter);

        //Product list
        _productList = view.findViewById(R.id.product_list);
        _productList.setHasFixedSize(true);
        _productList.getPreserveFocusAfterLayout();

        _layoutManager = new LinearLayoutManager(_c, LinearLayoutManager.VERTICAL, false);
        _productList.setLayoutManager(_layoutManager);
        _productList.getRecycledViewPool().setMaxRecycledViews(0,0);

        _productList.addItemDecoration(new CustomDivider(_c, LinearLayoutManager.VERTICAL, 16));

       if(_preferences.getInt("CATEGORY_ID", 0) != 0){
           getProductsByCategory(_preferences.getInt("CATEGORY_ID", 0));
       }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        //_shimmerFrameLayout.startShimmerAnimation();
        TextView  toolbarTitle = _c.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Bidhaa");

        //show back button
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

    }


    @Override
    public void onPause() {
        super.onPause();
        _shimmerFrameLayout.stopShimmerAnimation();
        _navigation.setVisibility(View.VISIBLE);
        _btnHome.setVisibility(View.VISIBLE);
        _openCartBtm.setVisibility(View.GONE);

    }

    public void showAddCartDialog(final String name, final String img, final int productId, final String price, final String sku, final int skuId) {
     /*   //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = _c.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        final View dialogView = LayoutInflater.from(_c).inflate(R.layout.dialog_addcart_products, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(_c);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        builder.setCancelable(false);
        //0684359

        //finally creating the alert dialog and displaying it
      *//*  _alertDialog = builder.create();
        _alertDialog.setCancelable(true);
        _alertDialog.show();
*//*
         *//* _skuList = dialogView.findViewById(R.id.sku_list);
        _skuList.setHasFixedSize(true);

        _skuAdapter = new SkuAdapter(_c,_skus);
        _skuList.setAdapter(_skuAdapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(_c, LinearLayoutManager.HORIZONTAL, false);
        _skuList.setLayoutManager(layoutManager);*//*

        Button cancel = dialogView.findViewById(R.id.button_cancel);
        Button confirmSale = dialogView.findViewById(R.id.button_confirm_sale);
        TextView productName = dialogView.findViewById(R.id.product_name);
        ImageView productImg = dialogView.findViewById(R.id.product_img);
        Button addItem = dialogView.findViewById(R.id.quantity_add);
        Button removeItem = dialogView.findViewById(R.id.quantity_remove);
        final TextView qntyNum = dialogView.findViewById(R.id.quantity);

        //get last stored quantity from db then assign it to the count variable;
        //then display it
        final int quantity = _dbHandler.getCartItemQnty(productId);
        final int[] count = {quantity};
        if(count[0] == 0){
            count[0] = 1;
        }

        qntyNum.setText(""+count[0]);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qntyNum.setText("" + ++count[0]);
            }
        });

        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count[0] > 1) {
                    qntyNum.setText("" + --count[0]);
                }
            }
        });

        productName.setText(name);

        Endpoint.setStorageUrl(img);
        String url = Endpoint.getStorageUrl();

        try {
            Glide.with(_c)
                    .load(new URL(url))
                    .into(productImg);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


       // cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _alertDialog.cancel();
            }
        });
*/

      /*  confirmSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               //calculate total price for the product
                int total = (Integer.parseInt(price) * count[0]);

                //create order list
                List<Order> orders = new ArrayList<>();
                orders.add(new Order(0,"","","",1,"",_outletId,0));

                //create order then cart based on order created
                _dbHandler.createOrder(orders);

                int orderId =_dbHandler.getLastId("orders");
                _dbHandler.createCart(productId,price,String.valueOf(total), name,img,count[0], sku,orderId,skuId);

                //refresh product list
                int categoryId =_preferences.getInt("CATEGORY_ID",0);

            }
        });*/


    }


    public void getProductsByCategory(int id) {

        _shimmerFrameLayout.setVisibility(View.VISIBLE);
        _shimmerFrameLayout.startShimmerAnimation();
        _errorMsg.setVisibility(View.GONE);

        Endpoint.setUrl("products/category/"+id);
        String url = Endpoint.getUrl();
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        _shimmerFrameLayout.stopShimmerAnimation();
                        _shimmerFrameLayout.setVisibility(View.GONE);

                        if (!AppManager.isNullOrEmpty(response)) {

                            ResponseData res = _gson.fromJson(response, ResponseData.class);

                            List<Product> products = res.getProducts();

                            if(products.size() != 0){

                                _errorMsg.setVisibility(View.GONE);
                                ProductAdapter productAdapter = new ProductAdapter(_c, products, ProductsFragment.this);
                                _productList.setAdapter(productAdapter);
                               // productAdapter.filterList(products);

                            }else {

                                _errorMsg.setVisibility(View.VISIBLE);
                               _productAdapter = new ProductAdapter(_c, products, ProductsFragment.this);
                                _productList.setAdapter(_productAdapter);

                            }



                        } else {
                            Toast.makeText(_c, "Kuna tatizo, angalia mtandao alafu jaribu tena!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

//                        _progressBar.setVisibility(View.GONE);
                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            //TODO: display errors based on the message from the server
                            Toast.makeText(_c, "Kuna tatizo, hakikisha mtandao upo sawa alafu jaribu tena!", Toast.LENGTH_LONG).show();

                        }

                        _shimmerFrameLayout.stopShimmerAnimation();
                        _shimmerFrameLayout.setVisibility(View.GONE);


                    }
                }
        ){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(jsonString, cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(String response) {
                super.deliverResponse(String.valueOf(response));
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
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
