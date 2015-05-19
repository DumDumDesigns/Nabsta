package com.spazomatic.nabsta.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.controls.MasterPlayButton;
import com.spazomatic.nabsta.controls.PlayButton;
import com.spazomatic.nabsta.controls.RecordButton;
import com.spazomatic.nabsta.views.TrackVisualizerView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Studio.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Studio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Studio extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SONG_NAME = "songName";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String songName;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param songName Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Studio.
     */
    // TODO: Rename and change types and number of parameters
    public static Studio newInstance(String songName, String param2) {
        Studio fragment = new Studio();
        Bundle args = new Bundle();
        args.putString(SONG_NAME, songName);
        args.putString(ARG_PARAM2, param2);
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
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_studio, container, false);
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
        try {
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
        }catch(Exception e){
            Log.e(NabstaApplication.LOG_TAG,"Error Creating Studio...", e);
        }

    }
}
