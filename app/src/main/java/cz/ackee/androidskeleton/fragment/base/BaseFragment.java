package cz.ackee.androidskeleton.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.activity.base.BaseFragmentActivity;
import cz.ackee.androidskeleton.utils.AccountType;
import cz.ackee.androidskeleton.utils.Storage;
import cz.ackee.androidskeleton.utils.Utils;


public abstract class BaseFragment extends Fragment implements IBaseFragment {
    public static String TAG = BaseFragment.class.getName();

    private FragmentDelegate delegate;

    public BaseFragment() {
        delegate = new FragmentDelegate(this);
        if (getArguments() == null) {
            setArguments(new Bundle());
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delegate.onCreate(savedInstanceState);
        // create our manager instance after the content view is set
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            setTitle(getTitle());
        }
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


    protected BaseFragmentActivity getFragmentActivity() {
        return (BaseFragmentActivity) getActivity();
    }

    protected void finish() {
        delegate.finish();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        delegate.onPrepareOptionsMenu(menu);
        super.onPrepareOptionsMenu(menu);
    }



    protected abstract String getTitle();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        delegate.onActivityCreated(savedInstanceState);
        initAB();
    }

    protected abstract void initAB();

    protected void baseSettingsAB() {
        delegate.baseSettingsAB();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Storage.getAccountType() == AccountType.REDMINE.value) {
            getFragmentActivity().tintManager.setStatusBarTintColor(getResources().getColor(R.color.r_primary_dark));
        }
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
     * Called when up is clicked
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

}
