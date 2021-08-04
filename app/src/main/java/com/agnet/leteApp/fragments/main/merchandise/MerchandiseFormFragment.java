package com.agnet.leteApp.fragments.main.merchandise;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.agnet.leteApp.fragments.main.HomeFragment;
import com.agnet.leteApp.fragments.main.ProjectFragment;
import com.agnet.leteApp.fragments.main.SuccessFragment;
import com.agnet.leteApp.fragments.main.adapters.MerchandiseImagesAdapter;
import com.agnet.leteApp.fragments.main.adapters.ProjectTypeAdapter;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.CustomerType;
import com.agnet.leteApp.models.MerchandiseImg;
import com.agnet.leteApp.models.Outlet;
import com.agnet.leteApp.models.OutletImage;
import com.agnet.leteApp.models.ResponseData;
import com.agnet.leteApp.models.User;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MerchandiseFormFragment extends Fragment {

    private FragmentActivity _c;
    private Gson _gson;
    private List<CustomerType> _customerTypes;
    private Spinner _custTypesSpinner;
    private static final int pic_id= 1;
    private ImageView _img;
    private Button _continueBtn;
    private String _outletType;
    private int _outletTypeId;
    private Bitmap _photo;
    private String _token;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private ProgressBar _progressBar;
    private String Token;
    private int _clientId;
    private String _location;
    private RecyclerView _merchandiseImgList;
    private String _outletImgStr;
    private  Bitmap[] _outletImgArr;
    private ArrayList<Bitmap> _imgList;
    private LinearLayout _continueBtnWrapper;
    private int _projectId;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchandise_form, container, false);
        _c = getActivity();
        _gson = new Gson();
        _preferences = _c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        EditText nameInput = view.findViewById(R.id.outlet_name_input);
        EditText phoneInput = view.findViewById(R.id.phone_num_input);
        RelativeLayout openCamera = view.findViewById(R.id.open_camera);

        _custTypesSpinner = view.findViewById(R.id.cust_types_spinner);
        _continueBtnWrapper = view.findViewById(R.id.continue_btn_wrapper);
        _continueBtn = view.findViewById(R.id.continue_btn);
        _progressBar = view.findViewById(R.id.progress_bar);
        TextView username = view.findViewById(R.id.user_name);

        try {
            Token = _preferences.getString("TOKEN", null);
            _clientId = _preferences.getInt("CLIENT_ID", 0);
            _projectId = _preferences.getInt("PROJECT_ID",0);

        } catch (NullPointerException e) {

        }
        _custTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                _outletType = adapterView.getItemAtPosition(position).toString();
                _outletTypeId = _customerTypes.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

       openCamera.setOnClickListener(view1 -> {

            //Get address base on location
            try {
                Geocoder geo = new Geocoder(_c.getApplicationContext(), Locale.getDefault());
                List<Address> addresses = geo.getFromLocation(Double.parseDouble(_preferences.getString("mLATITUDE", null)), Double.parseDouble(_preferences.getString("mLONGITUDE", null)), 1);
                if (addresses.isEmpty()) {

                } else {
                    if (addresses.size() > 0) {
                        _location = addresses.get(0).getSubAdminArea();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String name = nameInput.getText().toString();
            String phone = phoneInput.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(_c, "Ingiza jina la duka!", Toast.LENGTH_SHORT).show();
            } else if (phone.isEmpty()) {
                Toast.makeText(_c, "Ingiza namba ya simu!", Toast.LENGTH_SHORT).show();
            } else if (_outletTypeId == 0) {
                Toast.makeText(_c, "Chagua aina ya duka sahihi!", Toast.LENGTH_SHORT).show();
            }else{

                saveOutlet(name, phone, _outletTypeId);
            }
        });

        getCustomerType();
        getPermissions();

        return view;
    }

    private void getPermissions() {
        //request for camera permission
        Dexter.withActivity(_c).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
              //  Toast.makeText(_c, "Endelea na kazi", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(_c, "Huwezi Endelea na kazi", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }


    private void getCustomerType() {
        _customerTypes = new ArrayList<>();
        _customerTypes.add(new CustomerType(0, "Chagua aina ya duka"));
        _customerTypes.add(new CustomerType(1, "Mini Supermarket"));
        _customerTypes.add(new CustomerType(2, "Bars"));
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    new FragmentHelper(_c).replace(new ProjectFragment(),"ProjectFragment", R.id.fragment_placeholder);
                    return true;
                }
            }
            return false;
        });
    }

    private void saveOutlet(String name, String phone, int outletTypeId) {
        _progressBar.setVisibility(View.VISIBLE);

        Random rand = new Random();
        int randomQrCode = rand.nextInt((9999 - 100) + 1) + 10;

        // Log.d("CLIENTELE H", "Hey there stranger " + _clientId);
        String ROOT_URL = "http://letedeve.aggreyapps.com/api/public/index.php/api/outlet";

        StringRequest postRequest = new StringRequest(Request.Method.POST, ROOT_URL,
                response -> {
                    ResponseData res = _gson.fromJson(response, ResponseData.class);
                    try {

                        if(res.getOutlet() == null){
                            Toast.makeText(_c, "Duka halijasajiliwa, jaribu tena!", Toast.LENGTH_SHORT).show();
                        }
                        _editor.putInt("OUTLET_ID",res.getOutlet().getId());
                        _editor.commit();
                    }catch (NullPointerException e){

                    }


                  //  Log.d("HEREHAPA",""+ response);

                    new FragmentHelper(_c).replace(new MerchandiseCameraFragment(),"MerchandiseCameraFragment", R.id.fragment_placeholder);

                },
                error -> {

                    _progressBar.setVisibility(View.GONE);

                    Log.d("RESPONSE_ERROR", "here" + error.getMessage());
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        String errorString = new String(response.data);
                        Log.i("log error", errorString);
                    }

                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + "" + Token);
                return params;
            }

            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("phone", phone);
                params.put("lat", _preferences.getString("mLATITUDE", null));
                params.put("lng", _preferences.getString("mLONGITUDE", null));
                params.put("qr_code", ""+randomQrCode);
                params.put("vfd_cust_type", "6");
                params.put("vfd_cust_id", "NILL");
                params.put("user_id", "6");
                params.put("outlet_type_id", "" + outletTypeId);
                params.put("client_id", "" + _clientId);
                params.put("location", "" + _location);
                params.put("projectId", "" + _projectId);

                return params;
            }
        };

        mSingleton.getInstance(_c).addToRequestQueue(postRequest);
    }

}


