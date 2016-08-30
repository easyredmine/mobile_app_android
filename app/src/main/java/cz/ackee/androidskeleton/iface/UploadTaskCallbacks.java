package cz.ackee.androidskeleton.iface;

import java.io.File;

import cz.ackee.androidskeleton.model.response.ErrorResponse;
import retrofit.RetrofitError;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 24.4.2015.
 */
public interface UploadTaskCallbacks {
    void onPreExecute();

    void onProgressUpdate(String filename, String tag, int percent);

    void onCancelled();

    void onPostExecute(String filename, String tag, Object response);

    void startUpload(File file, String tag, String contentType);

    void onError(RetrofitError error, String tag);
}