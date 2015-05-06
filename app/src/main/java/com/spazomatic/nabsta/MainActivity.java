package com.spazomatic.nabsta;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;

import com.spazomatic.nabsta.controls.MasterPlayButton;
import com.spazomatic.nabsta.controls.PlayButton;
import com.spazomatic.nabsta.views.TrackVisualizerView;


public class MainActivity extends Activity {


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        //Hardware buttons setting to adjust the media

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PlayButton playTrack1Btn = (PlayButton)findViewById(R.id.play1);
        TrackVisualizerView trackVisualizerView = (TrackVisualizerView)findViewById(R.id.visualizer1);
        playTrack1Btn.prepareTrack(trackVisualizerView);

        PlayButton playTrack2Btn = (PlayButton)findViewById(R.id.play2);
        TrackVisualizerView trackVisualizerView2 = (TrackVisualizerView)findViewById(R.id.visualizer2);
        playTrack2Btn.prepareTrack(trackVisualizerView2);

        PlayButton playTrack3Btn = (PlayButton)findViewById(R.id.play3);
        TrackVisualizerView trackVisualizerView3 = (TrackVisualizerView)findViewById(R.id.visualizer3);
        playTrack3Btn.prepareTrack(trackVisualizerView3);


        MasterPlayButton masterPlayButton = (MasterPlayButton)findViewById(R.id.masterPlayBtn);
        masterPlayButton.prepareMasterTrack();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}