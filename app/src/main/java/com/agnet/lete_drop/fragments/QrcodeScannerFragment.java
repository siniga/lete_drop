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

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.activities.MainActivity;
import com.agnet.lete_drop.adapters.CartAdapter;
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
import com.agnet.lete_drop.models.RoutePath;
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
import com.facebook.shimmer.ShimmerFrameLayout;
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

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import xyz.belvi.mobilevisionbarcodescanner.BarcodeFragment;
import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever;

import static com.android.volley.VolleyLog.TAG;


public class QrcodeScannerFragment extends BarcodeFragment implements BarcodeRetriever {

    private FragmentActivity _c;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private DatabaseHandler _dbHandler;
    private BarcodeCapture barcodeCapture;
    private DecimalFormat _formatter;
    private RecyclerView _cartlist;
    private LinearLayoutManager _layoutManager;
    private Gson _gson;
    private TextView _orderNumber , _customerName,_driverName;
    private LinearLayout _barcodeWrapper;
    private Button _confirmOrderBtn;
    private NestedScrollView _receiptWrapper;
    private   Customer _customer;
    private String _currentTime, _currentDate;


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_scanner, container, false);
        _c = getActivity();

        //initialize
        _formatter = new DecimalFormat("#,###,###");
        _dbHandler = new DatabaseHandler(getContext());
        _preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();
        _gson = new Gson();

        //binding
        barcodeCapture = (BarcodeCapture) getChildFragmentManager().findFragmentById(R.id.barcode);

        //event
        barcodeCapture.setRetrieval(this);



        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onRetrieved(final Barcode barcode) {
        _c.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getCustomer(barcode.displayValue);

            }
        });

        barcodeCapture.stopScanning();
//
    }


    @Override
    public void onRetrievedMultiple(final Barcode closetToClick, final List<BarcodeGraphic> barcodeGraphics) {
        _c.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = "Code selected : " + closetToClick.displayValue + "\n\nother " +
                        "codes in frame include : \n";
                for (int index = 0; index < barcodeGraphics.size(); index++) {
                    Barcode barcode = barcodeGraphics.get(index).getBarcode();
                    message += (index + 1) + ". " + barcode.displayValue + "\n";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(_c)
                        .setTitle("code retrieved")
                        .setMessage(message);
                builder.show();
            }
        });
    }


    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }



    @Override
    public void onRetrievedFailed(String reason) {

    }

    @Override
    public void onPermissionRequestDenied() {

    }

    @Override
    public Camera retrieveCamera() {
        return null;
    }


    public void getCustomer(final String qrcode) {

        Endpoint.setUrl("customer/qrcode");
        String url = Endpoint.getUrl();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {

                    if (!AppManager.isNullOrEmpty(response)) {
                        ResponseData res = _gson.fromJson(response, ResponseData.class);
                        _customer = res.getCustomer();

                       // Log.d("HEERHEHEHE",  response);

                        if(res.getCode() == 200){

                            createOrder(_customer.getId());

                            _editor.putString("CUSTOMER_OBJECT", _gson.toJson(_customer));
                            _editor.commit();

                        }else {
                            new FragmentHelper(_c).replace(new ShopNotFoundFragment(),"ShopNotFoundFragment",R.id.fragment_placeholder);

                            Toast.makeText(_c, "Duka halipo, lisajili kwanza", Toast.LENGTH_LONG).show();
                        }

                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("RegistrationFragment", "here" + error.getMessage());
                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                        }

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("qrcode", qrcode);
                return params;
            }
        };
        mSingleton.getInstance(_c).addToRequestQueue(postRequest);
    }


    private void createOrder(int customerId) {

        //get current date and time
        _currentTime = DateHelper.getCurrentTime();
        _currentDate = DateHelper.getCurrentDate();

        //get time interval
        // String timeInterval = DateHelper.getTimeInterval(interval);

        //order data
        String deviceDateTime = _currentDate + " " + _currentTime;

        //generate a random order number
        String orderNum = AppManager.generateRandomString(8);


        double mLat = Double.parseDouble(_preferences.getString("mLATITUDE", null));
        double mLong = Double.parseDouble(_preferences.getString("mLONGITUDE", null));


        List<Order> orders = new ArrayList<>();
        orders.add(new Order(0, "", deviceDateTime, orderNum, 2, "", _dbHandler.getUser().getServerId(), customerId,mLat,mLong));

        //create order
        _dbHandler.createOrder(orders);
          new FragmentHelper(_c).replace(new ReceiptFragment(),"ReceiptFragment", R.id.fragment_placeholder);

    }





}
