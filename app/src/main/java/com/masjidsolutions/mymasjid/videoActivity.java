

package com.masjidsolutions.mymasjid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
import com.pedro.vlc.VlcListener;
import com.pedro.vlc.VlcVideoLibrary;
import java.util.Arrays;

public class videoActivity extends AppCompatActivity implements VlcListener, View.OnClickListener {


    Button playButton;
    public String videoRespUrl="";
    VlcVideoLibrary vlcVideoLibrary;

    private String[] options;

    {
        options = new String[]{"Landscape:fullscreen"};
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent videoIntent=getIntent();
        MasjidInfo masjidObject = (MasjidInfo)videoIntent.getSerializableExtra( "masjidObject" );
        videoRespUrl=masjidObject.getVideourl();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_video);

        SurfaceView surfaceView = this.findViewById(R.id.VideoView);
        @SuppressLint("CutPasteId") VideoView videoView = this.findViewById(R.id.VideoView);

        playButton = this.findViewById(R.id.playButton);
        playButton.setOnClickListener(this);
        vlcVideoLibrary = new VlcVideoLibrary(this, this, surfaceView);
        vlcVideoLibrary.setOptions(Arrays.asList(options));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(1, 1);
            }
        });




    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (!vlcVideoLibrary.isPlaying()) {
            vlcVideoLibrary.play(videoRespUrl);
            playButton.setText("Stop");
        } else {
            vlcVideoLibrary.stop();
            playButton.setText( "Play" );
        }
    }

    @Override
    public void onComplete() {
        Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onError() {
        Toast.makeText(this, "Error, make sure your endpoint is correct", Toast.LENGTH_SHORT).show();
        vlcVideoLibrary.stop();
        playButton.setText("Start");

    }
}
