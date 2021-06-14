package com.agnet.leteApp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
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

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.main.HomeFragment;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.service.Endpoint;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


public class CreatePinFragment extends Fragment implements View.OnClickListener {

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
    private EditText _phoneInput;
    private EditText _otpTextboxOne, _otpTextboxTwo, _otpTextboxThree, _otpTextboxFour;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_pin, container, false);
        _c = getActivity();

        //initialize
        _dbHandler = new DatabaseHandler(_c);
        _gson = new Gson();
        _progressBar = view.findViewById(R.id.progressBar_cyclic);
        _preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        //binding
        _btnLogin = view.findViewById(R.id.login_btn);
        _otpTextboxOne = view.findViewById(R.id.otp_edit_box1);
        _otpTextboxTwo = view.findViewById(R.id.otp_edit_box2);
        _otpTextboxThree = view.findViewById(R.id.otp_edit_box3);
        _otpTextboxFour = view.findViewById(R.id.otp_edit_box4);
        _progressBar = view.findViewById(R.id.progressBar_cyclic);

        //events
        _btnLogin.setOnClickListener(this);


        EditText[] edit = {_otpTextboxOne, _otpTextboxTwo, _otpTextboxThree, _otpTextboxFour};
/*
        _otpTextboxOne.addTextChangedListener(new GenericTextWatcher(_otpTextboxOne, edit));
        _otpTextboxTwo.addTextChangedListener(new GenericTextWatcher(_otpTextboxTwo, edit));
        _otpTextboxThree.addTextChangedListener(new GenericTextWatcher(_otpTextboxThree, edit));
        _otpTextboxFour.addTextChangedListener(new GenericTextWatcher(_otpTextboxFour, edit));*/


        return view;

    }


    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();
        _progressBar.setVisibility(View.GONE);
        _btnLogin.setVisibility(View.VISIBLE);


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
            case R.id.login_btn:
                isUserExist();
                break;
            default:
                new FragmentHelper(_c).replaceWithAnimSlideFromRight(new CreatePinFragment(), "LoginFragment", R.id.fragment_placeholder);
                break;


        }
    }

    private void isUserExist() {

        String pin1 = _otpTextboxOne.getText().toString();
        String pin2 = _otpTextboxTwo.getText().toString();
        String pin3 = _otpTextboxThree.getText().toString();
        String pin4 = _otpTextboxFour.getText().toString();

        if (pin1.isEmpty()) {
            Toast.makeText(_c, "Andika pin Sahihi", Toast.LENGTH_SHORT).show();
        } else if (pin2.isEmpty()) {
            Toast.makeText(_c, "Andika pin Sahihi", Toast.LENGTH_SHORT).show();
        } else if (pin3.isEmpty()) {
            Toast.makeText(_c, "Andika pin Sahihi", Toast.LENGTH_SHORT).show();
        } else if (pin4.isEmpty()) {
            Toast.makeText(_c, "Andika pin Sahihi", Toast.LENGTH_SHORT).show();
        } else {

            String pin = pin1 + pin2 + pin3 + pin4;
            createUserPin(pin);
        }

    }

    public void createUserPin(final String pin) {

        _progressBar.setVisibility(View.VISIBLE);
        _btnLogin.setVisibility(View.GONE);

        Endpoint.setUrl("saler/create/pin");
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            _progressBar.setVisibility(View.GONE);


                        try {

                            Log.d("EHEHHEEH", response);
                            JSONObject res = new JSONObject(response);

                            if (res.getInt("code") == 200) {

                                new FragmentHelper(_c).replace(new HomeFragment(), " HomeFragment", R.id.fragment_placeholder);
                            }else {
                                _progressBar.setVisibility(View.GONE);
                                _btnLogin.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        _otpTextboxOne.setText("");
                        _otpTextboxTwo.setText("");
                        _otpTextboxThree.setText("");
                        _otpTextboxFour.setText("");

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //  _progressBar.setVisibility(View.GONE);
                        Log.d("RegistrationFragment", "here" + error.getMessage());
                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                        }

                        _otpTextboxOne.setText("");
                        _otpTextboxTwo.setText("");
                        _otpTextboxThree.setText("");
                        _otpTextboxFour.setText("");

                        _progressBar.setVisibility(View.GONE);
                        _btnLogin.setVisibility(View.VISIBLE);

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pin", pin);
                params.put("phone", _dbHandler.getUserPhone());
                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);
    }


}
