package com.agnet.leteApp.fragments.main.merchandise;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.main.ProjectFragment;
import com.agnet.leteApp.fragments.main.SuccessFragment;
import com.agnet.leteApp.fragments.main.adapters.MerchandiseImagesAdapter;
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
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Mode;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MerchandiseCameraFragment extends Fragment {

    private FragmentActivity _c;
    private Gson _gson;
    private List<CustomerType> _customerTypes;
    private Spinner _custTypesSpinner;
    private static final int pic_id = 1;
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
    private List<MerchandiseImg> _list;
    private MerchandiseImagesAdapter _imgAdapter;
    private ArrayList<Bitmap> _imgList;
    private List<OutletImage> _outletImages;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_merchandise_camera, container, false);
        _c = getActivity();
        _gson = new Gson();
        _preferences = _c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
        _merchandiseImg = view.findViewById(R.id.merchandise_img_list);

        try {
            Token = _preferences.getString("TOKEN", null);
        } catch (NullPointerException e) {

        }

        LinearLayoutManager imgLayoutManager = new LinearLayoutManager(_c, RecyclerView.HORIZONTAL, false);
        _merchandiseImg.setLayoutManager(imgLayoutManager);

        getPermissions();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CameraView camera = view.findViewById(R.id.camera);
        camera.setLifecycleOwner(getViewLifecycleOwner());

        _imgList = new ArrayList<>();
        _outletImages = new ArrayList<>();

        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
                // A Picture was taken!

                // Access the raw data if needed.
                //   byte[] data = result.getData();
                result.toBitmap(new BitmapCallback() {

                    @Override
                    public void onBitmapReady(@Nullable Bitmap bitmap) {
                        Toast.makeText(_c, "Picha imechukuliwa!", Toast.LENGTH_SHORT).show();

                        _imgList.add(getResizedBitmap(bitmap, 300));
                        attachImagesToadapter();

                        if(_imgList.size() == 5){
                           if(_preferences.getInt("OUTLET_ID", 0) != 0 ){
                               saveImages(_preferences.getInt("OUTLET_ID", 0) );
                           };
                        }

                    }
                });

            }


            @Override
            public void onVideoTaken(VideoResult result) {
                // A Video was taken!
            }

            // And much more
        });


        RelativeLayout captureBtn = view.findViewById(R.id.capture_btn);
        captureBtn.setOnClickListener(view1 -> camera.takePicture());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Toast.makeText(_c, "Maliza kuchukua picha ili kuendelea!", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return false;
        });
    }

    protected int sizeOf(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else {
            return data.getByteCount();
        }
    }

    public void attachImagesToadapter() {
        _imgAdapter = new MerchandiseImagesAdapter(_c, _imgList);
        _merchandiseImg.setAdapter(_imgAdapter);
        _imgAdapter.notifyDataSetChanged();
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
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

                  //   Log.d("IMAGEOBJECT", _gson.toJson(response));

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
                params.put("img1", "" + _gson.toJson(new OutletImage(bitmapTObase64(_imgList.get(0)), name1)));
                params.put("img2", "" + _gson.toJson(new OutletImage(bitmapTObase64(_imgList.get(1)), name2)));
                params.put("img3", "" + _gson.toJson(new OutletImage(bitmapTObase64(_imgList.get(2)), name3)));
                params.put("img4", "" + _gson.toJson(new OutletImage(bitmapTObase64(_imgList.get(3)), name4)));
                params.put("img5", "" + "" + _gson.toJson(new OutletImage(bitmapTObase64(_imgList.get(4)), name5)));
                params.put("outlet_id", "" + outletId);

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

}


