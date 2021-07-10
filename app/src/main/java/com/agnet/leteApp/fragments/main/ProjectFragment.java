package com.agnet.leteApp.fragments.main;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.agnet.leteApp.helpers.AndroidDatabaseManager;
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
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectFragment extends Fragment {

    private FragmentActivity _c;
    private RecyclerView _projectList, _outletList;
    private LinearLayoutManager _projectTypeLayoutManager, _projectLayoutManager, _outletLayoutManager;
    private String Token;
    private SharedPreferences.Editor _editor;
    private SharedPreferences _preferences;
    private Gson _gson;
    private ShimmerFrameLayout _shimmerLoader;
    private User _user;
    private  LinearLayout _newOutletBtn;
    private int _pos;
    private LinearLayout _infoMsg;
    private TextView _infoMsgTxt;
    private TextView _searchProjectInput;
    private String _projectType;
    private Button _typeName;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project, container, false);
        _c = getActivity();

        _preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
        _gson = new Gson();

//        TextView username = view.findViewById(R.id.user_name);
        _projectList = view.findViewById(R.id.project_list);
//        _outletList = view.findViewById(R.id.outlet_list);
        _shimmerLoader = view.findViewById(R.id.shimmer_view_container);
//        _newOutletBtn = view.findViewById(R.id.new_outlet_btn);
//        LinearLayout userAcc = view.findViewById(R.id.view_user_account_btn);
        _infoMsg = view.findViewById(R.id.info_msg);
        _infoMsgTxt = view.findViewById(R.id.info_msg_txt);
        _typeName = view.findViewById(R.id.project_type);
//        _searchProjectInput = view.findViewById(R.id.search_project);

        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);
            _projectType = _preferences.getString("SELECTED_PROJECT_TYPE",null);

            _typeName.setText("Miradi yako ya "+_projectType);
//            username.setText(_user.getName());

        } catch (NullPointerException e) {

        }

        _projectLayoutManager = new LinearLayoutManager(_c, RecyclerView.VERTICAL, false);
        _projectList.setLayoutManager(_projectLayoutManager);


       /* _outletLayoutManager = new LinearLayoutManager(_c, RecyclerView.VERTICAL, false);
        _outletList.setLayoutManager(_outletLayoutManager);
*/

    /*    username.setOnClickListener(view13 -> {
            Intent intent = new Intent(_c, AndroidDatabaseManager.class);
            _c.startActivity(intent);
        });

        userAcc.setOnClickListener(view1 -> {
            _editor.remove("TOKEN");
            _editor.commit();

            new FragmentHelper(_c).replaceWithAnimSlideFromRight(new LoginFragment(), "LoginFragment", R.id.fragment_placeholder);


        });

        _searchProjectInput.setOnClickListener(view1 -> {
            Toast.makeText(_c, "Coming soon!", Toast.LENGTH_SHORT).show();
        });*/
/*

        _newOutletBtn.setOnClickListener(view12 -> new FragmentHelper(_c).replaceWithAnimSlideFromRight(new NewOutletFragment(), "NewOutletFragment", R.id.fragment_placeholder));
*/
        List<ProjectType> list = new ArrayList<>();
        list.add(new ProjectType(1, "Mauzo", "ic_truck", 100));
        list.add(new ProjectType(2, "Uwepo", "ic_mapping", 12));
        list.add(new ProjectType(3, "Vipeperushi", "ic_merchandise", 10));
        list.add(new ProjectType(4, "Maduka", "ic_outlets", 0));
        list.add(new ProjectType(4, "Risiti", "ic_receipts", 0));
        list.add(new ProjectType(4, "Pata ujumbe", "ic_notifications", 0));

        switch (_projectType){
            case "Mauzo":     getPorjects("Sales");
            break;
            case "Uwepo": getPorjects("Mapping");
            break;
            case "Vipeperushi": getPorjects("Merchandise");
            break;
            default: _projectList.setVisibility(View.GONE);
            break;
        }


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

        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    new FragmentHelper(_c).replace(new HomeFragment(),"HomeFragment", R.id.fragment_placeholder);
                    return true;
                }
            }
            return false;
        });
    }

    public void setupCircularBar(CircularProgressBar bar, float progress, float max, String progressColor, String bgColor) {

// Set Progress
//        _circularProgressBar.setProgress(65f);
// or with animation
        bar.setProgressWithAnimation(progress, Long.valueOf(1000)); // =1s

// Set Progress Max
        bar.setProgressMax(max);

// Set ProgressBar Color
        bar.setProgressBarColor(Color.parseColor(progressColor));
// or with gradient
       /* _circularProgressBar.setProgressBarColorStart(Color.GRAY);
        _circularProgressBar.setProgressBarColorEnd(Color.parseColor("#001689"));
        _circularProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);*/

// Set background ProgressBar Color
        bar.setBackgroundProgressBarColor(Color.parseColor(bgColor));
// or with gradient
      /*  _circularProgressBar.setBackgroundProgressBarColorStart(Color.WHITE);
        _circularProgressBar.setBackgroundProgressBarColorEnd(Color.parseColor("#001689"));
        _circularProgressBar.setBackgroundProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);*/

// Set Width
        bar.setProgressBarWidth(3f); // in DP
        bar.setBackgroundProgressBarWidth(2f); // in DP

// Other
        bar.setRoundBorder(true);
//       _circularProgressBar.setStartAngle(180f);
        bar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);
    }
    private List<ProjectType> getProjectTypes() {
        List<ProjectType> list = new ArrayList<>();
        list.add(new ProjectType(1, "Mauzo", "ic_type_sales_grey", 0));
        list.add(new ProjectType(2, "Uwepo", "ic_type_mapping_grey", 0));
        list.add(new ProjectType(3, "Vipeperushi", "ic_type_merchandise_grey", 0));
        list.add(new ProjectType(4, "Maduka", "ic_type_outlet_grey", 0));
        return list;
    }

/*    public void setProjectType(String type){
         _infoMsgTxt.setText("Huna mradi wa "+type+ " kwa sasa.");
    }*/
    public void getPorjects(String type) {

        _shimmerLoader.setVisibility(View.VISIBLE);
        _shimmerLoader.startShimmerAnimation();
        //_newOutletBtn.setVisibility(View.GONE);

        Endpoint.setUrl("projects/" + type + "/user/" +_user.getId());
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                response -> {

                    ResponseData res = _gson.fromJson(response, ResponseData.class);
                  //  _outletList.setVisibility(View.GONE);

                    Log.d("HEREHERESANA", _gson.toJson(res.getProjects()));

                    if(res.getProjects().size() == 0){
                       _infoMsg.setVisibility(View.VISIBLE);
                        _projectList.setVisibility(View.GONE);
                    }else {
                        _infoMsg.setVisibility(View.GONE);
                        _projectList.setVisibility(View.VISIBLE);

                        ProjectAdapter projectAdapter = new ProjectAdapter(_c, res.getProjects(), this);
                        _projectList.setAdapter(projectAdapter);
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
                        if(res.getOutlets().size() == 0){
                            _infoMsg.setVisibility(View.VISIBLE);
                        }else {
                            OutletAdapter outletAdapter = new OutletAdapter(_c, res.getOutlets());
                            _outletList.setAdapter(outletAdapter);
                            _infoMsg.setVisibility(View.GONE);

                        }

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
