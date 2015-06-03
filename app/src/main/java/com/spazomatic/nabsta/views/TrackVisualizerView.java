package com.spazomatic.nabsta.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.audio.TrackMessenger;
import com.spazomatic.nabsta.db.Track;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

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
    private UIHandler uiHandler;
    private Object lock = new Object();
    public TrackVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        uiHandler = new UIHandler(Looper.getMainLooper(),this);
        NabstaApplication.getInstance().getBaseContext();

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
        Log.d(NabstaApplication.LOG_TAG, String.format("Surface created trackview null = %b", trackView == null));

    try {
        trackView = new TrackView(holder);
        Log.d(NabstaApplication.LOG_TAG, String.format("TrackView Createdd trackview null = %b", trackView == null));
        Log.d(NabstaApplication.LOG_TAG, String.format("bimapCanvas null = %b", bitmapCanvas == null));
        trackView.setBitmapCanvas(bitmapCanvas);
        Log.d(NabstaApplication.LOG_TAG, String.format("canvasBitmap null = %b", canvasBitmap == null));
        trackView.setCanvasBitmap(canvasBitmap);
        Log.d(NabstaApplication.LOG_TAG, String.format("identityMatrix null = %b", identityMatrix == null));
        trackView.setIdentityMatrix(identityMatrix);
        Log.d(LOG_TAG,String.format("THE FECKING TRACK FECKING VIEW IS CREATED WTFEEEEE? IS IT FECKING SHOWN????? %b",isShown()));


        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.DKGRAY);
            loadBitMap(canvas);
            holder.unlockCanvasAndPost(canvas);
        }

    }catch(Exception e){
        Log.e(LOG_TAG,"HMMMMMMMMMMMMM HU");
    }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(NabstaApplication.LOG_TAG, String.format("Surface CHANGED trackview null = %b", trackView == null));

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(NabstaApplication.LOG_TAG, String.format("Surface DEstroyed trackview null = %b", trackView == null));
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
                Log.d(LOG_TAG, "DISPLAY TRACK IMAGE BITMAP");

                if (canvas != null) {
                    Log.d(LOG_TAG, String.format(
                            "Display ime %d wide %d", trackImage.getWidth(), trackImage.getHeight()));

                    canvas.drawBitmap(trackImage, 0, 0, null);

                }
            }
        }catch(Exception e){
            Log.e(LOG_TAG,String.format("Error with track bitmap: Error Message %s",e.getMessage()),e);
        }
    }
    @Override
    public void loadTrackBitMap(Track track, boolean isDisplayWaveFormRealTime) {
        this.track = track;
        this.isDisplayWaveFormRealTime = isDisplayWaveFormRealTime;

    }

    @Override
    public void trackBegin(int audioSessionId) {
        Log.d(NabstaApplication.LOG_TAG, "Setting up the feckin visualizer");
        reset();
        //Message trackCompleteMessage = uiHandler.obtainMessage(
        //        RESET_VISUALIZER, this);
        //trackCompleteMessage.sendToTarget();
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
        Log.d(LOG_TAG, String.format("SAving trackView image %s ", track.getBitmap_file_name()));

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
        //this.wf = waveform;
        //Message trackCompleteMessage = uiHandler.obtainMessage(
                //UPDATE_VISUALIZER, this);
        //trackCompleteMessage.sendToTarget();
        updateVisualizer(waveform);
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

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
    private static final int UPDATE_VISUALIZER = 6;
    private static final int RESET_VISUALIZER = 7;

    private static class UIHandler extends Handler {
        private WeakReference<TrackVisualizerView> trackVisualizerViewWeakReference;

        public UIHandler(Looper looper, TrackVisualizerView trackVisualizerView) {
            super(looper);
            this.trackVisualizerViewWeakReference = new WeakReference<>(trackVisualizerView);
        }

        @Override
        public void handleMessage(Message msg) {

            TrackVisualizerView trackVisualizerView = trackVisualizerViewWeakReference.get();
            switch(msg.what){
                case UPDATE_VISUALIZER:{
                    trackVisualizerView.updateVisualizer(trackVisualizerView.wf);
                    break;
                }
                case RESET_VISUALIZER: {
                    trackVisualizerView.reset();
                    break;
                }
                default:{
                    super.handleMessage(msg);
                    break;
                }
            }


        }
    }
}
