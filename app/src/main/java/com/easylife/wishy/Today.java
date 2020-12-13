package com.easylife.wishy;

import android.annotation.SuppressLint;
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
public class Today extends Fragment {

    RecyclerView recyclerView;
    BasicFunction basicFunction;
    List<Model> itemList;
    Adapter adapter;
    LinearLayout emptyView;

    String d;


    public Today(String data) {
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


        adapter= new Adapter(itemList,getContext(),"Today");

        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        makeList();




        return view;

    }

    private void makeList() {

        String data = d;
        String e="";


        try {
            int a= data.indexOf("Today's Birthdays");
            int b= data.indexOf("</article>");

            e = data.substring(a, b);
        }catch (Exception x){

        }
        while(true) {


            try {


                int g = e.indexOf("https://scontent") ;
                int f = e.indexOf("\"", g);
                String dpUrl = e.substring(g, f);
                dpUrl = dpUrl.replaceAll(";", "&");
                Log.i("dp_url",dpUrl);

                g = e.indexOf("alt=", f) + 5;
                f = e.indexOf(",", g);
                String name = e.substring(g, f);

                int j = e.indexOf("style=\"display:none\"");
                g = e.indexOf("href", j) + 7;
                f = e.indexOf("\">", g);
                String profileUrl = e.substring(g, f);
                Log.i("profile url",profileUrl);


                e = e.substring(f);

                if (!dpUrl.contains("https")) {
                    break;
                }

                if(profileUrl.contains(" ")){
                    continue;
                }

                if(name.contains("</") ||  name.contains("class=") || name.contains("<span>")){
                    continue;
                }


                Model model = new Model(name,dpUrl,profileUrl,"");
                itemList.add(model);


            } catch (Exception x) {
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
