package com.spazomatic.nabsta.views.actionBar;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.spazomatic.nabsta.MainActivity;
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
    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public CurrentSongActionProvider(Context context) {
        super(context);
        this.context = context;

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
                MainActivity activity = (MainActivity)((ContextThemeWrapper)context).getBaseContext();
                final Song song = activity.getSongInSession();
                AddTrackTask addTrackTask = new AddTrackTask();
                addTrackTask.execute(song.getId());
                //TODO:update Studio fragment with new view for track
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
