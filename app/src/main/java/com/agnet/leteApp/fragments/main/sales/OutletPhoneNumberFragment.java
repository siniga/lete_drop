package com.agnet.leteApp.fragments.main.sales;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.main.mapping.MappingFormListFragment;
import com.agnet.leteApp.fragments.main.merchandise.MerchandiseFormFragment;
import com.agnet.leteApp.fragments.main.outlets.NewBarcodeFragment;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.CustomerType;
import com.agnet.leteApp.models.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class OutletPhoneNumberFragment extends Fragment {

    private FragmentActivity _c;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private EditText _phone, _name;
    private LinearLayout _progressBar;
    private LinearLayout _newOuletBtn;
    private Gson _gson;
    private User _user;
    private String Token,_projectType;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outlet_phone_no, container, false);
        _c = getActivity();

        _preferences = _c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        _gson = new Gson();

        _phone = view.findViewById(R.id.phone_input);
        _name = view.findViewById(R.id.name_input);
        _newOuletBtn = view.findViewById(R.id.new_outlet_btn);
        _progressBar = view.findViewById(R.id.progress_bar_wrapper);

        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);
            _projectType =  _preferences.getString("PROJECT_TYPE", null);

        } catch (NullPointerException e) {

        }

        _newOuletBtn.setOnClickListener(view1 -> {

            String phone = _phone.getText().toString();
            String name = _name.getText().toString();

            if(name.isEmpty()){
                Toast.makeText(_c, "Ingiza jina la mteja!", Toast.LENGTH_LONG).show();
            }else if(phone.isEmpty()){
                Toast.makeText(_c, "Ingiza namba ya simu!", Toast.LENGTH_LONG).show();
            }else {

                _progressBar.setVisibility(View.VISIBLE);

                _editor.putString("PHONE", "00255"+phone);
                _editor.putString("NAME", name);
                _editor.commit();

                new FragmentHelper(_c).replaceWithbackStack(new ProductsFragment(), "ProductsFragment", R.id.fragment_placeholder);

            }


        });

        return view;

    }

    @Override
    public void onPause() {
        super.onPause();
        _progressBar.setVisibility(View.GONE);
    }



}
