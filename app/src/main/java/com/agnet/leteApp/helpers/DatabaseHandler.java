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
    private static final int DATABASE_VERSION = 20;

    // Database Name
    private static final String DATABASE_NAME = "lete_drop";


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
    private static final String KEY_SKU_ID = "sku_id";
    private static final String KEY_QUANTITY = "qnty";
    private static final String KEY_TOTAL_PRICE = "total_price";
    private static final String KEY_ORDER_ID = "order_id";
    private static final String KEY_ORIGINAL_PRICE = "original_price";
    private static final String KEY_SKU = "sku";

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
                + KEY_SERVER_ID + " INTEGER,"//product server id
                + KEY_ORIGINAL_PRICE + " TEXT,"
                + KEY_TOTAL_PRICE + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_SKU_ID + " INTEGER,"
                + KEY_SKU + " TEXT,"
                + KEY_IMG_URl + " TEXT,"
                + KEY_ORDER_ID + " INTEGER,"
                + KEY_QUANTITY + " INTEGER " + ")";

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

    public boolean isTableEmpty(String table) {

        boolean isEmpty = false;

        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT * FROM " + table;
        Cursor cursor = db.rawQuery(count, null);

        if (cursor.getCount() > 0) {
//            Toast.makeText(c, "" + isEmpty, Toast.LENGTH_SHORT).show();
            isEmpty = false;
        } else {
            isEmpty = true;
        }

        /* Toast.makeText(c, ""+isEmpty, Toast.LENGTH_SHORT).show();*/

        return isEmpty;
    }

    public boolean stringColumnExist(String column, String table, String WhereClouse) {

        boolean exist;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + table + " WHERE " + WhereClouse + " =?", new String[]{column});


        if (c.getCount() > 0) {
            //   Log.d("LOGHERE", "exist");
            exist = true;
        } else {
            // Log.d("LOGHERE", "doesnt exist");
            exist = false;
        }
        return exist;

    }

    public boolean intColumnExist(int column, String table, String WhereClouse) {

        boolean exist;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + table + " WHERE " + WhereClouse + " =?", new String[]{String.valueOf(column)});

        if (c.getCount() > 0) {
            Log.d("LOGHERE", "exist");
            exist = true;
        } else {
            Log.d("LOGHERE", "doesnt exist");
            exist = false;
        }
        return exist;

    }


    /*******************************************
     Begin user crude
     ********************************************/
    public void createUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_PHONE, user.getPhone());
        values.put(KEY_NAME, user.getName());
        /*values.put(KEY_IMG_URl, user.getImg());
        values.put(KEY_SERVER_ID, user.getServerId());
        values.put(KEY_SALER_ID, user.getSalerId());*/

        //if user table is empty, create user
        //otherwise update user number to indicate that number has been changed
        if (isTableEmpty(TABLE_USER)) {
            db.insert(TABLE_USER, null, values);
        } else {
            db.update(TABLE_USER, values, null, null);
        }


        db.close(); // Closing database connection
    }

  /*  public User getUser() {

        User user = null;

        String selectQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                user = new User(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_PHONE)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                        "",
                        cursor.getInt(cursor.getColumnIndex(KEY_NUM_PLATE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_SERVER_ID)),
                        cursor.getInt(cursor.getColumnIndex(KEY_SALER_ID))
                );

            } while (cursor.moveToNext());
        }

        database.close();

        return user;
    }*/


    public String getUserPhone() {
        String userPhone = "";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT phone FROM " + TABLE_USER, null);

        if (cursor.moveToFirst()) {
            do {
                userPhone = cursor.getString(cursor.getColumnIndex(KEY_PHONE));

            } while (cursor.moveToNext());
        }

        database.close();


        return userPhone;
    }

    public String getUserName() {
        String userName = "";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + KEY_NAME + " FROM " + TABLE_USER, null);

        if (cursor.moveToFirst()) {
            do {
                userName = cursor.getString(cursor.getColumnIndex(KEY_NAME));

            } while (cursor.moveToNext());
        }

        database.close();


        return userName;
    }

    public void deleteMuser() {

        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_USER);
        database.close();
    }


    /*******************************************
     Begin street crude
     ********************************************/

    public void addStreets(List<Street> streets) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        for (Street street : streets) {
            if (!stringColumnExist(street.getName(), "streets", "name")) {
                values.put(KEY_NAME, street.getName());
                db.insert(TABLE_STREET, null, values);
            }
        }

        db.close(); // Closing database connection
    }


    public List<Street> getStreets() {
        List<Street> streets = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_STREET;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // Log.d("CURRENTPRODUCT", "ksdjdjd");
                streets.add(new Street(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME))));

            } while (cursor.moveToNext());
        }

        database.close();

        return streets;
    }

    /*******************************************
     Begin outlets crude
     ********************************************/

    public void addCustomer(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, customer.getName());
        values.put(KEY_PHONE, customer.getPhone());
        values.put(KEY_QR_CODE, customer.getQrCode());


        db.insert(TABLE_CUSTOMER, null, values);
        db.close(); // Closing database connection
    }

    public void addOutlets(List<Customer> customers) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (Customer customer : customers) {
            if (!stringColumnExist(customer.getName(), "customers", KEY_NAME)) {
                values.put(KEY_NAME, customer.getName());
                values.put(KEY_IMG_URl, customer.getUrl());
                values.put(KEY_SERVER_ID, customer.getId());
                values.put(KEY_STREET_ID, customer.getStreetId());

                db.insert(TABLE_CUSTOMER, null, values);
            }
        }

        db.close(); // Closing database connection
    }

    public List<Customer> getOutletsByStreetId(int id) {
        List<Customer> customers = new ArrayList<>();

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_CUSTOMER + " WHERE " + KEY_STREET_ID + " =?", new String[]{String.valueOf(id)});

     /*   if (cursor.moveToFirst()) {
            do {
                customers.add(new Customer(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                        cursor.getInt(cursor.getColumnIndex(KEY_STREET_ID)),
                        "",
                        cursor.getString(cursor.getColumnIndex(KEY_IMG_URl)),
                        cursor.getString(cursor.getColumnIndex(KEY_LAT)),
                        cursor.getString(cursor.getColumnIndex("")),
                        cursor.getInt(cursor.getColumnIndex(KEY_SERVER_ID))));

            } while (cursor.moveToNext());
        }*/

        database.close();

        return customers;
    }

    /*******************************************
     Begin category crude
     ********************************************/
    public void addCategories(List<Category> categories) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        for (Category category : categories) {
            if (!stringColumnExist(category.getName(), "categories", KEY_NAME)) {
                values.put(KEY_NAME, category.getName());
                values.put(KEY_IMG_URl, category.getImgUrl());
                values.put(KEY_SERVER_ID, category.getId());

                db.insert(TABLE_CATEGORY, null, values);
            }
        }

        db.close(); // Closing database connection
    }

    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_CATEGORY, null);

        if (cursor.moveToFirst()) {
            do {
                categories.add(new Category(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)), cursor.getString(cursor.getColumnIndex(KEY_IMG_URl)), cursor.getInt(cursor.getColumnIndex(KEY_SERVER_ID))));

            } while (cursor.moveToNext());
        }

        database.close();
        return categories;
    }


    /*******************************************
     Begin products crude
     ********************************************/


    /*******************************************
     Begin cart crude
     ********************************************/
    public void createCart(List<Cart> carts) {

        //add data to cart the first time addToCart button is clicked

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        if (!intColumnExist(carts.get(0).getServerId(), "carts", KEY_SERVER_ID)) {//product id from server

            values.put(KEY_QUANTITY, carts.get(0).getQuantity());
            values.put(KEY_SERVER_ID, carts.get(0).getServerId());//product server id
            values.put(KEY_ORIGINAL_PRICE, carts.get(0).getOriginalPrice());
            values.put(KEY_TOTAL_PRICE, carts.get(0).getTotalPrice());
            values.put(KEY_NAME, carts.get(0).getName());
            values.put(KEY_IMG_URl, carts.get(0).getImgUrl());
            values.put(KEY_SKU, carts.get(0).getSku());
            values.put(KEY_ORDER_ID, 1);
            values.put(KEY_SKU_ID, carts.get(0).getSku());

            db.insert(TABLE_CART, null, values);
            db.close(); // Closing database connection
        }
    }

    public void updateCart(int qnty, int price, int serverId) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QUANTITY, qnty);
        values.put(KEY_TOTAL_PRICE, price);

        db.update(TABLE_CART, values, KEY_SERVER_ID + " = " + serverId, null);
        db.close();
    }

    public int getCartItemQnty(int productId) {
        int quantity = 0;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_CART + " WHERE " + KEY_SERVER_ID + " =?", new String[]{String.valueOf(productId)});

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


    public List<Cart> getCart() {
        List<Cart> cart = new ArrayList<>();

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_CART, null);

        if (cursor.moveToFirst()) {
            do {
                cart.add(new Cart(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getInt(cursor.getColumnIndex(KEY_SERVER_ID)),//server product id
                        cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMG_URl)),
                        cursor.getString(cursor.getColumnIndex(KEY_ORIGINAL_PRICE)),
                        cursor.getString(cursor.getColumnIndex(KEY_SKU)),
                        cursor.getInt(cursor.getColumnIndex(KEY_ORDER_ID))));

            } while (cursor.moveToNext());
        }
        database.close();
        return cart;
    }

    public List<Cart> getOrderCart() {
        List<Cart> cart = new ArrayList<>();

        String QUERY = "SELECT * FROM " + TABLE_ORDER + "  INNER JOIN " + TABLE_CART + " cart ON orders.id = carts.order_id";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(QUERY, null);

        /*if (cursor.moveToFirst()) {
            do {
                cart.add(new Cart(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getInt(cursor.getColumnIndex(KEY_SERVER_ID)),//server product id
                        cursor.getString(cursor.getColumnIndex(KEY_TOTAL_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMG_URl)),
                        cursor.getString(cursor.getColumnIndex(KEY_ORIGINAL_PRICE)),
                        cursor.getString(cursor.getColumnIndex(KEY_SKU)),
                        cursor.getString(cursor.getColumnIndex(KEY_DEVICE_TIME)),
                        cursor.getString(cursor.getColumnIndex(KEY_ORDER_NO))
                ))
            } while (cursor.moveToNext());
        }*/
        database.close();
        return cart;
    }

    public void deleteCartById(int cartId) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_CART + " WHERE " + KEY_ID + " = " + cartId);
        database.close();
    }


    /*******************************************
     Begin orders crude
     ********************************************/
    public void createOrder(List<Order> orders) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_CUSTOMER_ID, orders.get(0).getCustomerId());
        values.put(KEY_DEVICE_TIME, orders.get(0).getDeviceTime());
        values.put(KEY_ORDER_NO, orders.get(0).getOrderNo());
        values.put(KEY_STATUS, orders.get(0).getStatus());
        values.put(KEY_PICKUP_TIME, orders.get(0).getPickUpTime());
        values.put(KEY_DRIVER_ID, orders.get(0).getDriverId());
        values.put(KEY_LNG, orders.get(0).getLng());
        values.put(KEY_LAT, orders.get(0).getLat());
        values.put(KEY_SENT_STATUS, 0);
        values.put(KEY_SERVER_ID, 0);

        if (intColumnExist(orders.get(0).getCustomerId(), TABLE_ORDER, KEY_CUSTOMER_ID)) {

            db.update(TABLE_ORDER, values, KEY_CUSTOMER_ID + " = " + orders.get(0).getCustomerId(), null);

        } else {

            db.insert(TABLE_ORDER, null, values);
        }


        db.close(); // Closing database connection
    }

    public void updateOrder(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(KEY_SERVER_ID, id);

        db.update(TABLE_ORDER, values, KEY_CUSTOMER_ID + " = " + 1, null);

        db.close();
    }

    public void addDriverId(int driverId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // Log.d("DRIVERSSS11",""+ driverId);

        values.put(KEY_PARTNER_ID, 1);
        values.put(KEY_DRIVER_ID, driverId);

        db.update(TABLE_ORDER, values, KEY_CUSTOMER_ID + " = " + 1, null);

        db.close();
    }

    public int getDriverId() {
        int driverId = 0;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + KEY_DRIVER_ID + " FROM " + TABLE_ORDER, null);

        if (cursor.moveToFirst()) {
            do {
                driverId = cursor.getInt(cursor.getColumnIndex(KEY_DRIVER_ID));

            } while (cursor.moveToNext());
        }

        database.close();


        return driverId;
    }
    //update sent status of an order


    public String getOrderNumber() {
        String orderNumber = "";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT order_no FROM " + TABLE_ORDER+" ORDER BY "+KEY_ID+" DESC limit 1", null);

        if (cursor.moveToFirst()) {
            do {
                orderNumber = cursor.getString(cursor.getColumnIndex(KEY_ORDER_NO));

            } while (cursor.moveToNext());
        }

        database.close();


        return orderNumber;
    }

    public Order getOrder() {


        String selectQuery = "SELECT  * FROM " + TABLE_ORDER;
        Order order = null;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            order = new Order(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    "",
                    cursor.getString(cursor.getColumnIndex(KEY_DEVICE_TIME)),
                    cursor.getString(cursor.getColumnIndex(KEY_ORDER_NO)),
                    cursor.getInt(cursor.getColumnIndex(KEY_STATUS)),
                    "",
                    0,
                    cursor.getInt(cursor.getColumnIndex(KEY_CUSTOMER_ID)),
                    0,
                    0

            );


        }

        database.close();

        //Use GSON to serialize Array List to JSON
        return order;

    }


    public void updateSentStatus(int orderId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(KEY_SENT_STATUS, 1);
        db.update(TABLE_ORDER, values, KEY_ID + " = " + orderId, null);

        db.close(); // Closing database connection
    }


    public List<HashMap<String, String>> checkoutCart() {
        List<HashMap<String, String>> cartList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CART;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> cartMap = new HashMap<String, String>();

                cartMap.put(KEY_ID, "" + cursor.getInt(cursor.getColumnIndex(KEY_SERVER_ID)));
                cartMap.put(KEY_SKU, "" + cursor.getString(cursor.getColumnIndex(KEY_SKU)));
                cartMap.put("total_amount", "" + cursor.getString(cursor.getColumnIndex(KEY_TOTAL_PRICE)));
                cartMap.put("quantity", "" + cursor.getInt(cursor.getColumnIndex(KEY_QUANTITY)));

                cartList.add(cartMap);


            } while (cursor.moveToNext());

        }

        // database.close();

        return cartList;
    }

    public String checkoutOrders() {

        Gson gson = new GsonBuilder().create();
        HashMap<String, String> orderMap = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_ORDER;


        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                orderMap.put(KEY_ID, "" + cursor.getString(cursor.getColumnIndex(KEY_ID)));
                orderMap.put(KEY_DEVICE_TIME, "" + cursor.getString(cursor.getColumnIndex(KEY_DEVICE_TIME)));
                orderMap.put(KEY_ORDER_NO, "" + cursor.getString(cursor.getColumnIndex(KEY_ORDER_NO)));
                orderMap.put(KEY_STATUS, "" + cursor.getInt(cursor.getColumnIndex(KEY_STATUS)));
                orderMap.put(KEY_CUSTOMER_ID, "" + cursor.getInt(cursor.getColumnIndex(KEY_CUSTOMER_ID)));
                orderMap.put(KEY_LAT, "" + cursor.getString(cursor.getColumnIndex(KEY_LAT)));
                orderMap.put(KEY_LNG, "" + cursor.getString(cursor.getColumnIndex(KEY_LNG)));
                orderMap.put(KEY_DRIVER_ID, "" + cursor.getString(cursor.getColumnIndex(KEY_DRIVER_ID)));

            } while (cursor.moveToNext());

            orderMap.put("products", gson.toJson(checkoutCart()));

        }

        // database.close();

        //Use GSON to serialize Array List to JSON
        return gson.toJson(orderMap);

    }

    public void deleteOrderById() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_ORDER);
        database.close();
    }

    public void deleteCartByOrderId() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_CART);
        database.close();
    }

    /*******************************************
     Begin history crude
     ********************************************/


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
