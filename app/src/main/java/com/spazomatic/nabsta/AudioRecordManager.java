package com.spazomatic.nabsta;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.os.Message;
import android.util.Log;

import com.spazomatic.nabsta.mediaStateHandlers.MediaStateHandler;
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

    public  static final int DISPLAY_RECORD_BYTES = 2;
    private MediaStateHandler mediaStateHandler;

    public AudioRecordManager(String recordFileName) {
        this.recordFileName = recordFileName;
    }
    public AudioRecordManager(String recordFileName, MediaStateHandler mediaStateHandler) {
        this.recordFileName = recordFileName;
        this.mediaStateHandler = mediaStateHandler;
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
                Message trackCompleteMessage = mediaStateHandler.getUiHandler().obtainMessage(
                        AudioPlaybackManager.TRACK_COMPLETE_STATE, mediaStateHandler);
                trackCompleteMessage.sendToTarget();
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

        final TrackVisualizerView trackVisualizerView = mediaStateHandler.getTrackVisualizerView();
        if(trackVisualizerView != null) {
            trackVisualizerView.reset();
            //trackVisualizerView.setTrackDuration(trackPlayer.getDuration());
            trackVisualizer = new Visualizer(audioSessionID);
            trackVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
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
