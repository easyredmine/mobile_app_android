package cz.ackee.androidskeleton.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * TODO add description
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 23.4.2015.
 */
public class FileUtils {
    public static File isToFile(Context c, InputStream initialStream) throws IOException {
        byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);
        File targetFile = File.createTempFile("attachment", null, c.getCacheDir());
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        return targetFile;
    }
}
