package com.easylife.wishy;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


@SuppressLint("ValidFragment")
public class Upcoming extends Fragment {


    RecyclerView recyclerView;
    BasicFunction basicFunction;
    List<Model> itemList;
    Adapter adapter;

    String d;

    LinearLayout emptyView;

    @SuppressLint("ValidFragment")
    public Upcoming(String data) {
        this.d = data;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bd_view, container, false);
        recyclerView = view.findViewById(R.id.bd_re);
        basicFunction=new BasicFunction();
        itemList= new ArrayList<>();

        emptyView = view.findViewById(R.id.empty_view);

        adapter= new Adapter(itemList,getContext(),"Upcoming");

        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        makeList();




        return view;

    }

    private void makeList() {

        String data =d;
        try {

            int c= data.indexOf("Upcoming Birthdays");
            data= data.substring(c);
        }catch (Exception e){}



        while(true) {

            try {

                int a = data.indexOf("<li");
                int b = data.indexOf("</li>", a);
                String e = data.substring(a, b);

                int g = e.indexOf("https://scontent") ;
                int f = e.indexOf("\"", g);
                String dpUrl = e.substring(g, f);
                dpUrl = dpUrl.replaceAll(";", "&");

                g = e.indexOf("alt=") +5;
                f = e.indexOf(",", g);
                String name = e.substring(g, f);

                g = e.indexOf("<p class=",f) +23;
                f = e.indexOf("<", g);
                String time = e.substring(g, f);



                g = e.indexOf("a href=", a) + 9;
                f = e.indexOf("\"", g);
                String profileUrl = e.substring(g, f);



                data = data.substring(b);

                Log.i("time",time);
                if (time.contains("Tomorrow")) {
                    continue;
                }

                Model model = new Model(name, dpUrl, profileUrl,time);
                itemList.add(model);


            }catch (Exception e1){
                break;
            }

        }
        if(itemList==null || itemList.size()==0){
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        adapter.setItemlist(itemList);
        adapter.notifyDataSetChanged();
    }





}
