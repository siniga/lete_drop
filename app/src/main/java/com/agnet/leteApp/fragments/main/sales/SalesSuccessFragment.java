package com.agnet.leteApp.fragments.main.sales;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.main.mapping.MappingFormListFragment;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.User;
import com.google.gson.Gson;

public class SalesSuccessFragment extends Fragment {

    private FragmentActivity _c;
    private Gson _gson;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private ProgressBar _progressBar;
    private String Token;
    private User _user;
    private int _projectId;
    private String _projectName;
    private RecyclerView _formList;
    private LinearLayoutManager _formLayoutManager;
    private Button _continueBtn;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_success, container, false);
        _c = getActivity();

        _gson = new Gson();
        _preferences = _c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

       _continueBtn = view.findViewById(R.id.continue_btn);
       _continueBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               new FragmentHelper(_c).replace(new ProductsFragment(),"ProductsFragment", R.id.fragment_placeholder);
           }
       });

        return  view;
    }

}


