package com.easylife.wishy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import static android.media.AudioManager.AUDIOFOCUS_REQUEST_DELAYED;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_FAILED;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;

public class ReminderView extends Service {

    private WindowManager mWindowManager;
    private View mFloatingWidget;

    String message,proId;
    TextView msg;
    int i=0;
    Button button,wishnow;

    class  ServiceBinder extends Binder {
        public ReminderView getService() {

            return ReminderView.this;
        }
    }

    private IBinder mBinder = new ServiceBinder();



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }





    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "wish reminder";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Wishy")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }


    @Override
    public void onStart(final Intent intent, int startId) {
        super.onStart(intent, startId);



        mFloatingWidget = LayoutInflater.from(this).inflate(R.layout.reminder_view, null);


        final WindowManager.LayoutParams params;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)

        {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);


        }

        else {

            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

        }

        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        params.x = 0;
        params.y = 0;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingWidget, params);

        msg = mFloatingWidget.findViewById(R.id.reminder_text);
        msg.setText(message);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.tone1);


        mediaPlayer.start();

        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        for(int x=0;x<20;x++){
            mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        }
        mAudioManager.requestAudioFocus(null,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);






        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(i<3) {
                    mediaPlayer.start();
                    i++;
                }
            }
        });

        button = mFloatingWidget.findViewById(R.id.dm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                stopForeground(true);
                stopSelf();

            }
        });

        wishnow = mFloatingWidget.findViewById(R.id.wn);
        wishnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("toId",proId);
                Intent intent1 = new Intent(getApplicationContext(),GridView.class);
                intent1.putExtra("toId",proId);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                mediaPlayer.stop();
                stopForeground(true);
                stopSelf();

            }
        });


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingWidget != null) {
            mWindowManager.removeView(mFloatingWidget);
            stopForeground(true);
        }
    }


    public void setVars(String message, String proId) {
        this.message = message;
        this.proId = proId;
        msg.setText(message);
        if(proId.equals("")){
            wishnow.setVisibility(View.GONE);
        }

        button.setWidth(700);

    }


}
