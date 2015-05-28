package com.spazomatic.nabsta.views;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by samuelsegal on 5/16/15.
 */
public class TrackView {

    private final static String LOG_TAG = String.format(
            "Nabsta: %s", TrackView.class.getSimpleName());

    //TODO: SAve track state in either shared preferences or database
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private Measure measure;


    public TrackView(TrackVisualizerView surfaceView) {

        this.surfaceHolder = surfaceView.getHolder();
        this.surfaceView = surfaceView;
        measure = new Measure();
        measure.init(surfaceView);

    }

    public void draw(byte[] bytes) {
        Canvas canvas = surfaceHolder.lockCanvas(measure.getBounds());
        if (canvas != null) {
            measure.updateVisualizer(bytes, canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void clearVisualizer() {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas != null) {
            measure.clearVisualizer(surfaceView, canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }
}
