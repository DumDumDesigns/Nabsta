package com.spazomatic.nabsta.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spazomatic.nabsta.AudioPlaybackManager;
import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.mediaStateHandlers.MediaStateHandler;

/**
 * Created by samuelsegal on 4/24/15.
 */
public class MasterPlayButton extends Button {
    private String playBackFileName;
    //AudioPlaybackManager apm = null;
    //MediaStateHandler mediaStateHandler = null;
    AudioPlaybackManager [] apmAllTracks = null;


    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            Log.d(NabstaApplication.LOG_TAG, "About to add All PlaybackManagers............");
            for(AudioPlaybackManager apm: apmAllTracks) {
                if (apm.isReady()) {
                    //setText(getResources().getString(R.string.txt_stop_playing));
                    Log.d(NabstaApplication.LOG_TAG," IS READY ---------------------------");
                    Thread playbackThread = new Thread(apm);
                    playbackThread.start();
                } else {
                    apm.callStopPlaying();
                    //(getResources().getString(R.string.txt_start_playing));
                }
            }
        }
    };

    public MasterPlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        prepareAttributes(context, attrs);
        setOnClickListener(clicker);
    }

    private void prepareAttributes(Context context, AttributeSet attrs) {
        playTrack( context);
        /*
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.PlayButton);

        final int attrsCount = a.getIndexCount();
        for (int i = 0; i < attrsCount; ++i)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.PlayButton_playBackFileName:
                    String fileName =  a.getString(attr);
                    playTrack(fileName, context);
                    break;
            }
        }
        a.recycle();
        */
    }

    private void playTrack( Context context) {
        /*
        playBackFileName = NabstaApplication.NABSTA_ROOT_DIR.getAbsolutePath() + "/" + fileName;
        Log.d(NabstaApplication.LOG_TAG, "Got playBackFileName attr: " + playBackFileName);

        File f = new File(NabstaApplication.NABSTA_ROOT_DIR.getAbsolutePath(), fileName);
        f.setExecutable(true);
        f.setReadable(true);
        f.setWritable(true);
        if(!f.exists()) {
            Log.d(NabstaApplication.LOG_TAG,"Creating new playback file: " + f.getName());
            try {
                f.createNewFile();
            } catch (IOException e) {
                Log.e(NabstaApplication.LOG_TAG, "Error Creating File: " + f + " Error Message: " + e.getMessage());
            }
        }else{
            Log.d(NabstaApplication.LOG_TAG,"Playback file exists: " + f.getName() );
        }
        */
        apmAllTracks = new AudioPlaybackManager[NabstaApplication.ALL_TRACKS.length];
        for(int i = 0; i < NabstaApplication.ALL_TRACKS.length; i++ ){
            Log.d(NabstaApplication.LOG_TAG, "CREATING MEDIASTATE HANDLER AND PLAYYBACKMANAGER FOR: " + NabstaApplication.ALL_TRACKS);
            MediaStateHandler mediaStateHandler = new MediaStateHandler(context, this, NabstaApplication.ALL_TRACKS[i]);
            Log.d(NabstaApplication.LOG_TAG,"MEDIASTATEHANDLER CREATED");
            AudioPlaybackManager apm = new AudioPlaybackManager(mediaStateHandler);
            Log.d(NabstaApplication.LOG_TAG,"AUDIOPLAYBACKMANAGER CREATED");
            apmAllTracks[i] = apm;
        }

    }
}

