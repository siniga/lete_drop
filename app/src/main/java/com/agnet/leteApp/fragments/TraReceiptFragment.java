package com.agnet.leteApp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.adapters.ConfirmCartAdapter;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.main.HomeFragment;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.DateHelper;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Cart;
import com.agnet.leteApp.models.Customer;
import com.agnet.leteApp.models.History;
import com.agnet.leteApp.models.Invoice;
import com.agnet.leteApp.models.InvoiceDetail;
import com.agnet.leteApp.models.Order;
import com.agnet.leteApp.models.Receipt;
import com.agnet.leteApp.models.Sms;
import com.agnet.leteApp.models.Vfd;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

import static com.android.volley.VolleyLog.TAG;


public class TraReceiptFragment extends Fragment {

    private FragmentActivity _c;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private DatabaseHandler _dbHandler;
    private BarcodeCapture barcodeCapture;
    private DecimalFormat _formatter;
    private RecyclerView _cartlist;
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
    private WebView _webView;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tra_receipt, container, false);
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
        TextView qnty = view.findViewById(R.id.total_qnty);
        _cartlist = view.findViewById(R.id.product_list);
        _orderNumber = view.findViewById(R.id.order_num);
        _customerName = view.findViewById(R.id.customer_name);
        _verificationCode = view.findViewById(R.id.verification_code);
        _driverName = view.findViewById(R.id.driver_name);
        _barcodeWrapper = view.findViewById(R.id.barcode_wrapper);
        _receiptWrapper = view.findViewById(R.id.receipt_wrapper);
        _confirmOrderBtn = view.findViewById(R.id.button_confirm_order);
        _cancelOrderBtn = view.findViewById(R.id.button_cancel);
        _progressBar = view.findViewById(R.id.progress_bar);
        _webView = view.findViewById(R.id.webview);
        _webView.loadUrl("https://virtual.tra.go.tz//efdmsRctVerify//FEDAC0174");

        //methods
        _cartlist.setHasFixedSize(true);

        int totalPrice = _dbHandler.getTotalPrice();
        int totalQnty = _dbHandler.getTotalQnty();
        int total = (totalPrice + 0);

        totalAmnt.setText("" + (_formatter.format(total)));
        qnty.setText("" + totalQnty);

        //list
        _layoutManager = new LinearLayoutManager(_c, RecyclerView.VERTICAL, false);
        _cartlist.setLayoutManager(_layoutManager);

        ConfirmCartAdapter adapter = new ConfirmCartAdapter(_c, _dbHandler.getCart(), new CartFragment());
        _cartlist.setAdapter(adapter);

        //events
        _confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //uploadOrder();
                /*try {
                  //  sendSMS();
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        });

        _cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _dbHandler.deleteOrderById();
                _dbHandler.deleteCartByOrderId();

                Toast.makeText(_c, "Order Imekatishwa!", Toast.LENGTH_SHORT).show();
                new FragmentHelper(_c).replace(new HomeFragment(), "HomeFragment", R.id.fragment_placeholder);

            }
        });

        if (!_preferences.getString("CUSTOMER_OBJECT", null).equals(null)) {

            String customerJson = _preferences.getString("CUSTOMER_OBJECT", null);
            _customer = _gson.fromJson(customerJson, Customer.class);


        }


        try {
            sendVfd();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void sendVfd() throws JSONException {

        String url = "http://vfd.aggreyapps.com/maxvfd-api/apis/receive.php";

        List<Cart> cartList = _dbHandler.getCart();
        Order order = _dbHandler.getOrder();

        List<InvoiceDetail> invoiceDetails = new ArrayList<>();

        for (Cart cart : cartList) {
            invoiceDetails.add(new InvoiceDetail(cart.getName(), "" + cart.getQuantity(), "1", "" + cart.getTotalPrice()));

        }


        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(new Invoice(
                _currentDate, _currentTime,
                _preferences.getString("NEW_ORDER_NO", null),
                "1", "133521593",
                _customer.getName(), _customer.getPhone(),
                "RoutePro", invoiceDetails)
        );


        Vfd vfd = new Vfd(invoiceList);
        Gson gson = new GsonBuilder().create();

     //   Log.d("LOGHAPAPOA", _gson.toJson(cartList));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(gson.toJson(vfd)),
                response -> {
                    // Log.d(TAG, response.toString());

                    Receipt res = _gson.fromJson(String.valueOf(response), Receipt.class);

                    Log.d("LOGHAPAPOA", "" + _gson.toJson(res));
                    displayRecept(res);
                    //   Toast.makeText(_c, "haifiki right", Toast.LENGTH_SHORT).show();

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }


        };

        mSingleton.getInstance(_c).addToRequestQueue(jsonObjReq);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    public void sendSMS() throws JSONException {
        String url = "https://messaging-service.co.tz/api/sms/v1/text/single";

        String sms = "RoutePro\n CUSTNAME Khamis peter CUSTID 123455666\n RECEIPTNO 150\n DATE 2021 - 01 - 15\n VERCODE FEDAC0150\n TOTAL TZS 23999 \n TAX TZS 18999";
        Sms txtMsg = new Sms("NEXTSMS", "255768632087", sms);
        String smsAuth = getResources().getString(R.string.sms_auth);


        Log.d("HERSTRING", _gson.toJson(txtMsg));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(_gson.toJson(txtMsg)),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        //Receipt res = _gson.fromJson(String.valueOf(response), Receipt.class);

                        Log.d("LOGHAPAPOA", "" + response);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {

            /**
             * Passing some request headers
             * */
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

    private void displayRecept(Receipt receipt) {

        _orderNumber.setText(receipt.getVfdInvoiceNum());
        _customerName.setText(_customer.getName());
//        _driverName.setText(_dbHandler/.getUser().getName());
        _verificationCode.setText(receipt.getVerificationCode());

    }



}
