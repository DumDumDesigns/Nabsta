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
    }
    public Canvas getBitmapCanvas() {
        return bitmapCanvas;
    }

    public Bitmap getCanvasBitmap() {
        return canvasBitmap;
    }

    public Matrix getIdentityMatrix() {
        return identityMatrix;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(NabstaApplication.LOG_TAG, "Track VisuaLIZER SURFACE Created");
        trackView = new TrackView(this);
        Canvas canvas = getHolder().lockCanvas();
        if(canvas != null){
            canvas.drawColor(Color.DKGRAY);
            getHolder().unlockCanvasAndPost(canvas);
        }
        loadBitMap();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(NabstaApplication.LOG_TAG, "Track VisuaLIZER SURFACE CHANged");
        Log.d(NabstaApplication.LOG_TAG, String.format("Track null = %b", trackView == null));
        loadBitMap();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(NabstaApplication.LOG_TAG, "Track VisuaLIZER SURFACE Destroyed");
        Log.d(NabstaApplication.LOG_TAG, String.format("Track null = %b", trackView == null));
    }

    public void updateVisualizer(byte[] waveform) {

        try {
            if(trackView == null){
                trackView = new TrackView(this);
            }
            trackView.draw(waveform);
        }catch(Exception e){
            Log.e(LOG_TAG,"Error drawing trackView",e);
        }
    }
    private void reset(){
        if(trackView != null) {
            trackView.clearVisualizer();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas();
        bitmapCanvas.setBitmap(canvasBitmap);

        identityMatrix = new Matrix();

        setMeasuredDimension(w, h);
    }

    private void loadBitMap(){
        Bitmap trackImage = BitmapFactory.decodeFile(track.getBitmap_file_name());
        if(trackImage != null && isDisplayWaveFormRealTime){
            Log.d(LOG_TAG,"DISPLAY TRACK IMAGE BITMAP");
            Canvas canvas = getHolder().lockCanvas();
            if(canvas != null){
                Log.d(LOG_TAG,String.format(
                        "Display ime %d wide %d", trackImage.getWidth(), trackImage.getHeight()));
                canvas.drawBitmap(trackImage,0,0,null);
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }
    @Override
    public void loadTrackBitMap(Track track, boolean isDisplayWaveFormRealTime) {
        this.track = track;
        this.isDisplayWaveFormRealTime = isDisplayWaveFormRealTime;

    }

    @Override
    public void trackBegin(int audioSessionId) {
        Log.d(NabstaApplication.LOG_TAG,"Setting up the feckin visualizer");
        reset();

        trackVisualizer = new Visualizer(audioSessionId);
        trackVisualizer.setEnabled(false);
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
        Log.d(LOG_TAG, String.format("SAving trackView image %s ", track.getBitmap_file_name()));

        storeImage(canvasBitmap, track.getBitmap_file_name());
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
    private void storeImage(Bitmap image,String bitmapFileName) {
        try {
            Log.d(LOG_TAG,String.format("Storing track waveform image %s", bitmapFileName));
            FileOutputStream fos = new FileOutputStream(bitmapFileName);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (IOException e) {
            Log.d(NabstaApplication.LOG_TAG, String.format(
                    "Error Saving Bitmap %s ", e.getMessage()),e);
        }
    }

}
