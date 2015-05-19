package com.spazomatic.nabsta;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import com.spazomatic.nabsta.fragments.NewProjectDialog;

/**
 * Created by samuelsegal on 5/18/15.
 */
public class NabstaActionProvider extends ActionProvider
        implements MenuItem.OnMenuItemClickListener{

    Context context;
    private MenuItem newProjectMenuItem;
    private MenuItem deleteProjectMenuItem;
    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public NabstaActionProvider(Context context) {
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
        //TODO: Add Icons per action like X for delete
        //MenuInflater menuInflater = ((Activity)((ContextThemeWrapper)context)
        //       .getBaseContext()).getMenuInflater();
        //menuInflater.inflate(R.menu.menu_project,subMenu);
        newProjectMenuItem = subMenu.add(R.string.new_project);
        newProjectMenuItem.setOnMenuItemClickListener(this);
        newProjectMenuItem.setIcon(R.drawable.ic_launcher);

        deleteProjectMenuItem = subMenu.add(R.string.delete_project);
        deleteProjectMenuItem.setOnMenuItemClickListener(this);
        deleteProjectMenuItem.setIcon(R.drawable.delete_button_24_red);
    }

    @Override
    public boolean onPerformDefaultAction() {
        return super.onPerformDefaultAction();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final String itemTitle = item.getTitle().toString();
        Log.d(NabstaApplication.LOG_TAG,String.format("Item Title: %s",itemTitle));
        if(itemTitle.equals(newProjectMenuItem.getTitle())) {
            createProject();
        }else if(itemTitle.equals(deleteProjectMenuItem.getTitle())){
            Toast.makeText(context, "Delete Project", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private void createProject() {
        Activity activity = (Activity)((ContextThemeWrapper)context).getBaseContext();

        DialogFragment newProjectDialog = new NewProjectDialog();
        newProjectDialog.show(activity.getFragmentManager(),"NewProjectDialog");
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }
}
