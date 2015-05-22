package com.spazomatic.nabsta.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.audio.TrackMessenger;

import java.util.List;

/**
 * Created by samuelsegal on 5/20/15.
 */
public class SongPlayButton extends Button implements View.OnClickListener{

    List<TrackMessenger> trackMessengerList;

    public SongPlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
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
}
