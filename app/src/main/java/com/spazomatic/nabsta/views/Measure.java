package com.spazomatic.nabsta.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by samuelsegal on 5/7/15.
 */
public class Measure {

    private Rect bounds = new Rect();
    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();
    private Paint mForePaint = new Paint();
    private Canvas bitmapCanvas;
    private Bitmap canvasBitmap;
    private Matrix identityMatrix;

    public Measure(){
        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.BLUE);
    }

    public void setBitmapCanvas(Canvas bitmapCanvas) {
        this.bitmapCanvas = bitmapCanvas;
    }

    public void setCanvasBitmap(Bitmap canvasBitmap) {
        this.canvasBitmap = canvasBitmap;
    }

    public void setIdentityMatrix(Matrix identityMatrix) {
        this.identityMatrix = identityMatrix;
    }


    public void updateVisualizer(byte[] bytes, Canvas canvas) {
        mBytes = bytes;
        draw(canvas);
    }

    private void draw(Canvas canvas){
        if (mBytes == null) {
            return;
        }
        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }
        int left = bounds.left;
        int right = bounds.right;
        mRect = new Rect(left, bounds.top, right, bounds.bottom);
       // StringBuilder sb = new StringBuilder(mBytes.length);
        for (int i = 0; i < mBytes.length - 1; i++) {
            mPoints[i * 4] = mRect.left + i / (mBytes.length - 1);
            mPoints[i * 4 + 1] = mRect.height() / 2
                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
            mPoints[i * 4 + 2] = mRect.left + (i + 1) / (mBytes.length - 1);
            mPoints[i * 4 + 3] = mRect.height() / 2
                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
            //sb.append(String.format("points %f %f %f %f %n", mPoints[i*4],mPoints[i*4+1],mPoints[i*4+2],mPoints[i*4+3]));
        }
       // Log.d(NabstaApplication.LOG_TAG,sb.toString());
        bounds = new Rect(mRect.left+1,mRect.top,mRect.left+2,mRect.bottom);
        bitmapCanvas.drawLines(mPoints, mForePaint);
        //canvas.drawLines(mPoints, mForePaint);
        canvas.drawBitmap(canvasBitmap,identityMatrix,null);
    }
    public void clearVisualizer( Canvas canvas){
        bounds = new Rect(0,0,1, canvas.getHeight());
        canvas.drawColor(Color.CYAN);
    }


    public Rect getBounds(){
        return bounds;
    }

}
