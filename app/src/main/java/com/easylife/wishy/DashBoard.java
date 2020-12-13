package com.easylife.wishy;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.judemanutd.autostarter.AutoStartPermissionHelper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import org.jsoup.Jsoup;

import java.io.IOException;


public class DashBoard extends AppCompatActivity {

    Toolbar toolbar;

    TabLayout tabLayout;
    ViewPager viewPager;
    CardView floatingActionButton;
    BasicFunction basicFunction;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar=findViewById(R.id.toolbardw);
        toolbar.setTitle("");

        toolbar.setLogo(R.drawable.ic_baseline_cake_24);
        setSupportActionBar(toolbar);


        tabLayout=findViewById(R.id.tab);
        viewPager=findViewById(R.id.view_pager);



        tabLayout.addTab(tabLayout.newTab().setText("Tomorrow"));
        tabLayout.addTab(tabLayout.newTab().setText("Today"));
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));

       // tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        String data=getIntent().getStringExtra("data");
        Log.i("data",data);

        final MyAdapter adapter = new MyAdapter(this,getSupportFragmentManager(), tabLayout.getTabCount(),data);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        floatingActionButton= findViewById(R.id.float_sc);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ScList.class);
                startActivity(intent);
            }
        });

        basicFunction= new BasicFunction();


        basicFunction.resetBirthdayNotification(this,"dash");

        try {
            basicFunction.resetAllSchaduler(this);
        }catch (Exception e){

        }

        if(basicFunction.read(this,"autostart").equals("null")){
            askAutoStart();
        }

        new UpdateChecker().execute();
       // basicFunction.write(this,"rate","nf");


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void askAutoStart() {

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.bottom_dialog);
        TextView title = dialog.findViewById(R.id.title);
        TextView content = dialog.findViewById(R.id.content);
        Button ok = dialog.findViewById(R.id.ok);
        Button cancel = dialog.findViewById(R.id.cancel);

        title.setText("Enable auto start permission");
        content.setText("If you don't want to miss any reminder please enable auto start permission");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAppInfo();
                new BasicFunction().write(getApplicationContext(),"autostart","clicked");
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();




      /* String manufacturer = Build.MANUFACTURER;
        Log.i("manu",manufacturer);


        if (AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
            builder.setTitle("Enable auto start permission");

            builder.setMessage("To work properly please enable auto start permission")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AutoStartPermissionHelper.getInstance().getAutoStartPermission(getApplicationContext());
                            new BasicFunction().write(getApplicationContext(),"autostart","clicked");
                        }
                    })
                    .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();


        }else if(manufacturer.equals("realme")){
            
            AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
            builder.setTitle("Enable auto start permission");

            builder.setTitle("Enable auto start permission");

            builder.setMessage("To work properly please enable auto start permission")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            openAppInfo();
                            new BasicFunction().write(getApplicationContext(),"autostart","clicked");
                        }
                    })
                    .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();



        }*/
    }

    public void openAppInfo() {
        String packageName = getPackageName();

        try {
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            //e.printStackTrace();

            //Open the generic Apps page:
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivity(intent);

        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_menu, menu);
        return true;

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id){

            case (R.id.settings) :
                Intent intent = new Intent(getApplicationContext(),Settings.class);
                startActivity(intent);
                break;

            case (R.id.accounts) :
                Intent intent1 = new Intent(getApplicationContext(),Account.class);
                startActivity(intent1);
                break;

            case (android.R.id.home):
                finish();
        }

        return super.onOptionsItemSelected(item);
    }


    String packageName=null;

    class UpdateChecker extends AsyncTask {
        String data=null;
        String cverson;

        @Override
        protected Object doInBackground(Object[] objects) {
            packageName=getPackageName();



            try {
                data= Jsoup.connect("https://play.google.com/store/apps/details?id="+packageName).get().toString();
                //System.out.println(data);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                int a = data.indexOf("Current Version") + 15;

                int c = data.indexOf("IQ1z0d", a) + 7;
                int b = data.indexOf("htlgb", c) + 7;
                int d = data.indexOf("</span>", b);

                cverson = data.substring(b, d);
                float cva= Float.parseFloat(cverson);
                Log.i("version",cverson);

                String v = BuildConfig.VERSION_NAME;
                float cv = Float.parseFloat(v);


                if (cv<cva) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
                    builder.setTitle("Update available");

                    builder.setMessage("New update available. Update your app to fix problems and to get new features.")
                            .setCancelable(false)
                            .setPositiveButton("Update now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                                    }
                                }
                            })
                            .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();


                }
            }catch (Exception e){

            }


        }
    }




}
