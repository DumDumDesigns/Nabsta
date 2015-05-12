package com.spazomatic.nabsta.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spazomatic.nabsta.AudioPlaybackManager;
import com.spazomatic.nabsta.AudioRecordManager;
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
    private AudioRecordManager arm = null;
    private MediaStateHandler mediaStateHandler = null;
    private TrackVisualizerView trackVisualizerView = null;
    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            try {
                //TODO: UI Design has changed, revisit again to simplify the coupling of record and playback buttons
                if (mediaStateHandler.isInRecordMode()) {
                    if (arm == null) {
                        apm = null;
                        arm = new AudioRecordManager(mediaStateHandler.getFileName());
                    }
                    if (!arm.isRecording()) {
                        arm.setIsRecording(true);
                        setSelected(true);
                        Thread recordThread = new Thread(arm);
                        recordThread.start();
                    } else {
                        arm.setIsRecording(false);
                        setSelected(false);
                    }
                } else {
                    if (apm == null) {
                        arm = null;
                        apm = new AudioPlaybackManager(mediaStateHandler);
                    }
                    if (apm.isReady()) {
                        Thread playbackThread = new Thread(apm);
                        playbackThread.start();
                    } else {
                        apm.callStopPlaying();
                    }
                }
            }catch(Exception e){
                Log.e(NabstaApplication.LOG_TAG,String.format("Error settingSelect %s",e.getMessage()),e);
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

    public void prepareTrack(TrackVisualizerView trackVisualizerView, RecordButton recordButton){
        this.trackVisualizerView = trackVisualizerView;
        prepareTrack(recordButton);
    }
    public void prepareTrack(RecordButton recordButton) {
        String playBackFileName = NabstaApplication.NABSTA_ROOT_DIR.getAbsolutePath() + "/" + fileName;
        Log.d(NabstaApplication.LOG_TAG, String.format(
                "Got playBackFileName attr: %s",playBackFileName));

        File f = new File(NabstaApplication.NABSTA_ROOT_DIR.getAbsolutePath(), fileName);
        /*
        if(f.setExecutable(true) == false){
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error setting Executable Permission for file %s",f.getAbsolutePath()));
        }
        if(!f.setReadable(true)){
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error setting Readable Permission for file %s",f.getAbsolutePath()));
        }
        if(!f.setWritable(true)){
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error setting Writable Permission for file %s",f.getAbsolutePath()));
        }
        */
        if(!f.exists()) {
            Log.d(NabstaApplication.LOG_TAG, String.format(
                    "Creating new playback file: %s",f.getName()));
            try {
                if(!f.createNewFile()){
                    Log.e(NabstaApplication.LOG_TAG,String.format(
                            "Error Creating file %s",f.getAbsolutePath()));
                }
            } catch (IOException e) {
                Log.e(NabstaApplication.LOG_TAG, String.format(
                        "Error Creating File: %s Error Message: %s",f,e.getMessage()),e);
            }
        }else{
            Log.d(NabstaApplication.LOG_TAG, String.format(
                    "Playback file exists: %s", f.getName()));
        }
        if(trackVisualizerView != null && recordButton != null) {
            mediaStateHandler = new MediaStateHandler(getContext(),
                    this, playBackFileName, trackVisualizerView, recordButton);
        }else{
            mediaStateHandler = new MediaStateHandler(getContext(), this, playBackFileName);
        }


    }
}
