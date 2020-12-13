package com.easylife.wishy;

import android.annotation.SuppressLint;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("ValidFragment")
public class Tomorrow extends Fragment {

    RecyclerView recyclerView;
    BasicFunction basicFunction;
    List<Model> itemList;
    Adapter adapter;

    String d;
    LinearLayout emptyView;


    public Tomorrow(String data) {

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

        adapter= new Adapter(itemList,getContext(),"Tomorrow");

        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        makeList();




        return view;

    }

    private void makeList() {

        String data = d;
        try {

            int c = data.indexOf("Upcoming Birthdays");
            data = data.substring(c);

            while (true) {

                if (!data.contains("Tomorrow")) {
                    break;
                }

                int a = data.indexOf("https://scontent");
                int b = data.indexOf("\"", a);
                String dpUrl = data.substring(a, b);
                dpUrl = dpUrl.replaceAll(";", "&");

                int g = data.indexOf("alt",b) + 5;
                int f = data.indexOf(",", g);
                String name = data.substring(g, f);

                g = data.indexOf("a href=", f) + 9;
                f = data.indexOf("\"", g);
                String profileUrl = data.substring(g, f);

                data = data.substring(f);

                if(name.contains("</") ||  name.contains("class=") || name.contains("<span>")){
                    continue;
                }


                Model model = new Model(name, dpUrl, profileUrl,"");
                itemList.add(model);

            }

            if(itemList==null || itemList.size()==0){
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

            adapter.setItemlist(itemList);
            adapter.notifyDataSetChanged();
        }catch (Exception e){
        }

    }


}
