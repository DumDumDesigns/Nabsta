package com.spazomatic.nabsta.mediaStateHandlers;

import android.content.Context;
import android.util.Log;
import android.widget.Button;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.controls.PlayButton;

/**
 * Created by samuelsegal on 4/23/15.
 */
public class MediaStateHandler {
    private Button button;
    private Context context;
    String fileName;
    private boolean isComplete = true;
    private boolean isLooping;
    public MediaStateHandler(Context context, Button button, String fileName) {
        this.fileName = fileName;
        this.context = context;
        this.button = button;
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
        button.setText(context.getResources().getString(R.string.txt_start_playing));
    }

    public Button getButton() {
        return button;
    }

    public void setButton(PlayButton button) {
        this.button = button;
    }

}
