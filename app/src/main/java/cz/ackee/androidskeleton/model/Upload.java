package cz.ackee.androidskeleton.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 23.4.2015.
 */
public class Upload implements Parcelable {
    public String token;
    public String filename;
    public String description;
    public String contentType;

    public Upload(String token, String filename, String contentType) {
        this.token = token;
        this.filename = filename;
        this.contentType = contentType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeString(this.filename);
        dest.writeString(this.description);
        dest.writeString(this.contentType);
    }

    protected Upload(Parcel in) {
        this.token = in.readString();
        this.filename = in.readString();
        this.description = in.readString();
        this.contentType = in.readString();
    }

    public static final Parcelable.Creator<Upload> CREATOR = new Parcelable.Creator<Upload>() {
        public Upload createFromParcel(Parcel source) {
            return new Upload(source);
        }

        public Upload[] newArray(int size) {
            return new Upload[size];
        }
    };
}
