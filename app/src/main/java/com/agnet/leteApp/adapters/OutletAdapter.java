package com.agnet.leteApp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.fragments.ProductsFragment;
import com.agnet.leteApp.helpers.FragmentHelper;
import com.agnet.leteApp.models.Customer;

import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by alicephares on 8/5/16.
 */
public class OutletAdapter extends RecyclerView.Adapter<OutletAdapter.ViewHolder> {

    private List<Customer> customers = Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;


    // Provide a suitable constructor (depends on the kind of dataset)
    public OutletAdapter(Context c, List<Customer> customers) {
        this.customers = customers;
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
        View v = inflator.inflate(R.layout.card_shop, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }

    int count = 0;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Customer currentCustomer = customers.get(position);

        holder.mName.setText(currentCustomer.getName());
        // holder.mAddress.setText(currentCustomer.getStreet());


        /*Glide.with(c).load(getImage(currentCustomer.getUrl()))
                .asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.mImage) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(c.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                holder.mImage.setImageDrawable(circularBitmapDrawable);
            }
        });*/

        holder.mWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(c, "Selected shop "+currentCustomer.getName(), Toast.LENGTH_SHORT).show();
                _editor.putString("OUTLET_NAME", currentCustomer.getName());
                _editor.putInt("OUTLET_ID", currentCustomer.getServerId());
                _editor.commit();

                new FragmentHelper(c).replaceWithbackStack(new ProductsFragment(), "Productsfragment", R.id.fragment_placeholder);

            }
        });

    }

    public int getImage(String imageName) {

        int drawableResourceId = c.getResources().getIdentifier(imageName, "drawable", c.getPackageName());

        return drawableResourceId;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mWrapper;
        public TextView mName, mType;
        public ImageView mImage;

        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.shop_wrapper);
            mName = view.findViewById(R.id.shop_name);
//            mType = view.findViewById(R.id.outlet_type);
//            mImage = view.findViewById(R.id.shop_image);

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return customers.size();
    }

    public void filterList(List<Customer> filterCustomers) {
        this.customers = filterCustomers;
        notifyDataSetChanged();

    }

}