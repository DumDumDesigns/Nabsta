package com.spazomatic.nabsta.views.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.spazomatic.nabsta.audio.TrackMessenger;

import java.util.List;

/**
 * Created by samuelsegal on 5/20/15.
 */
public class TrackLayout extends LinearLayout{

    private List<TrackMessenger> trackMessengerList;

    public TrackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


}
