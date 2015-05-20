package com.spazomatic.nabsta;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.audiofx.Equalizer;
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
public class AudioPlaybackManager implements Runnable {
    public static final int TRACK_COMPLETE_STATE = 3;

    private final String playBackFileName;
    private MediaStateHandler mediaStateHandler = null;
    private Visualizer trackVisualizer = null;
    private Equalizer equalizer;
    private static final int MIN_BUFF_SIZE= AudioTrack.getMinBufferSize(
            44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
    private static final int FREQUENCY = 44100;
    private AudioTrack audioTrack;
    private volatile boolean isMuted;
    private int trackID;

    public int getTrackID() {
        return trackID;
    }

    public void setTrackID(int trackID) {
        this.trackID = trackID;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setIsMuted(boolean isMuted) {
        Log.d(NabstaApplication.LOG_TAG,String.format(
                "Mute track %s = %b",playBackFileName,isMuted));
        this.isMuted = isMuted;
        if(isMuted) {
            audioTrack.setStereoVolume(0.0f, 0.0f);
        }else{
            audioTrack.setStereoVolume(0.5f,0.5f);
        }
    }

    public AudioPlaybackManager(MediaStateHandler mediaStateHandler) {
        this.mediaStateHandler = mediaStateHandler;
        this.playBackFileName = mediaStateHandler.getFileName();
        //NativeAudio nativeAudio = new NativeAudio();
    }


    private void startPlaying() {
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
            Message trackCompleteMessage = mediaStateHandler.getUiHandler().obtainMessage(
                    TRACK_COMPLETE_STATE,mediaStateHandler);
            trackCompleteMessage.sendToTarget();
        }
        if(trackVisualizer != null){
            trackVisualizer.setEnabled(false);
            trackVisualizer.release();
            trackVisualizer = null;
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
            Log.e(NabstaApplication.LOG_TAG, "Error Writing bytes to Mix Track");

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

}
