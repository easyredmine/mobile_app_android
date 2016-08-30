package cz.ackee.androidskeleton.fragment.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public interface IBaseFragment {
    public FragmentManager getFragmentManager();

    public FragmentActivity getActivity();


    /**
     * Called when back button is pressed
     */
    public boolean onBackPressed();

    /**
     * Get fragment arguments
     *
     * @return
     */
    public Bundle getArguments();

    /**
     * Called when UP button is clicked
     */
    public void onUpButtonClicked();

    /**
     * Get name of this fragment for Google Analytics
     *
     * @return
     */
    public String getGAName();

    int getPositionInMenu();

    boolean hasHamburgerMenu();
}
