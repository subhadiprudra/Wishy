package com.easylife.wishy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;



public class Browser extends AppCompatActivity {

    Toolbar toolbar;
    WebView web;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        url= getIntent().getStringExtra("url");





        toolbar = findViewById(R.id.toolbardw);
        toolbar.setTitle("Wishy");
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        web = (WebView)  findViewById(R.id.browser);
        web.setWebViewClient(new MyBrowser());
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        web.loadUrl(url);



    }



    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        int i=0;

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (i == 1) {
                if (!url.equals("https://mbasic.facebook.com/login/save-password-interstitial")) {
                    i=2;
                    Intent intent = new Intent(Browser.this, Login.class);
                    startActivity(intent);
                    finish();
                }

            }

            if (url.equals("https://mbasic.facebook.com/login/save-password-interstitial")) {
                i = 1;
            }



        }


    }







    @Override
    public void onBackPressed() {
        if(web.canGoBack()){
            web.goBack();
        }
        else {
            finish();
        } }


}
