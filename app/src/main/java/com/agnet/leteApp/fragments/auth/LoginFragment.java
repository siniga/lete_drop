package com.agnet.leteApp.fragments.auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.MregistrationFragment;
import com.agnet.leteApp.fragments.main.HomeFragment;
import com.agnet.leteApp.fragments.main.ProjectFragment;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.helpers.StatusBarHelper;
import com.agnet.leteApp.models.ResponseData;
import com.agnet.leteApp.models.User;
import com.agnet.leteApp.service.Endpoint;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private FragmentActivity _c;
    private BottomSheetBehavior _bottomSheetBehavior;
    private DatabaseHandler _dbHandler;
    private SharedPreferences.Editor _editor;
    private ProgressBar _progressBar;
    private Gson _gson;
    private BottomNavigationView _navigation;
    private LinearLayout _btnHome;
    private RelativeLayout _openCartBtm;
    private TextView _signupLink;
    private Button _btnLogin;
    private SharedPreferences _preferences;
    private EditText _otpTextboxOne, _otpTextboxTwo, _otpTextboxThree, _otpTextboxFour;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        _c = getActivity();

        //initialize
        _dbHandler = new DatabaseHandler(_c);
        _gson = new Gson();
        _progressBar = view.findViewById(R.id.progressBar_cyclic);
        _preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        //binding
        _navigation = _c.findViewById(R.id.bottom_navigation);
        _btnHome = _c.findViewById(R.id.home_btn);
        _openCartBtm = _c.findViewById(R.id.open_cart_wrapper);
        _signupLink = view.findViewById(R.id.signup_link);
        _btnLogin = view.findViewById(R.id.login_btn);

        EditText phoneInput  = view.findViewById(R.id.phone_input);
        EditText passwordInput = view.findViewById(R.id.password_input);

        //events
        _signupLink.setOnClickListener(this);
        _btnLogin.setOnClickListener(this);

        //methods
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        new StatusBarHelper(_c).setStatusBarColor(R.color.colorPrimaryDark);

        _navigation.setVisibility(View.GONE);
        _btnHome.setVisibility(View.GONE);
        _openCartBtm.setVisibility(View.GONE);

        _btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone  = phoneInput.getText().toString();
                String password = passwordInput.getText().toString();
                //new FragmentHelper(_c).replace(new ProjectFragment(), "ProjectFragmen", R.id.fragment_placeholder);
                if(phone.isEmpty()){
                    Toast.makeText(_c, "Jaza namba ya simu ili kuendelea!", Toast.LENGTH_LONG).show();
                } else if(password.isEmpty()){
                    Toast.makeText(_c, "Jaza password ili kuendelea!", Toast.LENGTH_SHORT).show();
                }else {
                    loginUser(phone, password);
                }

            }
        });

        try{
            User user = _gson.fromJson(_preferences.getString("User",null), User.class);
            String Token = _preferences.getString("TOKEN", null);
           /* if(!Token.equals(null)){
                new FragmentHelper(_c).replace(new LoginFragment(), "LoginFragment", R.id.fragment_placeholder);
            }*/


            Log.d("USERSSS", _gson.toJson(user));
        }catch (NullPointerException e){

        }

        return view;

    }


    @Override
    public void onResume() {
        super.onResume();

        try{
            if(!_preferences.getString("TOKEN", null).isEmpty()){
                new FragmentHelper(_c).replaceWithbackStack(new ProjectFragment(),"ProjectFragment", R.id.fragment_placeholder);
            }
        }catch (NullPointerException e){

        }


    }

    @Override
    public void onPause() {
        super.onPause();


    }

    public static boolean isValidPhone(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 10 || phone.length() > 14) {
                check = false;

            } else {
                check = true;

            }
        } else {
            check = false;
        }
        return check;
    }

    private void handleBottomSheetBehaviors() {

        //If you want to handle callback of Sheet Behavior you can use below code
        _bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
              /*  if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    _bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }*/

                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        //Log.d(TAG, "State Collapsed");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        // Log.d(TAG, "State Dragging");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        //  Log.d(TAG, "State Expanded");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        // Log.d(TAG, "State Hidden");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        //  Log.d(TAG, "State Settling");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.signup_link:
                new FragmentHelper(_c).replaceWithAnimSlideFromRight(new MregistrationFragment(), "MregistrationFragment", R.id.fragment_placeholder);
                break;
            case R.id.login_btn:
                isUserExist();
                break;
            default:
                new FragmentHelper(_c).replaceWithAnimSlideFromRight(new LoginFragment(), "LoginFragment", R.id.fragment_placeholder);
                break;


        }
    }

    private void isUserExist() {


    }

    public void loginUser(String phone, String password) {

        _progressBar.setVisibility(View.VISIBLE);

        String ROOT_URL = "http://letedeve.aggreyapps.com/api/public/index.php/api/login";


        StringRequest postRequest = new StringRequest(Request.Method.POST, ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ResponseData res = _gson.fromJson(response, ResponseData.class);

                        _editor.putString("TOKEN", res.getToken());
                        _editor.commit();

                        getUser(res.getToken());
                        _progressBar.setVisibility(View.GONE);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(_c, "Kuna tatizo la mtandao, jaribu tena au wasiliana na wataalamu ", Toast.LENGTH_LONG).show();
                        _progressBar.setVisibility(View.GONE);
                        Log.d("RegistrationFragment", "here" + error.getMessage());
                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                        }

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", phone);
                params.put("password", password);
                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);
    }


    public void getUser(String token) {



        String ROOT_URL = "http://letedeve.aggreyapps.com/api/public/index.php/api/authenticated/user";


        StringRequest postRequest = new StringRequest(Request.Method.GET, ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ResponseData res = _gson.fromJson(response, ResponseData.class);
                        Log.d("USER_DATA", _gson.toJson(res.getUser()));

                        _editor.putString("User", _gson.toJson(res.getUser()));
                        _editor.commit();


                        new FragmentHelper(_c).replaceWithbackStack(new ProjectFragment(),"ProjectFragment", R.id.fragment_placeholder);
//
                        _progressBar.setVisibility(View.GONE);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        _progressBar.setVisibility(View.GONE);
                        Log.d("RegistrationFragment", "here" + error.getMessage());
                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                        }

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + "" + token);
                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);
    }


}
