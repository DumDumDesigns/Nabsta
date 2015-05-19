package com.spazomatic.nabsta.fragments;

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
import com.spazomatic.nabsta.db.Artist;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.dao.DaoSession;

/**
 * Created by samuelsegal on 5/18/15.
 */
public class NewProjectDialog extends DialogFragment{

    private String songNameValue;
    private String artistNameValue;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
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
                        createSong(songNameValue,artistNameValue);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void createSong(String songNameValue, String artistNameValue) {
        try {
            DaoSession daoSession = NabstaApplication.getInstance().getDaoSession();
            Artist artist = new Artist();
            artist.setName(artistNameValue);
            daoSession.getArtistDao().insert(artist);

            Song song = new Song();
            song.setName(songNameValue);
            song.setArtist(artist);
            daoSession.getSongDao().insert(song);
        }catch(Exception e){
            Log.e(NabstaApplication.LOG_TAG,"Error Saving to Database",e);
        }
    }
}
