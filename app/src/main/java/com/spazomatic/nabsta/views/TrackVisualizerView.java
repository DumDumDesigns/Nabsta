package com.spazomatic.nabsta.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.audio.TrackMessenger;

/**
 * Created by samuelsegal on 5/16/15.
 */
public class TrackVisualizerView extends SurfaceView implements
        SurfaceHolder.Callback, TrackMessenger.TrackStatusListener, Visualizer.OnDataCaptureListener {

    private Track track;
    private Visualizer trackVisualizer = null;
    public TrackVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(NabstaApplication.LOG_TAG,"Track VisuaLIZER SURFACE Created");
        track = new Track(this);
        Canvas canvas = holder.lockCanvas();
        if(canvas != null){
            canvas.drawColor(Color.CYAN);
            holder.unlockCanvasAndPost(canvas);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(NabstaApplication.LOG_TAG,"Track VisuaLIZER SURFACE CHANged");
        Log.d(NabstaApplication.LOG_TAG,String.format("Track null = %b", track == null) );
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(NabstaApplication.LOG_TAG,"Track VisuaLIZER SURFACE Destroyed");
        Log.d(NabstaApplication.LOG_TAG,String.format("Track null = %b", track == null) );
    }

    public void updateVisualizer(byte[] waveform) {

        try {
            if(track == null){
                track = new Track(this);
            }
            track.draw(waveform);
        }catch(Exception e){
            Log.e(NabstaApplication.LOG_TAG,"Error drawing track",e);
        }
    }
    public void reset(){
        if(track != null) {
            track.clearVisualizer();
        }
    }

    @Override
    public void trackBegin(int audioSessionId) {
        Log.d(NabstaApplication.LOG_TAG,"Setting up the feckin visualizer");
        reset();
        //trackVisualizerView.setTrackDuration(trackPlayer.getDuration());
        trackVisualizer = new Visualizer(audioSessionId);
        trackVisualizer.setEnabled(false);
        trackVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        Log.d(NabstaApplication.LOG_TAG, String.format("CaptureSize: %d", trackVisualizer.getCaptureSize()));
        //TODO: Test Best capture rate, currently set to Visualizer.getMaxCaptureRate(), Android example does Visualizer.getMaxCaptureRate()/2
        int resultOfSetDataCapture = trackVisualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate(), true, false);
        if(Visualizer.SUCCESS == resultOfSetDataCapture) {
            trackVisualizer.setEnabled(true);
        }else{
            //TODO: Handle error for end user.
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error setting dataCapture Listener: %d",
                    resultOfSetDataCapture));
        }

    }

    @Override
    public void trackComplete() {
        if(trackVisualizer != null){
            trackVisualizer.setEnabled(false);
            trackVisualizer.release();
            trackVisualizer = null;
        }
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        updateVisualizer(waveform);
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        //Not implementing yet
    }
}
