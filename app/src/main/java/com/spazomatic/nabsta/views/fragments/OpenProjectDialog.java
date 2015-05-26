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
import android.widget.TextView;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.actionBar.SongsActionProvider;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.tasks.LoadSongTask;

import java.util.concurrent.ExecutionException;

/**
 * Created by samuelsegal on 5/19/15.
 */
public class OpenProjectDialog extends DialogFragment{

    private OnOpenSongListener resetStudioFragment;

    public interface OnOpenSongListener {
        void onOpenSong(Song song);
    }

    public static OpenProjectDialog newInstance(Long songId){
        OpenProjectDialog openProjectDialog = new OpenProjectDialog();
        Bundle args = new Bundle();
        Log.d(NabstaApplication.LOG_TAG,String.format("Opening Song with id %d",songId));
        args.putLong(SongsActionProvider.SONG_ID, songId);
        openProjectDialog.setArguments(args);
        return openProjectDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        Long songId = getArguments().getLong(SongsActionProvider.SONG_ID);
        Log.d(NabstaApplication.LOG_TAG,String.format("Opening Song with id %d",songId));
        LoadSongTask loadSongTask = new LoadSongTask();
        loadSongTask.execute(songId);

        try {
            final Song song = loadSongTask.get();
            Log.d(NabstaApplication.LOG_TAG,String.format("Opening Project %s",song.getName()));
            View dialogView = inflater.inflate(R.layout.dialog_open_project, null);
            final TextView projectName =  (TextView)dialogView.findViewById(R.id.project_name);
            projectName.setText(song.getName());
            builder.setView(dialogView)
                    .setPositiveButton(R.string.open_project, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            resetStudioFragment.onOpenSong(song);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Canceled
                        }
                    });
        } catch (InterruptedException | ExecutionException e) {
            Log.e(NabstaApplication.LOG_TAG, String.format(
                    "Error selecting song from database with Error Message %s", e.getMessage()),e);
        }
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            resetStudioFragment = (OnOpenSongListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

}
