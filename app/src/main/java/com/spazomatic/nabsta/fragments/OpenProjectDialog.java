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
import android.widget.TextView;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.actionBar.SongsActionProvider;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.tasks.LoadSongsTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by samuelsegal on 5/19/15.
 */
public class OpenProjectDialog extends DialogFragment{

    private OnOpenSongListener resetStudioFragment;

    public interface OnOpenSongListener {
        void onOpenSong(Song song);
    }

    public static OpenProjectDialog newInstance(Bundle fB){
        OpenProjectDialog lstFrag = new OpenProjectDialog();
        //Bundle args = new Bundle();
        //args.putStringArray("lista", fB.getStringArray("lista"));//the list
        //args.putString("titulo", fB.getString("titulo"));//the title of the list
        lstFrag.setArguments(fB);

        return lstFrag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        Long songId = getArguments().getLong(SongsActionProvider.SONG_ID);//savedInstanceState.getLong(SongsActionProvider.SONG_ID);

        LoadSongsTask loadSongsTask = new LoadSongsTask();
        loadSongsTask.execute(songId);
        List<Song> songs = null;
        try {
             songs = loadSongsTask.get();

        } catch (InterruptedException | ExecutionException e) {
            Log.e(NabstaApplication.LOG_TAG, String.format(
                    "Error selecting song from database with Error Message %s", e.getMessage()),e);
        }
        final Song song = songs != null && !songs.isEmpty() ? songs.get(0) : null;
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
