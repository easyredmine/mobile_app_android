package cz.ackee.androidskeleton.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cz.ackee.androidskeleton.App;
import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.fragment.dialog.IssueErrorDialog;
import cz.ackee.androidskeleton.model.IssueHash;
import cz.ackee.androidskeleton.model.NameValueEntity;
import cz.ackee.androidskeleton.model.response.ErrorResponse;
import retrofit.RetrofitError;


public class Utils {
    public static final String TAG = "Utils";

    public static ArrayList<String> getStringList(ArrayList<NameValueEntity> items) {
        ArrayList<String> toRet = new ArrayList<>();
        for (NameValueEntity nve : items) {
            toRet.add(nve.getName());
        }
        return toRet;
    }

    public static void hideIme(View target) {
        if (target != null) {
            InputMethodManager imm = (InputMethodManager) target.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(target.getWindowToken(), 0);
        }
    }

    public static void startBrowserApp(Context ctx, String link) {
        if (link == null) {
            link = "";
        }
        if (!link.startsWith("http")) {
            link = "http://".concat(link);
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        if (i.resolveActivity(ctx.getPackageManager()) != null) {
            ctx.startActivity(i);
        }
    }


    public static void handleError(FragmentManager fragmentManager, RetrofitError error) {
        if (error != null) {
            if (error.getKind() == RetrofitError.Kind.NETWORK) {
                IssueErrorDialog.newInstance(App.getInstance().getString(R.string.no_internet_connection)).show(fragmentManager, IssueErrorDialog.class.getName());
            } else if (error.getResponse().getStatus() == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
                ErrorResponse errorResponse = (ErrorResponse) error.getBodyAs(ErrorResponse.class);
                String errors = "";
                for (String s : errorResponse.errors) {
                    errors += s + ", ";
                }
                if (errors.length() > 0) {
                    IssueErrorDialog.newInstance(errors.substring(0, errors.length() - 2)).show(fragmentManager, IssueErrorDialog.class.getName());
                }
            } else if (error.getResponse().getStatus() == HttpStatus.SC_FORBIDDEN) {
                IssueErrorDialog.newInstance(App.getInstance().getString(R.string.server_error_forbidden)).show(fragmentManager, IssueErrorDialog.class.getName());
            }else {
                IssueErrorDialog.newInstance(App.getInstance().getString(R.string.server_error_general_error)).show(fragmentManager, IssueErrorDialog.class.getName());
            }
        }
    }


    public static IssueHash getIssueHashFromFile() {

        try {
            FileInputStream fis = new FileInputStream(getTempIssueHashFile());
            return new Gson().fromJson(new InputStreamReader(fis), IssueHash.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveIssueHashToFile(IssueHash hash) {
        String json = new Gson().toJson(hash);
        try {
            FileOutputStream outputStreamWriter = new FileOutputStream(getTempIssueHashFile());
            outputStreamWriter.write(json.getBytes());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static File getTempIssueHashFile() {
        File dir = App.getInstance().getFilesDir();
        return new File(dir, "issue");
    }

}
