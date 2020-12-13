package com.easylife.wishy;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class Receiver extends BroadcastReceiver {

    BasicFunction basicFunction;
    String u_id, xs, fb_dtsg,xid;
    Context c;
    String toName;
    String message,type,number="";
    String toId="";

    @Override
    public void onReceive(Context context, Intent intent) {

        c = context;
        toName=intent.getStringExtra("name");
        message= intent.getStringExtra("message");
        type=intent.getStringExtra("type");
        xid=intent.getStringExtra("id");
        number= intent.getStringExtra("number");
        Log.i("number",number);



        basicFunction = new BasicFunction();
        u_id = basicFunction.getCookie().c_user;
        xs = basicFunction.getCookie().xs;
        fb_dtsg = basicFunction.getFbDtsg();

       if (type.equals("message")) {
            new SendMsg(intent.getStringExtra("toId"),message).execute();
            Log.i("XXXXXXXX",intent.getStringExtra("toId")+"\n"+message);
            toId=intent.getStringExtra("toId");

        }else if(type.equals("post")){
            new Post(intent.getStringExtra("toId"),message).execute();
           toId=intent.getStringExtra("toId");
        }else if(type.equals("reminder")){
            showReminder();
           toId=intent.getStringExtra("toId");
        }else {

           sendSms();
       }


    }


    public void sendSms(){

       try {
           String phoneNumber = number;
           SmsManager smsManager = SmsManager.getDefault();
           ArrayList<String> parts = smsManager.divideMessage(message);
           smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
           changeStatus("done",xid);
           basicFunction.showNotification(c,"SMS scheduler","Your SMS \""+message+"\" is sent successfully",ScList.class);
           basicFunction.write(c,"rate","ready");


       }catch (Exception e){
           Log.e("SMS error",e.toString());
           changeStatus("error",xid);
           basicFunction.showNotification(c,"Message scheduler\nError!!","Your text \""+message+"\" is not sent "+e.toString(),ScList.class);
       }

    }


    ServiceConnection serviceConnection;
    ReminderView reminderView;
    Intent intent;

    private void showReminder() {

        intent= new Intent(c,ReminderView.class);



        if(serviceConnection==null) {
            serviceConnection = new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                    ReminderView.ServiceBinder serviceBinder = (ReminderView.ServiceBinder) iBinder;
                    reminderView = serviceBinder.getService();
                    Log.i("msg", message);
                    reminderView.setVars(message,toId);
                    c.unbindService(serviceConnection);


                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {


                }
            };
        }

        c.getApplicationContext().bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);

        Notification notification = new Notification();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            c.startForegroundService(intent);
        }else {
            c.startService(intent);
        }
        changeStatus("done",xid);
        basicFunction.write(c,"rate","ready");




    }


    HashMap< String,String> header=new HashMap< String,String>();

    public class SendMsg extends AsyncTask {

        String id, msg;
        Connection.Response homePage=null;
        public SendMsg(String id, String msg) {
            this.id = id;
            this.msg = msg;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("c_user", u_id);
            hm.put("xs", xs);

            HashMap<String, String> d = new HashMap<String, String>();
            d.put("tids", id);
            d.put("ids[" + id + "]", "cid.c." + u_id + ":" + id);
            d.put("body", msg);
            d.put("send", "Send");
            d.put("fb_dtsg", fb_dtsg);
            //d.put("csid", "85ba43e4-3ca6-472f-9acb-24ad9f7b9bd2" );
            d.put("cver", "legacy");
            d.put("referrer", "");
            d.put("ctype", "");
            d.put("wwwupp", "C3");


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
            header.put("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");





            try {

                homePage = Jsoup.connect("https://mbasic.facebook.com/messages/send/?icm=1&refid=12")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36 OPR/62.0.3331.116")
                        .data(d)
                        .cookies(hm)
                        .headers(header)
                        .method(Connection.Method.POST)
                        .execute();

                System.out.print(homePage.url());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("failed");


            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(homePage==null){
                basicFunction.showNotification(c,"Message scheduler\nError!!","Your text \""+message+"\" is not sent\nMay be your internet connection is off",ScList.class);
                changeStatus("error",xid);
            }else {
                if(homePage.statusMessage().equals("OK")){
                    basicFunction.showNotification(c,"Message scheduler","Your text \""+message+"\" is sent Successfully",ScList.class);
                    changeStatus("done",xid);
                    basicFunction.write(c,"rate","ready");



                }else {
                    basicFunction.showNotification(c,"Message scheduler\nError!!","Your text \""+message+"\" is not sent",ScList.class);
                    changeStatus("error",xid);
                }
            }


        }
    }

    public class Post extends AsyncTask {

        String message;
        String Status;
        String id;
        Connection.Response homePage=null;

        public Post(String id,String message) {
            this.message = message;
            this.id=id;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            String eav = basicFunction.getEav();
            String prx = basicFunction.getPrivecyx();



            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("c_user", u_id);
            hm.put("xs", xs);

            HashMap<String, String> d = new HashMap<String, String>();
            d.put("privacyx", prx);
            d.put("target", id);
            d.put("c_src", "feed");
            d.put("cwevent", "composer_entry");
            d.put("fb_dtsg", fb_dtsg);
            //d.put("csid", "85ba43e4-3ca6-472f-9acb-24ad9f7b9bd2" );
            d.put("referrer", "feed");
            d.put("ctype", "inline");
            d.put("cver", "amber");
            d.put("rst_icv", "");
            d.put("xc_message", message);
            d.put("view_post", "Post");


            header.put("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            header.put("accept-language","en-IN,en-US;q=0.9,en;q=0.8");
            header.put("cache-control","max-age=0");
            header.put("content-type","application/x-www-form-urlencoded");
            header.put("origin","https://mbasic.facebook.com");
            header.put("referer","https://mbasic.facebook.com/profile.php?id="+u_id);
            header.put("sec-fetch-dest","document");
            header.put("sec-fetch-mode","navigate");
            header.put("sec-fetch-site","same-origin");
            header.put("sec-fetch-user","?1");
            header.put("upgrade-insecure-requests","1");
            header.put("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");



            try {


                String url="https://mbasic.facebook.com/composer/mbasic/?av="+u_id+"&"+eav+"&refid=17";



                homePage = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36")
                        .data(d)
                        .cookies(hm)
                        .headers(header)
                        .method(Connection.Method.POST)
                        .execute();

                Log.i(TAG, "doInBackground: "+url);





            } catch (Exception e) {
                basicFunction.showNotification(c,"error",eav,ScList.class);
                // Toast.makeText(c, e.toString(), Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if(homePage==null){
                basicFunction.showNotification(c,"Post scheduler\nError!!","Your text \""+message+"\" is not posted\nMay be your internet connection is off",ScList.class);
                changeStatus("error",xid);
            }else {
                if(homePage.statusMessage().equals("OK")){
                    basicFunction.showNotification(c,"Post scheduler","Your text \""+message+"\" is posted Successfully",ScList.class);
                    changeStatus("done",xid);
                    basicFunction.write(c,"rate","ready");

                }else {
                    basicFunction.showNotification(c,"Post scheduler\nError!!","Your text \""+message+"\" is not posted",ScList.class);
                    Toast.makeText(c, homePage.statusMessage(), Toast.LENGTH_SHORT).show();
                    changeStatus("error",xid);
                }
            }



        }
    }

    public void changeStatus(String status,String id){

        String data=basicFunction.readData(c,"data.txt");

        int a= data.indexOf(id);
        int b= data.indexOf("<status>",a)+8;
        int d= data.indexOf("</status>",b);

        String f= data.substring(0,b);
        String g= data.substring(d);

        String newData= f+status+g;


        try {
            new Write(newData,id).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    class Write extends AsyncTask {

        String data, id;

        public Write(String data, String id) {
            this.data = data;
            this.id = id;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            FileOutputStream fos;
            try {
                fos = c.openFileOutput("data.txt", MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(fos);

                osw.write(data);
                osw.flush();
                osw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

    }



}
