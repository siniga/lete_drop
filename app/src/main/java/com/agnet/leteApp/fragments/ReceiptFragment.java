package com.agnet.leteApp.fragments;

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
import com.agnet.leteApp.activities.MainActivity;
import com.agnet.leteApp.adapters.ConfirmCartAdapter;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.main.HomeFragment;
import com.agnet.leteApp.helpers.AppManager;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.DateHelper;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Cart;
import com.agnet.leteApp.models.Customer;
import com.agnet.leteApp.models.History;
import com.agnet.leteApp.models.Invoice;
import com.agnet.leteApp.models.InvoiceDetail;
import com.agnet.leteApp.models.OrderResponse;
import com.agnet.leteApp.models.Receipt;
import com.agnet.leteApp.models.Sms;
import com.agnet.leteApp.models.Vfd;
import com.agnet.leteApp.service.Endpoint;
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
                uploadOrder();
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

        Log.d("CUSTOMEROBJECT", _gson.toJson(_customer));

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

//        String url = "http://tra.aggreyapps.com/apis/receive.php";// testing
        String url = "http://vfd.aggreyapps.com/maxvfd-api/apis/receive.php";//live

        List<Cart> cartList = _dbHandler.getCart();

        _progressBar.setVisibility(View.VISIBLE);

        List<InvoiceDetail> invoiceDetails = new ArrayList<>();

        for (Cart cart : cartList) {
            //tax code 1 is equal to taxable 18%, 3 is equal to none taxable
            invoiceDetails.add(new InvoiceDetail(cart.getName(), "" + cart.getQuantity(), "3", "" + cart.getTotalPrice()));

        }
//        _preferences.getString("NEW_ORDER_NO", null)
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(new Invoice(
                _currentDate, _currentTime,
                _dbHandler.getOrderNumber(),
                _customer.getVfdCustType(), _customer.getVfdCustId(),
                _customer.getName(), _customer.getPhone(),
                "RoutePro", invoiceDetails)
        );


        Vfd vfd = new Vfd(invoiceList);
        Gson gson = new GsonBuilder().create();

//        Log.d("LOGHAPAPOA", ""+_dbHandler.getOrderNumber());


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(gson.toJson(vfd)),
                response -> {
                    // Log.d(TAG, response.toString());

                    Receipt res = _gson.fromJson(String.valueOf(response), Receipt.class);


                    Log.d("LOGHAPAPOAVFD", "" + _gson.toJson(response));
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
        _customerName.setText(_customer.getName());
//        _driverName.setText(_dbHandler.getUser().getName());
        _verificationCode.setText(receipt.getVerificationCode());
        _progressBar.setVisibility(View.GONE);

    }

    public void sendSMS() throws JSONException {
        String url = "https://messaging-service.co.tz/api/sms/v1/text/single";

        float totalAmnt =  _dbHandler.getTotalPrice();
        float vatTax = 18;

        //deduct tax from total amount
        float taxValue =  (vatTax / 100) * totalAmnt;

        String sms = "Lete\nCUST "+ _customer.getName()
                +" ID "+_customer.getVfdCustId()+"\nRECEIPT#"+_receipt.getVfdInvoiceNum()
                +"\n"+_receipt.getDate()+" "+_receipt.getVerificationUrl()+"\nTOTAL TZS "+
                _dbHandler.getTotalPrice();

//        255758131368

        Sms txtMsg = new Sms("NEXTSMS", _customer.getPhone(), sms);
        String smsAuth = getResources().getString(R.string.sms_auth);

        Log.d("HERSTRING", _gson.toJson(txtMsg));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(_gson.toJson(txtMsg)),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        //Receipt res = _gson.fromJson(String.valueOf(response), Receipt.class);

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


    private void uploadOrder() {

        HashMap<String, String> orderMap = new HashMap<String, String>();
        final String order = _dbHandler.checkoutOrders();

        orderMap.put("order", order);
        orderMap.put("receipt",  _gson.toJson(_receipt));


/*        Log.d("HEEHNHNEE",_gson.toJson(orderMap));*/
        _progressBar.setVisibility(View.VISIBLE);

        Endpoint.setUrl("order");
        String url = Endpoint.getUrl();

      //  try {
            JsonObjectRequest postRequest = new JsonObjectRequest(url, new JSONObject(orderMap),
                    response -> {

                        OrderResponse res = _gson.fromJson(String.valueOf(response), OrderResponse.class);
                        _order1 = res.getOrder();
                           //     Log.d("HEEHNHNEE",response.toString());
                        if (res.getCode() == 201) {

                            //reset quantity count on cart
                            ((MainActivity) getActivity()).setCartQnty(0);


                            _editor.putString("NEW_ORDER_NO", _order1.getOrderNo());
                            _editor.putInt("NEW_ORDER_ID", _order1.getId());
                            _editor.putInt("NEW_ORDER_STATUS", _order1.getStatus());
                            _editor.commit();

                            //remove all the fragment traces so that user can start another order afresh from category/home fragment
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                fm.popBackStack();
                            }

                            try {
                                 sendSMS();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //uploadReceipt();

                            _dbHandler.deleteOrderById();
                            _dbHandler.deleteCartByOrderId();





                            _progressBar.setVisibility(View.GONE);

                        } else {
                            Toast.makeText(_c, "Mtandao unasumbua, Jaribu tena!", Toast.LENGTH_LONG).show();
                        }

                        _progressBar.setVisibility(View.GONE);

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.networkResponse);
                    NetworkResponse response = error.networkResponse;
                    Toast.makeText(_c, "Kuna tatizo, hakikisha mtandao upo sawa alafu jaribu tena!", Toast.LENGTH_LONG).show();
                    AppManager.onErrorResponse(error, getContext());

                    _progressBar.setVisibility(View.GONE);
                    if (response != null && response.data != null) {
                        String errorString = new String(response.data);
                        Log.i("log error", errorString);
                    }
                }
            });

            postRequest.setRetryPolicy(new DefaultRetryPolicy(8000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mSingleton.getInstance(getContext()).addToRequestQueue(postRequest);

      /*  } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

}
