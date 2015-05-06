package com.spazomatic.nabsta.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spazomatic.nabsta.AudioPlaybackManager;
import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.mediaStateHandlers.MediaStateHandler;
import com.spazomatic.nabsta.views.TrackVisualizerView;

import java.io.File;
import java.io.IOException;

/**
 * Created by samuelsegal on 4/20/15.
 */
public class PlayButton extends Button {
    private String fileName;
    private AudioPlaybackManager apm = null;
    private MediaStateHandler mediaStateHandler = null;
    private TrackVisualizerView trackVisualizerView = null;
    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            if(apm != null) {
                if (apm.isReady()) {
                    Thread playbackThread = new Thread(apm);
                    playbackThread.start();
                } else {
                    apm.callStopPlaying();
                }
            }else{
                Log.d(NabstaApplication.LOG_TAG, "PlayBackManager is null.");
            }
        }
    };

    public PlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        prepareAttributes(attrs);
        setOnClickListener(clicker);
    }

    private void prepareAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PlayButton);
        try {
            this.fileName = a.getString(R.styleable.PlayButton_playBackFileName);
        }finally {
            a.recycle();
        }
    }
    public void prepareTrack(TrackVisualizerView trackVisualizerView){
        this.trackVisualizerView = trackVisualizerView;
        prepareTrack();
    }
    public void prepareTrack() {
        String playBackFileName = NabstaApplication.NABSTA_ROOT_DIR.getAbsolutePath() + "/" + fileName;
        Log.d(NabstaApplication.LOG_TAG, String.format("Got playBackFileName attr: %s",playBackFileName));

        File f = new File(NabstaApplication.NABSTA_ROOT_DIR.getAbsolutePath(), fileName);
        f.setExecutable(true);
        f.setReadable(true);
        f.setWritable(true);
        if(!f.exists()) {
            Log.d(NabstaApplication.LOG_TAG, String.format("Creating new playback file: %s",f.getName()));
            try {
                f.createNewFile();
            } catch (IOException e) {
                Log.e(NabstaApplication.LOG_TAG, String.format("Error Creating File: %s Error Message: %s",f,e.getMessage()));
            }
        }else{
            Log.d(NabstaApplication.LOG_TAG, String.format("Playback file exists: %s", f.getName()));
        }
        if(trackVisualizerView != null) {
            mediaStateHandler = new MediaStateHandler(getContext(), this, playBackFileName, trackVisualizerView);
        }else{
            mediaStateHandler = new MediaStateHandler(getContext(), this, playBackFileName);
        }
        apm = new AudioPlaybackManager(mediaStateHandler);
    }
}
