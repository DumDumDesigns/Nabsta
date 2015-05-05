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

import java.io.File;
import java.io.IOException;

/**
 * Created by samuelsegal on 4/20/15.
 */
public class RecordButton extends Button {
    private String recordFileName;

    AudioRecordManager arm = null;


    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            if(arm != null) {
                if (!arm.isRecording()) {
                    arm.setIsRecording(true);
                    setSelected(true);
                    Thread recordThread = new Thread(arm);
                    recordThread.start();
                } else {
                    arm.setIsRecording(false);
                    setSelected(false);
                }
            }else{
                Log.d(NabstaApplication.LOG_TAG, "AudioRecordManager is null.");
            }
        }
    };

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.RecordButton);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.RecordButton_recordFileName:
                    String fileName =  a.getString(attr);
                    recordTrack(fileName);
                    break;
            }
        }
        a.recycle();
        setOnClickListener(clicker);
    }

    private void recordTrack(String fileName) {
        recordFileName = NabstaApplication.NABSTA_ROOT_DIR.getAbsolutePath() + "/" + fileName;
        Log.d(NabstaApplication.LOG_TAG, "Got recordFileName attr: " + recordFileName);
        checkCreateFile(fileName);
        arm = new AudioRecordManager(recordFileName);
    }

    private void checkCreateFile(String fileName) {
        File f = new File(NabstaApplication.NABSTA_ROOT_DIR.getAbsolutePath(), fileName);
        f.setExecutable(true);
        f.setReadable(true);
        f.setWritable(true);

        if(!f.exists()) {
            Log.d(NabstaApplication.LOG_TAG,"Creating new record file: " + f.getName());
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.d(NabstaApplication.LOG_TAG,"Record file exists: " + f.getName() );
        }
    }
}
