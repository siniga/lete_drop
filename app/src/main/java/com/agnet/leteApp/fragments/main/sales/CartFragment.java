package com.agnet.leteApp.fragments.main.sales;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.application.mSingleton;
import com.agnet.leteApp.fragments.main.adapters.CartAdapter;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.DateHelper;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Cart;
import com.agnet.leteApp.models.Invoice;
import com.agnet.leteApp.models.InvoiceDetail;
import com.agnet.leteApp.models.Order;
import com.agnet.leteApp.models.Outlet;
import com.agnet.leteApp.models.Receipt;
import com.agnet.leteApp.models.ResponseData;
import com.agnet.leteApp.models.User;
import com.agnet.leteApp.models.Vfd;
import com.agnet.leteApp.service.Endpoint;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private SharedPreferences.Editor _editor;
    private SharedPreferences _preferences;
    private String Token;
    private User _user;
    private Gson _gson;
    private ProgressBar _progressBar;
    private LinearLayout _transparentLoader;
    private Button _placeOrderNoQrCodeBtn;
    private List<Order> _orders;
    private List<Outlet> _outlet;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        _c = getActivity();

        //initialize
        _preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
        _progressBar = view.findViewById(R.id.progress_bar);
        _transparentLoader = view.findViewById(R.id.transparent_loader);
        _dbHandler = new DatabaseHandler(_c);
        _formatter = new DecimalFormat("#,###,##0.00");
        _gson = new Gson();

        //binding
        _cartTotalAmnt = view.findViewById(R.id.total_cart_amount);
        _openCartBtm = _c.findViewById(R.id.open_cart_wrapper);
        _errorMsg = view.findViewById(R.id.error_msg);
        _cartlist = view.findViewById(R.id.cart_list);
        _placeOrderBtn = view.findViewById(R.id.place_order_btn);
        _placeOrderNoQrCodeBtn = view.findViewById(R.id.place_order_no_qrcode);

        _layoutManager = new LinearLayoutManager(_c, RecyclerView.VERTICAL, false);
        _cartlist.setLayoutManager(_layoutManager);

        _products = _dbHandler.getCart();

        try {
            _user = _gson.fromJson(_preferences.getString("User", null), User.class);
            Token = _preferences.getString("TOKEN", null);

            _outlet = _dbHandler.getOutlets();
            _orders =_dbHandler.getOrders();

        } catch (NullPointerException e) {

        }

        CartAdapter adapter = new CartAdapter(_c, _products, this);
        _cartlist.setAdapter(adapter);

        _placeOrderBtn.setOnClickListener(view12 -> {
            if (_products.size() > 0) {
                // _progressBar.setVisibility(View.VISIBLE);
                //  _transparentLoader.setVisibility(View.VISIBLE);
              //  _placeOrderBtn.setClickable(false);
            /*    if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(_c, Manifest.permission.CAMERA)) {
                    new FragmentHelper(_c).replaceWithbackStack(new OrderBarcodeFragment(), "OrderBarcodeFragment", R.id.fragment_placeholder);

                } else {
                    requestWritePermission(_c);
                }*/

                saveOrder();


            } else {
                Toast.makeText(_c, "Kikapu hakina bidhaa!", Toast.LENGTH_SHORT).show();

            }
        });

        _placeOrderNoQrCodeBtn.setOnClickListener(view1 -> {
            if (_products.size() > 0) {
                _placeOrderNoQrCodeBtn.setClickable(false);


                new FragmentHelper(_c).replace(new OutletPhoneNumberFragment(), "OutletNumberFragment", R.id.fragment_placeholder);


            } else {
                Toast.makeText(_c, "Kikapu hakina bidhaa!", Toast.LENGTH_SHORT).show();
            }
        });
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
        _progressBar.setVisibility(View.GONE);
        _transparentLoader.setVisibility(View.GONE);
    }

    private static void requestWritePermission(final Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(context).setMessage("This app needs permission to use The phone Camera in order to activate the Scanner")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 1);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    public void saveOrder() {
        _transparentLoader.setVisibility(View.VISIBLE);
        _progressBar.setVisibility(View.VISIBLE);

        Endpoint.setUrl("order");
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    _transparentLoader.setVisibility(View.GONE);
                    _progressBar.setVisibility(View.GONE);

                    ResponseData res = _gson.fromJson(response, ResponseData.class);
                      Toast.makeText(_c, "App inapakua Subiri...", Toast.LENGTH_SHORT).show();

                    if (res.getCode() == 201) {
                        try {
                            sendVfd();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    _placeOrderBtn.setClickable(true);
                },
                error -> {
                    error.printStackTrace();


                    _transparentLoader.setVisibility(View.GONE);
                    _progressBar.setVisibility(View.GONE);
                    _placeOrderBtn.setClickable(true);
                    NetworkResponse response = error.networkResponse;
                    String errorMsg = "";
                    if (response != null && response.data != null) {
                        String errorString = new String(response.data);
                        Log.i("log error", errorString);
                        //TODO: display errors based on the message from the server
                        Toast.makeText(_c, "Kuna tatizo, angalia mtandao alafu jaribu tena", Toast.LENGTH_SHORT).show();
                    }

                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + "" + Token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("createdDate", DateHelper.getCurrentDate());
                params.put("deviceTime", DateHelper.getCurrentDate() + " " + DateHelper.getCurrentTime());
                params.put("userId", "" + _orders.get(0).getUserId());
                params.put("orderNo", "" + _orders.get(0).getOrderNo());
                params.put("lat",""+_orders.get(0).getLat());
                params.put("lng", ""+_orders.get(0).getLng());
                params.put("products", _gson.toJson(_dbHandler.getCart()));
                params.put("outletId", "1");
                params.put("projectId", "" + _orders.get(0).getProjectId());
                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);

        postRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }

    public void sendVfd() throws JSONException {

        String url = "http://tra.aggreyapps.com/apis/receive.php";// testing
//        String url = "http://vfd.aggreyapps.com/maxvfd-api/apis/receive.php";//live

        List<InvoiceDetail> invoiceDetails = new ArrayList<>();

        for (Cart cart : _products) {
            //tax code 1 is equal to taxable 18%, 3 is equal to none taxable
            invoiceDetails.add(new InvoiceDetail(cart.getName(), "" + cart.getQuantity(), "3", "" + cart.getAmount()));

        }
//        _preferences.getString("NEW_ORDER_NO", null)
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(
                new Invoice(
                        DateHelper.getCurrentDate(),
                        DateHelper.getCurrentTime(),
                        ""+ _orders.get(0).getOrderNo(),
                        6,
                        null,
                        _outlet.get(0).getName(),
                        _outlet.get(0).getPhone(),
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

                    _editor.putString("VFD_RECEIPT",_gson.toJson(res));
                    _editor.putString("OUTLET_NAME", _outlet.get(0).getName());
                    _editor.commit();

                    Log.d("LOGHAPAPOAVFD", "" + _gson.toJson(res));

                    new FragmentHelper(_c).replace(new ReceiptFragment(),"ReceiptFragment",R.id.fragment_placeholder);


                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                _progressBar.setVisibility(View.GONE);
                Toast.makeText(_c, "Kuna tatizo, kama linaendelea wasiliana na IT!", Toast.LENGTH_LONG).show();
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



}
