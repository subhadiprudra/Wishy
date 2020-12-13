package com.easylife.wishy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    BasicFunction basicFunction;

    @Override
    public void onReceive(Context context, Intent intent) {

        basicFunction = new BasicFunction();

        try {

            basicFunction.resetBirthdayNotification(context.getApplicationContext(),"boot");
            basicFunction.resetAllSchaduler(context.getApplicationContext());

        }catch (Exception e){

        }



    }
}
