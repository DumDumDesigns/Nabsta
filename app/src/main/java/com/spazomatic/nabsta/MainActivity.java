package com.spazomatic.nabsta;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;

import com.spazomatic.nabsta.controls.MasterPlayButton;
import com.spazomatic.nabsta.controls.PlayButton;
import com.spazomatic.nabsta.controls.RecordButton;
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
        RecordButton recordButton1 = (RecordButton)findViewById(R.id.record1);
        TrackVisualizerView trackVisualizerView = (TrackVisualizerView)findViewById(R.id.visualizer1);
        recordButton1.prepareTrack(trackVisualizerView);
        playTrack1Btn.prepareTrack(trackVisualizerView, recordButton1);

        PlayButton playTrack2Btn = (PlayButton)findViewById(R.id.play2);
        RecordButton recordButton2 = (RecordButton)findViewById(R.id.record2);
        TrackVisualizerView trackVisualizerView2 = (TrackVisualizerView)findViewById(R.id.visualizer2);
        recordButton2.prepareTrack(trackVisualizerView2);
        playTrack2Btn.prepareTrack(trackVisualizerView2, recordButton2);

        PlayButton playTrack3Btn = (PlayButton)findViewById(R.id.play3);
        RecordButton recordButton3 = (RecordButton)findViewById(R.id.record3);

        TrackVisualizerView trackVisualizerView3 = (TrackVisualizerView)findViewById(R.id.visualizer3);
        recordButton3.prepareTrack(trackVisualizerView3);
        playTrack3Btn.prepareTrack(trackVisualizerView3, recordButton3);

        MasterPlayButton masterPlayButton = (MasterPlayButton)findViewById(R.id.masterPlayBtn);
        TrackVisualizerView masterVisualizer = (TrackVisualizerView)findViewById(R.id.masterVisualizer);
        masterPlayButton.prepareMasterTrack(trackVisualizerView,trackVisualizerView2,trackVisualizerView3, masterVisualizer);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}