package com.easylife.wishy;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Document;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

//import android.annotation.SuppressLint;

public class Login extends AppCompatActivity {

    CardView loginBtn;
  //  WebView webView;
    LinearLayout layout;
    TextView textView;
    Handler handler;
    BasicFunction functions;
    Intent intent;
    //FloatingActionButton floatingActionButton;
    ConstraintLayout cl;

    changeLan chngl;
    ProgressDialog dialog;

    //CheckBox checkBox;
    TextView terms;
    LottieAnimationView loadingView;

    WebView webView;
    Boolean onActivity=true;


    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.loginBack));
            getWindow().setStatusBarColor(getResources().getColor(R.color.loginBack));
        }
        functions= new BasicFunction();
        intent= new Intent(Login.this,DashBoard.class);

        chngl= new changeLan();
        new CheckLogInStatus().execute();

       // checkBox = findViewById(R.id.check);
        terms=findViewById(R.id.tarm);

        loginBtn=findViewById(R.id.login_btn);
        loadingView = findViewById(R.id.loading);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginDialog();
            }
        });


       // init();

       /* Log.e("xxxxxxxxxxxx","In login");

        try{

            layout.setVisibility(View.GONE);
            if(!functions.getCookie().c_user.equals("e")){
                Log.e("In login",functions.getCookie().c_user.substring(0,5)+"");
                chngl.execute();
            }else {
                layout.setVisibility(View.VISIBLE);
                Log.e("In login","nsid");
            }

        }catch (Exception e){
//            layout.setVisibility(View.VISIBLE);
            Log.e("In login","nsid");
        }*/

        webView=findViewById(R.id.webview);
        webView.setVisibility(View.GONE);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyBr());


      /*  button.setOnClickListener(new View.OnClickListener() {
            //@SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()) {
                    layout.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    cl.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(Login.this, "accept terms & conditions", Toast.LENGTH_SHORT).show();
                }

            }
        });*/

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,Browser.class);
                intent.putExtra("url","https://easylifemmle.blogspot.com/2020/07/wishy-terms.html");
                startActivity(intent);
            }
        });

     /*   floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {


                    Integer.parseInt(functions.getCookie().c_user.substring(0,5));
                    chngl.execute();

                }catch (Exception e){
                    Log.e("xxxxx",e.toString());
                    Toast.makeText(Login.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                }



            }
        });*/

        if(functions.read(getApplicationContext(),"today"+"IsOn").equals("null")){
            functions.write(getApplicationContext(),"today"+"IsOn","true");
        }
        if(functions.read(getApplicationContext(),"tomorrow"+"IsOn").equals("null")){
            functions.write(getApplicationContext(),"tomorrow"+"IsOn","true");
        }

        if(functions.read(getApplicationContext(),"today"+"hour").equals("null")){
            functions.write(getApplicationContext(),"today"+"hour","07");
        }

        if(functions.read(getApplicationContext(),"today"+"min").equals("null")){
            functions.write(getApplicationContext(),"today"+"min","00");
        }
        if(functions.read(getApplicationContext(),"tomorrow"+"hour").equals("null")){
            functions.write(getApplicationContext(),"tomorrow"+"hour","19");
        }

        if(functions.read(getApplicationContext(),"tomorrow"+"min").equals("null")){
            functions.write(getApplicationContext(),"tomorrow"+"min","00");
        }


    }


    String token;
    class saveToken extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] objects) {
            String sourceCode = functions.getSourceCode("https://m.facebook.com/composer/ocelot/async_loader/?publisher=feed");
            int a= sourceCode.indexOf("accessToken\\\":\\\"")+16;
            int b= sourceCode.indexOf("\\\"",a);
            token = sourceCode.substring(a,b);
            Log.i("access_token",token);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            functions.write(getApplicationContext(),"token",token);
            chngl.execute();
        }
    }

    long privecyx=0;
    class CheckLogInStatus extends AsyncTask{


        @SuppressLint("WrongThread")
        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                privecyx = Long.parseLong(functions.getPrivecyx());
               // chngl.execute();
            }catch (NumberFormatException e){

            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(privecyx==0){
                loadingView.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);

            }else {
                chngl.execute();
            }
        }
    }




    WebView loginwebView;
    Dialog logindialog;
    int x=0;

    void loginDialog(){
        logindialog = new Dialog(this);
        logindialog.setCancelable(true);
        logindialog.setContentView(R.layout.login_browser);
        loginwebView= logindialog.findViewById(R.id.webview);
        WebSettings webSettings = loginwebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        loginwebView.setWebViewClient(new MyBrowser());
        loginwebView.loadUrl("https://m.facebook.com");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (onActivity){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                loginwebView.evaluateJavascript( "console.log(document.cookie)", null);
                            } else {
                                loginwebView.loadUrl("javascript:" + "console.log(document.cookie)");
                            }

                        }
                    });

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();


        loginwebView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

                if(x==0) {
                    if (consoleMessage.message().contains("c_user")) {
                        x=1;
                        Log.i("c_user", consoleMessage.message());
                        Toast.makeText(Login.this, "you are logged in", Toast.LENGTH_SHORT).show();
                        new changeLan().execute();
                    } else {
                        //  Toast.makeText(Login.this, "You are logged out", Toast.LENGTH_SHORT).show();
                    }
                }

                return super.onConsoleMessage(consoleMessage);
            }
        });
        logindialog.show();
    }


 /*   private void init(){
        button=findViewById(R.id.login_btn);

        handler= handler = new Handler(this.getMainLooper());
        functions= new BasicFunction();

        layout.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyBrowser());
        webView.loadUrl("https://mbasic.facebook.com/");
        floatingActionButton=findViewById(R.id.floatingActionButton);

    }*/

    String gfid;


    private class changeLan extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            i=0;

            loginBtn.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
            if(logindialog!=null) {
                if (logindialog.isShowing()) {
                    logindialog.dismiss();
                }
            }


       //     webView.setVisibility(View.GONE);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            gfid=functions.getGfId();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
       //     webView.setVisibility(View.GONE);
            Log.i("gfid",gfid);
            webView.loadUrl("https://mbasic.facebook.com/a/language.php?l=en_US&lref=%2Fsettings%2Flanguage%2F&sref=legacy_mobile_settings&gfid="+gfid);
            i=1;


        }
    }



    String document;
    private class GetPage extends AsyncTask{


        @Override
        protected Object doInBackground(Object[] objects) {
            document =functions.getSourceCode("https://mbasic.facebook.com/events/birthdays/?_rdr");
            try {
                Log.i("data", document);
                intent.putExtra("data", document);
                startActivity(intent);
                onActivity=false;
                finish();

            }catch (java.lang.NullPointerException e){
               handler.post(new Runnable() {
                   @Override
                   public void run() {
                       showInternetOff();
                   }
               });
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }
    }

    private void showInternetOff() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("Internet off !");

        builder.setMessage("You are not connected to internet. Please turn on you internet connection and try again.")
                .setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }


    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
           // evlJs("console.log(document.cookie)");
            return true;
        }

        public void evlJs(String js){

        }

    }

    private class MyBr extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        int i=0;
        @Override
        public void onPageFinished(WebView view, String url) {
            if(i==0){
                new GetPage().execute();
            }
            i=1;
            super.onPageFinished(view, url);

        }
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
