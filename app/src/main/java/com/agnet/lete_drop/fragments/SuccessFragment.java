package com.agnet.lete_drop.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.activities.LocationActivity;
import com.agnet.lete_drop.activities.MainActivity;
import com.agnet.lete_drop.application.mSingleton;
import com.agnet.lete_drop.helpers.DatabaseHandler;
import com.agnet.lete_drop.helpers.FragmentHelper;
import com.agnet.lete_drop.models.Cart;
import com.agnet.lete_drop.models.Invoice;
import com.agnet.lete_drop.models.InvoiceDetail;
import com.agnet.lete_drop.models.Vfd;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.android.volley.VolleyLog.TAG;


public class SuccessFragment extends Fragment {

    private FragmentActivity _c;
    private BottomNavigationView _navigation;
    private LinearLayout _btnHome;
    private RelativeLayout _openCartBtm;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success, container, false);
        _c = getActivity();

        SharedPreferences preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        SharedPreferences.Editor _editor = preferences.edit();

        _navigation = _c.findViewById(R.id.bottom_navigation);
        _btnHome = _c.findViewById(R.id.home_btn);
        _openCartBtm = _c.findViewById(R.id.open_cart_wrapper);

        _navigation.setVisibility(View.GONE);
        _btnHome.setVisibility(View.GONE);
        _openCartBtm.setVisibility(View.GONE);


        if (!preferences.getString("NEW_ORDER_NO", null).equals(null)) {

            String orderNo = preferences.getString("NEW_ORDER_NO", null);

            TextView orderNoVuew = view.findViewById(R.id.order_no);
            orderNoVuew.setText(orderNo);
        }

        Button viewOrderBtn = view.findViewById(R.id.view_order_btn);
        viewOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //remove all the fragment traces so that user can start another order afresh from category/home fragment
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
              new FragmentHelper(_c).replace(new HomeFragment(), "HomeFragment", R.id.fragment_placeholder);
            }
        });



        return view;

    }


    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) getActivity()).setActionBarTitle("Oda Imefanikiwa");

        RelativeLayout viewCartWrapper = _c.findViewById(R.id.open_cart_wrapper);
        viewCartWrapper.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();


    }


}
