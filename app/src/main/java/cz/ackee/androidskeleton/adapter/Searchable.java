package cz.ackee.androidskeleton.adapter;

import android.support.annotation.NonNull;

/**
 * Interface for searchable adapters
 *
 * @author Michal Kučera [michal.kucera@ackee.cz]
 * @since 24/11/15
 **/
public interface Searchable {

    void searchString(@NonNull CharSequence string);

}
