package com.agnet.leteApp.fragments.main.merchandise;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.main.SuccessFragment;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.CustomerType;
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

public class MerchandiseFormFragment_1 extends Fragment {

    private FragmentActivity _c;
    private Gson _gson;
    private List<CustomerType> _customerTypes;
    private Spinner _custTypesSpinner;
    private static final int pic_id_1 = 1;
    private static final int pic_id_2 = 2;
    private static final int pic_id_3 = 3;
    private static final int pic_id_4 = 4;
    private static final int pic_id_5 = 5;
    private ImageView _img1, _img2, _img3, _img4, _img5;
    private Button _continueBtn;
    private String _outletType;
    private int _outletTypeId;
    private Bitmap _photo1, _photo2, _photo3, _photo4, _photo5;
    private String _token;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private ProgressBar _progressBar;
    private String Token;
    private int _clientId;
    private String _location;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchandise_form_1, container, false);
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

        _img1 = view.findViewById(R.id.img_1);
        _img2 = view.findViewById(R.id.img_2);
        _img3 = view.findViewById(R.id.img_3);
        _img4 = view.findViewById(R.id.img_4);
        _img5 = view.findViewById(R.id.img_5);

        _img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // it will open the camera for capture the image

                // Start the activity with camera_intent,
                // and request pic id
                startActivityForResult(openCamera(), pic_id_1);
            }
        });

        _continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FragmentHelper(getActivity()).replace(new SuccessFragment(), "SuccessFragment", R.id.fragment_placeholder);
            }
        });


        _img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // it will open the camera for capture the image


                // Start the activity with camera_intent,
                // and request pic id
                startActivityForResult(openCamera(), pic_id_2);
            }
        });

        _img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // it will open the camera for capture the image


                // Start the activity with camera_intent,
                // and request pic id
                startActivityForResult(openCamera(), pic_id_3);
            }
        });

        _img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // it will open the camera for capture the image


                // Start the activity with camera_intent,
                // and request pic id
                startActivityForResult(openCamera(), pic_id_4);
            }
        });
        _img5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // it will open the camera for capture the image


                // Start the activity with camera_intent,
                // and request pic id
                startActivityForResult(openCamera(), pic_id_5);
            }
        });

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
            } else if (_img1.getDrawable() == null) {
                Toast.makeText(_c, "Picha zote tano lazima zichukuliwe!", Toast.LENGTH_SHORT).show();
            } else if (_img2.getDrawable() == null) {
                Toast.makeText(_c, "Picha zote tano lazima zichukuliwe!", Toast.LENGTH_SHORT).show();
            } else if (_img3.getDrawable() == null) {
                Toast.makeText(_c, "Picha zote tano lazima zichukuliwe!", Toast.LENGTH_SHORT).show();
            } else if (_img4.getDrawable() == null) {
                Toast.makeText(_c, "Picha zote tano lazima zichukuliwe!", Toast.LENGTH_SHORT).show();
            } else if (_img5.getDrawable() == null) {
                Toast.makeText(_c, "Picha zote tano lazima zichukuliwe!", Toast.LENGTH_SHORT).show();

            } else {

                saveOutlet(name, phone, _outletTypeId);
            }


        });

        try {
            User user = _gson.fromJson(_preferences.getString("User", null), User.class);
            String client = _preferences.getString("CLIENT", null);

            Token = _preferences.getString("TOKEN", null);
            username.setText(client);

            _clientId = _preferences.getInt("CLIENT_ID", 0);

        } catch (NullPointerException e) {

        }

        getCustomerType();
        getPermissions();

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // BitMap is data structure of image file
        // which stor the image in memory

        try {
            if (data.getExtras() != null) {

                // Match the request 'pic id with requestCode
                if (requestCode == pic_id_1) {
                    _photo1 = (Bitmap) data.getExtras()
                            .get("data");
                    _img1.setImageBitmap(_photo1);
                } else if (requestCode == pic_id_2) {

                    _photo2 = (Bitmap) data.getExtras()
                            .get("data");
                    _img2.setImageBitmap(_photo2);
                } else if (requestCode == pic_id_3) {
                    _photo3 = (Bitmap) data.getExtras()
                            .get("data");
                    _img3.setImageBitmap(_photo3);
                } else if (requestCode == pic_id_4) {

                    _photo4 = (Bitmap) data.getExtras()
                            .get("data");
                    _img4.setImageBitmap(_photo4);
                } else if (requestCode == pic_id_5) {

                    _photo5 = (Bitmap) data.getExtras()
                            .get("data");
                    _img5.setImageBitmap(_photo5);
                }
            }else {
                Toast.makeText(_c, "Hakuna picha iliochaguliwa!", Toast.LENGTH_SHORT).show();

            }
        }catch (RuntimeException e){

        }


    }

    private void getPermissions() {
        //request for camera permission
        Dexter.withActivity(_c).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                Toast.makeText(_c, "Endelea na kazi", Toast.LENGTH_SHORT).show();
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


    private void saveOutlet(String name, String phone, int outletTypeId) {
        _progressBar.setVisibility(View.VISIBLE);

        Random rand = new Random();
        int randomQrCode = rand.nextInt((9999 - 100) + 1) + 10;

       // Log.d("CLIENTELE H", "Hey there stranger " + _clientId);
        String ROOT_URL = "http://letedeve.aggreyapps.com/api/public/index.php/api/outlet";

        StringRequest postRequest = new StringRequest(Request.Method.POST, ROOT_URL,
                response -> {
                    ResponseData res = _gson.fromJson(response, ResponseData.class);

                    Log.d("HEREHAPA", response);

                    saveImages(res.getOutlet().getId());

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

                return params;
            }
        };

        mSingleton.getInstance(_c).addToRequestQueue(postRequest);
    }

    public String bitmapTObase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        return encodedImage;
    }

    private void saveImages(int outletId) {

        String name1 = String.valueOf(Calendar.getInstance().getTimeInMillis());
        String name2 = String.valueOf(Calendar.getInstance().getTimeInMillis());
        String name3 = String.valueOf(Calendar.getInstance().getTimeInMillis());
        String name4 = String.valueOf(Calendar.getInstance().getTimeInMillis());
        String name5 = String.valueOf(Calendar.getInstance().getTimeInMillis());

        String ROOT_URL = "http://letedeve.aggreyapps.com/api/public/index.php/api/outlet-image";

        StringRequest postRequest = new StringRequest(Request.Method.POST, ROOT_URL,
                response -> {
                    //Response res = _gson.fromJson(response, Response.class);

                    // Log.d("IMAGEOBJECT", _gson.toJson(response));

                    _progressBar.setVisibility(View.GONE);
                    new FragmentHelper(_c).replaceWithbackStack(new SuccessFragment(), "SuccessFragment", R.id.fragment_placeholder);

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
                params.put("img1", "" + _gson.toJson(new OutletImage(bitmapTObase64(_photo1), name1)));
                params.put("img2", "" + _gson.toJson(new OutletImage(bitmapTObase64(_photo2), name2)));
                params.put("img3", "" + _gson.toJson(new OutletImage(bitmapTObase64(_photo3), name3)));
                params.put("img4", "" + _gson.toJson(new OutletImage(bitmapTObase64(_photo4), name4)));
                params.put("img5", "" + "" + _gson.toJson(new OutletImage(bitmapTObase64(_photo5), name5)));
                params.put("outlet_id", "" + outletId);

                return params;
            }
        };

        mSingleton.getInstance(_c).addToRequestQueue(postRequest);
/*
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ROOT_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {

                            JSONObject obj = new JSONObject(new String(response.data));
                            Log.d("RESPONSE_REQUEST", _gson.toJson(obj));
                            /// Toast.makeText(_c.getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError", "" + error.getMessage());
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + _token);
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
//                params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(img)));
                params.put("img1", new DataPart(imagename + ".png", getFileDataFromDrawable(_photo1), 1));
                params.put("img2", new DataPart(imagename + ".png", getFileDataFromDrawable(_photo2), 1));
                params.put("img3", new DataPart(imagename + ".png", getFileDataFromDrawable(_photo3), 1));
                params.put("img4", new DataPart(imagename + ".png", getFileDataFromDrawable(_photo4), 1));
                params.put("img5", new DataPart(imagename + ".png", getFileDataFromDrawable(_photo5), 1));
//                params.put("outlet_id", new DataPart(1));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(_c).add(volleyMultipartRequest);*/
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private Intent openCamera() {
        Intent camera_intent
                = new Intent(MediaStore
                .ACTION_IMAGE_CAPTURE);

        return camera_intent;
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


}


