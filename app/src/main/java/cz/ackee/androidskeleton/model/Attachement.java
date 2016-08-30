package cz.ackee.androidskeleton.model;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {18. 2. 2015}
 */
public class Attachement {
    public static final String TAG = Attachement.class.getName();
    int id;
    String filename;
    String filesize;
    String description;
    String contentUrl;
    IdNameEntity author;
    String createdOn;

    public int getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getFilesize() {
        return filesize;
    }

    public String getDescription() {
        return description;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public IdNameEntity getAuthor() {
        return author;
    }

    public String getCreatedOn() {
        return createdOn;
    }
}
