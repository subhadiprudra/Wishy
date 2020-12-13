package com.easylife.wishy;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class AddScdul extends AppCompatActivity {

    RelativeLayout ext_date,ext_time,msgview,numberView;
    Dialog mydialog;
    TextView nameTv,timeTv,dateTv;

    Toolbar toolbar;
    BasicFunction basicFunction ;
    Button button;
    String type;

    String toId;
    int min,hour,day,month,year;
    Calendar calendar;

    String from;

    EditText message;
    EditText numberTv;
    ImageView sugsn,import_contact;
    int test=0;
    Context mContext;
    String number ="";

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_scaduler);


        mContext=this;

        basicFunction = new BasicFunction();

        nameTv= findViewById(R.id.person_name);
        timeTv=findViewById(R.id.time_text);
        dateTv=findViewById(R.id.date_text);
        button = findViewById(R.id.add_sch);
        message = findViewById(R.id.sc_message);
        numberTv=findViewById(R.id.number);
        sugsn=findViewById(R.id.sugsn);
        import_contact=findViewById(R.id.import_contact);

        ext_date =findViewById(R.id.ext_date);
        ext_time =findViewById(R.id.ext_time);
        numberView =findViewById(R.id.numbrlayout);

        toolbar = findViewById(R.id.toolbarj);
        toolbar.setTitle("Add task");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendar= Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day=calendar.get(Calendar.DAY_OF_MONTH);

        BasicFunction.Calender calender = basicFunction.getNextDay(day,month+1,year);
        day = calender.day;
        month = calender.mount-1;
        year = calender.year;

        hour = 0;
        min = 0;


        timeTv.setText("12 AM");
        dateTv.setText(calender.text);

        String name=getIntent().getStringExtra("name");
        nameTv.setText(name);

        numberView.setVisibility(View.GONE);




        type=getIntent().getStringExtra("type");

        askAllPermission();


        if(type.equals("post") || type.equals("message")) {

            toId=getIntent().getStringExtra("toId");

        }else if(type.equals("reminder")){

            toId=getIntent().getStringExtra("toId");
            sugsn.setVisibility(View.GONE);
            message.setHint("Reminder message");

            if(!getIntent().getStringExtra("from").equals("customAdd")){
                message.setText("Wish "+name);
            }

        }else {
            numberView.setVisibility(View.VISIBLE);
        }

        sugsn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCaptions();
            }
        });


        import_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_PICK);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, 0);
            }
        });





        mydialog = new Dialog(this);



        ext_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        ext_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                test=0;


                if(type.equals("post") || type.equals("message")|| type.equals("reminder")) {

                    if(message.getText().toString().equals("")){
                        test=1;
                    }
                }else {
                    if(message.getText().toString().equals("") || numberTv.getText().toString().equals("")){
                        test=1;

                    }
                    number=numberTv.getText().toString();

                }

                if(test==1){
                    Toast.makeText(AddScdul.this, "Write correct information", Toast.LENGTH_SHORT).show();
                }else {

                    String d=dateTv.getText().toString();
                    Calendar c = Calendar.getInstance();
                    c.set(year,month,day,hour,min,0);
                    Log.i("time", hour+" "+min);
                    Log.i("millis", c.getTimeInMillis()+"");
                    try {

                        String id= String.valueOf((int) (Math.random() * 999999));

                        if(type.equals("reminder")){

                            if(basicFunction.isReminderAvailableInTimeRange(c.getTimeInMillis(),getApplicationContext())){
                                Toast.makeText(mContext, "A scheduler is already present on nearly same time. Change the time a little", Toast.LENGTH_LONG).show();
                            }else{
                                write(id,type,message.getText().toString(),nameTv.getText().toString(),timeTv.getText().toString(),d,toId,c.getTimeInMillis()+"");
                                basicFunction.setScheduler(getApplicationContext(),c.getTimeInMillis(),type,id,toId,nameTv.getText().toString(),message.getText().toString(),number);
                                addedDialog();
                            }

                        }else {
                            write(id,type,message.getText().toString(),nameTv.getText().toString(),timeTv.getText().toString(),d,toId,c.getTimeInMillis()+"");
                            basicFunction.setScheduler(getApplicationContext(),c.getTimeInMillis(),type,id,toId,nameTv.getText().toString(),message.getText().toString(),number);
                            addedDialog();

                        }

                        //(String id,  String message, String toName, String time, String date, String toId


                    } catch (IOException e) {
                        Toast.makeText(AddScdul.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


    }


    public void addedDialog(){

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.sch_added);

        TextView textView = dialog.findViewById(R.id.in);
        Button button = dialog.findViewById(R.id.ok);

        if(type.equals("post") || type.equals("message") ){

           textView.setVisibility(View.VISIBLE);
        }else {
           textView.setVisibility(View.GONE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.setCancelable(false);

        dialog.show();
        //basicFunction.write(this,"rate","ready");



    }


    int SMSCODE =1001;
    int SYSTEM_ALERT_WINDOW_PERMISSION =1000;




    private void askAllPermission(){

        if(type.equals("sms")){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(AddScdul.this);
                    builder.setTitle("We need SMS permission");

                    builder.setMessage("In order to support SMS automation functionality enable SMS permission")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                                            SMSCODE);
                                    return;

                                }
                            })
                            .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            }




        //    asktelephonePermission();





        }

        if(type.equals("reminder")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(getApplication())) {
                    final BottomSheetDialog dialog = new BottomSheetDialog(this);
                    dialog.setContentView(R.layout.bottom_dialog);
                    TextView title = dialog.findViewById(R.id.title);
                    TextView content = dialog.findViewById(R.id.content);
                    Button ok = dialog.findViewById(R.id.ok);
                    Button cancel = dialog.findViewById(R.id.cancel);

                    title.setText("Need floating window permission");
                    content.setText("In order to show reminder over other apps this app need floating window permission");

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            askOverlayPermission();
                            dialog.dismiss();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            Toast.makeText(mContext, "Permission not granted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                    dialog.setCancelable(false);
                    dialog.show();

                }
            }


    }
    }

   private void asktelephonePermission(){

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1002);
        } else {
            //TODO
        }
    }

    private void askSmsPermission(){

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1001);
        } else {
            //TODO
        }
    }


    private void askOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent,SYSTEM_ALERT_WINDOW_PERMISSION );
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case 1001: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(mContext, "Permission granted", Toast.LENGTH_SHORT).show();
                //    asktelephonePermission();

                } else {
                    Toast.makeText(mContext, "Permission not granted", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            case 1002: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(mContext, "Permission granted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(mContext, "Permission not granted", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }


        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("App", "OnActivity Result.");

        if (requestCode == SYSTEM_ALERT_WINDOW_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(mContext, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Permission not granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

        if (requestCode == 0 && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = this.getContentResolver().query(contactUri, projection,
                    null, null, null);

            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberIndex);

                numberTv.setText(number);

            }

            cursor.close();
        }
    }


        Dialog dialog;
        public void showCaptions () {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.caption_view);
            RecyclerView recyclerView = dialog.findViewById(R.id.cationView);
            final Adapter adapter = new Adapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            String[] types = { "Friend","Mom","Dad","Son","Daughter","Girl friend","Boy friend","Brother","Sister","Husband","Wife","Teacher","Student","Funny"};

            Spinner spin = dialog.findViewById(R.id.spinner);
            ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,types);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(aa);

            final Captions captions = new Captions();

            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (i){
                        case 0:
                            adapter.setList(captions.getFriend());
                            adapter.notifyDataSetChanged();
                            break;

                        case 1:
                            adapter.setList(captions.getMom());
                            adapter.notifyDataSetChanged();
                            break;
                        case 2:
                            adapter.setList(captions.getDad());
                            adapter.notifyDataSetChanged();
                            break;
                        case 3:
                            adapter.setList(captions.getSon());
                            adapter.notifyDataSetChanged();
                            break;
                        case 4:
                            adapter.setList(captions.getDaughter());
                            adapter.notifyDataSetChanged();
                            break;
                        case 5:
                            adapter.setList(captions.getGirlfriend());
                            adapter.notifyDataSetChanged();
                            break;
                        case 6:
                            adapter.setList(captions.getBoyfriend());
                            adapter.notifyDataSetChanged();
                            break;
                        case 7:
                            adapter.setList(captions.getBrother());
                            adapter.notifyDataSetChanged();
                            break;
                        case 8:
                            adapter.setList(captions.getSister());
                            adapter.notifyDataSetChanged();
                            break;
                        case 9:
                            adapter.setList(captions.getHusband());
                            adapter.notifyDataSetChanged();
                            break;
                        case 10:
                            adapter.setList(captions.getWife());
                            adapter.notifyDataSetChanged();
                            break;
                        case 11:
                            adapter.setList(captions.getTeacher());
                            adapter.notifyDataSetChanged();
                            break;
                        case 12:
                            adapter.setList(captions.getStudent());
                            adapter.notifyDataSetChanged();
                            break;
                        case 13:
                            adapter.setList(captions.getFunny());
                            adapter.notifyDataSetChanged();
                            break;

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            dialog.show();


        }


        public void showTimePicker () {

            TextView ok, cancel;
            final TimePicker timePicker;

            mydialog.setContentView(R.layout.time_picker);

            timePicker = mydialog.findViewById(R.id.time_picker);
            ok = mydialog.findViewById(R.id.time_picker_ok);
            cancel = mydialog.findViewById(R.id.time_picker_cancel);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        setTime( timePicker.getHour(), timePicker.getMinute());
                        Log.i("time", timePicker.getHour()+" "+ timePicker.getMinute());

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

        public void showDatePicker () {

            TextView ok, cancel;
            final DatePicker datePicker;

            mydialog.setContentView(R.layout.date_picker);

            datePicker = mydialog.findViewById(R.id.date_picker);
            ok = mydialog.findViewById(R.id.date_picker_ok);
            cancel = mydialog.findViewById(R.id.date_picker_cancel);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setDate(datePicker.getDayOfMonth() , datePicker.getMonth() ,datePicker.getYear());
                    year = datePicker.getYear();
                    month = datePicker.getMonth();
                    day = datePicker.getDayOfMonth();
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


        public void setName (String n){
            nameTv.setText(n);
        }
        public void setTime (int h, int m){

            hour = h;
            min = m;

            timeTv.setText(basicFunction.getTimeByAMPM(h,m));
        }

        public void setDate (int day,int mount,int year){
            int m= mount+1;
            dateTv.setText(day+" / "+m+" / "+year);
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            if (item.getItemId() == android.R.id.home) {
                finish();
            }
            return super.onOptionsItemSelected(item);
        }


        class ReadData implements Callable<String> {

            Context context;

            public ReadData(Context context) {
                this.context = context;
            }

            @Override
            public String call() throws Exception {


                String readString = "";
                FileInputStream fin = null;
                try {
                    fin = context.openFileInput("data.txt");
                } catch (FileNotFoundException e) {
                    FileOutputStream fos = context.openFileOutput("data.txt", MODE_PRIVATE);
                    fin = context.openFileInput("data.txt");
                }
                InputStreamReader inputStreamReader = new InputStreamReader(fin);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder(readString);
                while ((readString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(readString);
                }

                inputStreamReader.close();

                return stringBuilder.toString();
            }
        }

        public void write (String id, String type, String message, String toName, String
        time, String date, String toId, String millis) throws FileNotFoundException, IOException {

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<String> data = executorService.submit(new ReadData(getApplicationContext()));

            String pre = "";
            try {
                pre = data.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            FileOutputStream fos = openFileOutput("data.txt", MODE_PRIVATE);

            OutputStreamWriter osw = new OutputStreamWriter(fos);


            osw.append(pre);
            osw.append("<start>");
            osw.append("<id>" + id + "</id>");
            osw.append("<type>" + type + "</type>");
            osw.append("<status>" + "pending" + "</status>");
            osw.append("<message>" + message + "</message>");
            osw.append("<toName>" + toName + "</toName>");
            osw.append("<time>" + basicFunction.getTimeByAMPM(hour,min) + "</time>");
            osw.append("<date>" + date + "</date>");
            osw.append("<toId>" + toId + "</toId>");
            osw.append("<millis>" + millis + "</millis>");
            osw.append("<number>" + number + "</number>");
            osw.append("<end>");
            osw.flush();
            osw.close();


        }


        int i;

        public  class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

            List<String> list = new Captions().getFriend();

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.caption_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

                holder.caption.setText(list.get(position));

                holder.caption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setText(list.get(position));
                        dialog.dismiss();
                    }
                });

            }


            @Override
            public int getItemCount() {
                try {
                    return list.size();
                } catch (Exception e) {
                    return 0;
                }
            }

            public class ViewHolder extends RecyclerView.ViewHolder {
                TextView caption;

                public ViewHolder(@NonNull View itemView) {
                    super(itemView);
                    caption = itemView.findViewById(R.id.text);

                }
            }

            public void setList(List<String> list) {
                this.list = list;
            }
        }


        private void setText (String s){
            message.setText(s);
        }


    }


