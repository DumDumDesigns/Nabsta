package com.spazomatic.nabsta.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.spazomatic.nabsta.audio.TrackMessenger;

/**
 * Created by samuelsegal on 5/20/15.
 */
public class TrackLayout extends LinearLayout{

    private TrackMessenger trackMessenger;

    public TrackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
