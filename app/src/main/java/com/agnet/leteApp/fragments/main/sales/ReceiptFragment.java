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
    private Receipt _receipt;
    private int _vfdType, _outletTypeId;
    private String _lng, _lat;
    private String _location;
    private int _projectId;
    private User _user;
    private String Token;
    private String _phone, _name, _vfdId, _orderNo;
    private String _outletData;
    private Outlet _outletObj;


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

        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);
            _phone = _preferences.getString("PHONE", null);
            _name = _preferences.getString("NAME", null);
           /* _vfdType = _preferences.getInt("VFD_TYPE", 0);
            _vfdId = _preferences.getString("VFD_ID", null);
            _outletTypeId = _preferences.getInt("OUTLET_TYPE_ID", 0);
            _lng = _preferences.getString("mLONGITUDE", null);
            _lat = _preferences.getString("mLATITUDE", null);
            _projectId = _preferences.getInt("PROJECT_ID", 0);*/
            _orderNo = _preferences.getString("ORDER_NO", null);
            _outletData = _preferences.getString("OUTLET_OBJ", null);
            _outletObj = _gson.fromJson(_outletData, Outlet.class);


        } catch (NullPointerException e) {

        }


        //methods
        _cartlist.setHasFixedSize(true);

        int totalPrice = _dbHandler.getTotalPrice();
        int totalQnty = _dbHandler.getTotalQnty();
        int total = (totalPrice + 0);

        totalAmnt.setText("" + (_formatter.format(total)));
        qnty.setText("" + totalQnty);

        //list
        /*_layoutManager = new LinearLayoutManager(_c, RecyclerView.VERTICAL, false);
        _cartlist.setLayoutManager(_layoutManager);*/

     /*   ConfirmCartAdapter adapter = new ConfirmCartAdapter(_c, _dbHandler.getCart(), new CartFragment());
        _cartlist.setAdapter(adapter);*/

        //events
        _confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendSMS();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new FragmentHelper(_c).replace(new SalesSuccessFragment(), "SalesSuccessFragment", R.id.fragment_placeholder);

            }
        });

        _cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* _dbHandler.deleteOrderById();
                _dbHandler.deleteCartByOrderId();*/

                Toast.makeText(_c, "Order Imekatishwa!", Toast.LENGTH_SHORT).show();
                new FragmentHelper(_c).replace(new ProductsFragment(), "ProductsFragment", R.id.fragment_placeholder);

            }
        });

        /*if (!_preferences.getString("CUSTOMER_OBJECT", null).equals(null)) {

            String customerJson = _preferences.getString("CUSTOMER_OBJECT", null);
            _customer = _gson.fromJson(customerJson, Customer.class);
        }*/


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


        Random rand = new Random();
        int orderNoRandom = rand.nextInt((9999 - 100) + 1) + 10;

        String url = "http://tra.aggreyapps.com/apis/receive.php";// testing
//        String url = "http://vfd.aggreyapps.com/maxvfd-api/apis/receive.php";//live

        _progressBar.setVisibility(View.VISIBLE);

        List<Cart> cartList = _dbHandler.getCart();
        List<InvoiceDetail> invoiceDetails = new ArrayList<>();

        for (Cart cart : cartList) {
            //tax code 1 is equal to taxable 18%, 3 is equal to none taxable
            invoiceDetails.add(new InvoiceDetail(cart.getName(), "" + cart.getQuantity(), "3", "" + cart.getAmount()));

        }
//        _preferences.getString("NEW_ORDER_NO", null)
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(
                new Invoice(
                        _currentDate,
                        _currentTime,
                       ""+ orderNoRandom,
                        6,
                        null,
                        _name,
                        _phone,
                        "RoutePro",
                        invoiceDetails
                )
        );

        Log.d("HERERECEIPT", _gson.toJson(invoiceList));

        Vfd vfd = new Vfd(invoiceList);
        Gson gson = new GsonBuilder().create();

       // Log.d("CUSTOMEROBJECT", _gson.toJson(vfd));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(gson.toJson(vfd)),
                response -> {
                    // Log.d(TAG, response.toString());

                    Receipt res = _gson.fromJson(String.valueOf(response), Receipt.class);

                    Log.d("LOGHAPAPOAVFD", "" + _gson.toJson(res));
                    displayReceipt(res);


                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                _progressBar.setVisibility(View.GONE);
                Toast.makeText(_c, "Kuna tatizo, kama liaendelea wasiliana na IT!", Toast.LENGTH_LONG).show();
            }
        }) {

            //headers
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

    private void displayReceipt(Receipt receipt) {

        _receipt = receipt;
        _orderNumber.setText(receipt.getVfdInvoiceNum());
        _customerName.setText(_outletObj.getName());
        _driverName.setText(_user.getName());

        _progressBar.setVisibility(View.GONE);

    }

    public void sendSMS() throws JSONException {
        String url = "https://messaging-service.co.tz/api/sms/v1/text/single";

        float totalAmnt = _dbHandler.getTotalPrice();
        float vatTax = 18;

        //deduct tax from total amount
        float taxValue = (vatTax / 100) * totalAmnt;

        String sms = "Lete\nCUST " + _name
                + " ID " + _receipt.getVerificationCode() + "\nRECEIPT#" + _receipt.getVfdInvoiceNum()
                + "\n" + _receipt.getDate() + " " + _receipt.getVerificationUrl() + "\nTOTAL TZS " +
                _dbHandler.getTotalPrice();

//        255758131368

        Sms txtMsg = new Sms("NEXTSMS", _phone, sms);
        String smsAuth = getResources().getString(R.string.sms_auth);

        Log.d("HERSTRING", _gson.toJson(txtMsg));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(_gson.toJson(txtMsg)),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        //Receipt res = _gson.fromJson(String.valueOf(response), Receipt.class);
                        _dbHandler.deleteCart();

//                        Log.d("LOGHAPAPOAsms", "" + response);

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


