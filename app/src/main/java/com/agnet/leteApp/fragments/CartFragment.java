package com.agnet.leteApp.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
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

import com.agnet.leteApp.R;
import com.agnet.leteApp.activities.MainActivity;
import com.agnet.leteApp.adapters.CartAdapter;
import com.agnet.leteApp.helpers.CustomDivider;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Cart;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
        _navigation = _c.findViewById(R.id.bottom_navigation);
        _btnHome = _c.findViewById(R.id.home_btn);
        _openCartBtm = _c.findViewById(R.id.open_cart_wrapper);
        _errorMsg = view.findViewById(R.id.error_msg);
        _cartlist = view.findViewById(R.id.cart_list);
        _progressBar = view.findViewById(R.id.progressBar_cyclic);

        //methods
        _cartlist.setHasFixedSize(true);
        _navigation.setVisibility(View.GONE);
        _btnHome.setVisibility(View.GONE);
        _openCartBtm.setVisibility(View.VISIBLE);


        _layoutManager = new LinearLayoutManager(_c, RecyclerView.VERTICAL, false);
        _cartlist.setLayoutManager(_layoutManager);

        _cartlist.addItemDecoration(new CustomDivider(_c, LinearLayoutManager.VERTICAL, 16));

        _products = _dbHandler.getCart();

        CartAdapter adapter = new CartAdapter(_c, _products, this);
        _cartlist.setAdapter(adapter);

        _placeOrderBtn = view.findViewById(R.id.place_order_btn);
        _placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //submit order to the server
                if(!_dbHandler.isTableEmpty("carts")){

                    _progressBar.setVisibility(View.VISIBLE);

                    new FragmentHelper(_c).replace(new QrcodeScannerFragment(),"QrcodeScannerFragment",R.id.fragment_placeholder);



                }else {
                    Toast.makeText(getContext(), "Hakuna bidhaa kwenye kikapu!", Toast.LENGTH_LONG).show();
                }

            }
        });

        setHasOptionsMenu(true);

        return view;

    }

    public void showUserDetailsDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = _c.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        final View dialogView = LayoutInflater.from(_c).inflate(R.layout.dialog_user_details, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(_c);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        builder.setCancelable(false);

        //finally creating the alert dialog and displaying it
        _alertDialog = builder.create();
        _alertDialog.setCancelable(true);
        _alertDialog.show();



        final ProgressBar progressBar = _alertDialog.findViewById(R.id.progressBar_cyclic);

        EditText phoneNum = _alertDialog.findViewById(R.id.phone_number);


        Button confirmPhoneBtn  = _alertDialog.findViewById(R.id.button_confirm_phone);
     /*   String formattedNumber = PhoneNumberUtils.formatNumber("072348432");
        confirmPhoneBtn.setText(""+formattedNumber);*/

        confirmPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               progressBar.setVisibility(View.VISIBLE);
            }
        });

    }

  /*  @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(false);
    }*/


    public void setTotalCartAmnt(int totalCartAmnt) {
        _cartTotalAmnt.setText("" + _formatter.format(totalCartAmnt));
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView  toolbarTitle = _c.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Kikapu Changu");

        RelativeLayout openCart =_c.findViewById(R.id.open_cart_wrapper);
        openCart.setVisibility(View.GONE);

        //hide bottom navigation
       /* BottomNavigationView bottomNavigationView = _c.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
*/
        //show back button
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        int totalPrice = _dbHandler.getTotalPrice();
        _cartTotalAmnt.setText("" + _formatter.format(totalPrice));

        //if there are no products in the cart
        if (_products.size() == 0) {
            _errorMsg.setVisibility(View.VISIBLE);
        } else {
            _errorMsg.setVisibility(View.GONE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        _progressBar.setVisibility(View.GONE);
    }
}
