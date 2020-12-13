package com.easylife.wishy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ScList extends AppCompatActivity {

    Toolbar toolbar;
    Dialog mydialog;
    CardView floatingActionButton;
    TextView tdata;

    Adapter adapter;
    RecyclerView recyclerView;
    BasicFunction basicFunction;
    LinearLayout emptyView;
    RateDialog rateDialog;
    int i=0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sc_list);
        mydialog = new Dialog(this);
        floatingActionButton = findViewById(R.id.float_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();
            }
        });

        rateDialog = new RateDialog(this);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Scheduler list");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);






    }

    @Override
    protected void onResume() {
        super.onResume();

        basicFunction = new BasicFunction();

        String pre= basicFunction.readData(getApplicationContext(),"data.txt");
        Log.i("Data",pre);


        List<Model> list =getList(pre);
        recyclerView= findViewById(R.id.rcv);
        emptyView =findViewById(R.id.empty_view);



        adapter= new Adapter(this,list);
        if(list==null){
           recyclerView.setVisibility(View.GONE);
           emptyView.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            recyclerView.hasFixedSize();
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

        }



    }


    private void showDialog(){

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.custom_add);

        LinearLayout smsbtn = dialog.findViewById(R.id.sms_btn);
        LinearLayout rmdrbtn = dialog.findViewById(R.id.rmdr_btn);

        smsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddScdul.class);
                intent.putExtra("type","sms");
                intent.putExtra("name","Custom");
                intent.putExtra("toId","");
                intent.putExtra("from","customAdd");
                startActivity(intent);
                dialog.dismiss();
            }
        });
        rmdrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddScdul.class);
                intent.putExtra("type","reminder");
                intent.putExtra("name","Custom");
                intent.putExtra("toId","");
                intent.putExtra("from","customAdd");
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){ finish();}
        return super.onOptionsItemSelected(item);
    }







    public List<Model> getList(String dat){

        List<Model> list = new ArrayList<>();
        String data = dat;
        List<Model> list1 = new ArrayList<>();

        try {

            do {
                    String status = data.substring(data.indexOf("<status>") + 8, data.indexOf("</status>"));
                    String id = data.substring(data.indexOf("<id>") + 4, data.indexOf("</id>"));
                    String type = data.substring(data.indexOf("<type>") + 6, data.indexOf("</type>"));
                    String to = data.substring(data.indexOf("<toName>") + 8, data.indexOf("</toName>"));
                    String message = data.substring(data.indexOf("<message>") + 9, data.indexOf("</message>"));
                    String time = data.substring(data.indexOf("<time>") + 6, data.indexOf("</time>"));
                    String date = data.substring(data.indexOf("<date>") + 6, data.indexOf("</date>"));
                    String toId = data.substring(data.indexOf("<toId>") + 6, data.indexOf("</toId>"));
                    Model model = new Model(id, type, status, to, message, time, date, toId);
                    list.add(model);
                    int e = data.indexOf("<end>") + 4;
                    data = data.replace(data.substring(0, e), "");

                } while (data.contains("<end>"));


            for (int i = 0; i < list.size(); i++) {
                list1.add(list.get(list.size() - 1 - i));
            }



            return list1;



        }catch (Exception e){
            Log.e("Error",e.toString());

            return null;

        }




    }

    public class Model {

        public Model(String id,String type,String status, String person, String msg, String time, String date,String toId) {
            this.type = type;
            this.person = person;
            this.msg = msg;
            this.time = time;
            this.date = date;
            this.id=id;
            this.status=status;
            this.toId=toId;
        }

        String id,type,status,person,msg,time,date,toId;


    }


    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{


        private List<Model> itemlist ;
        private Context mContext;



        public Adapter(Context mcontext,List<Model> itemlist){
            this.itemlist=itemlist;
            this.mContext = mcontext;

        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(mContext).inflate(R.layout.sc_list_item,parent,false);
            return new  ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.type.setText(itemlist.get(position).type.toUpperCase());
            if(itemlist.get(position).type.equals("message")){
                holder.type.setText("FB "+itemlist.get(position).type.toUpperCase());
            }else if(itemlist.get(position).type.equals("post")){
                holder.type.setText("FB "+itemlist.get(position).type.toUpperCase());
            }
            holder.person.setText(itemlist.get(position).person);
            holder.msg.setText(itemlist.get(position).msg);
            holder.time.setText(itemlist.get(position).time);
            holder.date.setText(itemlist.get(position).date);


            if(itemlist.get(position).status.equals("pending")){
                holder.status.setImageResource(R.drawable.ic_baseline_access_time_blue_24);
            }if(itemlist.get(position).status.equals("done")){
                basicFunction.write(mContext,"rate","ready");
                holder.status.setImageResource(R.drawable.ic_baseline_done_all_24);
                if(i==0) {
                    rateDialog.showDialog();
                }
                i++;
            }if(itemlist.get(position).status.equals("error")){
                holder.status.setImageResource(R.drawable.ic_baseline_error_24);
            }

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {


                        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(mContext, Receiver.class);
                        intent.putExtra("id", itemlist.get(position).id);
                        intent.putExtra("name", itemlist.get(position).person);
                        intent.putExtra("message", itemlist.get(position).msg);
                        intent.putExtra("toId", itemlist.get(position).toId);
                        intent.putExtra("type", itemlist.get(position).type);


                        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, Integer.parseInt(itemlist.get(position).id), intent, 0);
                        alarmManager.cancel(pendingIntent);
                    }catch (Exception e){
                        Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    delete(itemlist.get(position).id);
                    itemlist.remove(position);
                    notifyDataSetChanged();

                    Toast.makeText(mContext, "Schedule cancelled successfully", Toast.LENGTH_SHORT).show();



                }
            });
        }

        @Override
        public int getItemCount() {

            try{
                return itemlist.size();}
            catch (Exception e){
                return 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView type,person,msg,time,date;
            ImageView delete;
            ImageView status;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                type=itemView.findViewById(R.id.sc_type);
                person=itemView.findViewById(R.id.sc_person);
                msg=itemView.findViewById(R.id.sc_msg);
                time=itemView.findViewById(R.id.sc_time);
                date=itemView.findViewById(R.id.sc_date);
                delete=itemView.findViewById(R.id.sc_cancel);
                status=itemView.findViewById(R.id.status);
            }


        }

        public void delete(String id){

            BasicFunction basicFunction = new BasicFunction();

            String data=basicFunction.readData(mContext,"data.txt");

            int a= data.indexOf(id)-11;
            int b= data.indexOf("<end>",a)+5;

            String f= data.substring(0,a);
            String g= data.substring(b);
            String newData= f+g;


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
                    fos = mContext.openFileOutput("data.txt", MODE_PRIVATE);
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

        public void setItemlist(List<Model> itemlist) {
            this.itemlist = itemlist;
        }
    }




}
