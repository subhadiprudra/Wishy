package com.easylife.wishy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.jsoup.nodes.Document;

import java.sql.Time;
import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {

    long millis;
    BasicFunction basicFunction;
    Context context;
    String type;


    @Override
    public void onReceive(Context context, Intent intent) {
        basicFunction= new BasicFunction();
        this.context=context;
        type= intent.getStringExtra("type");

        if(type.equals("tomorrow")){
            new GenarareTomorrowList().execute();
        }else {
            new GenarareTodayList().execute();
        }



    }



    class GenarareTomorrowList extends AsyncTask {

        Document document;
        String names="";
        String name="";

        @Override
        protected Object doInBackground(Object[] objects) {

            document= basicFunction.page("https://mbasic.facebook.com/events/birthdays");
            return null;
        }



        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            Log.i("xxxxxxxxx","complete");

            try {

                String data = document.toString();


                int c = data.indexOf("Upcoming Birthdays");
                data = data.substring(c);

                int i = 0;

                while (true) {

                    if (!data.contains("Tomorrow")) {
                        break;
                    }

                    int a = data.indexOf("<li");
                    int b = data.indexOf("</li>", a);
                    String e = data.substring(a, b);

                    int g = e.indexOf("<p class=") + 20;
                    int f = e.indexOf("</p>", g);
                    name = e.substring(g, f);

                    data = data.substring(b);

                    if (i == 0) {
                        names = name;
                    } else {
                        names = names + " , " + name;
                    }
                    i++;


                }

                basicFunction.showNotification(context, "Tap to Wish at 12 AM", "Tomorrow's birthday list : " + names, Login.class);
                Log.i("xxxxxxxxx", "Done");

                basicFunction.resetBirthdayNotification(context,"again");



            }catch (Exception e){

                if(e.toString().contains("java.lang.String org.jsoup.nodes.Document.toString()")) {

                    Log.e("Error", e.toString());

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent inten = new Intent(context, NotificationReceiver.class);
                    inten.putExtra("type","tomorrow");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 10, inten, 0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getResetMillis(), pendingIntent);

                    }
                    if (Build.VERSION.SDK_INT >= 19) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, getResetMillis(), pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, getResetMillis(), pendingIntent);
                    }

                }


            }



        }
    }




    class GenarareTodayList extends AsyncTask {

        Document document;
        String names = "";
        String name = "";

        @Override
        protected Object doInBackground(Object[] objects) {

            document = basicFunction.page("https://mbasic.facebook.com/events/birthdays");
            return null;
        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            Log.i("xxxxxxxxx", "complete");

            try {

                String data = document.toString();


                int a= data.indexOf("Today's Birthdays");
                int b= data.indexOf("</article>");

                data = data.substring(a, b);

                Log.i("data",data);




                int i = 0;

                while (true) {

                    try {

                        int g = data.indexOf("img src=") + 9;
                        int f = data.indexOf("\"", g);
                        String dpUrl = data.substring(g, f);
                        dpUrl = dpUrl.replaceAll(";", "&");

                        g = data.indexOf("alt=", f) + 5;
                        f = data.indexOf(",", g);
                        String name = data.substring(g, f);

                        data = data.substring(f);

                        if (i == 0) {
                            names = name;
                        } else {
                            names = names + " , " + name;
                        }


                        i++;

                        if (!dpUrl.contains("https")) {
                            break;
                        }
                    }catch (Exception e){
                        break;
                    }



                }

                basicFunction.showNotification(context, "Tap to Wish now", "Today's birthday list : " + names, Login.class);
                Log.i("xxxxxxxxx", "Done");


            } catch (Exception e) {

                Log.e("Error", e.toString());


                if(e.toString().contains("java.lang.String org.jsoup.nodes.Document.toString()")) {

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent inten = new Intent(context, NotificationReceiver.class);
                    inten.putExtra("type","today");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 11, inten, 0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getResetMillis(), pendingIntent);

                    }
                    if (Build.VERSION.SDK_INT >= 19) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, getResetMillis(), pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, getResetMillis(), pendingIntent);
                    }

                }

            }


        }
    }




    long getResetMillis(){

       return basicFunction.getCurrentTimeInMillis()+(5*60*1000);

    }

}
