package com.spazomatic.nabsta.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.util.Log;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.controls.TrackMuteButton;
import com.spazomatic.nabsta.controls.TrackRecordButton;
import com.spazomatic.nabsta.db.Track;
import com.spazomatic.nabsta.views.TrackVisualizerView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by samuelsegal on 5/20/15.
 */
public class TrackMessenger implements Runnable, TrackMuteButton.OnMuteTrackListener,
        TrackRecordButton.OnRecordTrackListener{

    private Track track;
    private static final int MIN_BUFF_SIZE= AudioTrack.getMinBufferSize(
            44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
    private static final int FREQUENCY = 44100;
    private AudioTrack audioTrack;
    private volatile boolean isMuted;
    private long trackID;
    private volatile boolean isRecording;
    private Visualizer trackVisualizer = null;
    private TrackVisualizerView trackVisualizerView;
    private OnTrackCompleteListener onTrackCompleteListener;

    public TrackMessenger(Track track) {
        this.track = track;
    }

    public interface OnTrackCompleteListener{
        void trackComplete();
    }
    @Override
    public void run() {

        try {
            if(isRecording()) {
                recordTrack();
            }else{
                playTrack();
            }
        } catch (IOException e) {
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error Play / Recording track with Error message %s",
                    e.getMessage()),e);
        }
    }

    private void playTrack(){

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
    private void recordTrack() throws IOException{
        File file = new File(track.getFile_name());

        if (file.exists()) {
            if(!file.delete()){
                //TODO:Propagate Unexpected Errors such as these to end user.
                Log.e(NabstaApplication.LOG_TAG,String.format(
                        "Error Deleting File %s Cancelling Record...",
                        file.getAbsolutePath()));
            }
        }
        if(!file.createNewFile()){
            //TODO:Propagate Unexpected Errors such as these to end user.
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error Creating File %s Cancelling Record...",
                    file.getAbsolutePath()));
        }

        OutputStream os = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        DataOutputStream dos = new DataOutputStream(bos);

        Log.d(NabstaApplication.LOG_TAG,String.format(
                "Begin Recording File: %s",
                file.getAbsolutePath()));

        AudioRecord audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                FREQUENCY,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                MIN_BUFF_SIZE);
        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                FREQUENCY,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                MIN_BUFF_SIZE,
                AudioTrack.MODE_STREAM,
                audioRecord.getAudioSessionId());

        try {
            byte[] buffer = new byte[MIN_BUFF_SIZE];
            audioRecord.startRecording();
            audioTrack.play();
            setUpVisualizer(audioTrack.getAudioSessionId());
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, MIN_BUFF_SIZE);
                for (int i = 0; i < bufferReadResult; i++) {
                    dos.writeByte(buffer[i]);
                }
                //TODO: Learn DSP to create own record visualizer in order to remove this hack of playing back recorded buffer for visualizer capability
                playWithAudioTrack(buffer, audioTrack);
            }

            Log.d(NabstaApplication.LOG_TAG, String.format(
                    "Finish Recording File %s", file.getAbsolutePath()));
        } catch (IOException e) {
            Log.e(NabstaApplication.LOG_TAG, String.format(
                    "Error Recording File %s",file.getAbsolutePath()), e);
        }finally{
            audioRecord.stop();
            audioRecord.release();
            dos.close();
            if (audioTrack != null) {
                audioTrack.stop();
                audioTrack.release();
                //Message trackCompleteMessage = mediaStateHandler.getUiHandler().obtainMessage(
                        //AudioPlaybackManager.TRACK_COMPLETE_STATE, mediaStateHandler);
               // trackCompleteMessage.sendToTarget();
            }

            if (trackVisualizer != null) {
                trackVisualizer.setEnabled(false);
                trackVisualizer.release();
                trackVisualizer = null;
            }
        }
    }
    private void playWithAudioTrack(byte[] buffer, AudioTrack audioTrack){

        int numberOfBytesWritten = audioTrack.write(buffer, 0, MIN_BUFF_SIZE);
        if (numberOfBytesWritten == AudioTrack.ERROR_INVALID_OPERATION ||
                numberOfBytesWritten == AudioTrack.ERROR_BAD_VALUE ||
                numberOfBytesWritten == AudioManager.ERROR_DEAD_OBJECT) {
            Log.e(NabstaApplication.LOG_TAG, "Error Writing bytes to Mix Track");
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
    @Override
    public void onMuteTrackClicked(boolean isMuted) {
        this.isMuted = isMuted;
        if(audioTrack != null) {
            if (isMuted) {
                audioTrack.setStereoVolume(0.0f, 0.0f);
            } else {
                audioTrack.setStereoVolume(0.5f, 0.5f);
            }
        }
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setIsMuted(boolean isMuted) {
        this.isMuted = isMuted;
    }

    public long getTrackID() {
        return trackID;
    }

    public void setTrackID(long trackID) {
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

    @Override
    public void recordTrackClicked(boolean record) {
        setIsRecording(record);
    }

    public void pauseTrack(){
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
}
