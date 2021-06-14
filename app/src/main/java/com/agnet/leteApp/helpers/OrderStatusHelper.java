package com.agnet.leteApp.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;


import com.agnet.leteApp.R;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

public class OrderStatusHelper {
    private Context c;

    public OrderStatusHelper(Context context) {
        this.c = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showButtonByStatus(TextView orderStatus, int status) {

        if (status == 1) {

            orderStatus.setText("Inasubiri");
            orderStatus.setBackground(c.getResources().getDrawable(R.drawable.round_corners_with_stroke_pending));
            orderStatus.setTextColor(ContextCompat.getColor(c, R.color.status_pending));
            orderStatus.setPadding(20, 5, 20, 5);


        } else if (status == 2) {

            orderStatus.setText("Imekubalika");
            orderStatus.setBackground(c.getResources().getDrawable(R.drawable.round_corners_with_stroke_accept));
            orderStatus.setPadding(20, 5, 20, 5);
            orderStatus.setTextColor(ContextCompat.getColor(c, R.color.status_accept));

        }  else if (status == 3) {

            orderStatus.setText("Ipo njiani");
            orderStatus.setBackground(c.getResources().getDrawable(R.drawable.round_corners_with_stroke_ready));
            orderStatus.setPadding(20, 5, 20, 5);
            orderStatus.setTextColor(ContextCompat.getColor(c, R.color.status_ready));

        }else if (status == 4) {

            orderStatus.setText("Imekabidhiwa");
            orderStatus.setBackground(c.getResources().getDrawable(R.drawable.round_corners_with_stroke_deliver));
            orderStatus.setPadding(20, 5, 20, 5);
            orderStatus.setTextColor(ContextCompat.getColor(c, R.color.status_deliver));

        } else if (status == 5) {

            orderStatus.setText("Imeghailishwa");
            orderStatus.setBackground(c.getResources().getDrawable(R.drawable.round_corners_with_stroke_cancel));
            orderStatus.setPadding(20, 5, 20, 5);
            orderStatus.setTextColor(ContextCompat.getColor(c, R.color.status_cancel));

            orderStatus.setForeground(c.getResources().getDrawable(R.drawable.strike_through));
        }

    }

    public void showActionButtonByStatus(Button actionBtn, int status) {


        if (status == 1) {

            actionBtn.setText("Nimekubali oda");//imekubal
            actionBtn.setBackgroundColor(ContextCompat.getColor(c, R.color.status_accept));
            actionBtn.setTextColor(ContextCompat.getColor(c, R.color.blue));
            actionBtn.setPadding(20, 5, 20, 5);
            actionBtn.setId(R.id.status_accept_btn);
            actionBtn.setTag(1);
            actionBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            actionBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);


        }   else if (status == 2) {

            actionBtn.setText("Nipo njiani");//imefika
            actionBtn.setBackgroundColor(ContextCompat.getColor(c, R.color.status_ready));
            actionBtn.setPadding(20, 5, 20, 5);
            actionBtn.setId(R.id.status_ready_btn);
            actionBtn.setTextColor(ContextCompat.getColor(c, R.color.white));
            actionBtn.setTag(2);


            actionBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        } else if (status == 3) {

            actionBtn.setText("Nimekabidhi oda");//imefika
            actionBtn.setBackgroundColor(ContextCompat.getColor(c, R.color.status_deliver));
            actionBtn.setPadding(20, 5, 20, 5);
            actionBtn.setId(R.id.status_delivered_btn);
            actionBtn.setTextColor(ContextCompat.getColor(c, R.color.white));
            actionBtn.setTag(3);


            actionBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        }else {
            Drawable img = c.getResources().getDrawable(R.drawable.ic_done_green_24dp);

            actionBtn.setText("Umeshakabidhi Oda");//imekabidhiwa
            actionBtn.setBackground(c.getResources().getDrawable(R.drawable.round_corners_with_stroke_deliver));
            actionBtn.setPadding(20, 5, 20, 5);
            actionBtn.setTextColor(ContextCompat.getColor(c, R.color.status_deliver));
            actionBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
            actionBtn.setTag(4);


            // actionBtn.setClickable(false);
        }
    }
}
