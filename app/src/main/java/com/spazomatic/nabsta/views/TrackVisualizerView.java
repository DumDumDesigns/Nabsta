package com.spazomatic.nabsta.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.audio.TrackMessenger;
import com.spazomatic.nabsta.db.Track;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by samuelsegal on 5/16/15.
 */
public class TrackVisualizerView extends SurfaceView implements SurfaceHolder.Callback,
        TrackMessenger.TrackStatusListener, Visualizer.OnDataCaptureListener {

    private final static String LOG_TAG = String.format(
            "Nabsta: %s", TrackVisualizerView.class.getSimpleName());
    private TrackView trackView;
    private Visualizer trackVisualizer = null;
    private Canvas bitmapCanvas;
    private Bitmap canvasBitmap;
    private Matrix identityMatrix;
    private Track track;
    private boolean isDisplayWaveFormRealTime;

    public TrackVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setDrawingCacheEnabled(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        trackView = new TrackView(holder);
        trackView.setBitmapCanvas(bitmapCanvas);
        trackView.setCanvasBitmap(canvasBitmap);
        trackView.setIdentityMatrix(identityMatrix);

        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.DKGRAY);
            loadBitMap(canvas);
            holder.unlockCanvasAndPost(canvas);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void updateVisualizer(byte[] waveform) {
        try {
            if(trackView != null) {
                trackView.draw(waveform);
            }else{
                Log.e(LOG_TAG,"PRANK CALL!!!!! PRANK CALL!!!!!");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error drawing trackView",e);
        }
    }
    private void reset(){
/*
        Log.d(LOG_TAG, String.format("calling RESET track view null = %b", trackView == null));
        Log.d(LOG_TAG, String.format("Is view Activated %b", isActivated()));
        Log.d(LOG_TAG,String.format("Is view shown %b",isShown()));
        Log.d(LOG_TAG,String.format("Is view enabled %b",isEnabled()));
        Log.d(LOG_TAG,String.format("Is view dirty %b",isDirty()));
        Log.d(LOG_TAG,String.format("Is view focusable %b",isFocusable()));
        Log.d(LOG_TAG,String.format("Is view isHapticFeedbackEnabled %b",isHapticFeedbackEnabled()));
        Log.d(LOG_TAG,String.format("Is view isAccessibilityFocused %b",isAccessibilityFocused()));
        Log.d(LOG_TAG,String.format("Is view drawingCacheEnabled %b", isDrawingCacheEnabled()));
        Log.d(LOG_TAG,String.format("Is view isAttachedToWindow %b",isAttachedToWindow()));
        Log.d(LOG_TAG,String.format("Is view isHardwareAccelerated %b",isHardwareAccelerated()));
        Log.d(LOG_TAG,String.format("Is view isClickable %b",isClickable()));
        Log.d(LOG_TAG,String.format("Is view isDuplicateParentStateEnabled %b",isDuplicateParentStateEnabled()));
        Log.d(LOG_TAG,String.format("Is view isSaveEnabled %b",isSaveEnabled()));
        Log.d(LOG_TAG,String.format("Is view isImportantForAccessibility %b",isImportantForAccessibility()));
        Log.d(LOG_TAG,String.format("Is view isSelected %b",isSelected()));
        Log.d(LOG_TAG,String.format("Is view isInEditMode %b",isInEditMode()));
        */
        if(trackView != null) {
            trackView.clearVisualizer();
        }else{
            Log.e(LOG_TAG, "WHYYYYYYYY THE FUCKKKKKKKKKK IS THE FUCKING TRACKVIEW FUCKING NULL FUCK");

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        //Log.d(LOG_TAG,String.format("ONMEASURE w: %d: h: %d",w,h));
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas();
        bitmapCanvas.drawColor(Color.DKGRAY);
        bitmapCanvas.setBitmap(canvasBitmap);

        identityMatrix = new Matrix();

        setMeasuredDimension(w, h);
    }

    private void loadBitMap(Canvas canvas){
        try {
            Bitmap trackImage = BitmapFactory.decodeFile(track.getBitmap_file_name());
            if (trackImage != null && isDisplayWaveFormRealTime) {
                if (canvas != null) {
                    canvas.drawBitmap(trackImage, 0, 0, null);
                }
            }
        }catch(Exception e){
            Log.e(LOG_TAG,String.format(
                    "Error with track bitmap: Error Message %s",e.getMessage()),e);
        }
    }
    @Override
    public void loadTrackBitMap(Track track, boolean isDisplayWaveFormRealTime) {
        this.track = track;
        this.isDisplayWaveFormRealTime = isDisplayWaveFormRealTime;

    }

    @Override
    public void trackBegin(int audioSessionId) {
        reset();
        trackVisualizer = new Visualizer(audioSessionId);
        //trackVisualizer.setEnabled(false);
        trackVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        Log.d(NabstaApplication.LOG_TAG, String.format(
                "CaptureSize: %d", trackVisualizer.getCaptureSize()));
        //TODO: Test Best capture rate, currently set to Visualizer.getMaxCaptureRate(), Android example does Visualizer.getMaxCaptureRate()/2
        int resultOfSetDataCapture = trackVisualizer.setDataCaptureListener(
                this, Visualizer.getMaxCaptureRate(), true, false);
        if (Visualizer.SUCCESS == resultOfSetDataCapture) {
            trackVisualizer.setEnabled(true);
        } else {
            //TODO: Handle error for end user.
            Log.e(NabstaApplication.LOG_TAG, String.format(
                    "Error setting dataCapture Listener: %d",
                    resultOfSetDataCapture));
        }

    }
    @Override
    public void trackComplete() {
        storeImage(canvasBitmap, track.getBitmap_file_name());
        if(trackVisualizer != null){
            trackVisualizer.setEnabled(false);
            trackVisualizer.release();
            trackVisualizer = null;
        }
    }

    private void updateVisualizer(double[] fft) {
        try {
            trackView.draw(fft);
        }catch(Exception e){
            Log.e(LOG_TAG,"Error drawing trackView",e);
        }
    }
    byte[] wf;
    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        updateVisualizer(waveform);
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

    }
    private void storeImage(Bitmap image,String bitmapFileName) {
        try {
            FileOutputStream fos = new FileOutputStream(bitmapFileName);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (IOException e) {
            Log.d(NabstaApplication.LOG_TAG, String.format(
                    "Error Saving Bitmap %s ", e.getMessage()),e);
        }
    }
}
