package com.easylife.wishy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.nodes.Document;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends AppCompatActivity {

    CircleImageView dp;
    TextView nameTv;
    BasicFunction basicFunction;
    String dpUrl;
    Handler handler;
    String data;
    Thread setDp,setName;
    Document document;
    Toolbar toolbar;

    CardView rate,share,bugs,policy,logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        dp = findViewById(R.id.dp);
        nameTv = findViewById(R.id.name);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Account");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        basicFunction = new BasicFunction();
        handler = new Handler(getMainLooper());

        if(!basicFunction.read(getApplicationContext(),basicFunction.getCookie().c_user+"name").equals("null")){
            nameTv.setText(basicFunction.read(getApplicationContext(),basicFunction.getCookie().c_user+"name"));
        }

        setName = new Thread(new Runnable() {
            @Override
            public void run() {
                document= basicFunction.page("https://mbasic.facebook.com/profile.php");
                basicFunction.write(getApplicationContext(),basicFunction.getCookie().c_user+"name",document.title());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        nameTv.setText(document.title());
                    }
                });
            }
        });

        setName.start();

        setDp= new Thread(new Runnable() {
            @Override
            public void run() {

                String url = "https://mbasic.facebook.com/profile/picture/view/?profile_id="+basicFunction.getCookie().c_user;
                data= basicFunction.getSourceCode(url);


                int a = data.indexOf("https://scontent");
                int b= data.indexOf("\"",a);

                dpUrl = data.substring(a,b);
                dpUrl= dpUrl.replaceAll(";","&");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(getApplicationContext()).load(dpUrl).into(dp);
                    }
                });

            }
        });


        setDp.start();


        rate= findViewById(R.id.rate);
        bugs= findViewById(R.id.bugs);
        share= findViewById(R.id.share);
        policy= findViewById(R.id.policy);
        logout= findViewById(R.id.logout);

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Are you very bad in remembering days ? \nand always forget to wish your best friends in their birthdays ?" +
                        "\nDownload wishy - auto birthday wisher \n" +"https://play.google.com/store/apps/details?id=" + getPackageName();
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Wishy");
                intent.putExtra(android.content.Intent.EXTRA_TEXT,message);
                startActivity(Intent.createChooser(intent,message));
            }
        });

        bugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent email= new Intent(Intent.ACTION_VIEW);
                email.setType("message/rfc822")
                        .setData(Uri.parse("mailto:wishy.bugreports@gmail.com"))
                        .putExtra(Intent.EXTRA_SUBJECT, "Bugs")
                        .setPackage("com.google.android.gm");
                startActivity(email);

            }
        });

        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Account.this,Browser.class);
                intent.putExtra("url","https://easylifemmle.blogspot.com/2020/07/wishy-pp.html");
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Account.this,Browser.class);
                intent.putExtra("url","https://mbasic.facebook.com/login/save-password-interstitial");
                startActivity(intent);

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id){


            case (android.R.id.home):
                finish();

        }

        return super.onOptionsItemSelected(item);
    }





}
