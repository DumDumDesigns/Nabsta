package com.spazomatic.nabsta.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by samuelsegal on 5/16/15.
 */
public class TrackView {

    private final static String LOG_TAG = String.format(
            "Nabsta: %s", TrackView.class.getSimpleName());

    private final SurfaceHolder surfaceHolder;
    private final Measure measure;

    public TrackView(SurfaceHolder holder) {
        this.surfaceHolder = holder;
        measure = new Measure();
    }

    public void draw(byte[] bytes) {
        try {
            Canvas canvas = surfaceHolder.lockCanvas(measure.getBounds());
            if (canvas != null) {
                measure.updateVisualizer(bytes, canvas);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }catch(Exception e){
            Log.e(LOG_TAG,String.format("ERROR in TRACK VIEW DRAW %s",e.getMessage()),e);
        }
    }

    public void clearVisualizer() {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas != null) {
            measure.clearVisualizer(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
    public void setBitmapCanvas(Canvas bitmapCanvas) {
        measure.setBitmapCanvas(bitmapCanvas);
    }

    public void setCanvasBitmap(Bitmap canvasBitmap) {
       measure.setCanvasBitmap(canvasBitmap);
    }

    public void setIdentityMatrix(Matrix identityMatrix) {
        measure.setIdentityMatrix(identityMatrix);
    }
}
