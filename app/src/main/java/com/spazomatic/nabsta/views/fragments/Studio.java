package com.spazomatic.nabsta.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.audio.TrackMessenger;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.Track;
import com.spazomatic.nabsta.views.TrackVisualizerView;
import com.spazomatic.nabsta.views.controls.SongPlayButton;
import com.spazomatic.nabsta.views.controls.TrackMuteButton;
import com.spazomatic.nabsta.views.controls.TrackRecordButton;

import java.util.List;

public class Studio extends Fragment {

    public Studio() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private TrackMessenger[] createTrackMessengerList(Song song) {

        if (song != null) {
            Log.d(NabstaApplication.LOG_TAG, String.format(
                    "Loading Song %s with %d tracks",
                    song.getName(),
                    song.getTracks().size()));
            final List<Track> trackList = song.getTracks();
            TrackMessenger[] trackMessengerList = new TrackMessenger[trackList.size()];
            //trackMessengerList = new ArrayList<>(trackList.size());
            int trackCount = 0;
            for (Track track : trackList) {
                TrackMessenger trackMessenger = new
                        TrackMessenger(track);
                trackMessenger.setTrackID(track.getId());
                trackMessengerList[trackCount++] = trackMessenger;
            }
            return trackMessengerList;
        }
        return null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(NabstaApplication.LOG_TAG,
                "<<<<<<<<<<<<<<<<LOW MEMORY IN STUDIO FRAGMENT>>>>>>>>>>>>");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View studioView = inflater.inflate(R.layout.fragment_studio, null);
        return studioView;
    }

    public void setProject(Song song){
        View studioView = getView();
        final TrackMessenger[] trackMessengerList = createTrackMessengerList(song);
        final ListView trackListView =
            (ListView) studioView.findViewById(R.id.list_view_tracks);

        final TrackListAdapter songAdapter = new TrackListAdapter(
                getActivity(), trackMessengerList);
        trackListView.setAdapter(songAdapter);
        final SongPlayButton songPlayButton =
                (SongPlayButton)studioView.findViewById(R.id.songPlayBtn);
        songPlayButton.setTrackMessengerList(trackMessengerList);

    }

    private static class SongViewHolder {
        final TrackMuteButton trackMuteButton;
        final TrackRecordButton trackRecordButton;
        final TrackVisualizerView trackVisualizerView;
        SongViewHolder(View rowView){

            trackMuteButton =
                    (TrackMuteButton) rowView.findViewById(R.id.track_mute_btn);
            trackRecordButton =
                    (TrackRecordButton) rowView.findViewById(R.id.recordBtn);
            trackVisualizerView =
                    (TrackVisualizerView) rowView.findViewById(R.id.trackVisualizer);

            rowView.setTag(this);
        }
        void setListeners(TrackMessenger trackMessenger){
            trackMessenger.setTrackStatusListener(trackVisualizerView);
            trackMuteButton.setOnMuteTrackListener(trackMessenger);
            trackRecordButton.setOnRecordTrackListener(trackMessenger);
        }
    }

    private class TrackListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private TrackMessenger[] trackMessengers;
        public TrackListAdapter(Context context, TrackMessenger[] objects) {
            //super(context, objects);
            this.layoutInflater = LayoutInflater.from(context);
            trackMessengers = objects;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final View rowView = convertView != null ?
                    convertView :
                    layoutInflater.inflate(R.layout.list_view_track, null);
            final SongViewHolder viewHolder = convertView == null ?
                    new SongViewHolder(rowView) :
                    (SongViewHolder) rowView.getTag();
            viewHolder.setListeners(getItem(position));

            return rowView;
            /*
            TrackLayout view;
            if (convertView == null) {
                view = (TrackLayout) layoutInflater.inflate(R.layout.list_view_track, null);
            } else {
                view = (TrackLayout) convertView;
            }
            TrackMessenger item = getItem(position);
            view.showTrack(item);

            return view;
            */
        }
        @Override
        public int getCount() {
            return trackMessengers.length;
        }

        @Override
        public TrackMessenger getItem(int position) {
            return trackMessengers[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


    }
}
