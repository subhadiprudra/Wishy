package com.easylife.wishy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import static com.easylife.wishy.App.CHANNEL_ID;


public class MyFCMService extends FirebaseMessagingService {


    Context context;
    BasicFunction basicFunction;


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
            basicFunction= new BasicFunction();
            basicFunction.showNotification(context,remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),Login.class);

        }

    }
}
