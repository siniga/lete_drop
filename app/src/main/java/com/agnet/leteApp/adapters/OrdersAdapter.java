package com.agnet.leteApp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.agnet.leteApp.R;
import com.agnet.leteApp.models.History;

import java.util.Collections;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by alicephares on 8/5/16.
 */
public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private List<History> history = Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;


    // Provide a suitable constructor (depends on the kind of dataset)
    public OrdersAdapter(Context c, List<History> history) {
        this.history = history;
        this.inflator = LayoutInflater.from(c);
        this.c = c;

        _preferences = c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();


    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = inflator.inflate(R.layout.card_order_history, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }

    int count = 0;

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final History currentHistory = history.get(position);

        // holder.mCreatedAt.setText(currentHistory.getCreatedAt());
        holder.mOrderNo.setText("#" + currentHistory.getOrderNo());
        holder.mOrderTime.setText(currentHistory.getOrderTime());


        /*OrderStatusHelper statusHelper = new OrderStatusHelper(c);
        statusHelper.showButtonByStatus(holder.mStatus, currentHistory.getStatus());*/
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper, mBtnWrapper;
        public TextView  mOrderNo, mOrderTime, mStatus;
        public RecyclerView mProducts;
        public ImageView mIcon, mRemoveCartProductBtn;
        public Button mBntTrackOrder, mBtnCall, mStatusBtn;


        public ViewHolder(Context context, View view) {
            super(view);

        //    mWrapper = view.findViewById(R.id.shop_wrapper);
            // mCreatedAt = view.findViewById(R.id.created_at);
            mOrderTime = view.findViewById(R.id.order_time);
            //mIcon = view.findViewById(R.id.history_icon);
            mOrderNo = view.findViewById(R.id.order_no);
//            mBtnWrapper = view.findViewById(R.id.order_tracking_btn_wrapper);
//            mStatus = view.findViewById(R.id.status);
        /*    mBntTrackOrder = view.findViewById(R.id.track_order_btn);
            mBtnCall = view.findViewById(R.id.call_btn);
            mStatusBtn = view.findViewById(R.id.status_btn);*/

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return history.size();
    }


}