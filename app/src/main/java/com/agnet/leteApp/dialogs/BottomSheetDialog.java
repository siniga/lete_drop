package com.agnet.leteApp.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.helpers.AppManager;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.helpers.DateHelper;
import com.agnet.leteApp.models.Order;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pusher.client.Pusher;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private DatabaseHandler _dbHandler;
    private DecimalFormat _formatter;
    private ProgressBar _progressBar;
    private String _timeInterval;
    private Dialog _dialog;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private Gson _gson;
    private Fragment fragment;
    private static final String PUSHER_API_KEY = "bfc6f755c4f72773c62f";
    private static final String PUSHER_CLUSTER = "ap2";
    private static final String AUTH_ENDPOINT = "https://pusher.com/channels/authorize";
    private Pusher pusher;

    public void setFragmentContext(Fragment fragment) {

        this.fragment = fragment;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.confirm_order_btm_sheet, null);
        dialog.setContentView(contentView);

        _dialog = dialog;
//        _dialog.setCancelable(false);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        _gson = gsonBuilder.create();

       /* pusher = new Pusher(PUSHER_API_KEY, new PusherOptions()
                .setEncrypted(false)
                .setCluster(PUSHER_CLUSTER)
                .setAuthorizer(new HttpAuthorizer(AUTH_ENDPOINT)));
*/




    /*    PrivateChannelEventListener subscriptionEventListener = new PrivateChannelEventListener() {

            @Override
            public void onEvent(PusherEvent event) {

            }

           *//* @Override
            public void onEvent(String channel, String event, final String data) {
              *//**//*  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });*//**//*
            }
*//*
            @Override
            public void onAuthenticationFailure(String message, Exception e) {
             *//*   Log.d(DEBUG_TAG, "Authentication failed.");
                Log.d(DEBUG_TAG, message);*//*
            }

            @Override
            public void onSubscriptionSucceeded(String message) {
               *//* Log.d(DEBUG_TAG, "Subscription Successful");
                Log.d(DEBUG_TAG, message);*//*
            }
        };*/

     /*   final PrivateChannel editorChannel = pusher.subscribePrivate("private-editor", subscriptionEventListener);
        editorChannel.bind("client-update", subscriptionEventListener);
        editorChannel.trigger("client-update", "kipande");*/
        _preferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        _formatter = new DecimalFormat("#,###,###");
        _dbHandler = new DatabaseHandler(getContext());

        int totalPrice = _dbHandler.getTotalPrice();
        int totalQnty = _dbHandler.getTotalQnty();

        _progressBar = dialog.findViewById(R.id.progressBar_cyclic);

        TextView totalAmnt = dialog.findViewById(R.id.total_amount);
        TextView qnty = dialog.findViewById(R.id.total_qnty);

        int total = (totalPrice + 0);

        totalAmnt.setText("" + (_formatter.format(total)));
        qnty.setText("" + totalQnty);


        createOrder();

        Button cancelDialogBtn = dialog.findViewById(R.id.button_cancel);
        cancelDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button confirmBtn = dialog.findViewById(R.id.button_confirm_order);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _progressBar.setVisibility(View.VISIBLE);

/*
                Intent intent = new Intent(getContext(), AndroidDatabaseManager.class);
                getActivity().startActivity(intent);*/


            }
        });

    }

  /*  @Override
    public void onResume() {
        super.onResume();
        pusher.connect();
    }

    @Override
    public void onPause() {
        pusher.disconnect();
        super.onPause();
    }*/

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
        orders.add(new Order(0, "", deviceDateTime, orderNum, 1, "", 0, 1,mLat,mLong));

        //create order
        _dbHandler.createOrder(orders);
    }



}