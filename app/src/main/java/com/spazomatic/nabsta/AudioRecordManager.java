package com.spazomatic.nabsta;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.util.Log;

import com.spazomatic.nabsta.views.TrackVisualizerView;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by samuelsegal on 4/16/15.
 */
public class AudioRecordManager implements Runnable, MediaRecorder.OnErrorListener,
        MediaRecorder.OnInfoListener{
    private volatile boolean isRecording;
    private String recordFileName;
    private MediaRecorder mRecorder = null;
    private static final int FREQUENCY = 44100;
    private static final int MIN_BUFF_SIZE = AudioRecord.getMinBufferSize(
            FREQUENCY, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    private Visualizer trackVisualizer = null;
    private final TrackVisualizerView trackVisualizerView;
    public AudioRecordManager(String recordFileName) {
        this.recordFileName = recordFileName;
        this.trackVisualizerView = null;
    }
    public AudioRecordManager(String recordFileName, TrackVisualizerView trackVisualizerView) {
        this.recordFileName = recordFileName;
        this.trackVisualizerView = trackVisualizerView;
    }
    @Override
    public void run() {
        Log.d(NabstaApplication.LOG_TAG, "AudioRecordManager Running: " + recordFileName);
        try {
            recordWithAudioRecorder();
            //recordWithMediaRecorder();
        }catch(Exception e){
            Log.d(NabstaApplication.LOG_TAG, String.format(
                    "Error in run of AudioRecordManager: %s", e.getMessage()), e);
        }
    }

    private void recordWithAudioRecorder() throws IOException{
        File file = new File(recordFileName);

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
        try {

            byte[] buffer = new byte[MIN_BUFF_SIZE];
            audioRecord.startRecording();
            setUpVisualizer(audioRecord.getAudioSessionId());
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, MIN_BUFF_SIZE);
                for (int i = 0; i < bufferReadResult; i++)
                    dos.writeByte(buffer[i]);
            }
            Log.d(NabstaApplication.LOG_TAG,String.format(
                    "Finish Recording File %s", file.getAbsolutePath()));
        } catch (IOException e) {
            Log.e(NabstaApplication.LOG_TAG, String.format(
                    "Error Recording File %s",file.getAbsolutePath()), e);
        }finally{
            audioRecord.stop();
            audioRecord.release();
            dos.close();
        }
    }

    private void recordWithMediaRecorder(){
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

    private void setUpVisualizer(int audioSessionID){
        final TrackVisualizerView trackVisualizerView = this.trackVisualizerView;
        if(trackVisualizerView != null) {
            trackVisualizerView.clearCanvas();
            //trackVisualizerView.setTrackDuration(trackPlayer.getDuration());
            trackVisualizer = new Visualizer(audioSessionID);
            trackVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

            //TODO: Test Best capture rate, currently set to Visualizer.getMaxCaptureRate(), Android example does Visualizer.getMaxCaptureRate()/2
            int resultOfSetDataCapture = trackVisualizer.setDataCaptureListener(
                    new Visualizer.OnDataCaptureListener() {
                        @Override
                        public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
                                                          int samplingRate) {
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
    }
    public boolean isRecording() {
        return isRecording;
    }

    public void setIsRecording(boolean isRecording) {
        this.isRecording = isRecording;
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
    public void onError(MediaRecorder mr, int what, int extra) {
        Log.e(NabstaApplication.LOG_TAG, String.format(
                "MediaPlayer.OnErrorListener  what: %d: extra: %d", what, extra));
        stopRecording();
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        Log.e(NabstaApplication.LOG_TAG, String.format(
                "MediaPlayer.OnInfoListener  what: %d: extra: %d", what, extra));
    }
}
