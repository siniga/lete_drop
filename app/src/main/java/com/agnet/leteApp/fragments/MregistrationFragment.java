package com.agnet.leteApp.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agnet.leteApp.R;
import com.agnet.leteApp.activities.LocationActivity;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.ResponseData;
import com.agnet.leteApp.models.User;
import com.agnet.leteApp.service.Endpoint;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


public class MregistrationFragment extends Fragment {

    private FragmentActivity _c;
    private BottomSheetBehavior _bottomSheetBehavior;
    private DatabaseHandler _dbHandler;
    private ProgressBar _progressBar;
    private Gson _gson;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_m_registration, container, false);
        _c = getActivity();

        _dbHandler = new DatabaseHandler(_c);

        _progressBar = view.findViewById(R.id.progressBar_cyclic);

        _gson = new Gson();

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        SharedPreferences preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        SharedPreferences.Editor _editor = preferences.edit();

        View signinLink = view.findViewById(R.id.signin_link);
        signinLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  new FragmentHelper(_c).replaceWithAnimSlideFromRight(new LoginFragment(), "LoginFragment", R.id.fragment_placeholder);
            }
        });

        EditText phoneInput = view.findViewById(R.id.phone_input);
        EditText nameInput = view.findViewById(R.id.name_input);
        Button registerButton = view.findViewById(R.id.register_btn);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(phoneInput.getText().toString(), nameInput.getText().toString());
            }
        });


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

    public void registerUser(String phone, String name) {

        _progressBar.setVisibility(View.VISIBLE);

        Endpoint.setUrl("saler");
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        _progressBar.setVisibility(View.GONE);


                            ResponseData res = _gson.fromJson(response, ResponseData.class);
                            User user = res.getUser();

                            if (res.getCode() == 201) {

                             //   JSONObject user = new JSONObject(res.getString("user"));
                                _dbHandler.createUser(user);


                                new FragmentHelper(_c).replace(new CreatePinFragment(), " CreatePinFragment", R.id.fragment_placeholder);

                            } else if (res.getCode() == 202) {
                                Toast.makeText(_c, "Namba imeshasajiliwa!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(_c, "Hakikisha umejaza maeneo yote!", Toast.LENGTH_LONG).show();
                            }

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
                        }


                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", phone);
                params.put("name", name);
                params.put("pin", "0");
                params.put("num_plate", "00");
                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);
    }

    public void showRegistrationSuccessDialog() {
        new AlertDialog.Builder(_c)
                .setTitle("Hongera")
                .setMessage("Umefanikiwa kujisajili, Endelea kutumia App.")
                .setPositiveButton("Endelea", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(_c, LocationActivity.class);
                        _c.startActivity(intent);
                        _c.finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

}
