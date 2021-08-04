package com.agnet.leteApp.fragments.main.mapping;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.main.ProjectFragment;
import com.agnet.leteApp.fragments.main.adapters.FormAdapter;
import com.agnet.leteApp.fragments.main.adapters.ProjectAdapter;
import com.agnet.leteApp.fragments.main.adapters.ProjectTypeAdapter;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.ResponseData;
import com.agnet.leteApp.models.User;
import com.agnet.leteApp.service.Endpoint;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class MappingFormListFragment extends Fragment {

    private FragmentActivity _c;
    private Gson _gson;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private ProgressBar _progressBar;
    private String Token;
    private User _user;
    private int _projectId;
    private String _projectName;
    private RecyclerView _formList;
    private LinearLayoutManager _formLayoutManager;
    private ShimmerFrameLayout _shimmerLoader;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapping_form_list, container, false);
        _c = getActivity();

        _gson = new Gson();
        _preferences = _c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        TextView username = view.findViewById(R.id.user_name);
        _formList = view.findViewById(R.id.form_list);
        _shimmerLoader = view.findViewById(R.id.shimmer_view_container);

        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);
            _projectId = _preferences.getInt("PROJECT_ID", 0);
            _projectName = _preferences.getString("PROJECT_NAME", null);
            username.setText(_user.getName());

        } catch (NullPointerException e) {

        }

        _formLayoutManager = new LinearLayoutManager(_c, RecyclerView.VERTICAL, false);
        _formList.setLayoutManager(_formLayoutManager);
        _formList.setHasFixedSize(true);

        getForms();

        return view;
    }
    @Override
    public void onPause() {
        super.onPause();
        _shimmerLoader.setVisibility(View.GONE);
        _shimmerLoader.stopShimmerAnimation();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        new FragmentHelper(_c).replace(new ProjectFragment(), "ProjectFragment", R.id.fragment_placeholder);

                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void getForms() {

        Endpoint.setUrl("forms/project/" + _projectId);
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                response -> {

                    ResponseData res = _gson.fromJson(response, ResponseData.class);
                    if (res.getCode() == 200) {
                        FormAdapter formAdapter = new FormAdapter(_c, res.getForms());
                        _formList.setAdapter(formAdapter);

                    }
                    _shimmerLoader.setVisibility(View.GONE);
                    _shimmerLoader.stopShimmerAnimation();
                },
                error -> {
                    error.printStackTrace();
                    _shimmerLoader.setVisibility(View.GONE);
                    _shimmerLoader.stopShimmerAnimation();

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


}


