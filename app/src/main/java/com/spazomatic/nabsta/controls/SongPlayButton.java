package com.spazomatic.nabsta.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.spazomatic.nabsta.audio.TrackMessanger;

import java.util.List;

/**
 * Created by samuelsegal on 5/20/15.
 */
public class SongPlayButton extends Button {

    List<TrackMessanger> trackMessangerList;

    public SongPlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            for(TrackMessanger trackMessanger : trackMessangerList){
                Thread trackThread = new Thread(trackMessanger);
                trackThread.start();
            }
        }
    };

    public List<TrackMessanger> getTrackMessangerList() {
        return trackMessangerList;
    }

    public void setTrackMessangerList(List<TrackMessanger> trackMessangerList) {
        this.trackMessangerList = trackMessangerList;
    }
}
