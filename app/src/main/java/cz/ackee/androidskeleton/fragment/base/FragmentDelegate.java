package cz.ackee.androidskeleton.fragment.base;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;

import cz.ackee.androidskeleton.activity.base.BaseFragmentActivity;
import cz.ackee.androidskeleton.activity.base.BaseMenuActivity;

/**
 * Common class for all types of fragments
 */
public class FragmentDelegate {
    static final String TAG = "FragmentDelegate";

    private static final String ERR_DIALOG_TAG = "error";
    private final IBaseFragment fragment;


    public FragmentDelegate(IBaseFragment fragment) {
        this.fragment = fragment;
    }

    private BaseMenuActivity getMenuActivity() {
        return (BaseMenuActivity) fragment.getActivity();
    }

    private BaseFragmentActivity getActivity() {
        return (BaseFragmentActivity) fragment.getActivity();
    }


    void setTitle(int title) {
        //getActivity().setTitle(title);
        setTitle(getActivity().getString(title));
        setSubtitle(null);
    }

    void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        getActivity().setTitle(title);
    }

    void setSubtitle(String subTitle) {
        getActivity().setSubtitle(subTitle);
    }

    void onCreate(Bundle savedInstanceState) {
    }

    void onActivityCreated(Bundle savedInstanceState) {
    }



    void onPrepareOptionsMenu(Menu menu) {
    }

    void finish() {
        if (fragment.getFragmentManager().getBackStackEntryCount() > 0) {
            fragment.getFragmentManager().popBackStack();
        } else {
            getActivity().finish();
        }
    }

    /**
     * Basic settings of actionbar
     */
    protected void baseSettingsAB() {
        ActionBar ab = getActivity().getSupportActionBar();
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        if (getActivity() instanceof BaseMenuActivity) {
            getMenuActivity().setNavIcon(fragment.hasHamburgerMenu());
        }
    }


    public void onStart() {
    }


}
