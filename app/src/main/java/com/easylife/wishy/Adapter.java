package com.easylife.wishy;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.easylife.wishy.R.layout.choose_wish_type;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<Model> itemlist;
    Context mContext;
    BottomSheetDialog dialog;
    LinearLayout msgbtn,postbtn,sms,remdr,withp,witht;
    int p=0;
    BasicFunction basicFunction;
    String type;
   // ProgressDialog progressDialog;
    String from;
    BottomSheetDialog dialog1;



    public Adapter(List<Model> itemlist, Context context, String from) {
        this.itemlist = itemlist;
        this.mContext = context;
        this.from=from;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.birthday_item,parent,false);
        dialog= new BottomSheetDialog(mContext);
        dialog.setContentView(R.layout.choose_scheduler_type);


        msgbtn=dialog.findViewById(R.id.msg_btn);
        postbtn=dialog.findViewById(R.id.post_btn);
        sms=dialog.findViewById(R.id.sms_btn);
        remdr=dialog.findViewById(R.id.rmdr_btn);
        basicFunction= new BasicFunction();

        dialog1= new BottomSheetDialog(mContext);
        dialog1.setContentView(choose_wish_type);

        withp =dialog1.findViewById(R.id.image_btn);
        witht = dialog1.findViewById(R.id.text_bt);

        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.name.setText(itemlist.get(position).name);
        holder.info.setText("Tomorrow is "+itemlist.get(position).name+"'s birthday");

        if(from.equals("Today")){
            holder.info.setText("Today is "+itemlist.get(position).name+"'s birthday");
        }

        Picasso.with(mContext).load(itemlist.get(position).upUrl).into(holder.dp);

        if(from.equals("Today")){
            holder.setSch.setVisibility(View.GONE);
        }else if(from.equals("Tomorrow")){
            holder.nowbtn.setVisibility(View.GONE);
        }else {
            holder.setSch.setVisibility(View.GONE);
            holder.nowbtn.setVisibility(View.GONE);
            holder.info.setText("Birthday on "+itemlist.get(position).time);
        }


        holder.setSch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p=position;
                try {
                    dialog.show();
                }catch (Exception e){

                }
            }
        });
        holder.nowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="now";
                p=position;
                dialog1.show();
            }
        });

        witht.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="nowt";
                new GetId(itemlist.get(p).profileUrl,itemlist.get(p).name,type).execute();

            }
        });

        withp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="nowp";
                new GetId(itemlist.get(p).profileUrl,itemlist.get(p).name,type).execute();

            }
        });

        msgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type="message";
                new GetId(itemlist.get(p).profileUrl,itemlist.get(p).name,type).execute();
            }
        });
        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type="post";
                new GetId(itemlist.get(p).profileUrl,itemlist.get(p).name,type).execute();
            }
        });

        remdr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="reminder";
                new GetId(itemlist.get(p).profileUrl,itemlist.get(p).name,type).execute();
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,AddScdul.class);
                intent.putExtra("type","sms");
                intent.putExtra("name",itemlist.get(p).name);
                mContext.startActivity(intent);
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDp(itemlist.get(position).upUrl,itemlist.get(position).name,itemlist.get(position).time);
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

        CircleImageView dp;
        TextView name,info;
        LinearLayout setSch,nowbtn,layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dp=itemView.findViewById(R.id.dp);
            name=itemView.findViewById(R.id.name);
            info=itemView.findViewById(R.id.info);
            setSch=itemView.findViewById(R.id.tomorrowbtn);
            nowbtn=itemView.findViewById(R.id.todaybtn);
            layout= itemView.findViewById(R.id.linerlayout);
        }
    }

    public void setItemlist(List<Model> itemlist) {
        this.itemlist = itemlist;
    }

    class GetId extends AsyncTask {

        String url,name;

        String type;
        String data;

      //  ProgressDialog pdialog;

        LoadingDialog loadingDialog;


        public GetId(String url, String name,String type) {
            this.url = url;
            this.name = name;
            this.type=type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                dialog.dismiss();
                dialog1.dismiss();
            }catch (Exception e){

            }

            loadingDialog= new LoadingDialog(mContext);
            loadingDialog.showDialog();

        /*    pdialog= new ProgressDialog(mContext);
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdialog.setTitle("Initialising...");
            pdialog.setMessage("Please wait...");
            pdialog.setIndeterminate(true);
            pdialog.setCanceledOnTouchOutside(false);
            pdialog.show();*/


        }

        @Override
        protected Object doInBackground(Object[] objects) {

            data = basicFunction.getSourceCode("https://mbasic.facebook.com/"+url);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

//            Log.e("xxxxxxxxxxxx",data);

            int a = data.indexOf("thread")+7;
            int b= data.indexOf("/",a);
            String id = data.substring(a,b);

            Intent intent = new Intent(mContext,AddScdul.class);
            if(type.equals("nowp")){
                intent = new Intent(mContext,GridView.class);
            }else if(type.equals("nowt")){
                intent = new Intent(mContext,WishWithText.class);
            }


            intent.putExtra("type",type);
            intent.putExtra("toId",id);
            intent.putExtra("name",name);
            intent.putExtra("from","adapter");
            mContext.startActivity(intent);

            loadingDialog.dismissDialog();


        }
    }

    String dpUrl="";
    Handler handler;

    public void showDp(final String url, String name, String date){

        Dialog dialog= new Dialog(mContext);
        dialog.setContentView(R.layout.dp_view);
        TextView nameTv = dialog.findViewById(R.id.name);
        TextView dateTv= dialog.findViewById(R.id.textView);
        final ImageView dp = dialog.findViewById(R.id.dpfull);
        final ProgressBar progressBar = dialog.findViewById(R.id.pr);
        progressBar.setVisibility(View.GONE);

        handler= new Handler(mContext.getMainLooper());



        nameTv.setText(name);

        if(from.equals("Today")){
            dateTv.setText("Today is "+name+"\'s birthday");
        }else if(from.equals("Tomorrow")){
            dateTv.setText("Tomorrow is "+name+"\'s birthday");
        }else {
            dateTv.setText("Birthday on "+date);
        }


        dialog.show();

      /*  Thread setDp = new Thread(new Runnable() {
            @Override
            public void run() {
                String data = basicFunction.getSourceCode("https://mbasic.facebook.com/"+url);
                int a = data.indexOf("thread")+7;
                int b= data.indexOf("/",a);
                String id = data.substring(a,b);

                String url = "https://mbasic.facebook.com/profile/picture/view/?profile_id="+id;
                data= basicFunction.getSourceCode(url);



                a = data.indexOf("https://scontent");
                b= data.indexOf("\"",a);

                dpUrl = data.substring(a,b);
                dpUrl= dpUrl.replaceAll(";","&");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(mContext).load(dpUrl).into(dp);
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }
        });


        setDp.start();*/
        Picasso.with(mContext).load(url).into(dp);



    }



}
