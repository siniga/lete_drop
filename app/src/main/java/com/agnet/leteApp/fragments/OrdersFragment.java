package com.agnet.leteApp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.agnet.leteApp.R;
import com.agnet.leteApp.adapters.OrdersAdapter;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.helpers.AppManager;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.models.History;
import com.agnet.leteApp.models.ResponseData;
import com.agnet.leteApp.models.User;
import com.agnet.leteApp.service.Endpoint;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class OrdersFragment extends Fragment {

    private FragmentActivity _c;
    private RecyclerView _historylist;
    private LinearLayoutManager _layoutManager;
    private Gson _gson;
    private DatabaseHandler _dbHandler;
    private EditText _phoneInput;
    private Button _phnConfirmBtn;
    private OrdersAdapter _adapter;
    private ShimmerFrameLayout _shimmerFrameLayout;
    private String _mPhone;
    private Handler _mHandler;
    private LinearLayout _noOderMsgWrapper;
    private BottomNavigationView _navigation;
    private LinearLayout _btnHome;
    private RelativeLayout _openCartBtm;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        _c = getActivity();


        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        _gson = gsonBuilder.create();

        _dbHandler = new DatabaseHandler(_c);
        _mPhone = _dbHandler.getUserPhone();
        _shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);

        _noOderMsgWrapper = view.findViewById(R.id.no_order_msg_wrapper);

        _phoneInput = view.findViewById(R.id.phone_input);
        _phnConfirmBtn = view.findViewById(R.id.button_confirm_phone);
        _historylist = view.findViewById(R.id.history_list);
        _navigation = _c.findViewById(R.id.bottom_navigation);
        _btnHome = _c.findViewById(R.id.home_btn);
        _openCartBtm = _c.findViewById(R.id.open_cart_wrapper);

        _navigation.setVisibility(View.VISIBLE);
        _btnHome.setVisibility(View.VISIBLE);
        _openCartBtm.setVisibility(View.GONE);
        _historylist.setHasFixedSize(true);


        _layoutManager = new LinearLayoutManager(_c, RecyclerView.VERTICAL, false);
        _historylist.setLayoutManager(_layoutManager);

//        _historylist.addItemDecoration(new CustomDivider(_c, LinearLayoutManager.VERTICAL, 16));

        getOrders();
        return view;

    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        _shimmerFrameLayout.stopShimmerAnimation();
    }


    public void getOrders() {

        _shimmerFrameLayout.setVisibility(View.VISIBLE);
        _shimmerFrameLayout.startShimmerAnimation();

/*
        final String phone = _dbHandler.getUserPhone();
        User user = _dbHandler.getUser();

      //  Log.d("ERRORRESPONSE",""+user.getServerId());


        if (!phone.isEmpty()) {


            Endpoint.setUrl("saler/history/"+user.getSalerId());
            String url = Endpoint.getUrl();

            StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d("ERRORRESPONSE", response);

                            _shimmerFrameLayout.stopShimmerAnimation();
                            _shimmerFrameLayout.setVisibility(View.GONE);

                            if (!AppManager.isNullOrEmpty(response)) {

                                ResponseData res = _gson.fromJson(response, ResponseData.class);
                                List<History> orders = res.getOrders();

                                if(orders.size() != 0){
                                    _adapter = new OrdersAdapter(_c, orders);
                                    _historylist.setAdapter(_adapter);

                                }else {
                                    _noOderMsgWrapper.setVisibility(View.VISIBLE);

                                }


                            } else {

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();

                            _shimmerFrameLayout.stopShimmerAnimation();
                            _shimmerFrameLayout.setVisibility(View.GONE);

                            NetworkResponse response = error.networkResponse;
                            String errorMsg = "";
                            if (response != null && response.data != null) {
                                String errorString = new String(response.data);
                                Log.i("log error", errorString);

                                //TODO: display errors based on the message from the server
                                Toast.makeText(_c, "Kuna tatizo, angalia mtandao alafu jaribu tena", Toast.LENGTH_LONG).show();
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

        } else {

            _historylist.setVisibility(View.GONE);

            _shimmerFrameLayout.stopShimmerAnimation();
            _shimmerFrameLayout.setVisibility(View.GONE);
        }*/
    }

}
