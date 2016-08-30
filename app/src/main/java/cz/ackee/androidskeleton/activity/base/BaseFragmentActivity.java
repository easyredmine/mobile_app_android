package cz.ackee.androidskeleton.activity.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.event.SelectProject;
import cz.ackee.androidskeleton.fragment.base.IBaseFragment;
import cz.ackee.androidskeleton.utils.AccountType;
import cz.ackee.androidskeleton.utils.Storage;


public class BaseFragmentActivity extends BaseActivity {
    public static final String TAG = "BaseFragmentActivity";

    protected static final String EXTRA_FRAGMENT_NAME = "fragment";
    private static final String EXTRA_ARGUMENTS = "arguments";
    public static final int CONTENT_VIEW_ID = R.id.fragmentContainer;

    public SystemBarTintManager tintManager;
    public Toolbar mToolbar;

    /**
     * gets intent for starting new activity with fragment defined by fragment name and passes extras to the starting intent
     *
     * @param ctx
     * @param fragmentName fragment to instantiate
     * @param args         to pass to the instantiated fragment
     */
    public static Intent generateIntent(Context ctx, String fragmentName, Bundle args) {
        return new Intent(ctx, BaseFragmentActivity.class).putExtra(EXTRA_FRAGMENT_NAME, fragmentName).putExtra(EXTRA_ARGUMENTS, args);
    }


    /**
     * Start specific activity and open fragment defined by name
     *
     * @param ctx
     * @param fragmentName
     * @param activityClass
     */
    public static void startActivity(Context ctx, String fragmentName, Class<?> activityClass, Bundle args) {
        Intent intent = new Intent(ctx, activityClass).putExtra(EXTRA_FRAGMENT_NAME, fragmentName).putExtra(EXTRA_ARGUMENTS, args);
        ctx.startActivity(intent);
    }

    /**
     * starts new activity with fragment defined by fragment name and passes extras to the starting intent
     *
     * @param ctx
     * @param fragmentName fragment to instantiate
     * @param args         to pass to the instantiated fragment
     */
    public static void startActivity(Context ctx, String fragmentName, Bundle args) {
        ctx.startActivity(generateIntent(ctx, fragmentName, args));
    }


    private View mContentView;

    public View getContentView() {
        return mContentView;
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(CONTENT_VIEW_ID);
    }

    /**
     * returns the name of the fragment to be instantiated
     *
     * @return
     */

    protected String getFragmentName() {
        return getIntent().getStringExtra(EXTRA_FRAGMENT_NAME);
    }

    /**
     * instantiates the fragment
     *
     * @return
     */
    protected Fragment instantiateFragment(String fragmentName) {
        return Fragment.instantiate(this, fragmentName);
    }

    protected View onCreateContentView() {
        View v = LayoutInflater.from(this).inflate(getLayoutId(), null);
        mToolbar = (Toolbar) v.findViewById(R.id.toolBar);
        if (mToolbar != null) {
            setSupportActionBar((android.support.v7.widget.Toolbar) mToolbar);
        }
        return v;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);

        if (Storage.getAccountType() == AccountType.REDMINE.value) {
            setTheme(R.style.AppTheme_Redmine_Transluent);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.r_primary_dark));
        } else {
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.er_primary_dark));
        }

        mContentView = onCreateContentView();

        setContentViewInternal(mContentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        String fragmentName = getFragmentName();
        if (fragmentName == null) {
            finish();
            return;
        }

        Bundle args = getIntent().getBundleExtra(EXTRA_ARGUMENTS);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentName);
        if ((fragment == null) && (savedInstanceState == null)) {
            fragment = instantiateFragment(fragmentName);
            if (args != null) {
                fragment.setArguments(args);
            }
            getSupportFragmentManager().beginTransaction().add(CONTENT_VIEW_ID, fragment, fragment.getClass().getName()).commit();
        }

    }


    /**
     * replace fragment with a new fragment, add it to the back stack and use fragment name as a
     * transaction tag
     *
     * @param fragment for container to be replaced with
     */
    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, fragment.getClass().getName(), true);
    }

    /**
     * replaces fragment with a new fragment and uses fragment name as a
     * transaction tag
     *
     * @param fragment for container to be replaced with
     */
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(fragment, fragment.getClass().getName(), addToBackStack);
    }

    /**
     * @param fragment       fragment for container to be replaced with
     * @param name           of the transaction, null if not needed
     * @param addToBackStack
     */
    public void replaceFragment(Fragment fragment, String name, boolean addToBackStack) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(CONTENT_VIEW_ID, fragment, fragment.getClass().getName());
            if (addToBackStack) {
                transaction.addToBackStack(name);
            }

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        } catch (Exception e) {//java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
            e.printStackTrace();
        }

    }

    protected void setContentViewInternal(View view, ViewGroup.LayoutParams params) {
        setContentView(view, params);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getCurrentFragment() != null) {
            getCurrentFragment().onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFragment() == null || (!((IBaseFragment) getCurrentFragment()).onBackPressed())) {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getCurrentFragment() != null) {
                    ((IBaseFragment) getCurrentFragment()).onUpButtonClicked();
                }
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public int getLayoutId() {
        return R.layout.activity_ordinary;
    }

    public Boolean isEasyRedmine() {
        if (Storage.getAccountType() == -1)
            return null;
        return Storage.getAccountType() == AccountType.EASY_REDMINE.value;
    }

    public void onEvent(SelectProject event) {

    }
}
