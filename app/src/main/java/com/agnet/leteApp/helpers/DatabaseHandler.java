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
import com.agnet.leteApp.models.Outlet;
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
    private static final int DATABASE_VERSION = 27;

    // Database Name
    private static final String DATABASE_NAME = "lete_app";


    // table names
    private static final String TABLE_USER = "users";
    private static final String TABLE_STREET = "streets";
    private static final String TABLE_OUTLET = "outlets";
    private static final String TABLE_ORDER = "orders";
    private static final String TABLE_CART = "carts";

    //user
    private static final String KEY_ID = "id";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_NAME = "name";

    //outlets
    private static final String KEY_IMG_URl = "img_url";
    private static final String KEY_OUTLET_ID = "outlet_id";
    private static final String KEY_QR_CODE = "qr_code";
    private static final String KEY_LOCATION = "location";


    //order
    private static final String KEY_DEVICE_TIME = "device_order_time";
    private static final String KEY_ORDER_NO = "order_no";
    private static final String KEY_STATUS = "status";
    private static final String KEY_PROJECT_ID = "project_id";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    private static final String KEY_CREATED_DATE= "created_date";
    private static final String KEY_USER_ID = "user_id";

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
                + KEY_PHONE + " TEXT,"
                + KEY_IMG_URl + " TEXT " + ")";

        String CREATE_STREET_TABLE = "CREATE TABLE " + TABLE_STREET + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT " + ")";

        String CREATE_OUTLET_TABLE = "CREATE TABLE " + TABLE_OUTLET + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_LOCATION+ " TEXT,"
                + KEY_QR_CODE + " TEXT " + ")";


        String CREATE_ORDER_TABLE = "CREATE TABLE " + TABLE_ORDER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DEVICE_TIME + " TEXT,"
                + KEY_ORDER_NO + " TEXT,"
                + KEY_CREATED_DATE + " TEXT,"
                + KEY_STATUS + " INTEGER,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_LAT + " TEXT,"
                + KEY_LNG + " TEXT,"
                + KEY_OUTLET_ID + " INTEGER,"
                + KEY_PROJECT_ID+ " INTEGER " + ")";

        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_ITEM_PRICE + " TEXT,"
                + KEY_TOTAL_PRICE + " TEXT,"
                + KEY_QUANTITY + " INTEGER,"
                + KEY_ORDER_ID + " INTEGER,"
                + KEY_PRODUCT_ID + " INTEGER " + ")";

        db.execSQL(CREATE_OUTLET_TABLE);
        db.execSQL(CREATE_ORDER_TABLE);
        db.execSQL(CREATE_CART_TABLE);
        db.execSQL(CREATE_USER_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OUTLET);
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
     Begin outlet crude
     ********************************************/
    public void createOutlet(Outlet outlet) {

        //add data to cart the first time addToCart button is clicked

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, outlet.getName());
        values.put(KEY_PHONE, outlet.getPhone());
        values.put(KEY_QR_CODE, outlet.getQrCode());
        db.insert(TABLE_OUTLET, null, values);
        db.close(); // Closing database connection
    }

    public List<Outlet> getOutlets() {
        List<Outlet> outlets = new ArrayList<>();

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_OUTLET, null);

        if (cursor.moveToFirst()) {
            do {
                outlets.add(new Outlet(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_PHONE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getString(cursor.getColumnIndex(KEY_QR_CODE))
                ));
            } while (cursor.moveToNext());
        }
        database.close();
        return outlets;
    }

    public void deleteOutlet(){
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are calling a method to delete our
        // course and we are comparing it with our course name.
        db.delete(TABLE_OUTLET, null,null);
        db.close();
    }



    /*******************************************
    Begin order crude
    ********************************************/
    public void createOrder(Order order) {

        //add data to cart the first time addToCart button is clicked

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DEVICE_TIME,order.getDeviceTime());
        values.put(KEY_ORDER_NO, order.getOrderNo());
        values.put(KEY_CREATED_DATE, order.getCreatedDate());
        values.put(KEY_STATUS, order.getStatus());
        values.put(KEY_USER_ID, order.getUserId());
        values.put(KEY_LAT, order.getLat());
        values.put(KEY_LNG, order.getLng());
        values.put(KEY_PROJECT_ID, order.getProjectId());
        values.put(KEY_OUTLET_ID, order.getOutletId());
        values.put(KEY_LNG, order.getLng());
        db.insert(TABLE_ORDER, null, values);
        db.close(); // Closing database connection

    }

    public List<Order> getOrders() {
        List<Order> order = new ArrayList<>();

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_ORDER, null);

        if (cursor.moveToFirst()) {
            do {
                order.add(new Order(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_DEVICE_TIME)),
                        cursor.getInt(cursor.getColumnIndex(KEY_ORDER_NO)),
                        cursor.getInt(cursor.getColumnIndex(KEY_STATUS)),
                        cursor.getString(cursor.getColumnIndex(KEY_LAT)),
                        cursor.getString(cursor.getColumnIndex(KEY_LNG)),
                        cursor.getString(cursor.getColumnIndex(KEY_CREATED_DATE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_USER_ID)),
                        cursor.getInt(cursor.getColumnIndex(KEY_PROJECT_ID)),
                        cursor.getInt(cursor.getColumnIndex(KEY_OUTLET_ID))


                ));
            } while (cursor.moveToNext());
        }
        database.close();
        return order;
    }

    public void deleteOrder(){
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are calling a method to delete our
        // course and we are comparing it with our course name.
        db.delete(TABLE_ORDER, null,null);
        db.close();
    }


    /*******************************************
     Begin cart crude
     ********************************************/
    public void createCart(Cart cart,int orderId) {

        //add data to cart the first time addToCart button is clicked

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, cart.getName());
        values.put(KEY_ITEM_PRICE, cart.getItemPrice());
        values.put(KEY_TOTAL_PRICE, cart.getAmount());
        values.put(KEY_QUANTITY, cart.getQuantity());
        values.put(KEY_PRODUCT_ID, cart.getProductId());
        values.put(KEY_ORDER_ID, orderId);
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
                        cursor.getDouble(cursor.getColumnIndex(KEY_TOTAL_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_PRODUCT_ID)),
                        cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_ITEM_PRICE))
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

    public void deleteCart(){
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are calling a method to delete our
        // course and we are comparing it with our course name.
        db.delete(TABLE_CART, null,null);
        db.close();
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
