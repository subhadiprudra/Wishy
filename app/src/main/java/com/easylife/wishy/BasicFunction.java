package com.easylife.wishy;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.easylife.wishy.App.CHANNEL_ID;
import static java.lang.System.*;

public class BasicFunction {

    public  User getCookie(){

        try{

            try {
                CookieManager cookieManager = CookieManager.getInstance();

                String cookies = cookieManager.getCookie("https://m.facebook.com");
                //Log.i("xxxxxxxxxxxxx",cookies);
                int id1= cookies.indexOf("c_user");
                int id2= cookies.indexOf("xs");

                String c_user= cookies.substring(id1+7,cookies.indexOf(";",id1));
                String xs= cookies.substring(id2+3,cookies.indexOf(";",id2));

                try{
                    Integer.parseInt(c_user.substring(0,6));
                }catch (NumberFormatException e){
                    c_user="e";
                    Log.e("error","e");
                }

                User user= new User(c_user,xs);

                return user;
            }catch (StringIndexOutOfBoundsException e){
                User user= new User("","");
                return user;
            }

        }
        catch (NullPointerException e){
            User user= new User("","");
            return user;
        }

    }




    public static class ReadData implements Callable<String> {

        Context context;
        String fileName;

        public ReadData(Context context, String fileName) {
            this.context = context;
            this.fileName = fileName;
        }



        @Override
        public String call() throws Exception {


            String readString = "";
            FileInputStream fin = null;
            try {
                fin = context.openFileInput(fileName);
            } catch (FileNotFoundException e) {
                FileOutputStream fos = context.openFileOutput(fileName,MODE_PRIVATE);
                fin = context.openFileInput(fileName);
            }
            InputStreamReader inputStreamReader = new InputStreamReader(fin);
            BufferedReader bufferedReader= new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder= new StringBuilder(readString);
            while ((readString=bufferedReader.readLine())!=null){
                stringBuilder.append(readString);
            }

            inputStreamReader.close();

            return stringBuilder.toString();
        }
    }

    public String readData(Context context, String fileName){

        ExecutorService executorService= Executors.newSingleThreadExecutor();
        Future<String> data = executorService.submit(new ReadData(context,fileName));

        String pre = null;
        try {
            pre=data.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return pre;

    }



    public class GetSourceCode implements Callable<Document> {
        String url;
        public GetSourceCode(String url) {
            this.url=url;
        }

        @Override
        public Document call() throws Exception {

            HashMap< String,String> header=new HashMap< String,String>();

            header.put("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            header.put("accept-language","en-IN,en-US;q=0.9,en;q=0.8");
            header.put("cache-control","max-age=0");
            header.put("content-type","application/x-www-form-urlencoded");
            header.put("origin","https://mbasic.facebook.com");
            //header.put("referer"," https://mbasic.facebook.com/subhadip.rudra.5?lst=100007397701041%3A100007397701041%3A1585648693&ref_component=mbasic_home_header&ref_page=%2Fwap%2Fprofile_timeline.php&refid=17&_rdr");
            header.put("sec-fetch-dest","document");
            header.put("sec-fetch-mode","navigate");
            header.put("sec-fetch-site","same-origin");
            header.put("sec-fetch-user","?1");
            header.put("upgrade-insecure-requests","1");
            header.put("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36");



            Document homePage;
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("c_user", getCookie().c_user);
            hm.put("xs", getCookie().xs);
            hm.put("m_pixel_ratio","2.0000000298023224");
            hm.put("wd", "1440x271");



            homePage = null;
            try {

                homePage = Jsoup.connect(url)
                        .cookies(hm)
                        .ignoreContentType(true)
                        .headers(header)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36 OPR/62.0.3331.116")
                        .get();
                return homePage;

            } catch (Exception e) {
                out.println(e);
                return homePage;

            }

        }
    }

    public String getSourceCode(String url){
        ExecutorService executorService= Executors.newSingleThreadExecutor();
        Future<Document> sourceCode = executorService.submit(new GetSourceCode(url));
        Document homepage=null;
        try {
            homepage=sourceCode.get();
            return homepage.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Document page(String url){
        ExecutorService executorService= Executors.newSingleThreadExecutor();
        Future<Document> sourceCode = executorService.submit(new GetSourceCode(url));
        Document homepage=null;
        try {
            homepage=sourceCode.get();
            return homepage;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getFbDtsg(){
        ExecutorService executorService= Executors.newSingleThreadExecutor();
        Future<Document> sourceCode = executorService.submit(new GetSourceCode("https://mbasic.facebook.com"));
        String homepage=null;

        {
            try {
                homepage = sourceCode.get().toString();
                int i= homepage.indexOf("fb_dtsg")+16;
                int j= homepage.indexOf("\"",i+5);
                executorService.shutdown();
                return  homepage.substring(i,j);

            } catch (Exception e) {
                e.printStackTrace();
                executorService.shutdown();
                return "error";
            }

        }

    }

    public String getGfId(){
        ExecutorService executorService= Executors.newSingleThreadExecutor();
        Future<Document> sourceCode = executorService.submit(new GetSourceCode("https://mbasic.facebook.com/language.php"));
        String homepage=null;

        {
            try {
                homepage = sourceCode.get().toString();
                int i= homepage.indexOf("/a/language.php?l=en_US");
                int j = homepage.indexOf("gfid",i)+5;
                int k= homepage.indexOf("\"",j);
                executorService.shutdown();
                return  homepage.substring(j,k);

            } catch (Exception e) {
                e.printStackTrace();
                executorService.shutdown();
                return "error";
            }

        }

    }


    public String getEav(){

        ExecutorService executorService= Executors.newSingleThreadExecutor();
        Future<Document> sourceCode = executorService.submit(new GetSourceCode("https://mbasic.facebook.com"));
        String data=null;

        try {
            data=sourceCode.get().toString();
            int s=data.indexOf("eav=");
            int end = data.indexOf("&amp",s);
            String eav= data.substring(s,end);

            return eav;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }



    }

    public String getPrivecyx(){
        ExecutorService executorService= Executors.newSingleThreadExecutor();
        Future<Document> sourceCode = executorService.submit(new GetSourceCode("https://mbasic.facebook.com"));
        String data=null;

        try {
            data=sourceCode.get().toString();
            int s=data.indexOf("privacyx\" value=\"")+17;
            int end = data.indexOf("\"",s);
            String p= data.substring(s,end);

            return p;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public void showNotification(Context context, String title, String message, Class c) {
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        long[] vibrate = { 0, 100, 200, 300 };
        Intent intent = new Intent(context,c);
        PendingIntent pendingIntent= PendingIntent.getActivity(context,0,intent,0);




        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentText(message)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .build();
        notification.vibrate = vibrate;
        notification.defaults |= Notification.DEFAULT_SOUND;




        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(m, notification);
    }





    public long getCurrentTimeInMillis(){
        Calendar calendar = Calendar.getInstance();


            calendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND)+3);

            return calendar.getTimeInMillis();

        }


    public boolean isInternetOn(){
        try {
            Jsoup.connect("https://google.com").get();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public int getDisplayWidth(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return  displayMetrics.widthPixels;
    }



    public int getDisplayHeight(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return  displayMetrics.heightPixels;
    }

    public void setScheduler(Context context, long timeInMillis, String type, String id, String toId, String name, String message,String number) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Receiver.class);
        intent.putExtra("id",id);
        intent.putExtra("name",name);
        intent.putExtra("message",message);
        intent.putExtra("toId",toId);
        intent.putExtra("type",type);
        intent.putExtra("number",number);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(id),intent,0);


        Log.i("settime",convertDate(timeInMillis,"dd/MM/yyyy hh:mm:ss"));

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            AlarmManager.AlarmClockInfo ac=
                    new AlarmManager.AlarmClockInfo(timeInMillis,pendingIntent);

            alarmManager.setAlarmClock(ac, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);

            Log.i("alarm version","19 - 21");
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            Log.i("alarm version","<19");
        }



    }

    public static String convertDate(long dateInMilliseconds,String dateFormat) {
        return DateFormat.format(dateFormat,dateInMilliseconds).toString();
    }


    public void resetAllSchaduler(Context context){

        String data=readData(context,"data.txt");

        do {

            String id = data.substring(data.indexOf("<id>") + 4, data.indexOf("</id>"));
            String type = data.substring(data.indexOf("<type>") + 6, data.indexOf("</type>"));
            String to = data.substring(data.indexOf("<toName>") + 8, data.indexOf("</toName>"));
            String message = data.substring(data.indexOf("<message>") + 9, data.indexOf("</message>"));
            String toId = data.substring(data.indexOf("<toId>") + 6, data.indexOf("</toId>"));
            String millis = data.substring(data.indexOf("<millis>") + 8, data.indexOf("</millis>"));
            String number = data.substring(data.indexOf("<number>") + 8, data.indexOf("</number>"));

            if(data.substring(data.indexOf("<status>") + 8, data.indexOf("</status>")).equals("pending")){
                setScheduler(context, Long.parseLong(millis),type,id,toId,to,message,number);


              // showNotification(context,"wishy","resting scheduler",Login.class);
            }

            int e = data.indexOf("<end>") + 4;
            data = data.replace(data.substring(0, e), "");

        } while (data.contains("<end>"));
    }


    public void resetBirthdayNotification(Context context,String from){

        Log.i("reset","birthdaynotification");
       // showNotification(context,"wishy","resting notification",Login.class);
        if(read(context,"todayIsOn").equals("true")){
            setBirthdayAlarm(context,"today",from);
        }else {
            cancelBirthdayAlarm(context,"today");
        }

        if(read(context,"tomorrowIsOn").equals("true")){
            setBirthdayAlarm(context,"tomorrow",from);
        }else {
            cancelBirthdayAlarm(context,"tomorrow");
        }

    }




    void setBirthdayAlarm(Context context,String type,String from){

        Calendar calendar = Calendar.getInstance();
        Log.i("xxxxxxx",calendar.get(Calendar.HOUR_OF_DAY)+" "+calendar.get(Calendar.MINUTE));

        int hour = Integer.parseInt(read(context,type+"hour"));
        int min=  Integer.parseInt(read(context,type+"min"));
        int rc = 100;

        if(type.equals("today")){
            rc =101;
        }

        int day =  calendar.get(Calendar.DAY_OF_MONTH);



        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                day,
                hour,
                min,
                0);




        long millis = calendar.getTimeInMillis();

        if(!from.equals("boot")) {

            if (millis < getCurrentTimeInMillis()) {
                day = day + 1;
            }
        }

        Log.i("c millis",getCurrentTimeInMillis()+"");
        Log.i("s millis",millis+"");
        
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                day,
                hour,
                min,
                0);

        millis = calendar.getTimeInMillis();





        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("type",type);
        intent.putExtra("millis",millis+"");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,rc,intent,0);


        if (android.os.Build.VERSION.SDK_INT >= 21) {
            AlarmManager.AlarmClockInfo ac=
                    new AlarmManager.AlarmClockInfo(millis,pendingIntent);

            alarmManager.setAlarmClock(ac, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);

            Log.i("alarm version","19 - 21");
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
            Log.i("alarm version","<19");
        }


    }



    void cancelBirthdayAlarm(Context context,String type){


        int rc = 100;

        if(type.equals("today")){
            rc =101;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("type",type);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,rc,intent,0);
        alarmManager.cancel(pendingIntent);

    }

    public String read (Context context,String key){

        String MyPREFERENCES = "MyPrefs" ;
        SharedPreferences sharedPreferences= context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String string = sharedPreferences.getString(key,"null");
        return string;

    }


    public void write(Context context,String key,String data){

        String MyPREFERENCES = "MyPrefs" ;
        SharedPreferences sharedPreferences= context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,data);
        editor.commit();
    }


    public String getTimeByAMPM(int hour,int min){
        int h;
        String s;

        if(hour>12){
            h= hour -12;
            s= "PM";
        }else {
            s="AM";
            h=hour;

            if(hour==0){
                h=12;
            }
            if(hour==12){
                h=12;
                s="PM";
            }
        }

        String l=h+ " : "+min +" "+s;
        return (l);
    }


    public Boolean isLeapYear(int year){

        boolean leap = false;

        if(year % 4 == 0)
        {
            if( year % 100 == 0)
            {
                // year is divisible by 400, hence the year is a leap year
                if ( year % 400 == 0)
                    leap = true;
                else
                    leap = false;
            }
            else
                leap = true;
        }
        else
            leap = false;

        return leap;

    }




    public Calender getNextDay(int day,int mount,int year){

        int d=day,m=mount,y=year;


            if(mount == 1  ||mount == 3 ||mount == 5 ||mount == 7 ||mount == 8 ||mount == 10 ||mount == 12  ){
                if(mount!=12) {
                    if (day == 31) {
                        d = 1;
                        m = mount+1;
                    }else {
                        d=day+1;
                    }
                }else {
                    d=1;
                    m=1;
                    y=year+1;
                }
            }else if(mount == 2){
                if(isLeapYear(year)){
                    if(day==29){
                        d=1;
                        m=3;
                    }else {
                        d=day+1;
                    }
                }else {
                    if(day==28){
                        d=1;
                        m=3;
                    }else {
                        d=day+1;
                    }
                }
            }else {
                if (day == 30) {
                    d = 1;
                    m = mount+1;
                }else {
                    d=day+1;
                }
            }

            Calender calender = new Calender(d,m,y,d+" / "+m+" / "+y);

            return (calender);


    }

    class Calender{

        int day,mount,year;
        String text;

        public Calender(int day, int mount, int year, String text) {
            this.day = day;
            this.mount = mount;
            this.year = year;
            this.text = text;
        }
    }





    public Boolean isReminderAvailableInTimeRange(Long setMillis,Context context){

        List<ScList.Model> list = new ArrayList<>();
        String data = readData(context,"data.txt");
        List<ScList.Model> list1 = new ArrayList<>();

        try {

            do {

                String type = data.substring(data.indexOf("<type>") + 6, data.indexOf("</type>"));
                Long millis = Long.valueOf(data.substring(data.indexOf("<millis>") + 8, data.indexOf("</millis>")));


                if(type.equals("reminder")){
                    if(millis>setMillis){
                        if(millis-setMillis>=0 && millis-setMillis<= 120000){
                            return true;
                        }

                    }else {
                        if(setMillis-millis>=0 && setMillis-millis<= 120000){
                            return true;
                        }
                    }
                }



                int e = data.indexOf("<end>") + 4;
                data = data.replace(data.substring(0, e), "");

            } while (data.contains("<end>"));


            return false;



        }catch (Exception e){
            Log.e("Error",e.toString());
            return false;

        }




    }
















}
