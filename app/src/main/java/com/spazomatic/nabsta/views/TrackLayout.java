package com.spazomatic.nabsta.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.audio.TrackMessenger;
import com.spazomatic.nabsta.views.controls.TrackMuteButton;
import com.spazomatic.nabsta.views.controls.TrackRecordButton;

/**
 * Created by samuelsegal on 6/4/15.
 */
public class TrackLayout extends LinearLayout{

    private TrackMuteButton trackMuteButton;
    private TrackRecordButton trackRecordButton;
    private TrackVisualizerView trackVisualizerView;

    public static final TrackMessenger EMPTY = new TrackMessenger(null);
    private TrackMessenger trackMessenger = EMPTY;

    public TrackLayout(Context context) {
        super(context);
    }

    public TrackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        trackMuteButton = (TrackMuteButton)findViewById(R.id.track_mute_btn);
        trackRecordButton = (TrackRecordButton)findViewById(R.id.recordBtn);
        trackVisualizerView = (TrackVisualizerView) findViewById(R.id.trackVisualizer);
    }

    public void showTrack(TrackMessenger trackMessenger){
        Log.d(NabstaApplication.LOG_TAG, "<<<<<<<<<<<<<<SHOWWWWWWWWWWWWW TRACK>>>>>>>>>>>>>>");
        this.trackMessenger = trackMessenger != null ? trackMessenger : EMPTY;
        trackMuteButton.setVisibility(VISIBLE);
        trackRecordButton.setVisibility(VISIBLE);
        trackVisualizerView.setVisibility(VISIBLE);
    }
    public void setListeners(){
        trackMuteButton.setOnMuteTrackListener(this.trackMessenger);
        trackRecordButton.setOnRecordTrackListener(this.trackMessenger);
        this.trackMessenger.setTrackStatusListener(trackVisualizerView);

    }
}
