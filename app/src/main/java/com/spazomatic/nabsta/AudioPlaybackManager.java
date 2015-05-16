package com.spazomatic.nabsta;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Message;
import android.util.Log;

import com.spazomatic.nabsta.mediaStateHandlers.MediaStateHandler;
import com.spazomatic.nabsta.views.TrackVisualizerView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by samuelsegal on 4/16/15.
 */
public class AudioPlaybackManager implements Runnable, MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    public static final int TRACK_COMPLETE_STATE = 3;
    private MediaPlayer trackPlayer = null;
    private final String playBackFileName;
    private MediaStateHandler mediaStateHandler = null;
    private Visualizer trackVisualizer = null;
    private static final int MIN_BUFF_SIZE= AudioTrack.getMinBufferSize(
            44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
    private static final int FREQUENCY = 44100;
    private AudioTrack audioTrack;

    public AudioPlaybackManager(MediaStateHandler mediaStateHandler) {
        this.mediaStateHandler = mediaStateHandler;
        this.playBackFileName = mediaStateHandler.getFileName();

    }
    private void startPlaying() {
        //playWithMediaPlayer();
        playWithAudioTrack();
    }
    public boolean isReady(){
            if(mediaStateHandler.isComplete()){
                mediaStateHandler.setIsComplete(false);
                mediaStateHandler.begin();
                return true;
            } else{
                return false;
            }
    }
    public void callStopPlaying(){

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
        if(audioTrack != null) {
            //audioTrack.stop();
            audioTrack.release();
        }

    }

    @Override
    public void run() {
        Log.d(NabstaApplication.LOG_TAG, String.format(
                "AudioPlaybackManager running: %s", playBackFileName));
        startPlaying();
    }

    private void playWithAudioTrack(){

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                FREQUENCY,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                MIN_BUFF_SIZE,
                AudioTrack.MODE_STREAM);

        byte[] track = convertStreamToByteArray(playBackFileName);

        setUpVisualizer(audioTrack.getAudioSessionId());
        audioTrack.play();
        int numberOfBytesWritten = audioTrack.write(track, 0, track.length);
        if(numberOfBytesWritten == AudioTrack.ERROR_INVALID_OPERATION ||
                numberOfBytesWritten == AudioTrack.ERROR_BAD_VALUE ||
                numberOfBytesWritten == AudioManager.ERROR_DEAD_OBJECT) {
            Log.e(NabstaApplication.LOG_TAG,"Error Writing bytes to Mix Track");
        }
        Log.d(NabstaApplication.LOG_TAG, String.format(
                "Wrote %d bytes to Track %s.", numberOfBytesWritten, playBackFileName
        ));

        if(audioTrack != null) {
            //audioTrack.stop();
            audioTrack.release();
            Message trackCompleteMessage = mediaStateHandler.getUiHandler().obtainMessage(TRACK_COMPLETE_STATE,mediaStateHandler);
            trackCompleteMessage.sendToTarget();
        }
        if(trackVisualizer != null){
            trackVisualizer.setEnabled(false);
            trackVisualizer.release();
            trackVisualizer = null;
        }

    }
    private void playWithMediaPlayer(){
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

    private void mixSound() {

        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                FREQUENCY,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                MIN_BUFF_SIZE,
                AudioTrack.MODE_STREAM);

        byte[] track1 = convertStreamToByteArray(NabstaApplication.ALL_TRACKS[0]);

        byte[] track2 = convertStreamToByteArray(NabstaApplication.ALL_TRACKS[1]);

        byte[] output = new byte[track1.length];
        setUpVisualizer(audioTrack.getAudioSessionId());
        audioTrack.play();

        for(int i=0; i < output.length; i++){

            float sampleFloat1 = track1.length > i ? track1[i] / 128.0f : 0.0f;
            float sampleFloat2 = track2.length > i ? track2[i] / 128.0f : 0.0f;

            float mixed = sampleFloat1 + sampleFloat2;
            // reduce volume
            mixed *= 0.8;
            if (mixed > 1.0f) mixed = 1.0f;
            if (mixed < -1.0f) mixed = -1.0f;

            byte outputSample = (byte)(mixed * 128.0f);
            output[i] = outputSample;

        }

        int numberOfBytesWritten = audioTrack.write(output, 0, output.length);
        if(numberOfBytesWritten == AudioTrack.ERROR_INVALID_OPERATION ||
                numberOfBytesWritten == AudioTrack.ERROR_BAD_VALUE ||
                numberOfBytesWritten == AudioManager.ERROR_DEAD_OBJECT){
            Log.e(NabstaApplication.LOG_TAG,"Error Writing bytes to Mix Track");

        }
        if(audioTrack != null) {
            audioTrack.release();
        }

    }

    private byte[] convertStreamToByteArray(String musacFile) {
        byte [] soundBytes = null;
        ByteArrayOutputStream bos = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        try {
            bos = new ByteArrayOutputStream();
            fis = new FileInputStream(musacFile);
            bis = new BufferedInputStream(fis);
            byte[] buffer = new byte[MIN_BUFF_SIZE/2];

            while(bis.read(buffer) != - 1){
                bos.write(buffer);
            }
            soundBytes = bos.toByteArray();

        } catch (IOException e) {
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error reading file %s", musacFile),e);
        }finally{
            try {
                if(bis != null) {
                    bis.close();
                }
                if(fis != null) {
                    fis.close();
                }
                if(bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                Log.e(NabstaApplication.LOG_TAG,String.format(
                        "Error Closing Stream with message: %s",
                        e.getMessage()),e);
            }
        }

        return soundBytes;
    }

    private void setUpVisualizer(int audioSessionID){
        final TrackVisualizerView trackVisualizerView = mediaStateHandler.getTrackVisualizerView();
        if(trackVisualizerView != null) {
            trackVisualizerView.reset();
            //trackVisualizerView.setTrackDuration(trackPlayer.getDuration());
            trackVisualizer = new Visualizer(audioSessionID);
            trackVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            Log.d(NabstaApplication.LOG_TAG, String.format("CaptureSize: %d", trackVisualizer.getCaptureSize()));
            //TODO: Test Best capture rate, currently set to Visualizer.getMaxCaptureRate(), Android example does Visualizer.getMaxCaptureRate()/2
            int resultOfSetDataCapture = trackVisualizer.setDataCaptureListener(
                    new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
                                                  int samplingRate) {
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
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(NabstaApplication.LOG_TAG, String.format(
                "MediaPlayer.OnErrorListener  what: %s: extra: %s",
                getErrorWhatCode(what), getErrorExtraCode(extra)));
        stopPlaying();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        setUpVisualizer(trackPlayer.getAudioSessionId());
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
