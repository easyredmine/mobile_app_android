package cz.ackee.androidskeleton.loader.base;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * @param <T> a class which Gson will parse the result to
 * @author David Bilik
 */
public abstract class BaseLoader<T> extends AsyncTaskLoader<BasicResponse<T>> {
    private static final String TAG = "BaseDownloader";

    private BasicResponse<T> mData;
    private boolean mDataDelivered;

    public BaseLoader(Context context) {
        super(context.getApplicationContext());
    }

    @Override
    public final BasicResponse<T> loadInBackground() {
        mData = loadData();
        return mData;
    }

    protected abstract BasicResponse<T> loadData();

    /********************************************************/
    /** (2) Deliver the results to the registered listener **/
    /**
     * ****************************************************
     */

    @Override
    public void deliverResult(BasicResponse<T> data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the
            // data.
            onReleaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // The old data may still be in use (i.e. bound to an adapter, etc.), so
        // we must protect it until the new data has been delivered.
        BasicResponse<T> oldData = mData;
        mData = data;
        super.deliverResult(data);

        // Invalidate the old data as we don't need it any more.
        if ((oldData != null) && (oldData != data)) {
            onReleaseResources(oldData);
        }
    }

    /*********************************************************/
    /** (3) Implement the Loader’s state-dependent behavior **/
    /**
     * *****************************************************
     */
    @Override
    protected void onStartLoading() {
        if ((mData != null) && !mDataDelivered) {
            // Deliver any previously loaded data immediately.
            deliverResult(mData);
        } else {
            forceLoad();
        }
    }

    @Override
    public void forceLoad() {
        if ((mData != null) && !mDataDelivered) {
            // Deliver any previously loaded data immediately.
            deliverResult(mData);
        } else {
            onReset();
            super.onForceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        Log.d(TAG, "onStopLoading");
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        mDataDelivered = false;

        // At this point we can release the resources associated with 'mData'.
        if (mData != null) {
            onReleaseResources(mData);
            mData = null;
        }
    }

    @Override
    public void onCanceled(BasicResponse<T> data) {
        // Attempt to onConnectionDialogCancel the current asynchronous load.
        super.onCanceled(data);
        Log.d(TAG, "onCanceled");
        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        onReleaseResources(data);
    }

    protected void onReleaseResources(BasicResponse<T> oldData) {
    }
}
