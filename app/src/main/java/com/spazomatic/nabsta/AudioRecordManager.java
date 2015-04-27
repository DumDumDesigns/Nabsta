package com.spazomatic.nabsta;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by samuelsegal on 4/16/15.
 */
public class AudioRecordManager implements Runnable, MediaRecorder.OnErrorListener{
    private volatile boolean isRecording;
    private String recordFileName;
    private MediaRecorder mRecorder = null;

    public AudioRecordManager(String recordFileName) {
        this.recordFileName = recordFileName;
    }


    public boolean isRecording() {
        return isRecording;
    }

    public void setIsRecording(boolean isRecording) {
        this.isRecording = isRecording;
    }

    private void startRecording() {
        mRecorder = null;
        mRecorder = new MediaRecorder();
        mRecorder.setOnErrorListener(this);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(recordFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
            Log.d(NabstaApplication.LOG_TAG, "Recording: " + recordFileName);

            while(isRecording) {
                if(!isRecording()){
                    break;
                }
            }

        } catch (IOException e) {
            Log.e(NabstaApplication.LOG_TAG, "Recording failed: " + e.getCause());
            e.printStackTrace();
        }finally {
            stopRecording();
        }
    }

    private void stopRecording() {
        Log.d(NabstaApplication.LOG_TAG, "Stop Recording: " + recordFileName);
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

    }

    @Override
    public void run() {
        Log.d(NabstaApplication.LOG_TAG, "AudioRecordManager Running: " + recordFileName);
        try {
            startRecording();
        }catch(Exception e){
            Log.d(NabstaApplication.LOG_TAG, e.getStackTrace().toString());
        }
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        Log.e(NabstaApplication.LOG_TAG, "MediaPlayer.OnErrorListener  what: " + what + " extra: " + extra);
        stopRecording();
    }
}
