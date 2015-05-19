package com.spazomatic.nabsta.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.tasks.LoadSongsTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by samuelsegal on 5/18/15.
 */
public class ManageProjectsDialog extends DialogFragment{


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LoadSongsTask loadSongsTask = new LoadSongsTask();
        loadSongsTask.execute();
        List<Song> songs = null;
        try {
            songs = loadSongsTask.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error loading songs with Error message %s", e.getMessage()),e);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_manage_projects, null);
        final ListView songListView = (ListView)dialogView.findViewById(R.id.song_list_view);

        SongListAdapter songAdapter = new SongListAdapter(getActivity(),
                android.R.layout.simple_list_item_1, songs);

        songListView.setAdapter(songAdapter);
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Song  song    = (Song) songListView.getItemAtPosition(position);
                Log.d(NabstaApplication.LOG_TAG,String.format("Song %s clicked",song.getName()));
            }

        });

        builder.setView(dialogView)
                .setPositiveButton(R.string.save_changes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO:Implement Manage projects Save Action
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Canceled
                    }
                });
        return builder.create();
    }

    private class SongListAdapter extends ArrayAdapter<Song> {

        private final Context context;
        private final List<Song> songs;
        public SongListAdapter(Context context, int textViewResourceId,List<Song> objects)
        {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.songs = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_view_songs, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.project_name);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.delete_project_btn);
            textView.setText(songs.get(position).getName());
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.delete_button_24_red));
            return rowView;
        }

        @Override
        public long getItemId(int position) {
            return songs.get(position).getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
