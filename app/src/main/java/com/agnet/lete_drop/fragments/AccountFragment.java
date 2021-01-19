package com.agnet.lete_drop.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.application.mSingleton;
import com.agnet.lete_drop.helpers.DatabaseHandler;
import com.agnet.lete_drop.helpers.FragmentHelper;
import com.agnet.lete_drop.models.ResponseData;
import com.agnet.lete_drop.service.Endpoint;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private FragmentActivity _c;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private DatabaseHandler _dbHandler;
    private ShimmerFrameLayout _shimmerFrameLayout;
    private NotificationManagerCompat _notificationManager;
    private Button _submitUserDetailsBtn;
    private TextView _logoutBtn;
    private EditText _mPhone, _mName, _mNumPlate;
    private BottomNavigationView _navigation;
    private LinearLayout _btnHome;
    private RelativeLayout _openCartBtm;
    private Button _updateUserBtn;
    private ProgressBar _progressBar;
    private Gson _gson;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        _c = getActivity();

        //initialization

        _dbHandler = new DatabaseHandler(_c);
        _preferences = _c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
        _gson =  new Gson();

        _mPhone = view.findViewById(R.id.phone_input);
        _mName = view.findViewById(R.id.name_input);
        _mNumPlate = view.findViewById(R.id.plate_number);
        _logoutBtn = view.findViewById(R.id.logout_btn);
        _submitUserDetailsBtn = view.findViewById(R.id.submit_user_details);
        _navigation = _c.findViewById(R.id.bottom_navigation);
        _btnHome = _c.findViewById(R.id.home_btn);
        _openCartBtm = _c.findViewById(R.id.open_cart_wrapper);
        _updateUserBtn = view.findViewById(R.id.submit_user_details);
        _progressBar = view.findViewById(R.id.progressBar_cyclic);


        _navigation.setVisibility(View.GONE);
        _btnHome.setVisibility(View.GONE);
        _openCartBtm.setVisibility(View.GONE);



        //events
        _logoutBtn.setOnClickListener(this);
        _updateUserBtn.setOnClickListener(this);

        //methods
        setUserAccount();

        return view;

    }

    private void setUserAccount() {

        //set user account data if they exist in db
        if(_mName.getText().toString().isEmpty()){
            _mName.setText(_dbHandler.getUser().getName());
        }

        if(_mPhone.getText().toString().isEmpty()){
            _mPhone.setText(_dbHandler.getUser().getPhone());
        }

        if(_mNumPlate.getText().toString().isEmpty()){
            _mNumPlate.setText(""+_dbHandler.getUser().getNumPlate());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout_btn:
                deleteUserFromDb();
                new FragmentHelper(_c).replace(new LoginFragment(),"LoginFragment", R.id.fragment_placeholder);
                break;
            case R.id.submit_user_details:
                updateUserAccount();
                break;
            default:
                new FragmentHelper(_c).replace(new AccountFragment(), "AccountFragment", R.id.fragment_placeholder);
                break;


        }
    }

    private void deleteUserFromDb() {
        _dbHandler.deleteMuser();
    }

    private void removeFragmentsBackStack() {
        //remove all the fragment traces so that user can start another order afresh from category/home fragment
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private void updateUserAccount() {
        registerUser();
    }

    public void registerUser() {

        _progressBar.setVisibility(View.VISIBLE);

        Endpoint.setUrl("saler/update");
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        _progressBar.setVisibility(View.GONE);

                        ResponseData res = _gson.fromJson(response, ResponseData.class);

                        Log.d("KKDKDK",""+res.getUser());
//                        _customers = res.getCustomers();
                        /*try {

                            JSONObject res = new JSONObject(response);




                            if (res.getString("code").equals("200")) {

                                String phone = _mPhone.getText().toString();
                                String name = _mName.getText().toString();
                                String numPlate= _mNumPlate.getText().toString();

                                _dbHandler.createUser(phone,name,numPlate,0);

                                Toast.makeText(_c, "Data Imehifadhiwa", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
*/
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _progressBar.setVisibility(View.GONE);

                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);


                            Toast.makeText(_c, "Namba haipo!", Toast.LENGTH_SHORT).show();
                        }


                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", _dbHandler.getUserPhone());
                params.put("name",_mName.getText().toString());
                params.put("pin", "0");
                params.put("num_plate", "0");
                params.put("new_phone", _mPhone.getText().toString());
                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);
    }

}
