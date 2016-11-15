package com.tbt;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by bradley on 21-08-2016.
 */
public class RadioActivity extends AppCompatActivity {

    MediaPlayer player;
    ImageView playpauseFAB;
    ProgressDialog progressDialog;
    final String radioURL = "http://streamasiahlscdn.atc-labs.com/bbdradioone.m3u8";
    boolean isPlaying = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radio_activity_layout);
        playpauseFAB = (ImageView) findViewById(R.id.play_pause_radio);
        playpauseFAB.setOnClickListener(new RadioButtonListener());
        progressDialog = new ProgressDialog(RadioActivity.this);
        progressDialog.setTitle("Waiting");
        progressDialog.setMessage("Please wait while the BBD 90.8 FM is being loaded");
        progressDialog.setCancelable(false);
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(RadioActivity.this, Uri.parse(radioURL));
            player.setOnPreparedListener(new RadioPreparedListener());
            player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                    progressDialog.show();
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    progressDialog.dismiss();
                }
            });
            player.prepareAsync();
            progressDialog.show();
        } catch (IOException e) {
            Log.e("TBT Error", e.getMessage());
            progressDialog.dismiss();
        }
    }

    void prepareRadioPlayer() {
        progressDialog.show();
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        player.stop();
    }

    class RadioPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            progressDialog.dismiss();
        }
    }

    class RadioButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if(isPlaying) {
                isPlaying = false;
                player.pause();
                playpauseFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
            } else {
                isPlaying = true;
                player.start();
                playpauseFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
            }
        }
    }
}
