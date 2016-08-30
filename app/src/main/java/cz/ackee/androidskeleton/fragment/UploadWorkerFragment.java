package cz.ackee.androidskeleton.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.File;

import cz.ackee.androidskeleton.iface.UploadTaskCallbacks;
import cz.ackee.androidskeleton.listener.ProgressListener;
import cz.ackee.androidskeleton.model.CountingTypedFile;
import cz.ackee.androidskeleton.model.response.UploadAttachmentResponse;
import cz.ackee.androidskeleton.rest.RestServiceGenerator;
import retrofit.RetrofitError;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 24.4.2015.
 */
public class UploadWorkerFragment extends Fragment {
    public static final String TAG = UploadWorkerFragment.class.getName();
    UploadTaskCallbacks mCallbacks;

    public static UploadWorkerFragment newInstance(Bundle args) {
        UploadWorkerFragment ntf = new UploadWorkerFragment();
        ntf.setArguments(args);
        return ntf;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (UploadTaskCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement UploadTaskCallbacks ");
        }
    }

    public void startUpload(File file, String tag, String contentType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new UploadAttachmentTask(file, tag, contentType).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new UploadAttachmentTask(file, tag, contentType).execute();
        }
    }

    private class UploadAttachmentTask extends AsyncTask<Void, Integer, UploadAttachmentResponse> {

        private File file;
        private String tag;
        private String contentType;
        private ProgressListener listener;

        public UploadAttachmentTask(File file, String tag, String contentType) {
            this.file = file;
            this.tag = tag;
            this.contentType = contentType;
        }

        @Override
        protected UploadAttachmentResponse doInBackground(Void... params) {
            final long totalSize = file.length();
            Log.d(TAG, "Progress Upload attachment Upload FileSize multipart " + totalSize);
            listener = new ProgressListener() {
                @Override
                public void transferred(long num) {
                    Log.d(TAG, "Progress publish progress multipart " + num + " " + totalSize + "=" + ((int) ((num / (float) totalSize) * 100)));
                    publishProgress((int) ((num / (float) totalSize) * 100));
                }
            };
            try {
                return RestServiceGenerator.createApiDescription().uploadFile(new CountingTypedFile("application/octet-stream", file, listener));
            } catch (RetrofitError e) {
                return new UploadAttachmentResponse(e);
            }
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) {
                mCallbacks.onCancelled();
            }
        }

        @Override
        protected void onPostExecute(UploadAttachmentResponse uploadAttachmentResponse) {
            super.onPostExecute(uploadAttachmentResponse);
            if (uploadAttachmentResponse.getError() != null) {
                Log.d(TAG, "error while uploading file");
                if (mCallbacks != null) {
                    mCallbacks.onError(uploadAttachmentResponse.getError(), tag);
                }
                return;
            }

            if (mCallbacks != null) {
                uploadAttachmentResponse.upload.contentType = contentType;
                uploadAttachmentResponse.upload.filename = file.getName();
                mCallbacks.onPostExecute(file.getName(), tag, uploadAttachmentResponse);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mCallbacks != null) {
                Log.d(TAG, "UPLOAD - worker progress update " + values[0]);
                mCallbacks.onProgressUpdate(file.getName(), tag, values[0]);
            }
        }
    }
}