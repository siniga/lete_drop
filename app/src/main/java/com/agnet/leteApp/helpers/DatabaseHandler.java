package com.agnet.leteApp.helpers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.agnet.leteApp.models.Cart;
import com.agnet.leteApp.models.Category;
import com.agnet.leteApp.models.Customer;
import com.agnet.leteApp.models.Order;
import com.agnet.leteApp.models.Street;
import com.agnet.leteApp.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context c;
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 23;

    // Database Name
    private static final String DATABASE_NAME = "lete_app";


    // table names
    private static final String TABLE_USER = "users";
    private static final String TABLE_STREET = "streets";
    private static final String TABLE_CUSTOMER = "customers";
    private static final String TABLE_CATEGORY = "categories";
    private static final String TABLE_ORDER = "orders";
    private static final String TABLE_CART = "carts";

    //user
    private static final String KEY_PHONE = "phone";
    private static final String KEY_NUM_PLATE = "num_plate";
    private static final String KEY_DRIVER_ID = "driver_id";

    //streets table
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SERVER_ID = "server_id";

    //outlets
    private static final String KEY_IMG_URl = "img_url";
    private static final String KEY_PRICE = "price";
    private static final String KEY_STREET_ID = "street_id";
    private static final String KEY_QR_CODE = "qr_code";


    //order
    private static final String KEY_DEVICE_TIME = "device_order_time";
    private static final String KEY_ORDER_NO = "order_no";
    private static final String KEY_STATUS = "status";
    private static final String KEY_PICKUP_TIME = "pickup_time";
    private static final String KEY_OUTLET_ID = "outlet_id";
    private static final String KEY_CUSTOMER_ID = "customer_id";
    private static final String KEY_SENT_STATUS = "sent_status";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    private static final String KEY_PARTNER_ID = "partner_id";
    private static final String KEY_SALER_ID = "saler_id";

    //product
    private static final String KEY_QUANTITY = "qnty";
    private static final String KEY_TOTAL_PRICE = "total_price";
    private static final String KEY_ORDER_ID = "order_id";
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_SKU = "sku";
    private static final String KEY_ITEM_PRICE = "item_price";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        c = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_NUM_PLATE + " INTEGER,"
                + KEY_PHONE + " TEXT,"
                + KEY_IMG_URl + " TEXT,"
                + KEY_SALER_ID + " TEXT,"
                + KEY_SERVER_ID + " server_id" + ")";

        String CREATE_STREET_TABLE = "CREATE TABLE " + TABLE_STREET + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT " + ")";

        String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + TABLE_CUSTOMER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_QR_CODE + " TEXT,"
                + KEY_SERVER_ID + " INTEGER,"
                + KEY_STREET_ID + " INTEGER " + ")";

        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_SERVER_ID + " INTEGER,"
                + KEY_IMG_URl + " TEXT " + ")";

        String CREATE_ORDER_TABLE = "CREATE TABLE " + TABLE_ORDER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DEVICE_TIME + " TEXT,"
                + KEY_ORDER_NO + " TEXT,"
                + KEY_STATUS + " INTEGER,"
                + KEY_PICKUP_TIME + " TEXT,"
                + KEY_OUTLET_ID + " INTEGER,"
                + KEY_SENT_STATUS + " INTEGER,"
                + KEY_SERVER_ID + " INTEGER,"
                + KEY_LAT + " TEXT,"
                + KEY_LNG + " TEXT,"
                + KEY_PARTNER_ID + " INTEGER,"
                + KEY_DRIVER_ID + " INTEGER,"
                + KEY_CUSTOMER_ID + " INTEGER " + ")";

        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_ITEM_PRICE + " TEXT,"
                + KEY_TOTAL_PRICE + " TEXT,"
                + KEY_QUANTITY + " INTEGER,"
                + KEY_PRODUCT_ID + " INTEGER " + ")";

        db.execSQL(CREATE_STREET_TABLE);
        db.execSQL(CREATE_CUSTOMER_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_ORDER_TABLE);
        db.execSQL(CREATE_CART_TABLE);
        db.execSQL(CREATE_USER_TABLE);


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STREET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /*******************************************
     General functions
     ********************************************/

    public int getLastId(String table) {
        int lastId = 0;

        SQLiteDatabase db = this.getWritableDatabase();

        // Select All Query
        String selectQuery = " SELECT " + KEY_ID + " FROM " + table + " ORDER BY " + KEY_ID + " DESC limit 1";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {

            lastId = cursor.getInt(0);
        }
        db.close();
        return lastId;
    }

    public boolean isColumnAvailable(String table, String WhereClouse, String column){

        boolean available = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + table + " WHERE " + WhereClouse + " =?", new String[]{column});

        if (c.getCount() > 0) {
               Log.d("LOGHERE", "available");
            available = true;
        } else {
             Log.d("LOGHERE", "not available");
            available = false;
        }

        return available;
    }

    /*******************************************
     Begin cart crude
     ********************************************/
    public void createCart(Cart cart) {

        //add data to cart the first time addToCart button is clicked

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, cart.getName());
        values.put(KEY_ITEM_PRICE, cart.getItemPrice());
        values.put(KEY_TOTAL_PRICE, cart.getAmount());
        values.put(KEY_QUANTITY, cart.getQuantity());
        values.put(KEY_PRODUCT_ID, cart.getProductId());
        db.insert(TABLE_CART, null, values);
        db.close(); // Closing database connection
    }

    public void updateCart(Cart cart) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, cart.getName());
        values.put(KEY_TOTAL_PRICE, cart.getAmount());
        values.put(KEY_QUANTITY, cart.getQuantity());
        values.put(KEY_PRODUCT_ID, cart.getProductId());

        db.update(TABLE_CART, values, KEY_PRODUCT_ID + " = " + cart.getProductId(), null);
        db.close();
    }

    public List<Cart> getCart() {
        List<Cart> cart = new ArrayList<>();

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_CART, null);

        if (cursor.moveToFirst()) {
            do {
                cart.add(new Cart(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                        cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_PRODUCT_ID)),
                        cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(KEY_ITEM_PRICE))
                ));
            } while (cursor.moveToNext());
        }
        database.close();
        return cart;
    }

    public int getCartItemQnty(int productId) {
        int quantity = 0;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_CART + " WHERE " + KEY_PRODUCT_ID + " =?", new String[]{String.valueOf(productId)});

        if (cursor.moveToFirst()) {
            do {
                quantity = cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY));

            } while (cursor.moveToNext());
        }

        database.close();
        return quantity;
    }

    public int getCartItemPrice(int productId) {
        int price = 0;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_CART + " WHERE " + KEY_SERVER_ID + " =?", new String[]{String.valueOf(productId)});

        if (cursor.moveToFirst()) {
            do {
                price = cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_PRICE));

            } while (cursor.moveToNext());
        }
        database.close();

        return price;
    }

    public int getTotalPrice() {

        int total = 0;

        String selectQuery = "SELECT SUM( total_price ) as total FROM " + TABLE_CART;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                total = cursor.getInt(cursor.getColumnIndex("total"));

            } while (cursor.moveToNext());
        }

        database.close();
        return total;
    }

    //get total quantity of all items in the cart table
    public int getTotalQnty() {

        int total = 0;

        String selectQuery = "SELECT SUM( qnty ) as total FROM " + TABLE_CART;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                total = cursor.getInt(cursor.getColumnIndex("total"));

            } while (cursor.moveToNext());
        }

        database.close();
        return total;
    }





    /*******************************************
     view database on the app
     ********************************************/

    //this method is requred for viewing database on app
    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"message"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }
}
