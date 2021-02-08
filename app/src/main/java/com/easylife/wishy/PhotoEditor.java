package com.easylife.wishy;

import androidx.appcompat.app.AppCompatActivity;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoEditor extends AppCompatActivity {

    String id;
    Context context;
    PhotoEditorView mPhotoEditorView;
    Bitmap bitmap;
    Handler handler;
    LoadingDialog loadingDialog;
    BasicFunction basicFunction;
    ja.burhanrashid52.photoeditor.PhotoEditor mPhotoEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);
        context=this;
        mPhotoEditorView = findViewById(R.id.photoEditorView);
        id= getIntent().getStringExtra("id");
        handler= new Handler(getMainLooper());
        loadingDialog = new LoadingDialog(this);
        basicFunction= new BasicFunction();
        mPhotoEditor = new ja.burhanrashid52.photoeditor.PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
               // .setDefaultTextTypeface(mTextRobotoTf)
               // .setDefaultEmojiTypeface(mEmojiTypeFace)
                .build();
        setPic.start();


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
                        mPhotoEditorView.setLayoutParams(parms);
                        mPhotoEditorView.getSource().setImageBitmap(bitmap);
                        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.raw.sticker1);
                        Bitmap bm1 = BitmapFactory.decodeResource(getResources(), R.raw.sticker2);
                        mPhotoEditor.addImage(bm);
                        mPhotoEditor.addImage(bm1);
                        loadingDialog.dismissDialog();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();

                Log.e("error",e.toString());
            }




        }
    });
}