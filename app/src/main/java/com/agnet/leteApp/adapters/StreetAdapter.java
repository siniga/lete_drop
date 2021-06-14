package com.agnet.leteApp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.main.HomeFragment;
import com.agnet.leteApp.models.Street;

import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by alicephares on 8/5/16.
 */
public class StreetAdapter extends RecyclerView.Adapter<StreetAdapter.ViewHolder> {

    private List<Street> streets = Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private int index = -1;
    private HomeFragment fragment;


    // Provide a suitable constructor (depends on the kind of dataset)
    public StreetAdapter(Context c, List<Street> streets, HomeFragment fragment) {
        this.streets = streets;
        this.inflator = LayoutInflater.from(c);
        this.fragment = fragment;
        this.c = c;

        _preferences = c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();


    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = inflator.inflate(R.layout.card_street, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }

    int count = 0;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Street currentStreet = streets.get(position);

        holder.mName.setText(currentStreet.getName());
//        holder.mAddress.setText(currentShop.getStreet());

        //Log.d("OUTDATA", ""+currentStreet.getId());


        holder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ((HomeFragment) fragment).getCustomers(currentStreet.getId());

               /* if(position == 3){
                    .smoothScrollToPosition(adapter.getItemCount()- 1);
                }*/

                index = position;
                notifyDataSetChanged();

            }
        });

        if (index == -1) {
            index = position;

        }



        if (index == position) {


            Log.d("OUTDATA", "" + currentStreet.getId());
            holder.mName.setBackgroundResource(R.drawable.round_corners_with_stroke_primary);
            holder.mName.setPadding(12,0,12,0);

           // holder.mName.setBackgroundColor(Color.parseColor("#008577"));
            holder.mName.setTextColor(Color.parseColor("#666666"));
        } else {
           // holder.mName.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.mName.setBackgroundResource(R.drawable.round_corners_with_stroke_grey);
            holder.mName.setTextColor(Color.parseColor("#666666"));
            holder.mName.setPadding(12,0,12,0);
        }


    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper;
        public TextView mName, mAddress;
        public ImageView mImage;
        public LinearLayout mBorderHighlight;

        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.street_wrapper);
            mName = view.findViewById(R.id.street_name);
          ///  mBorderHighlight = view.findViewById(R.id.border_bottom);


        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return streets.size();
    }

}