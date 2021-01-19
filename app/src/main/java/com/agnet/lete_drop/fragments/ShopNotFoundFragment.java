package com.agnet.lete_drop.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.activities.MainActivity;
import com.agnet.lete_drop.helpers.DatabaseHandler;
import com.agnet.lete_drop.helpers.FragmentHelper;
import com.agnet.lete_drop.models.Cart;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ShopNotFoundFragment extends Fragment {

    private FragmentActivity _c;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_not_found, container, false);
        _c = getActivity();

        SharedPreferences preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        SharedPreferences.Editor _editor = preferences.edit();

        Button createCustBtn = view.findViewById(R.id.create_customer_btn);
        createCustBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FragmentHelper(_c).replace(new CreateCustomerFragment(), "CreateCustomerFragment", R.id.fragment_placeholder);
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
