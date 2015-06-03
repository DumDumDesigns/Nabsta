package com.spazomatic.nabsta.views.actionBar;

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

/**
 * Created by samuelsegal on 5/18/15.
 */
public class CurrentSongActionProvider extends ActionProvider {

    private Context context;
    private MenuItem addTrackMenuItem;
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
        this.context = context;
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

        addTrackMenuItem = subMenu.add(R.string.add_track);
        addTrackMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                final Song song = NabstaApplication.getSongInSession();
                AddTrackTask addTrackTask = new AddTrackTask();
                addTrackTask.execute(song.getId());
                resetStudioFragment.onAddTrack(song);
                return true;
            }
        });
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
