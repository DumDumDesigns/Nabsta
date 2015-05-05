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
//
    public AudioPlaybackManager(MediaStateHandler mediaStateHandler) {
        this.mediaStateHandler = mediaStateHandler;
        this.playBackFileName = mediaStateHandler.getFileName();
    }
    private void startPlaying() {
        try {

            mPlayer = null;
/*
            // Request audio focus for playback
            int result = am.requestAudioFocus(focusChangeListener,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN);
                    */
            mPlayer = new MediaPlayer();
            mPlayer.setOnErrorListener(this);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setDataSource(playBackFileName);
            mPlayer.prepare();
        } catch (IOException | IllegalStateException | IllegalArgumentException e) {
            Log.e(NabstaApplication.LOG_TAG, String.format("Playback Failed: %s: Error Message: %s ", playBackFileName, e.getMessage()));
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
        mediaStateHandler.complete();
        stopPlaying();
    }

    private void stopPlaying() {
        Log.d(NabstaApplication.LOG_TAG, "stopPlayingCalled() called.");
        if(mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void run() {
        Log.d(NabstaApplication.LOG_TAG, String.format("AudioPlaybackManager running: %s", playBackFileName));
        startPlaying();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(NabstaApplication.LOG_TAG, String.format("MediaPlayer.OnErrorListener  what: %s: extra: %s", getErrorWhatCode(what), getErrorExtraCode(extra)));
        stopPlaying();
        return true;
    }



    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setLooping(mediaStateHandler.isLooping());
        mp.start();
        mediaStateHandler.begin();
        Log.d(NabstaApplication.LOG_TAG, String.format("Playing file: %s", playBackFileName));
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPlaying();
        mediaStateHandler.complete();
    }

    private String getErrorWhatCode(int what){
        switch(what){
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:{
                return "MediaPlayer.MEDIA_ERROR_UNKNOWN";
            }
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:{
                return "MediaPlayer.MEDIA_ERROR_SERVER_DIED";
            }
            default: return String.format("Error Code %d Unknown", what);
        }
    }
    private String getErrorExtraCode(int extra) {
        switch(extra){
            case MediaPlayer.MEDIA_ERROR_IO:{
                return "MediaPlayer.MEDIA_ERROR_IO";
            }
            case MediaPlayer.MEDIA_ERROR_MALFORMED:{
                return "MediaPlayer.MEDIA_ERROR_MALFORMED";
            }
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:{
                return "MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK";
            }
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:{
                return "MediaPlayer.MEDIA_ERROR_TIMED_OUT";
            }
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:{
                return "MediaPlayer.MEDIA_ERROR_UNSUPPORTED";
            }
            default: return String.format("Error Code %d Unknown", extra);
        }
    }

/*
    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {

                public void onAudioFocusChange(int focusChange) {
                    AudioManager am =
                            (AudioManager)NabstaApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);

                    switch (focusChange) {
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) :
                            // Lower the volume while ducking.
                            mPlayer.setVolume(0.2f, 0.2f);
                            break;

                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) :
                            //pause();
                            break;

                        case (AudioManager.AUDIOFOCUS_LOSS) :
                            //stop();
                            ComponentName component =
                                    new ComponentName(AudioPlaybackManager.this,
                                            MediaControlReceiver.class);
                            am.unregisterMediaButtonEventReceiver(component);
                            break;

                        case (AudioManager.AUDIOFOCUS_GAIN) :
                            // Return the volume to normal and resume if paused.
                            mPlayer.setVolume(1f, 1f);
                            mPlayer.start();
                            break;

                        default: break;
                    }
                }
            };
            */
}
