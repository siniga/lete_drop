package com.agnet.leteApp.fragments.main;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.agnet.leteApp.fragments.main.adapters.ProjectAdapter;
import com.agnet.leteApp.fragments.main.adapters.ProjectTypeAdapter;
import com.agnet.leteApp.helpers.DateHelper;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.CustomerType;
import com.agnet.leteApp.models.Project;
import com.agnet.leteApp.models.ProjectType;
import com.agnet.leteApp.models.ResponseData;
import com.agnet.leteApp.models.Stat;
import com.agnet.leteApp.models.User;
import com.agnet.leteApp.service.Endpoint;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
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

public class HomeFragment extends Fragment {

    private FragmentActivity _c;
    private RecyclerView _projectTypeList, _projectList, _outletList;
    private GridLayoutManager _projectTypeLayoutManager;
    private String Token;
    private SharedPreferences.Editor _editor;
    private SharedPreferences _preferences;
    private Gson _gson;
    private User _user;
    private Spinner _projectTypesSpinner;
    private List<ProjectType> _projectTypesData;
    private TextView _revenue, _revenueTarget,_mappingCount,_mappingTarget,_merchandiseCount,_merchandiseTarget,_outletCount,_outletTarget;
    private ShimmerFrameLayout _shimmer;
    private  CircularProgressBar _circularSales,_circularMapping,_circularMerchandise,_circularOutlets;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        _c = getActivity();

        _preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
        _gson = new Gson();


        TextView username = view.findViewById(R.id.user_name);
        _projectTypeList = view.findViewById(R.id.project_type_list);
        _projectList = view.findViewById(R.id.project_list);
        _outletList = view.findViewById(R.id.outlet_list);
        _revenue = view.findViewById(R.id.revenue);
        _revenueTarget = view.findViewById(R.id.revenue_target);
        _mappingCount =view.findViewById(R.id.mapping_count);
        _mappingTarget = view.findViewById(R.id.mapping_target);
        _merchandiseCount = view.findViewById(R.id.merchandise_count);
        _merchandiseTarget = view.findViewById(R.id.merchandise_target);
        _outletCount = view.findViewById(R.id.outlet_count);
        _outletTarget = view.findViewById(R.id.outlet_target);
        _shimmer = view.findViewById(R.id.shimmer_view_container);
        _circularSales = view.findViewById(R.id.circular_bar_sales);
      _circularMapping = view.findViewById(R.id.circular_bar_mapping);
       _circularMerchandise = view.findViewById(R.id.circular_bar_merchandise);
       _circularOutlets  = view.findViewById(R.id.circular_bar_outlets);

        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);
            username.setText(_user.getName());

        } catch (NullPointerException e) {

        }

//        _projectTypeLayoutManager = new LinearLayoutManager(_c, RecyclerView.HORIZONTAL, false);

        _projectTypeLayoutManager = new GridLayoutManager(_c, 3);
        _projectTypeList.setLayoutManager(_projectTypeLayoutManager);

        ProjectTypeAdapter typeAdapter = new ProjectTypeAdapter(_c, getProjectTypes());
        _projectTypeList.setAdapter(typeAdapter);


        getAgentStats();

        return view;
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

    @Override
    public void onPause() {
        super.onPause();
        _shimmer.setVisibility(View.GONE);
        _shimmer.stopShimmerAnimation();
    }

    private List<ProjectType> getProjectTypes() {
        List<ProjectType> list = new ArrayList<>();
        list.add(new ProjectType(1, "Mauzo", "ic_truck", 100));
        list.add(new ProjectType(2, "Uwepo", "ic_mapping", 12));
        list.add(new ProjectType(3, "Vipeperushi", "ic_merchandise", 10));
        list.add(new ProjectType(4, "Maduka", "ic_outlets", 0));
        list.add(new ProjectType(4, "Risiti", "ic_receipts", 0));
        list.add(new ProjectType(4, "Pata ujumbe", "ic_notifications", 0));
        return list;
    }

    /* private void getSpinnerProjectType() {
         _projectTypesData = new ArrayList<>();
         _projectTypesData.add(new ProjectType(0, "Data za Mauzo","",0));
         _projectTypesData.add(new ProjectType(1, "Data za Uwepo","",0));
         _projectTypesData.add(new ProjectType(2, "Data za Maduka","",0));
         _projectTypesData.add(new ProjectType(3, "Data za Vipeperushi","",0));


         // Creating adapter for spinner
         ArrayAdapter<ProjectType> dataAdapter = new ArrayAdapter<ProjectType>(_c, android.R.layout.simple_spinner_item, _projectTypesData);

         // Drop down layout style - list view with radio button
         dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

         // attaching data adapter to spinner
         _projectTypesSpinner.setAdapter(dataAdapter);
     }
 */
    private void setupCircularBar(CircularProgressBar bar, float progress, float max, String progressColor, String bgColor) {

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
        bar.setProgressBarWidth(6f); // in DP
        bar.setBackgroundProgressBarWidth(6f); // in DP

// Other
        bar.setRoundBorder(true);
//       _circularProgressBar.setStartAngle(180f);
        bar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);
    }

    private void getAgentStats(){
        String start  = DateHelper.getCurrentDate();
        String end = DateHelper.getCurrentDate();

        Endpoint.setUrl("agent/"+_user.getId()+"/stats?start="+start+"&end="+end);
        String url = Endpoint.getUrl();


        _shimmer.setVisibility(View.VISIBLE);
        _shimmer.startShimmerAnimation();;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                response -> {

                    ResponseData res = _gson.fromJson(response, ResponseData.class);
//                     Log.d("HEREHERESANA", _gson.toJson(res));

                     if(res.getCode() == 200){
                         Stat stat =  res.getStats();
                         _revenue.setText(stat.getRevenueFormatted());
                         _revenueTarget.setText(stat.getRevenueTargetFormatted());
                         _mappingCount.setText(""+stat.getMappingCount());
                         _mappingTarget.setText(""+stat.getMappingTarget());
                         _merchandiseCount.setText(""+stat.getMerchandiseCount());
                         _merchandiseTarget.setText(""+stat.getMerchandiseTarget());
                         _outletCount.setText(""+stat.getOutletCount());
                         _outletTarget.setText(""+stat.getOutletTarget());

                         setupCircularBar(_circularSales, stat.getRevenue(), stat.getRevenueTarget(), "#001689", "#ffffff");
                         setupCircularBar(_circularMapping, stat.getMappingCount(), stat.getMappingTarget(), "#ffb400", "#ffffff");
                         setupCircularBar(_circularMerchandise, +stat.getMerchandiseCount(), stat.getMerchandiseTarget(),"#34b0c3","#ffffff");
                         setupCircularBar(_circularOutlets, stat.getOutletCount(), stat.getOutletTarget(),"#ed1c24","#ffffff");

                         _editor.putString("STAT",_gson.toJson(stat));
                         _editor.commit();
                     }




                    _shimmer.setVisibility(View.GONE);
                    _shimmer.stopShimmerAnimation();
                },
                error -> {
                    error.printStackTrace();
                    _shimmer.setVisibility(View.GONE);
                    _shimmer.stopShimmerAnimation();

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

