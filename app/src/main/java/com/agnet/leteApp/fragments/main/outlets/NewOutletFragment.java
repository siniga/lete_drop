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

public class NewOutletFragment extends Fragment {

    private FragmentActivity _c;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private DatabaseHandler _dbHandler;
    private EditText _phone, _name;
    private BottomNavigationView _navigation;
    private LinearLayout _btnHome;
    private RelativeLayout _openCartBtm;
    private Button _updateUserBtn;
    private LinearLayout _progressBar;
    private BarcodeCapture _barcodeCapture;
    private LinearLayout _qrcodeScannerWrapper, _barcodeCapturedWrapper;
    private TextView _shopQrCode;
    private LinearLayout _newOuletBtn;
    private Spinner _custTypesSpinner, _vfdCustTypeSpinner;
    private String _shopType, _vfdType;
    private int _typeId, _vfdTypeId;
    private List<CustomerType> _customerTypes;
    private List<CustomerType> _vfdCustTypes;
    private EditText _vfdCustId;
    private Gson _gson;
    private User _user;
    private String Token;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_outlet, container, false);
        _c = getActivity();

        _dbHandler = new DatabaseHandler(_c);
        _preferences = _c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        _gson = new Gson();

        _phone = view.findViewById(R.id.phone_input);
        _name = view.findViewById(R.id.name_input);
        _progressBar = view.findViewById(R.id.progressBar_cyclic);
        _qrcodeScannerWrapper = view.findViewById(R.id.qr_codescanner_wrapper);
        _barcodeCapturedWrapper = view.findViewById(R.id.barcode_captured_wrapper);
        _shopQrCode = view.findViewById(R.id.shop_qr_code);
        _newOuletBtn = view.findViewById(R.id.new_outlet_btn);
        _custTypesSpinner = view.findViewById(R.id.cust_types_spinner);
        _vfdCustTypeSpinner = view.findViewById(R.id.vfd_cust_type_spinner);
        _vfdCustId = view.findViewById(R.id.vfd_cust_id_input);


        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);

        } catch (NullPointerException e) {

        }


        _custTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                _shopType = adapterView.getItemAtPosition(position).toString();
                _typeId = _customerTypes.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        _vfdCustTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position != 0) {
                    _vfdCustId.setText("NIL");
                    _vfdCustId.setVisibility(View.GONE);
                } else {
                    _vfdCustId.setVisibility(View.VISIBLE);
                }
                _vfdType = adapterView.getItemAtPosition(position).toString();
                _vfdTypeId = _vfdCustTypes.get(position).getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        _newOuletBtn.setOnClickListener(view1 -> {
            Toast.makeText(_c, "here", Toast.LENGTH_SHORT).show();

            _editor.putString("PHONE", "+255"+_phone.getText().toString());
            _editor.putString("NAME",  _name.getText().toString());
            _editor.putInt("VFD_TYPE",_vfdTypeId);
            _editor.putString("VFD_ID", _vfdCustId.getText().toString());
            _editor.putInt("OUTLET_TYPE_ID", _typeId);
            _editor.commit();

            new FragmentHelper(_c).replaceWithbackStack(new NewBarcodeFragment(), "NewBarcodeFragment", R.id.fragment_placeholder);

        });

        getCustomerType();
        getVfdCustomerType();

        return view;

    }

    private void getCustomerType() {
        _customerTypes = new ArrayList<>();
        _customerTypes.add(new CustomerType(1, "Mini Supermarket"));
        _customerTypes.add(new CustomerType(2, "Bar"));
        _customerTypes.add(new CustomerType(3, "WholeSale"));
        _customerTypes.add(new CustomerType(4, "Duka"));
        _customerTypes.add(new CustomerType(5, "Supermarket"));
        _customerTypes.add(new CustomerType(6, "Kiosk"));

        // Creating adapter for spinner
        ArrayAdapter<CustomerType> dataAdapter = new ArrayAdapter<CustomerType>(_c, android.R.layout.simple_spinner_item, _customerTypes);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        _custTypesSpinner.setAdapter(dataAdapter);
    }

    private void getVfdCustomerType() {
        _vfdCustTypes = new ArrayList<>();
        _vfdCustTypes.add(new CustomerType(1, "TIN NUMBER"));
        _vfdCustTypes.add(new CustomerType(6, "NILL"));

        // Creating adapter for spinner
        ArrayAdapter<CustomerType> dataAdapter = new ArrayAdapter<CustomerType>(_c, android.R.layout.simple_spinner_item, _vfdCustTypes);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        _vfdCustTypeSpinner.setAdapter(dataAdapter);
    }



    public void saveOutlet() {

      Endpoint.setUrl("outlet");
        String url = Endpoint.getUrl();

        _progressBar.setVisibility(View.VISIBLE);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        _progressBar.setVisibility(View.GONE);
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

                            _progressBar.setVisibility(View.GONE);
                            //TODO: display errors based on the message from the server
                            Toast.makeText(_c, "Kuna tatizo, angalia mtandao alafu jaribu tena", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("lng", _preferences.getString("mLONGITUDE", null));
                params.put("lat", _preferences.getString("mLATITUDE", null));
                params.put("phone", "255"+_phone.getText().toString());
                params.put("name", _name.getText().toString());
                params.put("type_id", "" + _typeId);
                params.put("pin", "0000");
                params.put("vfd_cust_type", "" + _vfdTypeId);
                params.put("vfd_cust_id", "" + _vfdCustId.getText().toString());
                params.put("qr_code", _preferences.getString("BARCODE", null));
                params.put("user_id", "" + _user.getId());

                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);

        postRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

}
