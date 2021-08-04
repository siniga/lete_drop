package com.agnet.leteApp.fragments.main.sales;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.main.adapters.CartAdapter;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.DateHelper;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Cart;
import com.agnet.leteApp.models.Order;
import com.agnet.leteApp.models.ResponseData;
import com.agnet.leteApp.models.User;
import com.agnet.leteApp.service.Endpoint;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class CartFragment extends Fragment {

    private FragmentActivity _c;
    private RecyclerView _cartlist;
    private LinearLayoutManager _layoutManager;
    private DatabaseHandler _dbHandler;
    private TextView _cartTotalAmnt;
    private DecimalFormat _formatter;
    private List<Cart> _products;
    private LinearLayout _errorMsg;
    private Button _placeOrderBtn;
    private AlertDialog _alertDialog;
    private BottomNavigationView _navigation;
    private LinearLayout _btnHome;
    private RelativeLayout _openCartBtm;
    private SharedPreferences.Editor _editor;
    private SharedPreferences _preferences;
    private String Token;
    private User _user;
    private Gson _gson;
    private ProgressBar _progressBar;
    private LinearLayout _transparentLoader;
    private Button _placeOrderNoQrCodeBtn;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        _c = getActivity();

        //initialize
        _preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
        _progressBar = view.findViewById(R.id.progress_bar);
        _transparentLoader = view.findViewById(R.id.transparent_loader);
        _dbHandler = new DatabaseHandler(_c);
        _formatter = new DecimalFormat("#,###,##0.00");
        _gson = new Gson();

        //binding
        _cartTotalAmnt = view.findViewById(R.id.total_cart_amount);
        _openCartBtm = _c.findViewById(R.id.open_cart_wrapper);
        _errorMsg = view.findViewById(R.id.error_msg);
        _cartlist = view.findViewById(R.id.cart_list);
        _placeOrderBtn = view.findViewById(R.id.place_order_btn);
        _placeOrderNoQrCodeBtn = view.findViewById(R.id.place_order_no_qrcode);

        _layoutManager = new LinearLayoutManager(_c, RecyclerView.VERTICAL, false);
        _cartlist.setLayoutManager(_layoutManager);

        _products = _dbHandler.getCart();

        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);

        } catch (NullPointerException e) {

        }

        CartAdapter adapter = new CartAdapter(_c, _products, this);
        _cartlist.setAdapter(adapter);

        _placeOrderBtn.setOnClickListener(view12 -> {
            if (_products.size() > 0) {
                // _progressBar.setVisibility(View.VISIBLE);
                //  _transparentLoader.setVisibility(View.VISIBLE);
              //  _placeOrderBtn.setClickable(false);
                if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(_c, Manifest.permission.CAMERA)) {
                    new FragmentHelper(_c).replaceWithbackStack(new OrderBarcodeFragment(), "OrderBarcodeFragment", R.id.fragment_placeholder);

                } else {
                    requestWritePermission(_c);
                }


            } else {
                Toast.makeText(_c, "Kikapu hakina bidhaa!", Toast.LENGTH_SHORT).show();

            }
        });

        _placeOrderNoQrCodeBtn.setOnClickListener(view1 -> {
            if (_products.size() > 0) {
                _placeOrderNoQrCodeBtn.setClickable(false);


                new FragmentHelper(_c).replace(new OutletPhoneNumberFragment(), "OutletNumberFragment", R.id.fragment_placeholder);


            } else {
                Toast.makeText(_c, "Kikapu hakina bidhaa!", Toast.LENGTH_SHORT).show();
            }
        });
        return view;

    }

    public void setTotalCartAmnt(int totalCartAmnt) {
        _cartTotalAmnt.setText("" + _formatter.format(totalCartAmnt));
    }

    @Override
    public void onResume() {
        super.onResume();

        int totalPrice = _dbHandler.getTotalPrice();
        _cartTotalAmnt.setText("" + _formatter.format(totalPrice));


    }

    @Override
    public void onPause() {
        super.onPause();
        _progressBar.setVisibility(View.GONE);
        _transparentLoader.setVisibility(View.GONE);
    }

    private static void requestWritePermission(final Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(context).setMessage("This app needs permission to use The phone Camera in order to activate the Scanner")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 1);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    public void saveOrder() {
        _transparentLoader.setVisibility(View.VISIBLE);
        _progressBar.setVisibility(View.VISIBLE);

        Random rand = new Random();
        int orderNoRandom = rand.nextInt((9999 - 100) + 1) + 10;

        Endpoint.setUrl("order");
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    _transparentLoader.setVisibility(View.GONE);
                    _progressBar.setVisibility(View.GONE);

                    ResponseData res = _gson.fromJson(response, ResponseData.class);
                    Log.d("HEREALSOE", _gson.toJson(res));

                    if (res.getCode() == 201) {
                       // _dbHandler.deleteCart();
                    }

                    _placeOrderBtn.setClickable(true);
                },
                error -> {
                    error.printStackTrace();


                    _transparentLoader.setVisibility(View.GONE);
                    _progressBar.setVisibility(View.GONE);
                    _placeOrderBtn.setClickable(true);
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

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("createdDate", DateHelper.getCurrentDate());
                params.put("deviceTime", DateHelper.getCurrentDate() + " " + DateHelper.getCurrentTime());
                params.put("userId", "" + _user.getId());
                params.put("orderNo", "" + orderNoRandom);
                params.put("lat", _preferences.getString("mLATITUDE", null));
                params.put("lng", _preferences.getString("mLONGITUDE", null));
                params.put("products", _gson.toJson(_dbHandler.getCart()));
                params.put("outletId", "1");
                params.put("projectId", "" + _preferences.getInt("PROJECT_ID", 0));
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
