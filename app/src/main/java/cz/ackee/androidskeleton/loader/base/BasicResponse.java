package cz.ackee.androidskeleton.loader.base;

import java.io.Serializable;

public class BasicResponse<U> implements Serializable {
    private Integer mResponseCode;
    private U data;

    public U getData() {
        return data;
    }

    public void setData(U data) {
        this.data = data;
    }
}
