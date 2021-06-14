package com.agnet.leteApp.fragments;


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

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.CustomerType;
import com.agnet.leteApp.models.ResponseData;
import com.agnet.leteApp.service.Endpoint;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever;

public class CreateCustomerFragment extends Fragment implements View.OnClickListener, BarcodeRetriever {

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
    private Button _addCustomerBtn;
    private Spinner _custTypesSpinner, _vfdCustTypeSpinner;
    private String _shopType, _vfdType;
    private int _typeId, _vfdTypeId;
    private List<CustomerType> _customerTypes;
    private List<CustomerType> _vfdCustTypes;
    private EditText _vfdCustId;
    private Gson _gson;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_customer, container, false);
        _c = getActivity();

        _dbHandler = new DatabaseHandler(_c);
        _preferences = _c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        _gson = new Gson();

        _phone = view.findViewById(R.id.phone_input);
        _name = view.findViewById(R.id.name_input);
        _navigation = _c.findViewById(R.id.bottom_navigation);
        _btnHome = _c.findViewById(R.id.home_btn);
        _openCartBtm = _c.findViewById(R.id.open_cart_wrapper);
        _updateUserBtn = view.findViewById(R.id.submit_user_details);
        _progressBar = view.findViewById(R.id.progressBar_cyclic);
        _qrcodeScannerWrapper = view.findViewById(R.id.qr_codescanner_wrapper);
        _barcodeCapturedWrapper = view.findViewById(R.id.barcode_captured_wrapper);
        _shopQrCode = view.findViewById(R.id.shop_qr_code);
        _addCustomerBtn = view.findViewById(R.id.add_customer_btn);
        _custTypesSpinner = view.findViewById(R.id.cust_types_spinner);
        _vfdCustTypeSpinner = view.findViewById(R.id.vfd_cust_type_spinner);
        _vfdCustId = view.findViewById(R.id.vfd_cust_id_input);


        _barcodeCapture = (BarcodeCapture) getChildFragmentManager().findFragmentById(R.id.barcode);
        _barcodeCapture.setRetrieval(this);


        _navigation.setVisibility(View.GONE);
        _btnHome.setVisibility(View.GONE);
        _openCartBtm.setVisibility(View.GONE);
        _addCustomerBtn.setOnClickListener(this);

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


        getCustomerType();
        getVfdCustomerType();

        return view;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_customer_btn:
                validateCustomer();
                break;
        }
    }


    @Override
    public void onRetrieved(final Barcode barcode) {
        _c.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                _shopQrCode.setText(barcode.displayValue);

                _qrcodeScannerWrapper.setVisibility(View.GONE);
                _barcodeCapturedWrapper.setVisibility(View.VISIBLE);

                _editor.putString("BARCODE", barcode.displayValue);
                _editor.commit();
            }
        });

        _barcodeCapture.stopScanning();
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

    @Override
    public void onPause() {
        super.onPause();

        _editor.remove("BARCODE");
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

    private void validateCustomer() {

        if (_shopQrCode.getText().toString().isEmpty()) {

            Toast.makeText(_c, "Scan QR Code kabla ya kuendela!" + _shopQrCode.getText().toString(), Toast.LENGTH_LONG).show();

        } else if (_phone.getText().toString().isEmpty()) {
            Toast.makeText(_c, "ddd" + _shopQrCode, Toast.LENGTH_SHORT).show();
            Toast.makeText(_c, "Simu ya mteja ni lazima iwepo!", Toast.LENGTH_LONG).show();

        } else if (_name.getText().toString().isEmpty()) {

            Toast.makeText(_c, "Jina la mteja lazima liwepo!", Toast.LENGTH_LONG).show();

        } else if (_vfdCustId.getText().toString().isEmpty()) {

            Toast.makeText(_c, "TIN Number lazima liwepo!", Toast.LENGTH_LONG).show();

        } else {
            saveCustomer();
        }


    }

    public void saveCustomer() {

        Endpoint.setUrl("customer/new");
        String url = Endpoint.getUrl();

        _progressBar.setVisibility(View.VISIBLE);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        _progressBar.setVisibility(View.GONE);
                        ResponseData res = _gson.fromJson(response, ResponseData.class);


                        if (res.getFlag().equals("phone_exist")) {

                            Toast.makeText(_c, "Namba ya simu imeshasajiliwa", Toast.LENGTH_LONG).show();

                        } else if (res.getFlag().equals("qr_exist")) {

                            Toast.makeText(_c, "QR code imeshasajiliwa", Toast.LENGTH_LONG).show();

                        } else {
                            //delete qr code from shared preference
                            _editor.remove("BARCODE");
                            new FragmentHelper(_c).replace(new SuccessCutomerRegistrationFragment(), "SuccessCutomerRegistrationFragment", R.id.fragment_placeholder);
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
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
//                params.put("registered_by", "" + _dbHandler.getUser().getSalerId());

                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);

        postRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

}
