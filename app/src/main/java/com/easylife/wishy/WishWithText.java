package com.easylife.wishy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class WishWithText extends AppCompatActivity {

    Toolbar toolbar;
    EditText editText;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_with_text);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Wish now");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = findViewById(R.id.edittext);
        button = findViewById(R.id.sbutton);

        editText.setText("Happy birthday");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCaptions();

            }
        });

        ImageView copy = findViewById(R.id.copy);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", editText.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Text copied", Toast.LENGTH_SHORT).show();
            }
        });






    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id){


            case (R.id.send) :
                /*Create an ACTION_SEND Intent*/
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT,editText.getText().toString());
                intent.putExtra(android.content.Intent.EXTRA_TEXT, editText.getText().toString());
                startActivity(Intent.createChooser(intent,editText.getText().toString()));
                break;


            case (android.R.id.home):
                finish();



        }

        return super.onOptionsItemSelected(item);
    }




    Dialog dialog;
    Adapter adapter;
    public void showCaptions () {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.caption_view);
        RecyclerView recyclerView = dialog.findViewById(R.id.cationView);
        adapter =new Adapter();
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





    int i;

    public  class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        List<String> list = new Captions().getFriend();

        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.caption_item, parent, false);
            return new Adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, final int position) {

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

        editText.setText(s);
    }




}