package com.easylife.liveipl;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import static com.easylife.liveipl.App.CHANNEL_ID;


public class MyFCMService extends FirebaseMessagingService {


    Context context;


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.i("token",s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification() != null){
            Log.d("xxxxxxxxxxxxxxxxxx",remoteMessage.getNotification().getBody());

            context = getApplicationContext();

            Intent intent = new Intent(context,Login.class);
            PendingIntent pendingIntent= PendingIntent.getActivity(context,0,intent,0);


            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;

            long[] vibrate = { 0, 1000, 2000, 3000 };


            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentIntent(pendingIntent);


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notification.setSmallIcon(R.drawable.ic_stat_name);
                notification.setColor(context.getResources().getColor(R.color.background));
            } else {
                notification.setSmallIcon(R.drawable.ic_stat_name);
            }

            Notification notification1 = notification.build();

            notification1.defaults |= Notification.DEFAULT_VIBRATE;



            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(m, notification1);

        }

    }
}
