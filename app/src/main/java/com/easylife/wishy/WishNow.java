package com.easylife.wishy;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WishNow extends AppCompatActivity {

    BasicFunction basicFunction;
    Handler handler;
    RecyclerView recyclerView;
    Context context ;
    ImageView imageView;
    Bitmap bitmap ;
    Toolbar toolbar;
    String path;
    Uri uri;
    EditText editText;
    Button button;


    String id;

    LoadingDialog loadingDialog;
    int z=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_now);

        context=this;
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Wish now");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = findViewById(R.id.edittext);
        button = findViewById(R.id.sbutton);
        editText.setText("Happy birthday");

        id= getIntent().getStringExtra("id");
        basicFunction = new BasicFunction();
        handler = new Handler(getMainLooper());
        imageView = findViewById(R.id.image);
        loadingDialog= new LoadingDialog(this);

        setPic.start();

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
                Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show();
            }
        });




    }



    Thread setPic = new Thread(new Runnable() {
        @Override
        public void run() {
            try {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.showDialog();
                    }
                });
                String page = basicFunction.getSourceCode("https://mbasic.facebook.com/photo/view_full_size/?fbid="+id);
                int a = page.indexOf("https://scontent");
                int b= page.indexOf("\"",a);
                String url = page.substring(a,b);
                url = url.replaceAll(";","&");
                final String finalUrl = url;

                URL u = new URL(url);
                bitmap =  BitmapFactory.decodeStream(u.openConnection().getInputStream());

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                uri=saveImageExternal(bitmap);


                float h = bitmap.getHeight();
                float w =bitmap.getWidth();

                Log.i("Display",h+" "+w);

                float ratio= h/w;
                Log.i("Photo",ratio+" ");

                final int w1 = basicFunction.getDisplayWidth(context);
                final float h1 =  w1*ratio;

                Log.i("Photo",h1+" "+w1);


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(w1, (int) h1);
                        imageView.setLayoutParams(parms);
                        Picasso.with(context).load(finalUrl).into(imageView);
                        loadingDialog.dismissDialog();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();

                Log.e("error",e.toString());
            }




        }
    });

    public boolean isStoragePermissionGranted() {
        String TAG = "Storage Permission";
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    private Uri saveImageExternal(Bitmap image) {
        Uri uri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "to-share.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.close();
            Log.i("file",file.toString());
            uri = Uri.fromFile(file);
        } catch (IOException e) {
            Log.d("h", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    void shareBitmap(Uri uri){
        z=1;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, editText.getText().toString());
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("*/*");
        startActivity(intent);

    }





    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1000);

        }

        if(z==1){
            new RateDialog(this).showDialog();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();

            } else {
                finish();
                Toast.makeText(context, "Permission not " +
                        "granted", Toast.LENGTH_SHORT).show();
            }
        }
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


                shareBitmap(uri);

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
