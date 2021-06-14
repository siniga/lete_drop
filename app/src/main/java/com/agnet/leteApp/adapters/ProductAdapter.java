package com.agnet.leteApp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agnet.leteApp.R;
import com.agnet.leteApp.activities.MainActivity;
import com.agnet.leteApp.fragments.ProductsFragment;
import com.agnet.leteApp.helpers.DatabaseHandler;
import com.agnet.leteApp.models.Cart;
import com.agnet.leteApp.models.Order;
import com.agnet.leteApp.models.Product;
import com.agnet.leteApp.service.Endpoint;
import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by alicephares on 8/5/16.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> products = Collections.emptyList();
    private LayoutInflater inflator;
    private Context c;
    private int locateId;
    private SharedPreferences _preferences;
    private SharedPreferences.Editor _editor;
    private List productlist = new ArrayList();
    private int cartItemCounts = 0;
    private int index = -1;
    private AlertDialog _alertDialog;
    private ProductsFragment fragment;
    private DatabaseHandler _dbHandler;


    // Provide a suitable constructor (depends on the kind of dataset)
    public ProductAdapter(Context c, List<Product> products, ProductsFragment fragment) {
        this.products = products;
        this.inflator = LayoutInflater.from(c);
        this.c = c;
        this.fragment = fragment;

        _preferences = c.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        _editor = _preferences.edit();

        _dbHandler = new DatabaseHandler(c);

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = inflator.inflate(R.layout.card_product, parent, false);
        // set the view's size, margins, padding and layout parameters

        ViewHolder vh = new ViewHolder(c, v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //get a position of a current saleItem
        final Product currentProduct = products.get(position);
        final int[] count = {1};

        DecimalFormat formatter = new DecimalFormat("#,###,###");
        int formatedPrice = Integer.parseInt(currentProduct.getPrice());

        holder.mName.setText(currentProduct.getName());
        holder.mPrice.setText("TZS: " +formatter.format(formatedPrice));
        holder.mSku.setText(currentProduct.getSku());
        holder.mQnty.setText("" + currentProduct.getQuantity());
//        holder.mQnty.setFocusable(false);


        Endpoint.setStorageUrl(currentProduct.getImgUrl());
        String url = Endpoint.getStorageUrl();

        try {
            Glide.with(c)
                    .load(new URL(url))
                    .into(holder.mImg);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //check if product is active, if not then dont show add button
        if(currentProduct.getActive() == 0){
            holder.mBtnAddToCart.setVisibility(View.GONE);
            holder.mInactiveBtn.setVisibility(View.VISIBLE);
        }

        //get item quantity and display it on page load if it is greater than 0
        int qnty = _dbHandler.getCartItemQnty(currentProduct.getId());

        if(qnty > 0){
            holder.mBtnAddToCart.setVisibility(View.GONE);
            holder.mQntyviewWrapper.setVisibility(View.VISIBLE);
            holder.mQnty.setText(""+qnty);
        }

        //get text as user is typing in edit text
        holder.mQnty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
             // holder.mQnty.setText(s.toString());

                if(!s.toString().isEmpty()){
                    try {
                        count[0] = Integer.parseInt(s.toString());

                        //calculate total price for the product
                        int total = (Integer.parseInt(currentProduct.getPrice()) * count[0]);

                        _dbHandler.updateCart(count[0],total, currentProduct.getId());

                        int totalQnty = _dbHandler.getTotalQnty();
                        ((MainActivity) c).setCartQnty(totalQnty);
                    }
                    catch (NumberFormatException nfe) {

                        nfe.printStackTrace();
                    }


                } else {

                    //  Toast.makeText(c, "djjd", Toast.LENGTH_SHORT).show();
                    //if edit text is empty reset value to 1
                    count[0] = 1;
                    holder.mQnty.setText("" + count[0]);
                }

            }
        });


        //hide add to cart button when clicked
        holder.mBtnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mBtnAddToCart.setVisibility(View.GONE);
                holder.mQntyviewWrapper.setVisibility(View.VISIBLE);

                holder.mQnty.setText("" + count[0]);

                //calculate total price for the product
                int total = (Integer.parseInt(currentProduct.getPrice()) * count[0]);

                List<Order> orders = new ArrayList<>();
                orders.add(new Order(0, "", "", "", 1, "", 0, 0, 0.0,0.0));

                //create order then cart based on order created
                _dbHandler.createOrder(orders);

                int orderId = _dbHandler.getLastId("orders");


                List cart = new ArrayList();
                cart.add(new Cart(0, currentProduct.getId(),total, count[0], currentProduct.getName(), currentProduct.getImgUrl(), currentProduct.getPrice(), currentProduct.getSku(), orderId));

                _dbHandler.createCart(cart);

                int totalQnty = _dbHandler.getTotalQnty();
                ((MainActivity) c).setCartQnty(totalQnty);


            }
        });


        //increment quantity count
        holder.mQntyViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //increment count only if edit text is not empty
                if (!holder.mQnty.getText().toString().isEmpty()) {

                    count[0] = Integer.parseInt(holder.mQnty.getText().toString());
                    count[0]++;

                    holder.mQnty.setText("" + count[0]);

                } else {
                    //if edit text is empty reset value to 1
                    count[0] = 1;
                    holder.mQnty.setText("" + count[0]);
                }

                //calculate total price for the product
                int total = (Integer.parseInt(currentProduct.getPrice()) * count[0]);

                _dbHandler.updateCart(count[0],total, currentProduct.getId());

                int totalQnty = _dbHandler.getTotalQnty();
                ((MainActivity) c).setCartQnty(totalQnty);


            }
        });

        //increment quantity count
        holder.mQntyViewRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //decrement count only if edit text is not empty
                if (!holder.mQnty.getText().toString().isEmpty()) {
                    count[0] = Integer.parseInt(holder.mQnty.getText().toString());

                    //if count is less than one stop decrementing
                    if (count[0] > 1) {

                        count[0]--;
                        holder.mQnty.setText("" + count[0]);
                    }

                    holder.mQnty.setText("" + count[0]);
                } else {

                  //  Toast.makeText(c, "djjd", Toast.LENGTH_SHORT).show();
                    //if edit text is empty reset value to 1
                    count[0] = 1;
                    holder.mQnty.setText("" + count[0]);
                }

                //calculate total price for the product
                int total = (Integer.parseInt(currentProduct.getPrice()) * count[0]);

                _dbHandler.updateCart(count[0],total, currentProduct.getId());

                int totalQnty = _dbHandler.getTotalQnty();
                ((MainActivity) c).setCartQnty(totalQnty);

            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mWrapper, mQntyviewWrapper;
        public TextView mName, mPrice,mSku;
        public EditText mQnty;
        public ImageView mImg;
        public ImageButton mBtnAddToCart;
        public Button mQntyViewRemove, mQntyViewAdd, mInactiveBtn;


        public ViewHolder(Context context, View view) {
            super(view);

            mWrapper = view.findViewById(R.id.shop_wrapper);
            mName = view.findViewById(R.id.name);
            mPrice = view.findViewById(R.id.price);
            mBtnAddToCart = view.findViewById(R.id.add_to_cart_btn);
            mQntyViewAdd = view.findViewById(R.id.quantity_view_add);
            mQntyViewRemove = view.findViewById(R.id.quantity_view_remove);
            mQntyviewWrapper = view.findViewById(R.id.qnty_view_wrapper);
            mQnty = view.findViewById(R.id.quantity);
            mImg = view.findViewById(R.id.product_img);
            mSku = view.findViewById(R.id.sku);
            mInactiveBtn = view.findViewById(R.id.inactive_btn);
        }

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void filterList(List<Product> filterdProducts) {
        this.products = filterdProducts;
        notifyDataSetChanged();

    }


    public int getImage(String imageName) {

        int drawableResourceId = c.getResources().getIdentifier(imageName, "drawable", c.getPackageName());

        return drawableResourceId;
    }

}