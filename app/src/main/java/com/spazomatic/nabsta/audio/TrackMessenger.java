package com.spazomatic.nabsta.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.db.Track;
import com.spazomatic.nabsta.views.controls.TrackMuteButton;
import com.spazomatic.nabsta.views.controls.TrackRecordButton;

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

    private final static String LOG_TAG = String.format(
            "Nabsta: %s", TrackMessenger.class.getSimpleName());

    private Track track;
    private static final int FREQUENCY = 44100;
    private static final int MIN_BUFF_SIZE= AudioTrack.getMinBufferSize(
            FREQUENCY, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

    private AudioTrack audioTrack;
    private AudioRecord audioRecord;
    private volatile boolean isMuted;
    private long trackID;
    private volatile boolean isRecording;
    private TrackStatusListener trackStatusListener;
    private TrackCompleteListener trackCompleteListener;

    private static int trackCount = 0;
    private synchronized void increaseTrackCount(){
         ++trackCount;
    }
    private synchronized int decreaseTrackCount(){
         return --trackCount;
    }

    private static int threadCount = 0;
    public TrackMessenger(Track track) {
        this.track = track;
        Log.d(NabstaApplication.LOG_TAG,String.format(
                "TrackMessenger created %d threads",++threadCount));
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
        Log.d(LOG_TAG,String.format("----------PLAY TRACk %s-------------", track.getName()));
        byte[] trackBytes = convertStreamToByteArray(track.getFile_name());
        if(trackBytes == null || trackBytes.length <=0 ){
            Log.d(NabstaApplication.LOG_TAG,String.format(
                    "File %s Empty", track.getFile_name()));
            return;
        }
        try {
            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    FREQUENCY,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    MIN_BUFF_SIZE,
                    AudioTrack.MODE_STREAM);
            if(isMuted()){
                onMuteTrackClicked(true);
            }
            callListenersTrackBegin(audioTrack.getAudioSessionId(), false);
            audioTrack.play();
            int numberOfBytesWritten = audioTrack.write(trackBytes, 0, trackBytes.length);
            if (numberOfBytesWritten == AudioTrack.ERROR_INVALID_OPERATION ||
                    numberOfBytesWritten == AudioTrack.ERROR_BAD_VALUE ||
                    numberOfBytesWritten == AudioManager.ERROR_DEAD_OBJECT) {
                Log.e(NabstaApplication.LOG_TAG, "Error Writing bytes to Mix Track");
            }
        }finally {
            stopTrack(false);
        }
        Log.d(LOG_TAG,String.format("----------END PLAY TRACk %s-------------", track.getName()));

    }

    private void recordTrack() throws IOException {
        File file = new File(track.getFile_name());
        Log.d(LOG_TAG,String.format("----------RECORD TRACk %s-------------", track.getName()));
        OutputStream os = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        DataOutputStream dos = new DataOutputStream(bos);
        try {
             audioRecord = new AudioRecord(
                     MediaRecorder.AudioSource.MIC,
                     FREQUENCY,
                     AudioFormat.CHANNEL_IN_MONO,
                     AudioFormat.ENCODING_PCM_16BIT,
                     MIN_BUFF_SIZE);
             audioTrack = new AudioTrack(
                     AudioManager.STREAM_MUSIC,
                     FREQUENCY,
                     AudioFormat.CHANNEL_OUT_MONO,
                     AudioFormat.ENCODING_PCM_16BIT,
                     MIN_BUFF_SIZE,
                     AudioTrack.MODE_STREAM,
                     audioRecord.getAudioSessionId());

            callListenersTrackBegin(audioTrack.getAudioSessionId(), true);
            byte[] buffer = new byte[MIN_BUFF_SIZE];
            audioRecord.startRecording();
            audioTrack.play();

            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, MIN_BUFF_SIZE);
                for (int i = 0; i < bufferReadResult; i++) {
                    dos.writeByte(buffer[i]);
                }

                playWithAudioTrack(buffer, audioTrack);
            }

        } catch (Exception e) {
            Log.e(NabstaApplication.LOG_TAG, String.format(
                    "Error Recording File %s",file.getAbsolutePath()), e);
        }finally{
            stopTrack(true);
            try {
                if(dos != null) {
                    dos.close();
                }
                if(bos != null) {
                    bos.close();
                }
                if(os != null) {
                    os.close();
                }
            } catch (IOException e) {
                Log.e(NabstaApplication.LOG_TAG,String.format(
                        "Error Closing Stream with message: %s",
                        e.getMessage()),e);
            }
        }
        Log.d(LOG_TAG,String.format("----------END RECORD TRACk %s-------------", track.getName()));
    }

    private void playWithAudioTrack(byte[] buffer, AudioTrack audioTrack){

        int numberOfBytesWritten = audioTrack.write(buffer, 0, MIN_BUFF_SIZE);
        if (numberOfBytesWritten == AudioTrack.ERROR_INVALID_OPERATION ||
                numberOfBytesWritten == AudioTrack.ERROR_BAD_VALUE ||
                numberOfBytesWritten == AudioManager.ERROR_DEAD_OBJECT) {
            Log.e(NabstaApplication.LOG_TAG, "Error Writing bytes to Mix Track");
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
        trackStatusListener.loadTrackBitMap(track,isRecording);
    }


    @Override
    public void recordTrackClicked(boolean record) {
        setIsRecording(record);
    }

    public void pauseTrack(){
        Log.d(NabstaApplication.LOG_TAG, "pauseTrack() called.");

        //callListenersTrackComplete();
        if(audioRecord != null){
            isRecording = false;
            //audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if(audioTrack != null) {
            //audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }
    private void stopTrack(boolean isDisplayWaveFormRealTime){
        Log.d(NabstaApplication.LOG_TAG, "stopTrack() called.");

        callListenersTrackComplete(isDisplayWaveFormRealTime);
        if(audioRecord != null){
            isRecording = false;
            //audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if(audioTrack != null) {
            //audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }

    public interface TrackStatusListener{
        void loadTrackBitMap(Track track, boolean realTimeWaveForm);
        void trackBegin(int audioSessionId);
        void trackComplete();
    }

    public interface TrackCompleteListener{
        //for master controller songPlayButton
        void onTrackFinished(int trackCount);
    }

    public void setTrackStatusListener(TrackStatusListener trackStatusListener){
        this.trackStatusListener = trackStatusListener;
        this.trackStatusListener.loadTrackBitMap(track, true);
    }

    private void callListenersTrackComplete(boolean isDisplayWaveFormRealTIme) {
        if(isDisplayWaveFormRealTIme) {
            trackStatusListener.trackComplete();
        }
        trackCompleteListener.onTrackFinished(decreaseTrackCount());
    }

    private void callListenersTrackBegin(int audioSessionId, boolean isDisplayWaveFormRealTIme) {
        increaseTrackCount();
        if(isDisplayWaveFormRealTIme) {
            trackStatusListener.trackBegin(audioSessionId);
        }
    }

    public void setTrackCompleteListener(TrackCompleteListener trackCompleteListener) {
        this.trackCompleteListener = trackCompleteListener;
    }
}
