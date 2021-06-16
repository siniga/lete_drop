package com.agnet.leteApp.fragments.main.outlets;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.main.mapping.MappingFormListFragment;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.CustomerType;
import com.agnet.leteApp.models.User;
import com.agnet.leteApp.service.Endpoint;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture;
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever;

public class NewBarcodeFragment extends Fragment  implements BarcodeRetriever {

    private FragmentActivity _c;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private Gson _gson;
    private User _user;
    private String Token;
    private String _phone, _name, _vfdId;
    private int _vfdType, _outletTypeId;
    private BarcodeCapture barcodeCapture;

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

        _gson = new Gson();

        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);
            _phone = _preferences.getString("PHONE", null);
            _name = _preferences.getString("NAME",  null);
           _vfdType = _preferences.getInt("VFD_TYPE",0);
            _vfdId = _preferences.getString("VFD_ID", null);
            _outletTypeId = _preferences.getInt("OUTLET_TYPE_ID", 0);

        } catch (NullPointerException e) {

        }



        return view;

}
    @Override
    public void onRetrieved(Barcode barcode) {
        // Log.d(TAG, "Barcode read: " + barcode.displayValue);
        _c.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                saveOutlet(barcode.displayValue);

                Log.d("BACKCODEREADER", "Barcode read: " + barcode.displayValue);

                new FragmentHelper(_c).replace(new OutletSuccessFragment()," OutletSuccessFragment", R.id.fragment_placeholder);
            }
        });

        barcodeCapture.stopScanning();

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

    public void saveOutlet(String qrcode) {

      Endpoint.setUrl("outlet");
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                      Log.d("RESPONSE_HERE",response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


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
                params.put("lng", _preferences.getString("mLONGITUDE", null));
                params.put("lat", _preferences.getString("mLATITUDE", null));
                params.put("phone", _phone);
                params.put("name",_name);
                params.put("outlet_type_id", "" + _outletTypeId);
                params.put("vfd_cust_type", "" + _vfdType);
                params.put("vfd_cust_id", "" +_vfdId);
                params.put("qr_code", qrcode);
                params.put("user_id", "" + _user.getId());

                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);

        postRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


}