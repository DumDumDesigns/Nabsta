package com.spazomatic.nabsta;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;


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
    public void onPause() {
        super.onPause();
    }
}