package com.agnet.leteApp.fragments.main.sales;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.main.outlets.NewOutletFragment;
import com.agnet.leteApp.fragments.main.outlets.OutletSuccessFragment;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.DateHelper;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Outlet;
import com.agnet.leteApp.models.ResponseData;
import com.agnet.leteApp.models.User;
import com.agnet.leteApp.service.Endpoint;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture;
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever;

public class OrderBarcodeFragment extends Fragment implements BarcodeRetriever {

    private FragmentActivity _c;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private Gson _gson;
    private User _user;
    private String Token;
    private String _phone, _name, _vfdId;
    private int _vfdType, _outletTypeId;
    private BarcodeCapture barcodeCapture;
    private String _lng, _lat;
    private String _location;
    private ProgressBar _progressBar;
    private LinearLayout _transparentLoader;
    private DatabaseHandler _dbHandler;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barcode, container, false);
        _c = getActivity();

        barcodeCapture = (BarcodeCapture) getChildFragmentManager().findFragmentById(R.id.barcode);
        barcodeCapture.setRetrieval(this);

        _preferences = _c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
        _dbHandler = new DatabaseHandler(_c);
        _gson = new Gson();

        _progressBar = view.findViewById(R.id.progress_bar);
        _transparentLoader = view.findViewById(R.id.transparent_loader);
        Button registerOutletBt = view.findViewById(R.id.register_outlet_btn);


        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);

        } catch (NullPointerException e) {

        }

        registerOutletBt.setOnClickListener(view1 -> new FragmentHelper(_c).replace(new NewOutletFragment(), "NewOutletFragment", R.id.fragment_placeholder));

        return view;

    }

    @Override
    public void onRetrieved(Barcode barcode) {
        // Log.d(TAG, "Barcode read: " + barcode.displayValue);
        _c.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                isQrCodeAvailable(barcode.displayValue);

            }
        });


    }

    @Override
    public void onRetrievedMultiple(Barcode closetToClick, List<BarcodeGraphic> barcode) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onRetrievedFailed(String reason) {

    }

    @Override
    public void onPermissionRequestDenied() {

    }

    private void isQrCodeAvailable(String qrcode) {

        Endpoint.setUrl("outlet/qrcode/" + qrcode);
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                response -> {

                    _transparentLoader.setVisibility(View.GONE);
                    _progressBar.setVisibility(View.GONE);

                    ResponseData res = _gson.fromJson(response, ResponseData.class);
                    if (res.getCode() == 409) {
                        Log.d("HEREHAPA", response);
                        Outlet outlet = res.getOutlet();
                        saveOrder(outlet.getId());
                    } else {
                        Toast.makeText(_c, "Qr code haipo, sajili qr code", Toast.LENGTH_LONG).show();
                    }
                    //


                },
                error -> {
                    error.printStackTrace();


                    _transparentLoader.setVisibility(View.GONE);
                    _progressBar.setVisibility(View.GONE);

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

    public void saveOrder(int outletId) {
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
                    //Log.d("CARTPRODUCT", response);
                    ResponseData res = _gson.fromJson(response, ResponseData.class);

                    if (res.getCode() == 201) {
                        _dbHandler.deleteCart();
                        new FragmentHelper(_c).replace(new SalesSuccessFragment(), "SalesSuccessFragment", R.id.fragment_placeholder);
                    }


                },
                error -> {
                    error.printStackTrace();


                    _transparentLoader.setVisibility(View.GONE);
                    _progressBar.setVisibility(View.GONE);

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
                params.put("outletId", "" + outletId);
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
