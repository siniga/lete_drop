package com.agnet.leteApp.fragments.main.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.activities.MainActivity;
import com.agnet.leteApp.fragments.main.sales.CartFragment;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.models.Cart;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by alicephares on 8/5/16.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<Cart> products = Collections.emptyList();
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
    public CartAdapter(Context c, List<Cart> products, CartFragment cartFragment) {
        this.products = products;
        this.inflator = LayoutInflater.from(c);
        this.c = c;
        this.cartFragment = cartFragment;

        _preferences = c.getSharedPreferences("SharedProductsData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        _dbHandler = new DatabaseHandler(c);

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = inflator.inflate(R.layout.card_cart, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Cart currentProduct = products.get(position);

        final DecimalFormat formatter = new DecimalFormat("#,###,###");

        holder.mName.setText(currentProduct.getName());
        holder.mPrice.setText("TZS:" +formatter.format(currentProduct.getAmount()));
        holder.mQnty.setText("" + currentProduct.getQuantity());

        final int[] count = {currentProduct.getQuantity()};


        //calculate total price for the product

        holder.mIncrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mQnty.setText("" + ++count[0]);

                Double total = (currentProduct.getItemPrice() * count[0]);
              //  _dbHandler.updateCart(count[0], total, currentProduct.getServerId());

                int totalQnty = _dbHandler.getTotalQnty();


             //   holder.mPrice.setText("" + formatter.format(total));

                ((CartFragment) cartFragment).setTotalCartAmnt(_dbHandler.getTotalPrice());

            }
        });

        holder.mDecrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count[0] > 1) {
                    holder.mQnty.setText("" + --count[0]);
                   // int total = (Integer.parseInt(currentProduct.getOriginalPrice()) * count[0]);

                  //  _dbHandler.updateCart(count[0], total, currentProduct.getServerId());

                    int totalQnty = _dbHandler.getTotalQnty();

//                    holder.mPrice.setText("" + formatter.format(total));

                    ((CartFragment) cartFragment).setTotalCartAmnt(_dbHandler.getTotalPrice());
                }
            }
        });

        holder.mRemoveCartProductBtn.setColorFilter(Color.parseColor("#df352e"));
        holder.mRemoveCartProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Snackbar snackbar = Snackbar
                        .make(v, "Delete Product?", Snackbar.LENGTH_LONG).setActionTextColor(Color.parseColor("#fbbe02"))
                        .setAction("Delete", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar1 = Snackbar.make(view, "Product is deleted!", Snackbar.LENGTH_SHORT);
                                snackbar1.show();

                            //    _dbHandler.deleteCartById(currentProduct.getId());
                                ((CartFragment) cartFragment).setTotalCartAmnt(_dbHandler.getTotalPrice());
                                removeAt(position);

                                int totalQnty = _dbHandler.getTotalQnty();

                            }
                        });

                snackbar.show();


            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper;
        public RelativeLayout mQntyChangeBtn;
        public TextView mName, mPrice;
        public ImageView mImg, mRemoveCartProductBtn;
        public TextView mQnty, mSku;
        public Button mDecrementBtn, mIncrementBtn;


        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.shop_wrapper);
            mName = view.findViewById(R.id.name);
            mPrice = view.findViewById(R.id.price);
            mImg = view.findViewById(R.id.product_img);
            mQnty = view.findViewById(R.id.quantity);
           // mQntyChangeBtn = view.findViewById(R.id.qnty_change_btn_wrapper);
            mDecrementBtn = view.findViewById(R.id.quantity_view_remove);
            mIncrementBtn = view.findViewById(R.id.quantity_view_add);
            mRemoveCartProductBtn = view.findViewById(R.id.remove_cart_product);
            mSku = view.findViewById(R.id.sku);
        }

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void filterList(List<Cart> filterdProducts) {
        this.products = filterdProducts;
        notifyDataSetChanged();

    }

    public int getImage(String imageName) {

        int drawableResourceId = c.getResources().getIdentifier(imageName, "drawable", c.getPackageName());

        return drawableResourceId;
    }

    public void removeAt(int position) {
        products.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, products.size());
    }


}