package com.spazomatic.nabsta.mediaStateHandlers;

import android.content.Context;
import android.util.Log;
import android.widget.Button;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.views.TrackVisualizerView;

/**
 * Created by samuelsegal on 4/23/15.
 */
public class MediaStateHandler {
    private Button button;
    private Context context;
    private String fileName;
    private boolean isComplete = true;
    private boolean isLooping;
    private boolean isOfMasterTrack;
    private static int trackCountForMaster;
    private TrackVisualizerView trackVisualizerView;
    public MediaStateHandler(Context context, Button button, String fileName) {
        this.fileName = fileName;
        this.context = context;
        this.button = button;
    }
    public MediaStateHandler(Context context, Button button, String fileName, TrackVisualizerView trackVisualizerView){
        this(context, button, fileName);
        this.trackVisualizerView = trackVisualizerView;
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

    public void complete(){
        Log.d(NabstaApplication.LOG_TAG, "Media state handler completing.");
        isComplete = true;
        if(isOfMasterTrack){
            if (decreaseTrackCountForMaster() == 0) {
                button.setSelected(false);
            }
        }else{
            button.setSelected(false);
        }
    }

    public void begin() {
        if(isOfMasterTrack){
            increaseTrackCountForMaster();
        }
        button.setSelected(true);
    }
}
