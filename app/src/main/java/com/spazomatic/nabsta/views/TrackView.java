package com.spazomatic.nabsta.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.SurfaceHolder;

/**
 * Created by samuelsegal on 5/16/15.
 */
public class TrackView {

    private final static String LOG_TAG = String.format(
            "Nabsta: %s", TrackView.class.getSimpleName());

    //TODO: SAve track state in either shared preferences or database
    private SurfaceHolder surfaceHolder;

    private Measure measure;


    public TrackView(SurfaceHolder holder) {

        this.surfaceHolder = holder;

        measure = new Measure();
        measure.init();

    }

    public void draw(byte[] bytes) {
        Canvas canvas = surfaceHolder.lockCanvas(measure.getBounds());
        if (canvas != null) {
            measure.updateVisualizer(bytes, canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
    public void draw(double[] bytes) {
        Canvas canvas = surfaceHolder.lockCanvas(measure.getBounds());
        if (canvas != null) {
            measure.updateVisualizer(bytes, canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void clearVisualizer() {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas != null) {
            measure.clearVisualizer( canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }
    public void setBitmapCanvas(Canvas bitmapCanvas) {
        measure.setBitmapCanvas( bitmapCanvas);
    }

    public void setCanvasBitmap(Bitmap canvasBitmap) {
       measure.setCanvasBitmap(canvasBitmap);
    }

    public void setIdentityMatrix(Matrix identityMatrix) {
        measure.setIdentityMatrix(identityMatrix);
    }
}
