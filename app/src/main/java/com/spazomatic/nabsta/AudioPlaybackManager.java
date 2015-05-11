package com.spazomatic.nabsta;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.Log;

import com.spazomatic.nabsta.mediaStateHandlers.MediaStateHandler;
import com.spazomatic.nabsta.views.TrackVisualizerView;

import java.io.IOException;

/**
 * Created by samuelsegal on 4/16/15.
 */
public class AudioPlaybackManager implements Runnable, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer trackPlayer = null;
    private final String playBackFileName;
    private MediaStateHandler mediaStateHandler = null;
    private Visualizer trackVisualizer = null;
    //private static final float VISUALIZER_HEIGHT_DIP = 50f;
    public AudioPlaybackManager(MediaStateHandler mediaStateHandler) {
        this.mediaStateHandler = mediaStateHandler;
        this.playBackFileName = mediaStateHandler.getFileName();
    }
    private void startPlaying() {
        try {

            trackPlayer = null;
            trackPlayer = new MediaPlayer();
            trackPlayer.setOnErrorListener(this);
            trackPlayer.setOnPreparedListener(this);
            trackPlayer.setOnCompletionListener(this);
            trackPlayer.setDataSource(playBackFileName);
            trackPlayer.prepare();

        } catch (IOException | IllegalStateException | IllegalArgumentException e) {
            Log.e(NabstaApplication.LOG_TAG, String.format(
                    "Playback Failed: %s: Error Message: %s ",
                    playBackFileName, e.getMessage()), e);
        }
    }
    public boolean isReady(){
            if(mediaStateHandler.isComplete()){
                mediaStateHandler.setIsComplete(false);
                return true;
            } else{
                return false;
            }

    }
    public void callStopPlaying(){
        mediaStateHandler.complete();
        stopPlaying();
    }

    private void stopPlaying() {
        Log.d(NabstaApplication.LOG_TAG, "stopPlayingCalled() called.");
        if(trackPlayer != null) {
            trackPlayer.stop();
            trackPlayer.release();
            trackPlayer = null;
        }
        if(trackVisualizer != null){
            trackVisualizer.setEnabled(false);
            trackVisualizer.release();
            trackVisualizer = null;
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

        final TrackVisualizerView trackVisualizerView = mediaStateHandler.getTrackVisualizerView();
        if(trackVisualizerView != null) {
            trackVisualizerView.clearCanvas();
            trackVisualizerView.setTrackDuration(trackPlayer.getDuration());
            trackVisualizer = new Visualizer(trackPlayer.getAudioSessionId());
            trackVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

            //TODO: Test Best capture rate, currently set to Visualizer.getMaxCaptureRate(), Android example does Visualizer.getMaxCaptureRate()/2
            int resultOfSetDataCapture = trackVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                    //TODO: AddMaster trackView
                    trackVisualizerView.updateVisualizer(waveform);
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                    trackVisualizerView.updateVisualizer(fft);
                }
            }, Visualizer.getMaxCaptureRate(), true, false);
            if(Visualizer.SUCCESS == resultOfSetDataCapture) {
                trackVisualizer.setEnabled(true);
            }else{
                //TODO: Handle error for end user.
                Log.e(NabstaApplication.LOG_TAG,String.format(
                        "Error setting dataCapture Listener: %d",
                        resultOfSetDataCapture));
            }
        }

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

}
