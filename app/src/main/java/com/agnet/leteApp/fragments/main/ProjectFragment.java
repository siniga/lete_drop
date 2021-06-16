package com.agnet.leteApp.fragments.main;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.auth.LoginFragment;
import com.agnet.leteApp.fragments.main.adapters.OutletAdapter;
import com.agnet.leteApp.fragments.main.adapters.ProjectAdapter;
import com.agnet.leteApp.fragments.main.adapters.ProjectTypeAdapter;
import com.agnet.leteApp.fragments.main.outlets.NewOutletFragment;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.ProjectType;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectFragment extends Fragment {

    private FragmentActivity _c;
    private RecyclerView _projectTypeList, _projectList, _outletList;
    private LinearLayoutManager _projectTypeLayoutManager, _projectLayoutManager, _outletLayoutManager;
    private String Token;
    private SharedPreferences.Editor _editor;
    private SharedPreferences _preferences;
    private Gson _gson;
    private ShimmerFrameLayout _shimmerLoader;
    private User _user;
    private  LinearLayout _newOutletBtn;
    private int _pos;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project, container, false);
        _c = getActivity();

        _preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
        _gson = new Gson();

        TextView username = view.findViewById(R.id.user_name);
        _projectTypeList = view.findViewById(R.id.project_type_list);
        _projectList = view.findViewById(R.id.project_list);
        _outletList = view.findViewById(R.id.outlet_list);
        _shimmerLoader = view.findViewById(R.id.shimmer_view_container);
        _newOutletBtn = view.findViewById(R.id.new_outlet_btn);
        LinearLayout userAcc = view.findViewById(R.id.view_user_account_btn);

       /*

        LinearLayout salesBtn = view.findViewById(R.id.sales_btn);
        ImageView userAcc = view.findViewById(R.id.user_account);
        LinearLayout merchandiSe = view.findViewById(R.id.merchandise_btn);*/


        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);
            username.setText(_user.getName());



        } catch (NullPointerException e) {

        }

        _projectTypeLayoutManager = new LinearLayoutManager(_c, RecyclerView.HORIZONTAL, false);
        _projectTypeList.setLayoutManager(_projectTypeLayoutManager);

        ProjectTypeAdapter typeAdapter = new ProjectTypeAdapter(_c, getProjectTypes(), this);
        _projectTypeList.setAdapter(typeAdapter);


        _projectLayoutManager = new LinearLayoutManager(_c, RecyclerView.VERTICAL, false);
        _projectList.setLayoutManager(_projectLayoutManager);


        _outletLayoutManager = new LinearLayoutManager(_c, RecyclerView.VERTICAL, false);
        _outletList.setLayoutManager(_outletLayoutManager);


        userAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _editor.remove("TOKEN");
                _editor.commit();
                new FragmentHelper(_c).replaceWithAnimSlideFromRight(new LoginFragment(), "LoginFragment", R.id.fragment_placeholder);

            }
        });

        _newOutletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FragmentHelper(_c).replaceWithAnimSlideFromRight(new NewOutletFragment(), "NewOutletFragment", R.id.fragment_placeholder);

            }
        });

        getPorjects("Mapping");

        return view;
    }

    private List<ProjectType> getProjectTypes() {
        List<ProjectType> list = new ArrayList<>();
        list.add(new ProjectType(1, "Mapping", "ic_type_mapping_grey", "ic_type_mapping_white"));
        list.add(new ProjectType(2, "Merchandise", "ic_type_merchandise_grey", "ic_type_merchandise_white"));
        list.add(new ProjectType(3, "Sales", "ic_type_sales_grey", "ic_type_sales_white"));
        list.add(new ProjectType(4, "Outlets", "ic_type_outlet_grey", "ic_type_outlet_white"));
        return list;
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

        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    _c.finish();
                    return true;
                }
            }
            return false;
        });
    }

    public void getPorjects(String type) {


        _shimmerLoader.setVisibility(View.VISIBLE);
        _shimmerLoader.startShimmerAnimation();
        _newOutletBtn.setVisibility(View.GONE);

        Endpoint.setUrl("projects/" + type + "/user/" + _user.getId());
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                response -> {

                    _projectList.setVisibility(View.VISIBLE);
                    _outletList.setVisibility(View.GONE);

                    ResponseData res = _gson.fromJson(response, ResponseData.class);

                    ProjectAdapter projectAdapter = new ProjectAdapter(_c, res.getProjects());
                    _projectList.setAdapter(projectAdapter);

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

    public void getUserOutlets() {

        _shimmerLoader.setVisibility(View.VISIBLE);
        _shimmerLoader.startShimmerAnimation();
        _newOutletBtn.setVisibility(View.VISIBLE);

        Endpoint.setUrl("outlets/user/"+_user.getId());
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                 ResponseData res = _gson.fromJson(response, ResponseData.class);

                    _projectList.setVisibility(View.GONE);
                    _outletList.setVisibility(View.VISIBLE);

                    if(res.getCode() == 200){
                        Log.d("RESPONSE_HHERE", _gson.toJson(res.getOutlets()));

                        OutletAdapter outletAdapter = new OutletAdapter(_c, res.getOutlets());
                        _outletList.setAdapter(outletAdapter);
                    }

                    _shimmerLoader.setVisibility(View.GONE);
                    _shimmerLoader.stopShimmerAnimation();

                },
                error -> {
                    _shimmerLoader.setVisibility(View.GONE);
                    _shimmerLoader.stopShimmerAnimation();

                    error.printStackTrace();
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
