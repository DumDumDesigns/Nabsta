package com.spazomatic.nabsta.mediaStateHandlers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import com.spazomatic.nabsta.AudioPlaybackManager;
import com.spazomatic.nabsta.AudioRecordManager;
import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.controls.RecordButton;
import com.spazomatic.nabsta.views.TrackVisualizerView;

import java.lang.ref.WeakReference;

/**
 * Created by samuelsegal on 4/23/15.
 */
public class MediaStateHandler {

    private  UIHandler uiHandler;
    private RecordButton recordButton;
    private Button button;
    private Context context;
    private String fileName;
    private boolean isComplete = true;
    private boolean isLooping;
    private boolean isInRecordMode;
    private boolean isOfMasterTrack;
    private static int trackCountForMaster;
    private TrackVisualizerView trackVisualizerView;

    public MediaStateHandler(Context context, Button button, String fileName) {
        this.fileName = fileName;
        this.context = context;
        this.button = button;
        this.uiHandler = new UIHandler(Looper.getMainLooper(),this);
    }
    public MediaStateHandler(Context context, Button button, String fileName, TrackVisualizerView trackVisualizerView){
        this(context, button, fileName);
        this.trackVisualizerView = trackVisualizerView;
    }
    public MediaStateHandler(Context context, Button button, String fileName, TrackVisualizerView trackVisualizerView,RecordButton recordButton) {
        this(context, button, fileName,trackVisualizerView);
        this.recordButton = recordButton;
        this.isInRecordMode = recordButton.isSelected();
        recordButton.setMediaStateHandler(this);
    }
    public MediaStateHandler(Context context, Button button, String fileName, TrackVisualizerView trackVisualizerView,boolean isOfMasterTrack) {
        this(context, button, fileName,trackVisualizerView);
        this.isOfMasterTrack = isOfMasterTrack;
    }



    public TrackVisualizerView getTrackVisualizerView() {
        return trackVisualizerView;
    }

    public static synchronized void increaseTrackCountForMaster(){
        ++trackCountForMaster;
        Log.d(NabstaApplication.LOG_TAG, String.format("InCREASING TRACK COUNT: %d", trackCountForMaster));
    }
    private static synchronized int decreaseTrackCountForMaster(){
        --trackCountForMaster;
        Log.d(NabstaApplication.LOG_TAG, String.format("DECREASING TRACK COUNT: %d", trackCountForMaster));
        return trackCountForMaster;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void setIsLooping(boolean isLooping) {
        this.isLooping = isLooping;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public boolean isInRecordMode() {
        return isInRecordMode;
    }

    public RecordButton getRecordButton() {
        return recordButton;
    }

    public void setRecordButton(RecordButton recordButton) {
        this.recordButton = recordButton;
    }

    public void setIsInRecordMode(boolean isInRecordMode) {
        this.isInRecordMode = isInRecordMode;
    }

    public void complete(){
        Log.d(NabstaApplication.LOG_TAG, "Media state handler completing.");
        isComplete = true;
        try {
            if (isOfMasterTrack) {
                if (decreaseTrackCountForMaster() == 0) {
                    button.setSelected(false);
                }
            } else {
                if(button.isSelected()) {
                    button.setSelected(false);
                }
            }
        }catch(Exception e){
            Log.e(NabstaApplication.LOG_TAG,String.format("Error settingSelect %s",e.getMessage()),e);
        }
    }

    public void begin() {
        Log.d(NabstaApplication.LOG_TAG,"BEGINNNNNNNNNN");

        if(isOfMasterTrack){
            increaseTrackCountForMaster();
        }
        try {
            button.setSelected(true);
        }catch(Exception e){
            Log.e(NabstaApplication.LOG_TAG,String.format("Error settingSelect %s",e.getMessage()),e);
        }

    }

    public Handler getUiHandler() {
        return uiHandler;
    }


    private static class UIHandler extends Handler{
        private WeakReference<MediaStateHandler> mediaStateHandlerWeakReference;

        public UIHandler(Looper looper, MediaStateHandler mediaStateHandler) {
            super(looper);
            this.mediaStateHandlerWeakReference = new WeakReference<>(mediaStateHandler);
        }

        @Override
        public void handleMessage(Message msg) {

            MediaStateHandler mediaStateHandler = mediaStateHandlerWeakReference.get();
            switch(msg.what){
                case AudioPlaybackManager.TRACK_COMPLETE_STATE:{
                    mediaStateHandler.complete();
                    break;
                }
                case AudioRecordManager.DISPLAY_RECORD_BYTES:{
                    mediaStateHandler.updateVisualizer((byte[])msg.obj);
                    break;
                }
                default:{
                    super.handleMessage(msg);
                    break;
                }
            }


        }
    }

    private void updateVisualizer(byte[] buffer) {
        trackVisualizerView.updateVisualizer(buffer);
    }
}
