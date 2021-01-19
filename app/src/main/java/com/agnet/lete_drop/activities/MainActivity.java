package com.agnet.lete_drop.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.application.mSingleton;
import com.agnet.lete_drop.fragments.AccountFragment;
import com.agnet.lete_drop.fragments.CartFragment;
import com.agnet.lete_drop.fragments.CustomerFragment;
import com.agnet.lete_drop.fragments.LoginFragment;
import com.agnet.lete_drop.fragments.OrdersFragment;
import com.agnet.lete_drop.fragments.HomeFragment;
import com.agnet.lete_drop.helpers.AppManager;
import com.agnet.lete_drop.helpers.DatabaseHandler;
import com.agnet.lete_drop.helpers.FragmentHelper;
import com.agnet.lete_drop.helpers.StatusBarHelper;
import com.agnet.lete_drop.service.Endpoint;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PermissionsListener, LocationEngineCallback<LocationEngineResult> {

    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private Gson _gson;
    private DatabaseHandler _dbHandler;
    private TextView _toolbarTitle;
    private BottomNavigationView _navigation;
    private CoordinatorLayout _coordinator;
    private TextView _cartQntyCount;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private final String TAG = "OrderActivity";
    private LinearLayout _homeBtn;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        //initialize
        _preferences = getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
        _dbHandler = new DatabaseHandler(this);
        _gson = new Gson();


        //binding
        _cartQntyCount = findViewById(R.id.cart_qnty_count);
        _coordinator = findViewById(R.id.coordinator);
        _toolbarTitle = findViewById(R.id.toolbar_title);
        _homeBtn = findViewById(R.id.home_btn);


        //confirm order bottom sheet


        _navigation = findViewById(R.id.bottom_navigation);
        _navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setHomeIconBottomNav();


        _toolbarTitle.setText("Nyumbani");

        //events
        RelativeLayout openCart = findViewById(R.id.open_cart_wrapper);
        openCart.setOnClickListener(v -> new FragmentHelper(MainActivity.this).replaceWithAnimSlideFromRight(new CartFragment(), "CartFragment", R.id.fragment_placeholder));

        _homeBtn.setOnClickListener(view -> {
            new FragmentHelper(MainActivity.this).replace(new HomeFragment(), "HomeFragment", R.id.fragment_placeholder);

        });

        //methods
        makeStatusBarTransparent();
        new StatusBarHelper(this).setStatusBarColor(R.color.white);
        initLocationEngine();
        forceEnableLocation();

        if(_dbHandler.getUserPhone().isEmpty()){
            new FragmentHelper(this).replace(new LoginFragment(), "LoginFragment", R.id.fragment_placeholder);
        }else {
            new FragmentHelper(this).replace(new HomeFragment(), "Homeragment", R.id.fragment_placeholder);
        }


    }

    public void setHomeIconBottomNav() {
        _navigation.getMenu().getItem(1).setChecked(true);
    }

    private void setHeader(String header) {
        getSupportActionBar().setTitle(header);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
/*
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Toast.makeText(this, "Resume Activity", Toast.LENGTH_SHORT).show();

        //show qunatity count
        if (_dbHandler.getTotalQnty() != 0) {
            int totalQnty = _dbHandler.getTotalQnty();
            _cartQntyCount.setText("" + getRoughNumber(totalQnty));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        forceEnableLocation();
    }
    public static String getRoughNumber(long value) {
        if (value <= 999) {
            return String.valueOf(value);
        }

        final String[] units = new String[]{"", "K", "M", "B", "P"};
        int digitGroups = (int) (Math.log10(value) / Math.log10(1000));
        return new DecimalFormat("#,##0.#").format(value / Math.pow(1000, digitGroups)) + "" + units[digitGroups];

    }

    public void setCartQnty(int totalQnty) {
        _cartQntyCount.setText("" + getRoughNumber(totalQnty));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationEngine = LocationEngineProvider.getBestLocationEngine(this);

            LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                    .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                    .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

            locationEngine.requestLocationUpdates(request,this, getMainLooper());
            locationEngine.getLastLocation(this);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

    }

    @Override
    public void onSuccess(LocationEngineResult result) {
        //   OrderActivity activity = activityWeakReference.get();

        if (this != null) {
            Location location = result.getLastLocation();

            if (location == null) {
                return;
            }

            _editor.putString("mLATITUDE",""+result.getLastLocation().getLatitude());
            _editor.putString("mLONGITUDE",""+result.getLastLocation().getLongitude());
            _editor.commit();

            //  Log.d("OrderActivity", "" + result.getLastLocation().getLatitude());
              /*  Toast.makeText(activity, String.format("location",
                        String.valueOf(result.getLastLocation().getLatitude()),
                        String.valueOf(result.getLastLocation().getLongitude())),
                        Toast.LENGTH_SHORT).show();*/
        }
    }

    @Override
    public void onFailure(@NonNull Exception exception) {

        if (this != null) {
            Toast.makeText(this, exception.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        //TODO: explain why you need location permission
        //  Toast.makeText(this,"user location permission explainaion", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            initLocationEngine();
        } else {
            Toast.makeText(this, "Location permission is not granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            boolean mState = !item.isChecked();
            switch (item.getItemId()) {
                case R.id.action_home:
                    item.setChecked(mState);
                    _toolbarTitle.setText("Nyumbani");
                    new FragmentHelper(MainActivity.this).replace(new HomeFragment(), "HomeFragment", R.id.fragment_placeholder);

                    break;
                case R.id.action_notificaion:
                    item.setChecked(mState);
                    _toolbarTitle.setText("Taarifa");
                    new FragmentHelper(MainActivity.this).replaceWithbackStack(new CustomerFragment(), " CustomerFragment", R.id.fragment_placeholder);

                    break;
                case R.id.action_orders:
                    item.setChecked(mState);
                    _toolbarTitle.setText("Oda Zako");
                    new FragmentHelper(MainActivity.this).replaceWithbackStack(new OrdersFragment(), "OrdersFragment", R.id.fragment_placeholder);

                    break;
            }
            return false;
        }
    };

    public void getPartners() {

        Endpoint.setUrl("mobile/partners");
        String url = Endpoint.getUrl();
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (!AppManager.isNullOrEmpty(response)) {

                            try {
                                JSONObject resObj = new JSONObject(response);
                                String partners = resObj.getString("partners");

                                _editor.putString("PARTNERS", partners);
                                _editor.commit();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();


                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            //TODO: display errors based on the message from the server
                            Toast.makeText(MainActivity.this, "Kuna tatizo, angalia mtandao alafu jaribu tena", Toast.LENGTH_SHORT).show();
                        }


                    }
                }
        ) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(jsonString, cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(String response) {
                super.deliverResponse(String.valueOf(response));
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };
        mSingleton.getInstance(this).addToRequestQueue(postRequest);

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

    public void getDrivers() {

        Endpoint.setUrl("salers-location");
        String url = Endpoint.getUrl();
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (!AppManager.isNullOrEmpty(response)) {

                            try {
                                JSONObject resObj = new JSONObject(response);
                                String drivers = resObj.getString("salers");

                                _editor.putString("DRIVERS", drivers);
                                _editor.commit();

                                Log.d("DRIVERS", drivers);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();


                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            //TODO: display errors based on the message from the server
                            Toast.makeText(MainActivity.this, "Kuna tatizo, angalia mtandao alafu jaribu tena", Toast.LENGTH_SHORT).show();
                        }


                    }
                }
        ) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(jsonString, cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(String response) {
                super.deliverResponse(String.valueOf(response));
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };
        mSingleton.getInstance(this).addToRequestQueue(postRequest);

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

    public Snackbar showRegistrationErrorSnackBar(View v) {
        Snackbar snackbar = Snackbar
                .make(_coordinator, "Sajili jina la duka ili upate huduma bora zaidi!", Snackbar.LENGTH_LONG).setActionTextColor(Color.parseColor("#fbbe02"))
                .setAction("Sawa", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      /*  Snackbar snackbar1 = Snackbar.make(view, "Product is deleted!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();*/
                        new FragmentHelper(MainActivity.this).replaceWithbackStack(new AccountFragment(), "AccountFragment", R.id.fragment_placeholder);

                    }
                });


        return snackbar;
    }

    private void forceEnableLocation() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);


        Task<LocationSettingsResponse> result;
        result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        MainActivity.this,
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Log.i(TAG, "onActivityResult: GPS Enabled by user");
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.i(TAG, "onActivityResult: User rejected GPS request");
                        forceEnableLocation();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void makeStatusBarTransparent() {
        //transparent status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.black_transparent));
        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

}
