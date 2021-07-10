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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.main.SuccessFragment;
import com.agnet.leteApp.fragments.main.adapters.MerchandiseImagesAdapter;
import com.agnet.leteApp.fragments.main.adapters.ProjectTypeAdapter;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.CustomerType;
import com.agnet.leteApp.models.MerchandiseImg;
import com.agnet.leteApp.models.OutletImage;
import com.agnet.leteApp.models.ResponseData;
import com.agnet.leteApp.models.User;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
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
    private RecyclerView _merchandiseImg;
    private String _outletImg;


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

        _custTypesSpinner = view.findViewById(R.id.cust_types_spinner);
        _continueBtn = view.findViewById(R.id.continue_btn);
        _progressBar = view.findViewById(R.id.progress_bar);
        TextView username = view.findViewById(R.id.user_name);

        try {
            User user = _gson.fromJson(_preferences.getString("User", null), User.class);
            String client = _preferences.getString("CLIENT", null);

            Token = _preferences.getString("TOKEN", null);
            username.setText(client);

            _clientId = _preferences.getInt("CLIENT_ID", 0);

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


        _continueBtn.setOnClickListener(view1 -> {

            new FragmentHelper(_c).replace(new MerchandiseCameraFragment(),"MerchandiseCameraFragment", R.id.fragment_placeholder);
         /*   //Get address base on location
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
            }*/
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

    public Bitmap stringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }


}


