package com.agnet.lete_drop.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agnet.lete_drop.R;
import com.agnet.lete_drop.models.Customer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by alicephares on 8/5/16.
 */
public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    private List<Customer> customers = Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;




    // Provide a suitable constructor (depends on the kind of dataset)
    public CustomerAdapter(Context c, List<Customer> customers) {
        this.customers = customers;
        this.inflator = LayoutInflater.from(c);
        this.c = c;

        _preferences = c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

    }

    @Override
    public CustomerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View v = inflator.inflate(R.layout.card_customer, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Customer currentCustomer = customers.get(position);

         holder.mName.setText(currentCustomer.getName());
         holder.mPhone.setText(currentCustomer.getPhone());
         holder.mCustType.setText(currentCustomer.getType());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper;
        public TextView mName, mPhone, mCustType;




        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.category_wrapper);
            mName = view.findViewById(R.id.name);
            mPhone = view.findViewById(R.id.phone);
            mCustType = view.findViewById(R.id.customer_type);

        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return customers.size();
    }

}