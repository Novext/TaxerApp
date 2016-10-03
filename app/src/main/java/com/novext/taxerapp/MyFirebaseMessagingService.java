package com.novext.taxerapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by angel on 10/2/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("FIREBASE", remoteMessage.getNotification().getTitle());

        sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        sendNewStopBroadcast(remoteMessage);

    }

    private void sendNewStopBroadcast(RemoteMessage remoteMessage) {
        Intent intent = new Intent("notification");
        intent.putExtra("latitude", remoteMessage.getData().get("latitude"));
        intent.putExtra("longitude", remoteMessage.getData().get("longitude"));
        intent.putExtra("_id", remoteMessage.getData().get("_id"));
        intent.putExtra("description",remoteMessage.getData().get("description"));
        intent.putExtra("minutes",remoteMessage.getData().get("minutes"));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent);
    }

    public void sendNotification(String title,String message){
        final int NOTIFICATION_ID = 1;
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MapsActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
