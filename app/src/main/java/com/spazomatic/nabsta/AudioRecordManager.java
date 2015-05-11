package com.spazomatic.nabsta;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by samuelsegal on 4/16/15.
 */
public class AudioRecordManager implements Runnable, MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener{
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
        mRecorder.setOnInfoListener(this);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(recordFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
            Log.d(NabstaApplication.LOG_TAG, String.format("Recording: %s",recordFileName));

            while(isRecording) {
                if(!isRecording()){
                    break;
                }
            }

        } catch (IOException e) {
            Log.e(NabstaApplication.LOG_TAG, String.format("Recording failed: %s",e.getCause()),e);
        }finally {
            stopRecording();
        }
    }

    private void stopRecording() {
        Log.d(NabstaApplication.LOG_TAG, String.format("Stop Recording: %s", recordFileName));
        if(mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    @Override
    public void run() {
        Log.d(NabstaApplication.LOG_TAG, "AudioRecordManager Running: " + recordFileName);
        try {
            startRecording();
        }catch(Exception e){
            Log.d(NabstaApplication.LOG_TAG, String.format("Error in run of AudioRecordManager: %s",e.getMessage()), e);
        }
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        Log.e(NabstaApplication.LOG_TAG, String.format("MediaPlayer.OnErrorListener  what: %d: extra: %d", what, extra));
        stopRecording();
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        Log.e(NabstaApplication.LOG_TAG, String.format("MediaPlayer.OnInfoListener  what: %d: extra: %d", what, extra));
    }
}
