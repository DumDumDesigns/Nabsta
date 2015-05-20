package com.spazomatic.nabsta.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.spazomatic.nabsta.audio.TrackMessanger;

/**
 * Created by samuelsegal on 5/20/15.
 */
public class TrackMuteButton extends Button {

    private OnMuteTrackListener onMuteTrackListener;
    private TrackMessanger trackMessanger;
    public TrackMuteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(onClickListener);
    }

    public interface OnMuteTrackListener {
        void onMuteTrackListener(boolean isMute);
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(trackMessanger.isMuted()) {
                onMuteTrackListener.onMuteTrackListener(false);
            }else{
                onMuteTrackListener.onMuteTrackListener(true);
            }
        }
    };

    public TrackMessanger getTrackMessanger() {
        return trackMessanger;
    }

    public void setTrackMessanger(TrackMessanger trackMessanger) {
        this.trackMessanger = trackMessanger;
    }
}
