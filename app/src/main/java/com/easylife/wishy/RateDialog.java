package com.easylife.wishy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import static com.easylife.wishy.R.drawable.ic_baseline_star_24;
import static com.easylife.wishy.R.drawable.ic_baseline_star_border_24;

public class RateDialog {

    Context context;
    ImageView star1,star2,star3,star4,star5;
    Button rate,notNow;
    int count=0;
    BasicFunction basicFunction;

    public RateDialog(Context context) {
        this.context = context;
        basicFunction = new BasicFunction();
    }

    public void showDialog(){
        Log.i("value",basicFunction.read(context,"nf"));
        if(!basicFunction.read(context,"rate").equals("done")) {
            show();
        }
    }

    private void show(){

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.rate_dialog);

        star1= bottomSheetDialog.findViewById(R.id.star1);
        star2= bottomSheetDialog.findViewById(R.id.star2);
        star3= bottomSheetDialog.findViewById(R.id.star3);
        star4= bottomSheetDialog.findViewById(R.id.star4);
        star5= bottomSheetDialog.findViewById(R.id.star5);
        rate= bottomSheetDialog.findViewById(R.id.rate_btn);
        notNow= bottomSheetDialog.findViewById(R.id.not_now_btn);

        rate.setEnabled(false);
        rate.setTextColor(Color.GRAY);


        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(R.layout.bottom_dialog);
        final TextView title = dialog.findViewById(R.id.title);
        final TextView content = dialog.findViewById(R.id.content);
        final Button ok = dialog.findViewById(R.id.ok);
        final Button cancel = dialog.findViewById(R.id.cancel);

        cancel.setVisibility(View.GONE);



        star1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                star1.setImageResource(ic_baseline_star_24);
                star2.setImageResource(ic_baseline_star_border_24);
                star3.setImageResource(ic_baseline_star_border_24);
                star4.setImageResource(ic_baseline_star_border_24);
                star5.setImageResource(ic_baseline_star_border_24);
                rate.setEnabled(true);
                rate.setTextColor(ContextCompat.getColor(context, R.color.green));

                count=1;


            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(ic_baseline_star_24);
                star2.setImageResource(ic_baseline_star_24);
                star3.setImageResource(ic_baseline_star_border_24);
                star4.setImageResource(ic_baseline_star_border_24);
                star5.setImageResource(ic_baseline_star_border_24);
                rate.setEnabled(true);
                rate.setTextColor(ContextCompat.getColor(context, R.color.green));

                count=2;
            }
        });

        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(ic_baseline_star_24);
                star2.setImageResource(ic_baseline_star_24);
                star3.setImageResource(ic_baseline_star_24);
                star4.setImageResource(ic_baseline_star_border_24);
                star5.setImageResource(ic_baseline_star_border_24);
                rate.setEnabled(true);
                rate.setTextColor(ContextCompat.getColor(context, R.color.green));

                count=3;
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                star1.setImageResource(ic_baseline_star_24);
                star2.setImageResource(ic_baseline_star_24);
                star3.setImageResource(ic_baseline_star_24);
                star4.setImageResource(ic_baseline_star_24);
                star5.setImageResource(ic_baseline_star_border_24);
                rate.setEnabled(true);
                rate.setTextColor(ContextCompat.getColor(context, R.color.green));

                count=4;
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(ic_baseline_star_24);
                star2.setImageResource(ic_baseline_star_24);
                star3.setImageResource(ic_baseline_star_24);
                star4.setImageResource(ic_baseline_star_24);
                star5.setImageResource(ic_baseline_star_24);
                rate.setEnabled(true);
                rate.setTextColor(ContextCompat.getColor(context, R.color.green));

                count=5;
            }
        });


        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(count>2){
                    title.setText("Show some love");
                    content.setText("Please rate us on playstore. Your honest opinion will help others to find this app");

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }else {
                    title.setText("Feedback");
                    content.setText("Will you let us know why you don't like this app? your honest opinion will help us to improve this app.");

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent email= new Intent(Intent.ACTION_VIEW);
                            email.setType("message/rfc822")
                                    .setData(Uri.parse("mailto:wishy.bugreports@gmail.com"))
                                    .putExtra(Intent.EXTRA_SUBJECT, "Bugs")
                                    .setPackage("com.google.android.gm");
                            context.startActivity(email);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }

                bottomSheetDialog.dismiss();

                basicFunction.write(context,"rate","done");
            }
        });

        notNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        if(!bottomSheetDialog.isShowing()) {
            bottomSheetDialog.show();
        }


    }

}
