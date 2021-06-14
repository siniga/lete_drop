package com.agnet.leteApp.helpers;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class AppManager {
    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static Random RANDOM = new Random();


    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }

    public static void setImageUrlFromUri(ImageView imageView, String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(imageView.getContext())
                    .load(imageUrl)
                    .into(imageView);
        }
    }
    public static String generateRandomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {

            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }

    public static void onErrorResponse(VolleyError error, Context context) {
        String json = null;

        NetworkResponse response = error.networkResponse;
        if(response != null && response.data != null){
            switch(response.statusCode){
                case 400:
                    json = new String(response.data);
                    json = trimMessage(json, "message");
                    if(json != null) displayMessage(json, context);
                    break;
            }
            //Additional cases
        }
    }

    public static  String trimMessage(String json, String key){
        String trimmedString = null;

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    //Somewhere that has access to a context
    public static void displayMessage(String toastString, Context context){
       // Log.d("HERE_TAG",toastString);
      //  Toast.makeText(context, "SINGUala"+toastString, Toast.LENGTH_LONG).show();
    }

}
