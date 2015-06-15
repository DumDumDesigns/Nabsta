package com.spazomatic.nabsta.views.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.db.Track;
import com.spazomatic.nabsta.tasks.MixTracksTask;

/**
 * Created by samuelsegal on 5/18/15.
 */
public class MixMasterTrackDialog extends DialogFragment{

    private String masterTrackNameValue;

    private OnMasterTrackCreatedListener masterTrackCreatedListener;

    public interface OnMasterTrackCreatedListener {
        void onMasterTrackCreated(Track masterTrack);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_mix_tracks, null);
        final EditText masterTrackName = (EditText)dialogView.findViewById(R.id.master_track_name);
        final Button mixTracksBtn = (Button)dialogView.findViewById(R.id.mixTracksBtn);
        final ProgressBar mixtrackProgressBar =
                (ProgressBar)dialogView.findViewById(R.id.mixTrackProgressBar);

        mixTracksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                masterTrackNameValue = masterTrackName.getText().toString();
                Track masterTrack = createMasterTrack(masterTrackNameValue, mixtrackProgressBar);
                masterTrackCreatedListener.onMasterTrackCreated(masterTrack);
            }
        });

        builder.setView(dialogView).setPositiveButton(R.string.save_as_master_track,
                new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO:SAve new master as master track or maybe add to database an alternative to save many masters
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //Canceled
                }
            });
        return builder.create();
    }

    private Track createMasterTrack(String masterTrackNameValue, ProgressBar progressBar) {
        try {
            MixTracksTask mixTracksTask = new MixTracksTask(progressBar);
            mixTracksTask.execute(masterTrackNameValue);
            Track masterTrack = mixTracksTask.get();
            return masterTrack;
        }catch(Exception e){
            Log.e(NabstaApplication.LOG_TAG,"Error Saving to Database",e);
        }
        return null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            masterTrackCreatedListener = (OnMasterTrackCreatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

}
