package com.spazomatic.nabsta.views.controls;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.audio.TrackMessenger;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by samuelsegal on 5/20/15.
 */
public class SongPlayButton extends Button implements View.OnClickListener,
        TrackMessenger.TrackCompleteListener{

    private UIHandler uiHandler;
    private List<TrackMessenger> trackMessengerList;
    private final static int TRACKS_ALL_COMPLETE_STATE = 3;
    @Override
    public void onTrackFinished(int trackCount) {
        Log.d(NabstaApplication.LOG_TAG,String.format("TRACK #%d FINISHED",trackCount));
        if(trackCount == 0){
            Message trackCompleteMessage = uiHandler.obtainMessage(
                    TRACKS_ALL_COMPLETE_STATE, this);
            trackCompleteMessage.sendToTarget();

        }
    }

    public SongPlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
        uiHandler = new UIHandler(Looper.getMainLooper(),this);
    }

    public List<TrackMessenger> getTrackMessengerList() {
        return trackMessengerList;
    }

    public void setTrackMessengerList(List<TrackMessenger> trackMessengerList) {

        this.trackMessengerList = trackMessengerList;

    }

    @Override
    public void onClick(View v) {

        if(!isSelected()){
            if(trackMessengerList != null) {
                Log.d(NabstaApplication.LOG_TAG, String.format(
                        "Playing %d tracks", trackMessengerList.size()));
                for (TrackMessenger trackMessenger : trackMessengerList) {
                    trackMessenger.setTrackCompleteListener(this);
                    Thread trackThread = new Thread(trackMessenger);
                    trackThread.start();
                }
            }
            setSelected(true);
        }else{
            if(trackMessengerList != null) {
                Log.d(NabstaApplication.LOG_TAG, String.format(
                        "Pausing %d tracks", trackMessengerList.size()));
                for (TrackMessenger trackMessenger : trackMessengerList) {
                    trackMessenger.pauseTrack();
                }
            }
            setSelected(false);
        }
    }

    private static class UIHandler extends Handler{
        private WeakReference<SongPlayButton> songPlayButtonWeakReference;

        public UIHandler(Looper looper, SongPlayButton songPlayButton) {
            super(looper);
            this.songPlayButtonWeakReference = new WeakReference<>(songPlayButton);
        }

        @Override
        public void handleMessage(Message msg) {

            SongPlayButton songPlayButton = songPlayButtonWeakReference.get();
            switch(msg.what){
                case TRACKS_ALL_COMPLETE_STATE:{
                    songPlayButton.setSelected(false);
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
