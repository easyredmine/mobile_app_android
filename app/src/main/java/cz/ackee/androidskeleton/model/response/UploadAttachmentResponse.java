package cz.ackee.androidskeleton.model.response;

import cz.ackee.androidskeleton.model.Upload;
import retrofit.RetrofitError;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 23.4.2015.
 */
public class UploadAttachmentResponse {
    private final RetrofitError mError;
    public Upload upload;

    public UploadAttachmentResponse(RetrofitError errResponse) {
        mError = errResponse;
    }

    public RetrofitError getError() {
        return mError;
    }
}
