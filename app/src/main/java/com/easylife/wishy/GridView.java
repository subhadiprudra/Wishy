package com.easylife.wishy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class GridView extends AppCompatActivity {
    BasicFunction basicFunction;
    Handler handler;
    List <String> photosId;
    Context context;
    Adapter adapter;
    RecyclerView recyclerView;
    Boolean stop=false;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_images);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select a photo to wish");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        basicFunction = new BasicFunction();
        handler = new Handler(getMainLooper());
        photosId= new ArrayList<String>();
        context = this;

        adapter = new Adapter();
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setAdapter(adapter);

        new GetPhotos(getIntent().getStringExtra("toId")).execute();




    }

    class GetPhotos extends AsyncTask {

        String USerId;

        public GetPhotos(String USerId) {
            this.USerId = USerId;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            String e = null;

            int x=0;
            int no=600;

            for(int i=0;i<no;i=i+12) {
                String UserId=this.USerId;
                Document document =basicFunction.page("https://mbasic.facebook.com/"+UserId+"/photoset/pb."+UserId+"?owner_id="+UserId+"&offset="+i);
                String page= document.toString();

                while (page.contains("fbid=")){

                    if(stop){
                        break;
                    }

                    int a = page.indexOf("fbid=")+5;
                    int b= page.indexOf("&",a);
                    String fbid = page.substring(a,b);

                    a= page.indexOf("https://scontent");
                    b= page.indexOf("\"",a);
                    String url = page.substring(a,b);
                    url = url.replaceAll(";","&");


                    final String finalUrl = url;
                    final String finalE = fbid;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Model model = new Model(finalE,finalUrl);
                            adapter.addItem(model);
                            adapter.notifyDataSetChanged();

                        }
                    });

                    page= page.substring(b);
                    x++;

                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                if(stop){
                    break;
                }
            }

            return null;
        }
    }



    class GetPhoto extends AsyncTask {

        String USerId;

        public GetPhoto(String USerId) {
            this.USerId = USerId;
        }



        @Override
        protected Object doInBackground(Object[] objects) {

            String e = null;

            int x=0;
            int no=48;

            for(int i=0;i<no;i=i+12) {
                String UserId=this.USerId;
                Elements elements =basicFunction.page("https://mbasic.facebook.com/"+UserId+"/photoset/pb."+UserId+"?owner_id="+UserId+"&offset="+i).getElementsByClass("z ba bb");

                for(Element el : elements) {

                    e= el.toString().substring(el.toString().indexOf("fbid=")+5,el.toString().indexOf("&amp"));

                    String page = basicFunction.getSourceCode("https://mbasic.facebook.com/photo.php?fbid="+e);
                    int a= page.indexOf("https://scontent");
                    int b= page.indexOf("\"",a);
                    String url = page.substring(a,b);
                    url = url.replaceAll(";","&");


                    final String finalUrl = url;
                    final String finalE = e;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            Model model = new Model(finalE,finalUrl);
                            adapter.addItem(model);
                            adapter.notifyDataSetChanged();


                        }
                    });
                    x++;

                    if(stop){
                        break;
                    }
                }


                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

            }


            return null;
        }
    }



    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

        List<Model> list = new ArrayList<Model>();


        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(context).inflate(R.layout.image_item,parent,false);
            return new Adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, final int position) {

            Picasso.with(context).load(list.get(position).url).into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,WishNow.class);
                    intent.putExtra("id",list.get(position).photoId);
                    context.startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            try {
                return list.size();
            }catch (Exception e){
                return 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image);
                int w = basicFunction.getDisplayWidth(context);
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(w/3,w/3);
                imageView.setLayoutParams(parms);
            }
        }

        public void addItem(Model item) {
            list.add(item);
        }
    }

    class Model{
        String photoId,url;

        public Model(String photoId, String url) {
            this.photoId = photoId;
            this.url = url;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stop=true;
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