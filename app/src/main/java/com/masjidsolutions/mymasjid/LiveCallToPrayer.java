package com.masjidsolutions.mymasjid;

import  android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LiveCallToPrayer extends AppCompatActivity {


    private Button btn;
    public String audioRespUrl="";
    private boolean playPause;
    public MediaPlayer mediaPlayer;
    private ProgressDialog progressDialog;
    private boolean initialStage = true;
    private SimpleDateFormat dateFormat;
    private String date;
    Date todayDate = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    String todayStr = formatter.format(todayDate);
    ImageButton speakerImg = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent videoIntent=getIntent();
        MasjidInfo masjidObject = (MasjidInfo)videoIntent.getSerializableExtra( "masjidObject" );
        audioRespUrl=masjidObject.getAudiourl();


        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_live_call_to_prayer);
        btn = (Button) findViewById(R.id.audioStreamBtn);
        mediaPlayer = new MediaPlayer();
        Log.d("mediaplayer","mediaplayer created");
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog = new ProgressDialog(this);
        speakerImg = findViewById( R.id.speakerImage );

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.getCollapseIcon();
        UmmalquraCalendar cal = new UmmalquraCalendar();
        String day = String.valueOf(cal.get( Calendar.DAY_OF_MONTH));
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK);
        String year = String.valueOf(cal.get(Calendar.YEAR));


        toolbar.setSubtitle(day + " " + month + " " + year);
        toolbar.setSubtitleTextColor( Color.WHITE);
//        dateTimeDisplay = (TextView)findViewById(R.id.dateView);
        dateFormat = new SimpleDateFormat("EEEE dd MMMM ");
        String todayString = dateFormat.format(todayDate);
        getSupportActionBar().setTitle(todayString);

        ImageButton imgbutton = (ImageButton) findViewById(R.id.refreshButton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        Button button = (Button) findViewById(R.id.vidbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                startActivity(new Intent(getApplicationContext(),videoActivity.class));

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!playPause) {
                    btn.setText("Pause Live");
                    speakerImg.setBackgroundResource( R.drawable.volume_on_white_24dp );

                    if (initialStage) {
                        new Player().execute(audioRespUrl);
                    } else {
                        if (!mediaPlayer.isPlaying())
                            mediaPlayer.start();
                    }

                    playPause = true;

                } else {
                    btn.setText("Listen Live");
                    speakerImg.setBackgroundResource( R.drawable.volume_off_white_24dp );

                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }

                    playPause = false;
                }
            }
        });

        btn.setText("Pause Live");
        speakerImg.setBackgroundResource( R.drawable.volume_on_white_24dp );
        if (initialStage) {
            new Player().execute(audioRespUrl);
        } else {
            if (!mediaPlayer.isPlaying())
                mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    class Player extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean prepared = false;

            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        initialStage = true;
                        playPause = false;
                        btn.setText("Start Live");
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });

                mediaPlayer.prepare();
                prepared = true;

            } catch (Exception e) {
                Log.e("MyAudioStreamingApp", e.getMessage());
                prepared = false;
            }

            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }

            mediaPlayer.start();
            initialStage = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Buffering...");
            progressDialog.show();
        }
    }
}