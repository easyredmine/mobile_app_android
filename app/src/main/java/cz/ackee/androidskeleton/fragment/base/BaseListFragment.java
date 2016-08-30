package cz.ackee.androidskeleton.fragment.base;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import cz.ackee.androidskeleton.activity.base.BaseFragmentActivity;
import cz.ackee.androidskeleton.utils.Utils;

public abstract class BaseListFragment extends ListFragment implements IBaseFragment {
    public static String TAG = BaseListFragment.class.getName();

    protected FragmentDelegate delegate;

    public BaseListFragment() {
        delegate = new FragmentDelegate(this);
        if (getArguments() == null) {
            setArguments(new Bundle());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            setTitle(getTitle());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delegate.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void setEmptyText(CharSequence text) {
        super.setEmptyText(text);
        View empty = getListView().getEmptyView();

    }


    protected void setTitle(int title) {
        delegate.setTitle(title);
    }

    protected void setTitle(String title) {
        delegate.setTitle(title);
    }

    protected void setSubtitle(String subTitle) {
        delegate.setSubtitle(subTitle);
    }


    protected void finish() {
        delegate.finish();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        delegate.onPrepareOptionsMenu(menu);
        super.onPrepareOptionsMenu(menu);
    }

    protected BaseFragmentActivity getFragmentActivity() {
        return (BaseFragmentActivity) getActivity();
    }

    protected abstract String getTitle();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        delegate.onActivityCreated(savedInstanceState);
        initAB();
    }

    /**
     * Initialize actionbar
     */
    protected abstract void initAB();

    protected void baseSettingsAB() {
        delegate.baseSettingsAB();
    }

    /**
     * Called when back button is pressed
     */
    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onUpButtonClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when up button is clicked
     */
    public void onUpButtonClicked() {
    }


    @Override
    public void onStart() {
        super.onStart();
        delegate.onStart();
    }


    @Override
    public boolean hasHamburgerMenu() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideIme();
    }

    /**
     * Hides keyboard if it is shown
     */
    protected void hideIme() {
        if (getFragmentActivity().getContentView() != null) { // try to hide keyboard when destroying fragment view
            View focusedView = getFragmentActivity().getContentView().findFocus();
            if (focusedView != null) {
                Utils.hideIme(focusedView);
            }
        }
    }
}
