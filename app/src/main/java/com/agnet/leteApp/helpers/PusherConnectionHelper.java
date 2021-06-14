package com.agnet.leteApp.helpers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.agnet.leteApp.R;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import static com.agnet.leteApp.application.App.CHANNEL_1_ID;


public class PusherConnectionHelper {
    private static Pusher _pusher;
    private static Context c;

    public PusherConnectionHelper(Context context) {

        this.c = context;

        PusherOptions options = new PusherOptions();
        options.setCluster("ap2");
        _pusher = new Pusher("bfc6f755c4f72773c62f", options);

        _pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.i("Pusher", "State changed from " + change.getPreviousState() +
                        " to " + change.getCurrentState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.i("Pusher", "There was a problem connecting! " +
                        "\ncode: " + code +
                        "\nmessage: " + message +
                        "\nException: " + e
                );
            }
        }, ConnectionState.ALL);

    }

    public static Channel subscribePusherChannel(String mChannel) {

        Channel channel = _pusher.subscribe(mChannel);
        return channel;
    }

    public static void alertNotification(Intent activityIntent) {

       NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
        PendingIntent contentIntent = PendingIntent.getActivity(c,
                0, activityIntent, 0);
    /*    Intent fullScreenIntent = new Intent(this, LocationActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        finish();
*/
        Notification notification = new NotificationCompat.Builder(c, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_basket)
                .setContentTitle("Message Kutoka Lete")
                .setContentText("Oda mpya imeingia")
                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(Color.BLUE)
                .setVibrate(new long[]{100, 200, 100, 200, 100, 200, 100})
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);
    }
}
