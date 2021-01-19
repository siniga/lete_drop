package com.agnet.lete_drop.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
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

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.activities.MainActivity;
import com.agnet.lete_drop.adapters.ConfirmCartAdapter;
import com.agnet.lete_drop.application.mSingleton;
import com.agnet.lete_drop.helpers.AppManager;
import com.agnet.lete_drop.helpers.DatabaseHandler;
import com.agnet.lete_drop.helpers.DateHelper;
import com.agnet.lete_drop.helpers.FragmentHelper;
import com.agnet.lete_drop.models.Cart;
import com.agnet.lete_drop.models.Customer;
import com.agnet.lete_drop.models.History;
import com.agnet.lete_drop.models.Invoice;
import com.agnet.lete_drop.models.InvoiceDetail;
import com.agnet.lete_drop.models.Order;
import com.agnet.lete_drop.models.OrderResponse;
import com.agnet.lete_drop.models.Receipt;
import com.agnet.lete_drop.models.ResponseData;
import com.agnet.lete_drop.models.Vfd;
import com.agnet.lete_drop.service.Endpoint;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture;
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.belvi.mobilevisionbarcodescanner.BarcodeFragment;
import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever;

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
    private TextView _orderNumber, _customerName, _driverName;
    private LinearLayout _barcodeWrapper;
    private Button _confirmOrderBtn;
    private NestedScrollView _receiptWrapper;
    private Customer _customer;
    private String _currentTime, _currentDate;
    private Button _cancelOrderBtn;
    private History _order1;
    private LinearLayout _progressBar;


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
        _driverName = view.findViewById(R.id.driver_name);
        _barcodeWrapper = view.findViewById(R.id.barcode_wrapper);
        _receiptWrapper = view.findViewById(R.id.receipt_wrapper);
        _confirmOrderBtn = view.findViewById(R.id.button_confirm_order);
        _cancelOrderBtn = view.findViewById(R.id.button_cancel);
        _progressBar  = view.findViewById(R.id.progress_bar);

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


        displayRecept();
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    private void displayRecept() {

        if (!_preferences.getString("CUSTOMER_OBJECT", null).equals(null)) {

            String customerJson = _preferences.getString("CUSTOMER_OBJECT", null);
            _customer = _gson.fromJson(customerJson, Customer.class);

//            _orderNumber.setText(_dbHandler.getOrderNumber());
            _orderNumber.setText("171");
            _customerName.setText(_customer.getName());
            _driverName.setText(_dbHandler.getUser().getName());
        }

    }


    private void uploadOrder() {

        final String order = _dbHandler.checkoutOrders();

        _progressBar.setVisibility(View.VISIBLE);

        Endpoint.setUrl("order");
        String url = Endpoint.getUrl();

       // Log.d("HEHRHHEE", order);

        try {
            JsonObjectRequest postRequest = new JsonObjectRequest(url, new JSONObject(order),
                    response -> {
                      //  Log.d("HEHRHHEE", response.toString());
                        OrderResponse res = _gson.fromJson(String.valueOf(response), OrderResponse.class);

                        _order1 = res.getOrder();

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
                                sendVfd();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            new FragmentHelper(getActivity()).replace(new SuccessFragment(), "SuccessFragment", R.id.fragment_placeholder);
                            _progressBar.setVisibility(View.GONE);

                        } else {
                            Toast.makeText(_c, "Mtandao unasumbu, Jaribu tena!", Toast.LENGTH_LONG).show();
                        }

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void sendVfd() throws JSONException {

        ///  Log.d("LOGHERE", "" + _gson.toJson(_dbHandler.getOrder()));

        String url = "http://vfd.aggreyapps.com/maxvfd-api/apis/receive.php";

        List<Cart> cartList = _dbHandler.getCart();
        Order order = _dbHandler.getOrder();

        List<InvoiceDetail> invoiceDetails = new ArrayList<>();

        for (Cart cart : cartList) {
            invoiceDetails.add(new InvoiceDetail(cart.getName(), ""+cart.getQuantity(), "1", "" + cart.getTotalPrice()));

        }


        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(new Invoice(
                _currentDate, _currentTime,
                _preferences.getString("NEW_ORDER_NO", null),
                "6", "NIL",
                _customer.getName(), _customer.getPhone(),
                "RoutePro", invoiceDetails)
        );



        Vfd vfd = new Vfd(invoiceList);
        Gson gson = new GsonBuilder().create();

      //  Log.d("LOGHAPAPOA", gson.toJson(vfd));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(gson.toJson(vfd)),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        Receipt res = _gson.fromJson(String.valueOf(response), Receipt.class);

                        Log.d("LOGHAPAPOA", "" + response);

                     //   Toast.makeText(_c, "haifiki right", Toast.LENGTH_SHORT).show();

                        //delete order after a successful submit
                        _dbHandler.deleteOrderById();
                        _dbHandler.deleteCartByOrderId();
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
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }


        };

        mSingleton.getInstance(_c).addToRequestQueue(jsonObjReq);
    }

}
