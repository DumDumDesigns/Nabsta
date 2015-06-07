package com.spazomatic.nabsta.views.fragments;

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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.Track;
import com.spazomatic.nabsta.tasks.DeleteTracksTask;
import com.spazomatic.nabsta.views.controls.TrackRecordButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by samuelsegal on 5/18/15.
 */
public class DeleteTracksDialog extends DialogFragment{

    private List<Track> listOfTracksToDelete;
    private int countOfTracksToDelete;
    Song song;
    private OnDeleteTracksListener onDeleteTracksListener;
    public interface OnDeleteTracksListener{
        void onDeleteTracks(Song song);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        onDeleteTracksListener = (OnDeleteTracksListener)getActivity();
        song = NabstaApplication.getInstance().getSongInSession();
        List<Track> trackList = song.getTracks();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_manage_projects, null);
        final ListView songListView = (ListView)dialogView.findViewById(R.id.song_list_view);

        TrackListAdapter trackListAdapter = new TrackListAdapter(
                getActivity(), R.layout.list_view_delete_tracks, trackList);

        songListView.setAdapter(trackListAdapter);

        builder.setView(dialogView)
                .setPositiveButton(R.string.delete_tracks, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DeleteTracksTask deleteTracksTask = new DeleteTracksTask();
                            Track[] tracksToDelete = new Track[countOfTracksToDelete];

                            for (int i = 0; i < listOfTracksToDelete.size(); i++) {
                                if (listOfTracksToDelete.get(i) != null) {
                                    Track trackForDeletion = listOfTracksToDelete.get(i);
                                    Log.i(NabstaApplication.LOG_TAG, String.format(
                                            "Deleting Track %s", trackForDeletion.getName()));
                                    tracksToDelete[--countOfTracksToDelete] = trackForDeletion;
                                }
                            }
                            deleteTracksTask.execute(tracksToDelete);
                            try {
                                song = deleteTracksTask.get();
                            } catch (InterruptedException | ExecutionException e) {
                                Log.e(NabstaApplication.LOG_TAG, String.format(
                                        "Error deleting tracks with Error Message %s",
                                        e.getMessage()), e);
                            } finally {
                                onDeleteTracksListener.onDeleteTracks(song);
                            }
                        }
                    }
                ).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Canceled
                    }
                }
        );
                    return builder.create();
                }

        private static class TrackViewHolder {
        final CheckBox deleteCheckBox;
        final TextView trackNameTextView;
        TrackViewHolder(View rowView){
            deleteCheckBox =
                    (CheckBox) rowView.findViewById(R.id.delete_track_checkbox);
            trackNameTextView =
                    (TrackRecordButton) rowView.findViewById(R.id.recordBtn);
            rowView.setTag(this);
        }

    }

    private class TrackListAdapter extends ArrayAdapter<Track> {

        private final LayoutInflater layoutInflater;
        private final List<Track> tracks;
        public TrackListAdapter(Context context, int textViewResourceId,List<Track> objects) {
            super(context, textViewResourceId, objects);
            this.layoutInflater = LayoutInflater.from(context);
            this.tracks = objects;
            listOfTracksToDelete = new ArrayList<>(tracks.size());
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final View rowView = convertView != null ?
                    convertView :
                    layoutInflater.inflate(R.layout.list_view_delete_tracks, null);
            final TrackViewHolder viewHolder = convertView == null ?
                    new TrackViewHolder(rowView) :
                    (TrackViewHolder) rowView.getTag();

            viewHolder.deleteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        ++countOfTracksToDelete;
                        listOfTracksToDelete.add(getItem(position));
                    }else{
                        --countOfTracksToDelete;
                        listOfTracksToDelete.remove(getItem(position));
                    }
                }
            });
            return rowView;
        }

    }

}
