package com.easylife.wishy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Settings2 extends AppCompatActivity {

    String type ;
    Switch s;
    BasicFunction basicFunction;
    TextView textView,time;
    RelativeLayout relativeLayout;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        basicFunction= new BasicFunction();

        type = getIntent().getStringExtra("type");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(type+" notification");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        s = findViewById(R.id.sw);
        textView = findViewById(R.id.n_is_on);
        relativeLayout= findViewById(R.id.n2);
        time= findViewById(R.id.time);

        if(basicFunction.read(getApplicationContext(),type+"IsOn").equals("true")){

            s.setChecked(true);
            textView.setText("On");
            relativeLayout.setVisibility(View.VISIBLE);

        }else {

            s.setChecked(false);
            textView.setText("Off");
            relativeLayout.setVisibility(View.GONE);

        }

        int h = Integer.parseInt(basicFunction.read(getApplicationContext(),type+"hour"));
        int m = Integer.parseInt(basicFunction.read(getApplicationContext(),type+"min"));

        time.setText(basicFunction.getTimeByAMPM(h,m));



        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                basicFunction.write(getApplicationContext(),type+"IsOn",s.isChecked()+"");


                if(s.isChecked()){
                    s.setChecked(true);
                    textView.setText("On");
                    relativeLayout.setVisibility(View.VISIBLE);
                }else {
                    s.setChecked(false);
                    textView.setText("Off");
                    relativeLayout.setVisibility(View.GONE);
                }

            }
        });


        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });



    }


    Dialog mydialog;

    public void showTimePicker () {

        TextView ok, cancel;
        final TimePicker timePicker;

        mydialog= new Dialog(this);

        mydialog.setContentView(R.layout.time_picker);

        timePicker = mydialog.findViewById(R.id.time_picker);
        ok = mydialog.findViewById(R.id.time_picker_ok);
        cancel = mydialog.findViewById(R.id.time_picker_cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    setTime(timePicker.getHour() , timePicker.getMinute());

                } else {
                    setTime( timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                }

                mydialog.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mydialog.dismiss();

            }
        });

        mydialog.show();


    }

    void setTime(int hour,int min){


        time.setText(basicFunction.getTimeByAMPM(hour,min));

        basicFunction.write(getApplicationContext(),type+"hour",hour+"");
        basicFunction.write(getApplicationContext(),type+"min",min+"");


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