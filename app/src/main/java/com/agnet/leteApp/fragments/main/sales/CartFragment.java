package com.agnet.leteApp.fragments.main.sales;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.main.adapters.CartAdapter;
import com.agnet.leteApp.helpers.CustomDivider;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.models.Cart;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.List;


public class CartFragment extends Fragment {

    private FragmentActivity _c;
    private RecyclerView _cartlist;
    private LinearLayoutManager _layoutManager;
    private DatabaseHandler _dbHandler;
    private TextView _cartTotalAmnt;
    private DecimalFormat _formatter;
    private List<Cart> _products;
    private LinearLayout _errorMsg;
    private Button _placeOrderBtn;
    private AlertDialog _alertDialog;
    private BottomNavigationView _navigation;
    private LinearLayout _btnHome;
    private RelativeLayout _openCartBtm;
    private ProgressBar _progressBar;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        _c = getActivity();

        //initialize
        _dbHandler = new DatabaseHandler(_c);
        _formatter = new DecimalFormat("#,###,###");

        //binding
        _cartTotalAmnt = view.findViewById(R.id.total_cart_amount);
        _openCartBtm = _c.findViewById(R.id.open_cart_wrapper);
        _errorMsg = view.findViewById(R.id.error_msg);
        _cartlist = view.findViewById(R.id.cart_list);
        _progressBar = view.findViewById(R.id.progressBar_cyclic);

        _layoutManager = new LinearLayoutManager(_c, RecyclerView.VERTICAL, false);
        _cartlist.setLayoutManager(_layoutManager);

        _products = _dbHandler.getCart();

        CartAdapter adapter = new CartAdapter(_c, _products, this);
        _cartlist.setAdapter(adapter);

        return view;

    }

    public void setTotalCartAmnt(int totalCartAmnt) {
        _cartTotalAmnt.setText("" + _formatter.format(totalCartAmnt));
    }

    @Override
    public void onResume() {
        super.onResume();

        int totalPrice = _dbHandler.getTotalPrice();
        _cartTotalAmnt.setText("" + _formatter.format(totalPrice));


    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
