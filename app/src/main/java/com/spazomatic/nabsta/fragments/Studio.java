package com.spazomatic.nabsta.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.actionBar.SongsActionProvider;
import com.spazomatic.nabsta.audio.TrackMessanger;
import com.spazomatic.nabsta.controls.SongPlayButton;
import com.spazomatic.nabsta.controls.TrackMuteButton;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.Track;
import com.spazomatic.nabsta.tasks.LoadSongTask;
import com.spazomatic.nabsta.views.TrackVisualizerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Studio.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Studio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Studio extends Fragment {

    private static final String SONG_NAME = "songName";

    private String songName;
    private Long songId;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param songName Song name.
     * @param songId Song id.
     * @return A new instance of fragment Studio.
     */
    // TODO: Rename and change types and number of parameters
    public static Studio newInstance(String songName, Long songId) {
        Studio fragment = new Studio();
        Bundle args = new Bundle();
        args.putString(SONG_NAME, songName);
        args.putLong(SongsActionProvider.SONG_ID, songId);
        fragment.setArguments(args);
        return fragment;
    }

    public Studio() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songName = getArguments().getString(SONG_NAME);
            songId = getArguments().getLong(SongsActionProvider.SONG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View studioView = inflater.inflate(R.layout.fragment_studio, container, false);
        Long songId = getArguments().getLong(SongsActionProvider.SONG_ID);
        Log.d(NabstaApplication.LOG_TAG,String.format("Search with songId %d", songId));
        LoadSongTask loadSongTask = new LoadSongTask();
        loadSongTask.execute(songId);
        try {
            final Song song = loadSongTask.get();
            if(song != null) {
                Log.d(NabstaApplication.LOG_TAG,String.format(
                        "Loading Song %s with %d tracks", song.getName(),
                        song.getTracks().size()));

                final List<Track> trackList = song.getTracks();
                final ListView trackListView =
                        (ListView) studioView.findViewById(R.id.list_view_tracks);

                TrackListAdapter songAdapter = new TrackListAdapter(getActivity(),
                        android.R.layout.simple_list_item_1, trackList);

                trackListView.setAdapter(songAdapter);
                trackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Track track = (Track) trackListView.getItemAtPosition(position);
                        Log.d(NabstaApplication.LOG_TAG, String.format(
                                "Song %s clicked", track.getName()));
                    }

                });

                SongPlayButton songPlayButton =
                        (SongPlayButton) studioView.findViewById(R.id.songPlayBtn);
                songPlayButton.setTrackMessangerList(songAdapter.getTrackMessangerList());
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error creating TrackAdapter with error message %s", e.getMessage()),e);
        }
        return studioView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();

        /*
        PlayButton playTrack1Btn = (PlayButton) getView().findViewById(R.id.play1);
        RecordButton recordButton1 = (RecordButton) getView().findViewById(R.id.record1);
        TrackVisualizerView trackVisualizerView = (TrackVisualizerView) getView().findViewById(R.id.visualizer1);
        recordButton1.prepareTrack();
        playTrack1Btn.prepareTrack(trackVisualizerView, recordButton1);

        PlayButton playTrack2Btn = (PlayButton) getView().findViewById(R.id.play2);
        RecordButton recordButton2 = (RecordButton) getView().findViewById(R.id.record2);
        TrackVisualizerView trackVisualizerView2 = (TrackVisualizerView) getView().findViewById(R.id.visualizer2);
        recordButton2.prepareTrack();
        playTrack2Btn.prepareTrack(trackVisualizerView2, recordButton2);

        PlayButton playTrack3Btn = (PlayButton) getView().findViewById(R.id.play3);
        RecordButton recordButton3 = (RecordButton) getView().findViewById(R.id.record3);

        TrackVisualizerView trackVisualizerView3 = (TrackVisualizerView) getView().findViewById(R.id.visualizer3);
        recordButton3.prepareTrack();
        playTrack3Btn.prepareTrack(trackVisualizerView3, recordButton3);

        MasterPlayButton masterPlayButton = (MasterPlayButton) getView().findViewById(R.id.masterPlayBtn);
        TrackVisualizerView masterVisualizer = (TrackVisualizerView) getView().findViewById(R.id.masterVisualizer);
        masterPlayButton.prepareMasterTrack(trackVisualizerView, trackVisualizerView2, trackVisualizerView3, masterVisualizer);
        */

    }
    private class TrackListAdapter extends ArrayAdapter<Track> {

        private final Context context;
        private final List<Track> tracks;
        //TODO: trackMessangerList may be better as a map...
        private List<TrackMessanger> trackMessangerList;
        public TrackListAdapter(Context context, int textViewResourceId, List<Track> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.tracks = objects;
            this.trackMessangerList = new ArrayList<>(objects.size());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_view_track, parent, false);

            Track track = tracks.get(position);
            TrackMessanger trackMessanger = new TrackMessanger(track);

            LinearLayout trackLayout = (LinearLayout)rowView.findViewById(R.id.track_layout);
            TrackMuteButton trackMuteButton = (TrackMuteButton)rowView.findViewById(R.id.track_mute_btn);
            trackMuteButton.setTrackMessanger(trackMessanger);

            TrackVisualizerView trackVisualizerView =
                    (TrackVisualizerView) rowView.findViewById(R.id.trackVisualizer);
            trackMessanger.setTrackVisualizerView(trackVisualizerView);
            trackMessangerList.add(trackMessanger);

            return rowView;
        }

        @Override
        public long getItemId(int position) {
            return tracks.get(position).getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public List<TrackMessanger> getTrackMessangerList() {
            return trackMessangerList;
        }

        public void setTrackMessangerList(List<TrackMessanger> trackMessangerList) {
            this.trackMessangerList = trackMessangerList;
        }
    }

}
