package com.agnet.lete_drop.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.activities.MainActivity;
import com.agnet.lete_drop.application.mSingleton;
import com.agnet.lete_drop.helpers.AppManager;
import com.agnet.lete_drop.helpers.DatabaseHandler;
import com.agnet.lete_drop.helpers.DateHelper;
import com.agnet.lete_drop.helpers.FragmentHelper;
import com.agnet.lete_drop.helpers.StatusBarHelper;
import com.agnet.lete_drop.models.History;
import com.agnet.lete_drop.models.Invoice;
import com.agnet.lete_drop.models.InvoiceDetail;
import com.agnet.lete_drop.models.Order;
import com.agnet.lete_drop.models.OrderResponse;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import static com.android.volley.VolleyLog.TAG;


public class ConfirmOrderFragment extends Fragment implements View.OnClickListener {

    private FragmentActivity _c;
    private BottomSheetBehavior _bottomSheetBehavior;
    private DatabaseHandler _dbHandler;
    private SharedPreferences.Editor _editor;
    private ProgressBar _progressBar;
    private Gson _gson;
    private BottomNavigationView _navigation;
    private LinearLayout _btnHome;
    private RelativeLayout _openCartBtm;
    private TextView _signupLink;
    private Button _btnLogin;
    private SharedPreferences _preferences;
    private DecimalFormat _formatter;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_confirmation, container, false);
        _c = getActivity();

        //initialize
        _dbHandler = new DatabaseHandler(_c);
        _gson = new Gson();
        _formatter = new DecimalFormat("#,###,###");
        _progressBar = view.findViewById(R.id.progressBar_cyclic);
        _preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        //binding
        _navigation = _c.findViewById(R.id.bottom_navigation);
        _btnHome = _c.findViewById(R.id.home_btn);
        _openCartBtm = _c.findViewById(R.id.open_cart_wrapper);
        _progressBar = view.findViewById(R.id.progressBar_cyclic);

        TextView totalAmnt = view.findViewById(R.id.total_amount);
        TextView qnty = view.findViewById(R.id.total_qnty);
        Button confirmBtn = view.findViewById(R.id.button_confirm_order);

        //events
        confirmBtn.setOnClickListener(this);

        //methods
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        new StatusBarHelper(_c).setStatusBarColor(R.color.colorPrimaryDark);

        int totalPrice = _dbHandler.getTotalPrice();
        int totalQnty = _dbHandler.getTotalQnty();

        int total = (totalPrice + 0);

        totalAmnt.setText("" + (_formatter.format(total)));
        qnty.setText("" + totalQnty);

        createOrder();

        _navigation.setVisibility(View.GONE);
        _btnHome.setVisibility(View.GONE);
        _openCartBtm.setVisibility(View.GONE);

        _preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();


        return view;

    }


    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();


    }


    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.button_confirm_order:
//               uploadOrder();
           break;
       }
    }


    private void createOrder() {

        //get current date and time
        String currentTime = DateHelper.getCurrentTime();
        String currentDate = DateHelper.getCurrentDate();

        //get time interval
        // String timeInterval = DateHelper.getTimeInterval(interval);

        //order data
        String deviceDateTime = currentDate + " " + currentTime;

        //generate a random order number
        String orderNum = AppManager.generateRandomString(8);


        double mLat = Double.parseDouble(_preferences.getString("mLATITUDE", null));
        double mLong = Double.parseDouble(_preferences.getString("mLONGITUDE", null));


        List<Order> orders = new ArrayList<>();
        orders.add(new Order(0, "", deviceDateTime, orderNum, 1, "", 0, 0,mLat,mLong));

        //create order
        _dbHandler.createOrder(orders);
    }

    /*private void uploadOrder() {

        final String order = _dbHandler.checkoutOrders();
        final String phone = _preferences.getString("CLOSEST_DRIVER_PHONE", null);

        Log.d("HEREITIS", order);

        Endpoint.setUrl("order?driver_phone=" + phone);
        String url = Endpoint.getUrl();

        //   Log.d("HEHRHHEE", order);
        try {
            JsonObjectRequest postRequest = new JsonObjectRequest(url, new JSONObject(order),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //   Log.d("HEHRHHEE", response.toString());
                            OrderResponse res = _gson.fromJson(String.valueOf(response), OrderResponse.class);

                            History order = res.getOrder();

                            if (res.getCode() == 201) {
                                // Log.d("ODADDA", "hehe"+order.getOrderNo() +" id"+order.getId());

                                //update order with an id returned from server
                                _dbHandler.updateOrder(order.getId());

                                //delete order after a successful submit
                                _dbHandler.deleteOrderById();
                                _dbHandler.deleteCartByOrderId();

                                //reset quantity count on cart
                                ((MainActivity) getActivity()).setCartQnty(0);

                                //   Toast.makeText(getContext(), "Oda yako imekamilika!", Toast.LENGTH_LONG).show();
                                Toast.makeText(_c, "Oda Imeshatumwa", Toast.LENGTH_SHORT).show();

                                _editor.putString("NEW_ORDER_NO", order.getOrderNo());
                                _editor.putInt("NEW_ORDER_ID", order.getId());
                                _editor.putInt("NEW_ORDER_STATUS", order.getStatus());
                                _editor.commit();

                                //remove all the fragment traces so that user can start another order afresh from category/home fragment
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                    fm.popBackStack();
                                }

                                new FragmentHelper(getActivity()).replace(new SuccessFragment(), "SuccessFragment", R.id.fragment_placeholder);


                                // Toast.makeText(getContext(), ""+res.getOrderId(), Toast.LENGTH_SHORT).show();

                                //redirect back to home activity
                                *//*Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity( intent);*//*


                            } else {
                                Toast.makeText(getContext(), "Mtandao unasumbu, Jaribu tena!", Toast.LENGTH_LONG).show();
                            }
                            _progressBar.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.networkResponse);
                    NetworkResponse response = error.networkResponse;
                    Toast.makeText(getContext(), "Kuna tatizo, hakikisha mtandao upo sawa alafu jaribu tena!", Toast.LENGTH_LONG).show();
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
*/
}
