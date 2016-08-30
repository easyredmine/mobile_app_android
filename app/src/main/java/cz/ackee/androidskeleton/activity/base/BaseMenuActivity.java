package cz.ackee.androidskeleton.activity.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.activity.LoginActivity;
import cz.ackee.androidskeleton.activity.NewTaskActivity;
import cz.ackee.androidskeleton.db.DatabaseHelper;
import cz.ackee.androidskeleton.event.SelectProject;
import cz.ackee.androidskeleton.fragment.LoginFragment;
import cz.ackee.androidskeleton.fragment.ProjectDetailFragment;
import cz.ackee.androidskeleton.fragment.TasksFragment;
import cz.ackee.androidskeleton.fragment.base.IBaseFragment;
import cz.ackee.androidskeleton.iface.OnMenuItemClickListener;
import cz.ackee.androidskeleton.rest.RestServiceGenerator;
import cz.ackee.androidskeleton.utils.AccountType;
import cz.ackee.androidskeleton.utils.Storage;
import cz.ackee.androidskeleton.utils.Utils;

/**
 * Base activity with menu drawer Created by David Bilik[david.bilik@ackee.cz] on {5. 2. 2015}
 */
public class BaseMenuActivity extends BaseFragmentActivity implements OnMenuItemClickListener {
    public static final String TAG = BaseMenuActivity.class.getName();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected View onCreateContentView() {
        return (FrameLayout) LayoutInflater.from(this).inflate(R.layout.activity_menu, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Display display = getWindowManager().getDefaultDisplay();
        // new api has method getSize, but we support since api 8
        int width = display.getWidth();
        View v = findViewById(R.id.fragmentMenu);
        v.setLayoutParams(new DrawerLayout.LayoutParams((int) (width * 0.8),
                DrawerLayout.LayoutParams.MATCH_PARENT, Gravity.START));
        setSupportActionBar((Toolbar) findViewById(R.id.toolBar));
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        Toolbar t = (Toolbar) findViewById(R.id.toolBar);
        if (((IBaseFragment) getCurrentFragment()).hasHamburgerMenu()) {
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(final int pos) {
        Fragment f = null;
        switch (pos) {
            case cz.ackee.androidskeleton.Configuration.MENU_POS_NEW_ISSUE:
                // f = new NewTaskFragment();
                NewTaskActivity.start(this);
                break;
            case cz.ackee.androidskeleton.Configuration.MENU_POS_TASKS:
                f = new TasksFragment();
                break;
            case cz.ackee.androidskeleton.Configuration.MENU_POS_LOGOUT:
                logout();
                break;
        }
        if (f != null && ((IBaseFragment) getCurrentFragment()).getPositionInMenu() != pos) {
            replaceFragment(f);
        }
        mListener = null;
        mDrawerLayout.closeDrawers();
    }

    private void logout() {
        new MaterialDialog.Builder(this).title(R.string.logout_title).content(R.string.logout_content)
                .positiveText(R.string.agree).negativeText(R.string.disagree).callback(
                new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        int oldAccType = Storage.getAccountType();
                        Storage.setLogged(false);
                        Storage.setAccountType(null);
                        Storage.setCredentials(null);
                        Storage.setFilter("");


                        // Clears database
                        new Thread(new Runnable() {
                            public void run() {
                                DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                                helper.clearTables();
                                helper.close();
                                Utils.getTempIssueHashFile().delete();
                            }
                        }).start();

                        RestServiceGenerator.invalidate();
                        BaseFragmentActivity.startActivity(BaseMenuActivity.this, LoginFragment.class.getName(), LoginActivity.class, LoginFragment.getBundle(oldAccType));
                        BaseMenuActivity.this.finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }
                }).show();
    }

    DrawerLayout.DrawerListener mListener;

    @Override
    public int getSelectedPosition() {
        return ((IBaseFragment) getCurrentFragment()).getPositionInMenu();
    }

    public void setNavIcon(boolean hamburger) {
        mDrawerToggle.setDrawerIndicatorEnabled(hamburger);
    }

    public void onEvent(SelectProject event) {
        replaceFragment(ProjectDetailFragment.newInstance(event.id));
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

}
