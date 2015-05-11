package com.spazomatic.nabsta.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spazomatic.nabsta.AudioRecordManager;
import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.mediaStateHandlers.MediaStateHandler;

import java.io.File;
import java.io.IOException;

/**
 * Created by samuelsegal on 4/20/15.
 */
public class RecordButton extends Button {
    private String recordFileName;
    private MediaStateHandler mediaStateHandler;
    AudioRecordManager arm = null;


    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            if(mediaStateHandler.isInRecordMode()){
                mediaStateHandler.setIsInRecordMode(false);
                setSelected(false);
            }else {
                mediaStateHandler.setIsInRecordMode(true);
                setSelected(true);
            }
        }
    };

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.RecordButton);
        try {
            String fileName = a.getString(R.styleable.RecordButton_recordFileName);
            recordTrack(fileName);
        }finally {
            a.recycle();
        }
        setOnClickListener(clicker);
    }

    private void recordTrack(String fileName) {

        recordFileName = NabstaApplication.NABSTA_ROOT_DIR.getAbsolutePath() + "/" + fileName;
        Log.d(NabstaApplication.LOG_TAG, String.format("Got recordFileName attr: %s",recordFileName));
        checkCreateFile(fileName);
        arm = new AudioRecordManager(recordFileName);
    }

    private void checkCreateFile(String fileName) {
        File f = new File(NabstaApplication.NABSTA_ROOT_DIR.getAbsolutePath(), fileName);
        f.setExecutable(true);
        f.setReadable(true);
        f.setWritable(true);

        if(!f.exists()) {
            Log.d(NabstaApplication.LOG_TAG,String.format("Creating new record file: %s",f.getName()));
            try {
                f.createNewFile();
            } catch (IOException e) {
                Log.e(NabstaApplication.LOG_TAG,String.format(
                        "Error creating file %s with error message %s",
                        f.getAbsolutePath().toString(),e.getMessage()), e);
            }
        }else{
            Log.d(NabstaApplication.LOG_TAG,String.format("Record file exists: %s", f.getName()));
        }
    }

    public MediaStateHandler getMediaStateHandler() {
        return mediaStateHandler;
    }

    public void setMediaStateHandler(MediaStateHandler mediaStateHandler) {
        this.mediaStateHandler = mediaStateHandler;
    }
}
