package com.agnet.leteApp.fragments.main.sales;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.main.HomeFragment;
import com.agnet.leteApp.fragments.main.adapters.CartAdapter;
import com.agnet.leteApp.fragments.main.adapters.ReceiptProductsAdapter;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.DateHelper;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Cart;
import com.agnet.leteApp.models.Customer;
import com.agnet.leteApp.models.History;
import com.agnet.leteApp.models.Invoice;
import com.agnet.leteApp.models.InvoiceDetail;
import com.agnet.leteApp.models.Outlet;
import com.agnet.leteApp.models.Receipt;
import com.agnet.leteApp.models.Sms;
import com.agnet.leteApp.models.User;
import com.agnet.leteApp.models.Vfd;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.android.volley.VolleyLog.TAG;


public class ReceiptFragment extends Fragment {

    private FragmentActivity _c;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private DatabaseHandler _dbHandler;
    private BarcodeCapture barcodeCapture;
    private DecimalFormat _formatter;
    private RecyclerView _cartRecyclerview;
    private LinearLayoutManager _layoutManager;
    private Gson _gson;
    private TextView _orderNumber, _customerName, _driverName, _verificationCode;
    private LinearLayout _barcodeWrapper;
    private Button _confirmOrderBtn;
    private NestedScrollView _receiptWrapper;
    private Customer _customer;
    private String _currentTime, _currentDate;
    private Button _cancelOrderBtn;
    private History _order1;
    private LinearLayout _progressBar;
    private User _user;
    private String Token;
    private Receipt _vfdReceipt;
    private List<Outlet> _outlets;
    private TextView _receiptTime;
    private TextView _traUrl;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipt, container, false);
        _c = getActivity();

        //initialize
        _formatter = new DecimalFormat("#,###,###");
        _dbHandler = new DatabaseHandler(getContext());
        _preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
        _gson = new Gson();
        _currentTime = DateHelper.getCurrentTime();
        _currentDate = DateHelper.getCurrentDate();

        //binding
        TextView totalAmnt = view.findViewById(R.id.total_amount);
       // TextView qnty = view.findViewById(R.id.total_qnty);
        _cartRecyclerview = view.findViewById(R.id.product_list);
        _orderNumber = view.findViewById(R.id.order_num);
        _customerName = view.findViewById(R.id.customer_name);
        _verificationCode = view.findViewById(R.id.verification_code);
        _driverName = view.findViewById(R.id.driver_name);
       _receiptTime =  view.findViewById(R.id.time);
       _traUrl = view.findViewById(R.id.tra_url);
        _barcodeWrapper = view.findViewById(R.id.barcode_wrapper);
        _receiptWrapper = view.findViewById(R.id.receipt_wrapper);
        _confirmOrderBtn = view.findViewById(R.id.button_confirm_order);
        _cancelOrderBtn = view.findViewById(R.id.button_cancel);
        _progressBar = view.findViewById(R.id.progress_bar);

        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);
            String receipt = _preferences.getString("VFD_RECEIPT", null);
            _vfdReceipt = _gson.fromJson(receipt, Receipt.class);

            _outlets = _dbHandler.getOutlets();

        } catch (NullPointerException e) {

        }


        //methods

        int totalPrice = _dbHandler.getTotalPrice();
        int totalQnty = _dbHandler.getTotalQnty();
        int total = (totalPrice + 0);

        totalAmnt.setText("" + (_formatter.format(total)));
       // qnty.setText("" + totalQnty);

        //list
        _layoutManager = new LinearLayoutManager(_c, RecyclerView.VERTICAL, false);
        _cartRecyclerview.setLayoutManager(_layoutManager);
        _cartRecyclerview.setHasFixedSize(true);

        ReceiptProductsAdapter adapter = new ReceiptProductsAdapter(_c, _dbHandler.getCart(), new CartFragment());
        _cartRecyclerview.setAdapter(adapter);

        //events
        _confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendSMS();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new FragmentHelper(_c).replace(new HomeFragment(), "HomeFragment", R.id.fragment_placeholder);

            }
        });

        _cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* _dbHandler.deleteOrderById();
                _dbHandler.deleteCartByOrderId();*/

                Toast.makeText(_c, "Order Imekatishwa!", Toast.LENGTH_SHORT).show();
                new FragmentHelper(_c).replace(new HomeFragment(), "HomeFragment", R.id.fragment_placeholder);

            }
        });

        displayReceipt();
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void displayReceipt() {

        _orderNumber.setText(_vfdReceipt.getVfdInvoiceNum());
        _customerName.setText(_outlets.get(0).getName());
        _driverName.setText(_user.getName());
        _verificationCode.setText(_vfdReceipt.getVerificationCode());
        _receiptTime.setText(_vfdReceipt.getTime());
        _traUrl.setText(_vfdReceipt.getVerificationUrl());

        _progressBar.setVisibility(View.GONE);
    }

    public void sendSMS() throws JSONException {
        String url = "https://messaging-service.co.tz/api/sms/v1/text/single";

        float totalAmnt = _dbHandler.getTotalPrice();
        float vatTax = 18;

        //deduct tax from total amount
        float taxValue = (vatTax / 100) * totalAmnt;

        String sms = "Lete\nCUST " +_outlets.get(0).getName()
                + " ID " + _vfdReceipt.getVerificationCode() + "\nRECEIPT#" + _vfdReceipt.getVfdInvoiceNum()
                + "\n" + _vfdReceipt.getDate() + " " + _vfdReceipt.getVerificationUrl() + "\nTOTAL TZS " +
                _dbHandler.getTotalPrice();

//        255758131368

        Sms txtMsg = new Sms("NEXTSMS", _outlets.get(0).getPhone(), sms);
        String smsAuth = getResources().getString(R.string.sms_auth);

        Log.d("HERSTRING", _gson.toJson(txtMsg));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(_gson.toJson(txtMsg)),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        _dbHandler.deleteOutlet();
                        _dbHandler.deleteOrder();
                        _dbHandler.deleteCart();

                        Log.d("LOGHAPAPOAsms", "" + response);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {

            //headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Basic Um91dGVQcm86emV5MTIzMzIxUVE=");
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }


        };

        mSingleton.getInstance(_c).addToRequestQueue(jsonObjReq);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


}


