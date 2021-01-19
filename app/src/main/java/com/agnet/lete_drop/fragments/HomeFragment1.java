package com.agnet.lete_drop.fragments;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.activities.MainActivity;
import com.agnet.lete_drop.adapters.CategoryAdapter;
import com.agnet.lete_drop.adapters.CompanyAdapter;
import com.agnet.lete_drop.application.mSingleton;
import com.agnet.lete_drop.helpers.AppManager;
import com.agnet.lete_drop.helpers.DatabaseHandler;
import com.agnet.lete_drop.helpers.PusherConnectionHelper;
import com.agnet.lete_drop.models.Category;
import com.agnet.lete_drop.models.Company;
import com.agnet.lete_drop.models.ResponseData;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.agnet.lete_drop.application.App.CHANNEL_1_ID;

public class HomeFragment1 extends Fragment {

    private FragmentActivity _c;
    private RecyclerView _categoryList, _companyList;
    private GridLayoutManager _categoryGrid;
    private LinearLayoutManager _companyLayoutManager;
    private Gson _gson;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private ProgressBar _progressBar;
    private TextView _errorMsg;
    private DatabaseHandler _dbHandler;
    private ShimmerFrameLayout _shimmerFrameLayout;
    private Channel _channel;
    private String _mPhone;
    private Handler _mHandler;
    private NotificationManagerCompat _notificationManager;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        _c = getActivity();

        _gson = new Gson();

        //confirm order bottom sheet
        _dbHandler = new DatabaseHandler(_c);
        _mPhone = _dbHandler.getUserPhone();

        _notificationManager = NotificationManagerCompat.from(_c);

        new PusherConnectionHelper(_c);

        _channel = PusherConnectionHelper.subscribePusherChannel(_mPhone);

        _channel.bind("status-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {

//                _adapter.notifyDataSetChanged();
                try {
                    JSONObject jObject = new JSONObject(event.toString());

                    JSONObject data = jObject.getJSONObject("data");
                    JSONObject message = data.getJSONObject("message");
//                    JSONObject orderData = customer.getJSONObject("response");

                    //

                    //  Log.d("JOBJECTOTSTRING", message.toString());

                    Message handlerMessage = _mHandler.obtainMessage(0, message);
                    handlerMessage.sendToTarget();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //  showNotification();
            }
        });

        _mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {


                try {
                    JSONObject msg = new JSONObject(message.obj.toString());

                    if (msg.getInt("status") == 2) {
                        showNotification("Oda yako inashughulikiwa,tutakutaarifu ikiwa tayari. Asante!");
                    }
                    if (msg.getInt("status") == 3) {
                        showNotification("Oda yako ipo njiani,itakufikia muda sio mrefu, Asante!");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


//
            }
        };
        _shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);

        _preferences = _c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        _errorMsg = view.findViewById(R.id.error_msg);

        //companies
        _companyList = view.findViewById(R.id.company_list);
        _companyList.setHasFixedSize(true);

        _companyLayoutManager = new LinearLayoutManager(_c, RecyclerView.HORIZONTAL, false);
        _companyList.setLayoutManager(_companyLayoutManager);

        //category
        _categoryList = view.findViewById(R.id.category_list);
        _categoryList.setHasFixedSize(true);

        _categoryGrid = new GridLayoutManager(_c, 1);
        _categoryList.setLayoutManager(_categoryGrid);


        getCategories();
        getLocalCategory();

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();


        //((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) _c).setHomeIconBottomNav();

        //getData();

    }

    @Override
    public void onPause() {
        super.onPause();
        _shimmerFrameLayout.stopShimmerAnimation();
    }


    public void getLocalCategory() {

        List<Company> companies = new ArrayList<>();
        companies.add(new Company(1, "TCC", "ic_beer_bottle" ));
        companies.add(new Company(1, "TBL", "whiskey" ));
        companies.add(new Company(1, "ATC", "ic_wine_bottle"));

        CompanyAdapter adapter = new CompanyAdapter(_c, companies);
        _companyList.setAdapter(adapter);
    }


    public void getCategories() {

        _shimmerFrameLayout.setVisibility(View.VISIBLE);
        _shimmerFrameLayout.startShimmerAnimation();

        Endpoint.setUrl("mobile/categories");
        String url = Endpoint.getUrl();
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (!AppManager.isNullOrEmpty(response)) {


                            _shimmerFrameLayout.stopShimmerAnimation();
                            _shimmerFrameLayout.setVisibility(View.GONE);

                            _editor.putString("RESPONSE", response);
                            _editor.commit();

                            ResponseData res = _gson.fromJson(response, ResponseData.class);
                            List<Category> categories = res.getCategories();

                            _dbHandler.addCategories(categories);

                            CategoryAdapter adapter = new CategoryAdapter(_c, categories, HomeFragment1.this);
                            _categoryList.setAdapter(adapter);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        _shimmerFrameLayout.stopShimmerAnimation();
                        _shimmerFrameLayout.setVisibility(View.GONE);

                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            //TODO: display errors based on the message from the server
                            Toast.makeText(_c, "Kuna tatizo, angalia mtandao alafu jaribu tena", Toast.LENGTH_SHORT).show();
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

    public void showNotification(String msg) {

        Intent activityIntent = new Intent(_c, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(_c,
                0, activityIntent, 0);

        Intent fullScreenIntent = new Intent(_c, MainActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(_c, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new NotificationCompat.Builder(_c, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_basket)
                .setContentTitle("Message Kutoka Lete")
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(Color.BLUE)
                .setVibrate(new long[]{100, 200, 100, 200, 100, 200, 100})
                .setContentIntent(contentIntent)
                .setAutoCancel(false)

                .build();

        _notificationManager.notify(1, notification);

    }


}
