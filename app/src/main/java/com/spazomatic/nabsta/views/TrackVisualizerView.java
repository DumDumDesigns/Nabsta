package com.spazomatic.nabsta.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.spazomatic.nabsta.NabstaApplication;

/**
 * Created by samuelsegal on 5/16/15.
 */
public class TrackVisualizerView extends SurfaceView implements SurfaceHolder.Callback {

    private Track track;
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

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void updateVisualizer(byte[] waveform) {
        try {
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
}
