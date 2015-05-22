package com.spazomatic.nabsta.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spazomatic.nabsta.NabstaApplication;

/**
 * Created by samuelsegal on 5/20/15.
 */
public class TrackMuteButton extends Button {

    private OnMuteTrackListener onMuteTrackListener;
    //private TrackMessenger trackMessenger;
    public TrackMuteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(onClickListener);
    }

    public interface OnMuteTrackListener {
        void onMuteTrackClicked(boolean isMute);
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(NabstaApplication.LOG_TAG, String.format("Mute track clicked %b", isSelected()));
            if(isSelected()){
                setSelected(false);
                onMuteTrackListener.onMuteTrackClicked(false);
            }else{
                setSelected(true);
                onMuteTrackListener.onMuteTrackClicked(true);
            }

        }
    };


    public void setOnMuteTrackListener(OnMuteTrackListener trackMessenger) {
        this.onMuteTrackListener = trackMessenger;
    }
}
