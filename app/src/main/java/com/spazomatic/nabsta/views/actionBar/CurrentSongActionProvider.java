package com.spazomatic.nabsta.views.actionBar;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.tasks.AddTrackTask;
import com.spazomatic.nabsta.tasks.MixTracksTask;
import com.spazomatic.nabsta.views.fragments.DeleteTracksDialog;

import java.util.concurrent.ExecutionException;

/**
 * Created by samuelsegal on 5/18/15.
 */
public class CurrentSongActionProvider extends ActionProvider {


    public static final String SONG_ID = "songId";
    private OnAddTrackListener resetStudioFragment;

    public interface OnAddTrackListener{
        void onAddTrack(Song song);
    }
    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public CurrentSongActionProvider(Context context) {
        super(context);
        Log.d(NabstaApplication.LOG_TAG, "CurrentSong ActionProvider onCreateActionView");
        resetStudioFragment = (OnAddTrackListener)((ContextThemeWrapper)context).getBaseContext();
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.clear();

        MenuItem addTrackMenuItem = subMenu.add(R.string.add_track);
        addTrackMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addTrackMenuItemClick();
                return true;
            }
        });

        MenuItem deleteTrackMenuItem = subMenu.add(R.string.delete_track);
        deleteTrackMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                deleteTrackMenuItemClick();
                return true;
            }
        });

        MenuItem mixTracks = subMenu.add(R.string.mix_tracks_to_master);
        mixTracks.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mixTracksItemClick();
                return true;
            }
        });
    }



    private void deleteTrackMenuItemClick() {
        Activity activity = (Activity)((ContextThemeWrapper)getContext()).getBaseContext();
        DeleteTracksDialog manageProjectsDialog = new DeleteTracksDialog();
        manageProjectsDialog.show(activity.getFragmentManager(), "DeleteTracksDialog");
    }

    private void addTrackMenuItemClick(){
        Song song = NabstaApplication.getInstance().getSongInSession();
        AddTrackTask addTrackTask = new AddTrackTask();
        addTrackTask.execute(song.getId());
        try {
            song = addTrackTask.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error adding track with Error Message %s",e.getMessage()),e);
        }
        if(resetStudioFragment == null){
            resetStudioFragment = (OnAddTrackListener)(
                    (ContextThemeWrapper)getContext()).getBaseContext();
        }
        resetStudioFragment.onAddTrack(song);
    }

    private void mixTracksItemClick() {
        MixTracksTask mixTracksTask = new MixTracksTask();
        mixTracksTask.execute();
    }
    @Override
    public boolean onPerformDefaultAction() {
        return super.onPerformDefaultAction();
    }


    @Override
    public boolean hasSubMenu() {
        return true;
    }



}
