package com.agnet.leteApp.fragments.main.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.main.sales.CartFragment;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.models.Cart;
import com.agnet.leteApp.models.Outlet;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by alicephares on 8/5/16.
 */
public class OutletAdapter extends RecyclerView.Adapter<OutletAdapter.ViewHolder> {

    private List<Outlet> outlets = Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private int locateId;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private List productlist = new ArrayList();
    private int cartItemCounts = 0;
    private int index = -1;
    private DatabaseHandler _dbHandler;
    private CartFragment cartFragment;
    private static int SPLASH_TIME_OUT = 5000;



    // Provide a suitable constructor (depends on the kind of dataset)
    public OutletAdapter(Context c, List<Outlet> outlets) {
        this.outlets = outlets;
        this.inflator = LayoutInflater.from(c);
        this.c = c;

        _preferences = c.getSharedPreferences("SharedProductsData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        _dbHandler = new DatabaseHandler(c);

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = inflator.inflate(R.layout.card_outlet, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Outlet currentOutlet = outlets.get(position);

        holder.mName.setText(currentOutlet.getName());

        //Get address base on location
        try{
            Geocoder geo = new Geocoder(c.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(currentOutlet.getLat(),currentOutlet.getLng(), 1);
            if (addresses.isEmpty()) {
                holder.mLocation.setText("Waiting for Location");
            }
            else {
                if (addresses.size() > 0) {
                    holder.mLocation.setText(""+addresses.get(0).getSubAdminArea());
                 /*   Log.d("LOCALITYIAZION",addresses.get(0).getFeatureName() + " "+addresses.get(0).getLocality() +""+addresses.get(0).getAdminArea()
                             +""+addresses.get(0).getCountryName());*/
                    Log.d("LOCALITYIAZION",""+addresses.get(0).getSubThoroughfare()+""+addresses.get(0).getFeatureName());

                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper;
        public TextView mName;
        public TextView mLocation;


        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.wrapper);
            mName = view.findViewById(R.id.name);
            mLocation = view.findViewById(R.id.location);
        }

    }

    @Override
    public int getItemCount() {
        return outlets.size();
    }

}