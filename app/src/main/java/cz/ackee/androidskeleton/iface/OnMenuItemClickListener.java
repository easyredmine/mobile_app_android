package cz.ackee.androidskeleton.iface;

/**
 * Listener that is called when item in menu is clicked
 * Created by David Bilik[david.bilik@ackee.cz] on {17. 2. 2015}
 */
public interface OnMenuItemClickListener {

    public int getSelectedPosition();

    public void onItemClicked(int pos);
}
