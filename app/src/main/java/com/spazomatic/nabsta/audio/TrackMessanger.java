package com.spazomatic.nabsta.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.audiofx.Visualizer;
import android.util.Log;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.controls.TrackMuteButton;
import com.spazomatic.nabsta.db.Track;
import com.spazomatic.nabsta.views.TrackVisualizerView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by samuelsegal on 5/20/15.
 */
public class TrackMessanger implements Runnable, TrackMuteButton.OnMuteTrackListener {

    private Track track;
    private static final int MIN_BUFF_SIZE= AudioTrack.getMinBufferSize(
            44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
    private static final int FREQUENCY = 44100;
    private AudioTrack audioTrack;
    private volatile boolean isMuted;
    private int trackID;
    private boolean isRecording;
    private Visualizer trackVisualizer = null;
    private TrackVisualizerView trackVisualizerView;
    private OnTrackCompleteListener onTrackCompleteListener;
    public TrackMessanger(Track track) {
        this.track = track;
    }

    public interface OnTrackCompleteListener{
        void trackComplete();
    }
    @Override
    public void run() {

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                FREQUENCY,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                MIN_BUFF_SIZE,
                AudioTrack.MODE_STREAM);

        byte[] trackBytes = convertStreamToByteArray(track.getFile_name());

        setUpVisualizer(audioTrack.getAudioSessionId());
        audioTrack.play();
        int numberOfBytesWritten = audioTrack.write(trackBytes, 0, trackBytes.length);
        if(numberOfBytesWritten == AudioTrack.ERROR_INVALID_OPERATION ||
                numberOfBytesWritten == AudioTrack.ERROR_BAD_VALUE ||
                numberOfBytesWritten == AudioManager.ERROR_DEAD_OBJECT) {
            Log.e(NabstaApplication.LOG_TAG, "Error Writing bytes to Mix Track");
        }
        Log.d(NabstaApplication.LOG_TAG, String.format(
                "Wrote %d bytes to Track %s.", numberOfBytesWritten, track.getFile_name()
        ));

        if(audioTrack != null) {
            //audioTrack.stop();
            audioTrack.release();
            //Message trackCompleteMessage = mediaStateHandler.getUiHandler().obtainMessage(
            //        TRACK_COMPLETE_STATE,mediaStateHandler);
            //trackCompleteMessage.sendToTarget();
        }
        if(trackVisualizer != null){
            trackVisualizer.setEnabled(false);
            trackVisualizer.release();
            trackVisualizer = null;
        }

    }

    private void setUpVisualizer(int audioSessionID){
        final TrackVisualizerView trackVisualizerView = this.trackVisualizerView;
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

    @Override
    public void onMuteTrackListener(boolean isMuted) {
        this.isMuted = isMuted;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setIsMuted(boolean isMuted) {
        this.isMuted = isMuted;
    }

    public int getTrackID() {
        return trackID;
    }

    public void setTrackID(int trackID) {
        this.trackID = trackID;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setIsRecording(boolean isRecording) {
        this.isRecording = isRecording;
    }

    public TrackVisualizerView getTrackVisualizerView() {
        return trackVisualizerView;
    }

    public void setTrackVisualizerView(TrackVisualizerView trackVisualizerView) {
        this.trackVisualizerView = trackVisualizerView;
    }
}
