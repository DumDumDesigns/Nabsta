package com.spazomatic.nabsta;

import android.media.MediaPlayer;
import android.util.Log;

import com.spazomatic.nabsta.mediaStateHandlers.MediaStateHandler;

import java.io.IOException;

/**
 * Created by samuelsegal on 4/16/15.
 */
public class AudioPlaybackManager implements Runnable, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {


    private MediaPlayer mPlayer = null;
    private final String playBackFileName;
    private MediaStateHandler mediaStateHandler = null;

    public AudioPlaybackManager(MediaStateHandler mediaStateHandler) {
        this.mediaStateHandler = mediaStateHandler;
        this.playBackFileName = mediaStateHandler.getFileName();
    }
    private void startPlaying() {
        try {
            mPlayer = null;
            mPlayer = new MediaPlayer();
            mPlayer.setOnErrorListener(this);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setDataSource(playBackFileName);
            mPlayer.prepare();
        } catch (IOException | IllegalStateException | IllegalArgumentException e) {
            Log.e(NabstaApplication.LOG_TAG, "Playback Failed: " + playBackFileName + " : Error message: " + e.getMessage());
        }/*finally{
            stopPlaying();
        }*/
    }
    public boolean isReady(){
            if(mediaStateHandler.isComplete()){
                mediaStateHandler.setIsComplete(false);
                return true;
            }
            else return false;

    }
    public void callStopPlaying(){
        mediaStateHandler.setIsComplete(true);
        stopPlaying();
    }

    private void stopPlaying() {
        Log.d(NabstaApplication.LOG_TAG, "stopPlayingCalled() called.");
        if(mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void run() {
        Log.d(NabstaApplication.LOG_TAG, "AudioPlaybackManager running: " + playBackFileName);
        startPlaying();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(NabstaApplication.LOG_TAG, "MediaPlayer.OnErrorListener  what: " + what + " extra: " + extra);
        stopPlaying();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setLooping(mediaStateHandler.isLooping());
        mp.start();
        Log.d(NabstaApplication.LOG_TAG, "Playing file: " + playBackFileName);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPlaying();
        mediaStateHandler.complete();
    }
}
