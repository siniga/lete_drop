package com.agnet.lete_drop.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.activities.LocationActivity;
import com.agnet.lete_drop.activities.MainActivity;
import com.agnet.lete_drop.helpers.DatabaseHandler;
import com.agnet.lete_drop.helpers.FragmentHelper;
import com.agnet.lete_drop.models.Cart;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SuccessCutomerRegistrationFragment extends Fragment {

    private FragmentActivity _c;
    private RecyclerView _cartlist;
    private LinearLayoutManager _layoutManager;
    private DatabaseHandler _dbHandler;
    private TextView _cartTotalAmnt;
    private DecimalFormat _formatter;
    private List<Cart> _products;
    private TextView _errorMsg;
    private Button _placeOrderBtn;
    private AlertDialog _alertDialog;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_registration, container, false);
        _c = getActivity();

        SharedPreferences preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        SharedPreferences.Editor _editor = preferences.edit();

        Gson gson = new Gson();

        TextView goBackBtn = view.findViewById(R.id.go_home);
        goBackBtn.setOnClickListener(v -> {

            //remove all the fragment traces so that user can start another order afresh from category/home fragment
            FragmentManager fm = getActivity().getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }

            new FragmentHelper(_c).replace(new CustomerFragment(), "CustomerFragment", R.id.fragment_placeholder);

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
