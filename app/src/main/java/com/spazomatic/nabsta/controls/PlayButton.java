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

import java.io.File;
import java.io.IOException;

/**
 * Created by samuelsegal on 4/20/15.
 */
public class PlayButton extends Button {
    private String playBackFileName;
    AudioPlaybackManager apm = null;
    MediaStateHandler mediaStateHandler = null;
    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            if(apm != null) {
                if (apm.isReady()) {
                    setText(getResources().getString(R.string.txt_stop_playing));
                    Thread playbackThread = new Thread(apm);
                    playbackThread.start();
                } else {
                    apm.callStopPlaying();
                    setText(getResources().getString(R.string.txt_start_playing));
                }
            }else{
                Log.d(NabstaApplication.LOG_TAG,"PlayBackManager is null.");
            }
        }
    };

    public PlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        prepareAttributes(context, attrs);
        setOnClickListener(clicker);
    }

    private void prepareAttributes(Context context, AttributeSet attrs) {
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
    }

    private void playTrack(String fileName, Context context) {
        playBackFileName = NabstaApplication.NABSTA_ROOT_DIR.getAbsolutePath() + "/" + fileName;
        Log.d(NabstaApplication.LOG_TAG,"Got playBackFileName attr: " + playBackFileName);

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
        mediaStateHandler = new MediaStateHandler(context, this, playBackFileName);
        apm = new AudioPlaybackManager(mediaStateHandler);
    }
}
