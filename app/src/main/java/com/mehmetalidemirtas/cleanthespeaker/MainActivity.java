package com.mehmetalidemirtas.cleanthespeaker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdRequest;


public class MainActivity extends AppCompatActivity {
    MediaPlayer music=null;
    private  long backPressedTime;
    private  Toast backToast;
    private int inLayout = 1;
    private ProgressBar progressBar;
    TextView timeText;
    private double currentProgress = 0.0;
    private int a = 0;
    CountDownTimer countDownTimer;
    Dialog dialog;
    Button btnInfo;
    private Vibrator vibrator;
    private AdView mAdView1,mAdView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnInfo = findViewById(R.id.btninfo);
        dialog = new Dialog(this);
        vibrator=(Vibrator) getSystemService(VIBRATOR_SERVICE);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView1 = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        if(inLayout==2){
            stop();
        }else
        {
            if(backPressedTime + 2000 > System.currentTimeMillis()){
                backToast.cancel();
                super.onBackPressed();
                finish();
                System.exit(0);
                return;
            }else{
                backToast = Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();
        }
    }

    public void startMusicButton(View view) {
        inLayout = 2;
        setContentView(R.layout.activity_eject);
        mAdView2 = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest);
        progressBar =findViewById(R.id.progressBar);
        timeText = findViewById(R.id.textView2);

        countDownTimer = new CountDownTimer(180*1000,1000) {
            @Override
            public void onTick(long l) {
                currentProgress = currentProgress + 0.556;
                a = (int) currentProgress;
                progressBar.setProgress(a);
                progressBar.setMax(100);
                timeText.setText(a +"%");
            }
            @Override
            public void onFinish() {
                setContentView(R.layout.activity_succesful);
                music.pause();
                vibrator.cancel();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stop();
                    }
                },3000);
            }
        };

        music=MediaPlayer.create(MainActivity.this,R.raw.waves);
        countDownTimer.start();
        if(music!=null)
        {
            music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    music.stop();
                    vibrator.cancel();
                    music.release();
                    music=null;
                    SystemClock.sleep(100);
                }
            });
            music.start();
            long[] pattern ={20,200,30,300,40,400,50,500,60,600,70,700};
            vibrator.vibrate(pattern,0);
        }
    }

    public void stopMusic(View view) {
        stop();
    }
    public void stop(){
        vibrator.cancel();
        music.pause();
        music.stop();
        currentProgress = 0.0;
        inLayout = 1;
        setContentView(R.layout.activity_main);
        countDownTimer.cancel();
        mAdView1 = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest);
    }

    public void rate(View view)
    {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+"com.mehmetalidemirtas.cleanthespeaker")));
    }
    public void info(View view) {
        dialog.setContentView(R.layout.popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button closeDialogButton = dialog.findViewById(R.id.btnclose);
        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}