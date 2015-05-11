package com.spazomatic.nabsta.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.spazomatic.nabsta.NabstaApplication;


/**
 * Created by samuelsegal on 5/5/15.
 */
public class TrackVisualizerView extends View {
    private byte[] mBytes;
    private float[] mPoints;
    private float[] allPoints;

    private int trackDuration;
    private Paint mForePaint = new Paint();
    private int measureBeginning, measureEnd, measureHeight;
    private int measureLength = 5;

    public TrackVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mBytes = null;
        allPoints = new float[0];
        measureBeginning = 0;
        measureEnd = measureLength;

        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.BLUE);
        setBackgroundColor(Color.CYAN);
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if(mBytes == null){
                return;
            }
            if (mPoints == null || mPoints.length < mBytes.length * 4) {
                mPoints = new float[mBytes.length * 4];
            }

            int destPoint = allPoints.length;
            int newSize = allPoints.length + mPoints.length;
            allocateAllPoints(allPoints, newSize);

            for (int i = 0; i < mBytes.length-1; i++) {
                mPoints[i*4] = measureBeginning + (i / (mBytes.length - 1));
                mPoints[i *4 + 1] = measureHeight / 2
                        + ((byte) (mBytes[i] + 128)) * (measureHeight / 2) / 128;
                mPoints[i *4 + 2] = measureEnd + ( (i+1) / (mBytes.length - 1));
                mPoints[i *4 + 3] = measureHeight / 2
                        + ((byte) (mBytes[i + 1] + 128)) * (measureHeight / 2) / 128;
            }

            System.arraycopy(mPoints, 0, allPoints, destPoint, mPoints.length);
            canvas.drawLines(allPoints, mForePaint);
            measureBeginning += measureLength;
            measureEnd += measureLength;
            measureHeight=getBottom();

        }catch(Exception e){
            Log.e(NabstaApplication.LOG_TAG, String.format(
                "Error in onDraw - Error Message: %s: Cause: %s: stackTrace:",
                e.getMessage(),e.getCause()),e
            );
        }
    }

    public void setTrackDuration(int trackDuration) {
        this.trackDuration = trackDuration;
    }
    public void clearCanvas(){
        allPoints = new float[0];
        measureBeginning = 0;
        measureEnd = measureLength;
    }
    private void allocateAllPoints (float[] oldArray, int newSize) {
        allPoints = new float[newSize];
        System.arraycopy(oldArray, 0, allPoints, 0, oldArray.length);
    }
}