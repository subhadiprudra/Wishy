package com.easylife.wishy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    TextView tomorrowTv,todayTv;
    BasicFunction basicFunction;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        basicFunction= new BasicFunction();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        RelativeLayout today = findViewById(R.id.todayBtn);
        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Settings2.class);
                intent.putExtra("type","today");
                startActivity(intent);
            }
        });

        RelativeLayout tomorrow = findViewById(R.id.tomorrowBtn);
        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Settings2.class);
                intent.putExtra("type","tomorrow");
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        todayTv = findViewById(R.id.ons2);
        tomorrowTv=findViewById(R.id.ons1);


        if(basicFunction.read(getApplicationContext(),"tomorrow"+"IsOn").equals("true")){
            tomorrowTv.setText("On");
        }else {
            tomorrowTv.setText("Off");
        }

        if(basicFunction.read(getApplicationContext(),"today"+"IsOn").equals("true")){
            todayTv.setText("On");
        }else {
            todayTv.setText("Off");
        }

        new BasicFunction().resetBirthdayNotification(this,"dash");


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