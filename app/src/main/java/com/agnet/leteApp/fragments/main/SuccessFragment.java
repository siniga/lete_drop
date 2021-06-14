package com.agnet.leteApp.fragments.main;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Client;
import com.agnet.leteApp.models.Project;

import java.util.ArrayList;
import java.util.List;

public class SuccessFragment extends Fragment {

    private FragmentActivity _c;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success, container, false);
        _c = getActivity();


        Button successbtn = view.findViewById(R.id.success_btn);

        successbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FragmentHelper(_c).replaceWithbackStack(new ProjectFragment(), "ProjectFragment",R.id.fragment_placeholder);
            }
        });
        return view;
    }


}
