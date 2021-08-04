package com.agnet.leteApp.fragments.main.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.QrcodeScannerFragment;
import com.agnet.leteApp.fragments.main.HomeFragment;
import com.agnet.leteApp.fragments.main.outlets.OutletSuccessFragment;
import com.agnet.leteApp.fragments.main.sales.OrderBarcodeFragment;
import com.agnet.leteApp.fragments.main.sales.OutletPhoneNumberFragment;
import com.agnet.leteApp.fragments.main.sales.ProductsFragment;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Outlet;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class QrcodeBtmSheet extends BottomSheetDialogFragment {
  
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.qr_code_btm_sheet,
                                  container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        SharedPreferences.Editor _editor = preferences.edit();

        DatabaseHandler dbHandler = new DatabaseHandler(getContext());
  
        Button qrcodeBtn = v.findViewById(R.id.with_qrcode_btn);
        Button phoneBtn = v.findViewById(R.id.with_phone_btn);
        TextView cancelBtn = v.findViewById(R.id.cancel_btn);
        LinearLayout cancelPhoneLayoutBtn = v.findViewById(R.id.cancel_phone_layout_btn);
        Button regByPhoneBtn = v.findViewById(R.id.register_byPhone_btn);

        EditText phoneTxt = v.findViewById(R.id.phone_input);
        EditText nameTxt = v.findViewById(R.id.name_input);
  
        qrcodeBtn.setOnClickListener(v12 -> {
            new FragmentHelper(getActivity()).replaceWithbackStack(new OrderBarcodeFragment(),"OrderBarcodeFragment", R.id.fragment_placeholder);
          //  dismiss();
        });
  
        phoneBtn.setOnClickListener(v1 -> {
            new FragmentHelper(getActivity()).replaceWithbackStack(new OutletPhoneNumberFragment(),"OutletPhoneNumberFragment", R.id.fragment_placeholder);
//                dismiss();
        });

        regByPhoneBtn.setOnClickListener(view -> {

            String phone = phoneTxt.getText().toString();
            String name = nameTxt.getText().toString();

            if(name.isEmpty()){
                Toast.makeText(getContext(), "Ingiza jina la mteja!", Toast.LENGTH_LONG).show();
            }else if(phone.isEmpty()){
                Toast.makeText(getContext(), "Ingiza namba ya simu!", Toast.LENGTH_LONG).show();
            }else {

                dbHandler.createOutlet(new Outlet(
                        0,name,phone,
                        "",""
                ));

                dismiss();
                new FragmentHelper(getActivity()).replaceWithbackStack(new ProductsFragment(), "ProductsFragment", R.id.fragment_placeholder);

            }

        });

        cancelPhoneLayoutBtn.setOnClickListener(view -> new FragmentHelper(getActivity()).replaceWithbackStack(new HomeFragment(),"HomeFragment", R.id.fragment_placeholder));
        cancelBtn.setOnClickListener(view -> new FragmentHelper(getActivity()).replaceWithbackStack(new HomeFragment(),"HomeFragment", R.id.fragment_placeholder));

        return v;
    }
}