package com.agnet.lete_drop.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.application.mSingleton;
import com.agnet.lete_drop.helpers.DatabaseHandler;
import com.agnet.lete_drop.helpers.FragmentHelper;
import com.agnet.lete_drop.helpers.GenericTextWatcher;
import com.agnet.lete_drop.helpers.StatusBarHelper;
import com.agnet.lete_drop.models.Customer;
import com.agnet.lete_drop.models.Invoice;
import com.agnet.lete_drop.models.InvoiceDetail;
import com.agnet.lete_drop.models.ResponseData;
import com.agnet.lete_drop.models.User;
import com.agnet.lete_drop.models.Vfd;
import com.agnet.lete_drop.service.Endpoint;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.android.volley.VolleyLog.TAG;


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
        _otpTextboxOne = view.findViewById(R.id.otp_edit_box1);
        _otpTextboxTwo = view.findViewById(R.id.otp_edit_box2);
        _otpTextboxThree = view.findViewById(R.id.otp_edit_box3);
        _otpTextboxFour = view.findViewById(R.id.otp_edit_box4);

        //events
        _signupLink.setOnClickListener(this);
        _btnLogin.setOnClickListener(this);

        //methods
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        new StatusBarHelper(_c).setStatusBarColor(R.color.colorPrimaryDark);

        _navigation.setVisibility(View.GONE);
        _btnHome.setVisibility(View.GONE);
        _openCartBtm.setVisibility(View.GONE);


        EditText[] edit = {_otpTextboxOne, _otpTextboxTwo, _otpTextboxThree, _otpTextboxFour};

    /*    _otpTextboxOne.addTextChangedListener(new GenericTextWatcher(_otpTextboxOne, edit));
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
            loginUser(pin);
        }

    }

    public void loginUser(final String pin) {

        _progressBar.setVisibility(View.VISIBLE);

        Endpoint.setUrl("saler/login");
        String url = Endpoint.getUrl();

        _otpTextboxOne.setText("");
        _otpTextboxTwo.setText("");
        _otpTextboxThree.setText("");
        _otpTextboxFour.setText("");


        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        _progressBar.setVisibility(View.GONE);

                        ResponseData res = _gson.fromJson(response, ResponseData.class);
                        User user = res.getUser();

                     //   Log.d("LOGHAPA", response);
                        if (res.getCode() == 201) {

                            _dbHandler.createUser(user.getPhone(), user.getName(), "", user.getServerId());

                            new FragmentHelper(_c).replace(new HomeFragment(), " HomeFragment", R.id.fragment_placeholder);


                        } else if (res.getCode() ==202) {

                            Toast.makeText(_c, "Namba imeshasajiliwa!", Toast.LENGTH_LONG).show();

                        } else {

                            Toast.makeText(_c, "Namba hii haipo!", Toast.LENGTH_LONG).show();
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

                        _progressBar.setVisibility(View.GONE);
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

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pin", pin);
                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);
    }


}
