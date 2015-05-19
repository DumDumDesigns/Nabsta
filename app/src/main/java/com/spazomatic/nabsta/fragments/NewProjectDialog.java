package com.spazomatic.nabsta.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.tasks.CreateSongTask;

/**
 * Created by samuelsegal on 5/18/15.
 */
public class NewProjectDialog extends DialogFragment{

    private String songNameValue;
    private String artistNameValue;
    private OnNewSongListener resetStudioFragment;

    public interface OnNewSongListener {
        void onNewSong(Song song);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_new_project, null);
        final EditText projectName =  (EditText)dialogView.findViewById(R.id.project_name);
        final EditText artistName =  (EditText)dialogView.findViewById(R.id.artist_name);
        builder.setView(dialogView)
                .setPositiveButton(R.string.create_project, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        songNameValue = projectName.getText().toString();
                        artistNameValue = artistName.getText().toString();
                        Song newSong = createSong(songNameValue,artistNameValue);
                        resetStudioFragment.onNewSong(newSong);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Canceled
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            resetStudioFragment = (OnNewSongListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private Song createSong(String songNameValue, String artistNameValue) {
        try {
            CreateSongTask createSongTask = new CreateSongTask();
            String [] params = {songNameValue,artistNameValue};
            createSongTask.execute(params);
            Song song = createSongTask.get();
            return song;
        }catch(Exception e){
            Log.e(NabstaApplication.LOG_TAG,"Error Saving to Database",e);
        }
        return null;
    }
}
