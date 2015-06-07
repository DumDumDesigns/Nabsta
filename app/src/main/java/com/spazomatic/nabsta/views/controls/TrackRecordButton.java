package com.spazomatic.nabsta.views.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spazomatic.nabsta.NabstaApplication;

/**
 * Created by samuelsegal on 5/20/15.
 */
public class TrackRecordButton extends Button implements View.OnClickListener{

    private OnRecordTrackListener onRecordTrackListener;

    public TrackRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    public interface OnRecordTrackListener{
        void recordTrackClicked(boolean record);
    }

    @Override
    public void onClick(View v) {
        Log.d(NabstaApplication.LOG_TAG,String.format("Selecting Record BUtton %b", !isSelected()));
        if(isSelected()){
            setSelected(false);
            onRecordTrackListener.recordTrackClicked(false);
        }else{
            setSelected(true);
            onRecordTrackListener.recordTrackClicked(true);
        }
    }

    public void setOnRecordTrackListener(OnRecordTrackListener trackMessenger) {
        this.onRecordTrackListener = trackMessenger;
    }
}
