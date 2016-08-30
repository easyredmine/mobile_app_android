package cz.ackee.androidskeleton.fragment;

import retrofit.RetrofitError;

/**
 * Listener for uploading attachments
 *
 * @author Michal Kučera [michal.kucera@ackee.cz]
 * @since 14/12/15
 **/
public interface UploadProgressListener {

    void onProgressUpdate(String filename, String tag, int progressValue);

    void onUploadComplete(String filename, String tag, Object response);

    void showError(RetrofitError error, String tag);

    void setShouldSend(boolean b);

}
