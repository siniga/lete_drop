package com.agnet.lete_drop.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.adapters.CategoryAdapter;
import com.agnet.lete_drop.adapters.CustomerAdapter;
import com.agnet.lete_drop.application.mSingleton;
import com.agnet.lete_drop.helpers.AppManager;
import com.agnet.lete_drop.helpers.DatabaseHandler;
import com.agnet.lete_drop.helpers.FragmentHelper;
import com.agnet.lete_drop.models.Category;
import com.agnet.lete_drop.models.Customer;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomerFragment extends Fragment implements View.OnClickListener {

    private FragmentActivity _c;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private DatabaseHandler _dbHandler;
    private BottomNavigationView _navigation;
    private LinearLayout _btnHome;
    private RelativeLayout _openCartBtm;
    private RecyclerView _customerList;
    private LinearLayoutManager _customerLInearLayoutManager;
    private CustomerAdapter _customerAdapter;
    private LinearLayout _addCustomerBtn;
    private ProgressBar _progressBar;
    private Gson _gson;
    private List<Customer> _customers;
    private ShimmerFrameLayout _shimmer;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        _c = getActivity();

        //initialize
        _dbHandler = new DatabaseHandler(_c);

        _preferences = _c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
        _gson = new Gson();

        _navigation = _c.findViewById(R.id.bottom_navigation);
        _btnHome = _c.findViewById(R.id.home_btn);
        _openCartBtm = _c.findViewById(R.id.open_cart_wrapper);
        _customerList = view.findViewById(R.id.customer_list);
        _addCustomerBtn = view.findViewById(R.id.add_customer_btn);
        _progressBar = view.findViewById(R.id.progressBar_cyclic);
        _shimmer = view.findViewById(R.id.shimmer_view_container);

        _navigation.setVisibility(View.GONE);
        _btnHome.setVisibility(View.GONE);
        _openCartBtm.setVisibility(View.GONE);

        //event
        _addCustomerBtn.setOnClickListener(this);


        //methods
        _customerList.setHasFixedSize(true);

        //list
        _customerLInearLayoutManager = new LinearLayoutManager(_c, RecyclerView.VERTICAL, false);
        _customerList.setLayoutManager(_customerLInearLayoutManager);

        getMcustomers();
        return view;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_customer_btn:
                _progressBar.setVisibility(View.VISIBLE);
                new FragmentHelper(_c).replaceWithbackStack(new CreateCustomerFragment(), "CreateCustomerFragment", R.id.fragment_placeholder);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        _progressBar.setVisibility(View.GONE);
        _shimmer.stopShimmerAnimation();
    }


    public void getMcustomers() {
        _shimmer.setVisibility(View.VISIBLE);
        _shimmer.startShimmerAnimation();

        Endpoint.setUrl("customers/"+_dbHandler.getUser().getServerId());
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                response -> {

                    if (!AppManager.isNullOrEmpty(response)) {

                        ResponseData res = _gson.fromJson(response, ResponseData.class);
                        _customers = res.getCustomers();

                        _customerAdapter = new CustomerAdapter(_c, _customers);
                        _customerList.setAdapter(_customerAdapter);

                        _shimmer.setVisibility(View.GONE);
                        _shimmer.stopShimmerAnimation();

                    }else {

                        Toast.makeText(_c, "Kuna tatizo", Toast.LENGTH_SHORT).show();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();


                        _shimmer.setVisibility(View.GONE);
                        _shimmer.stopShimmerAnimation();

                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            //TODO: display errors based on the message from the server
                            Toast.makeText(_c, "Kuna tatizo, angalia mtandao alafu jaribu tena", Toast.LENGTH_SHORT).show();
                        }


                    }
                }
        );
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
