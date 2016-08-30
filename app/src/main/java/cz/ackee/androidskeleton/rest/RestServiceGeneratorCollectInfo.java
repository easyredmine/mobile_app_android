package cz.ackee.androidskeleton.rest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Original project name : easyredmine-android-aplikace
 * Created by Petr Lorenc[petr.lorenc@ackee.cz] on 11.6.2015.
 * Using : Service for sending additional data to https://es.easyproject.cz/... to collect them
 */
public class RestServiceGeneratorCollectInfo {
    public static final String TAG = RestServiceGeneratorCollectInfo.class.getName();

    private static ApiDescriptionCollectInfo apiDescriptionCollectInfo;

    private static final Gson gson = new GsonBuilder().setFieldNamingPolicy(
            FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    public static ApiDescriptionCollectInfo getApiServiceCollectInfo() {
        if(apiDescriptionCollectInfo == null){
            apiDescriptionCollectInfo = createApiDescription();
        }
        return apiDescriptionCollectInfo;
    }

    private static ApiDescriptionCollectInfo createApiDescription(){
        return new RestAdapter.Builder().setEndpoint("https://es.easyproject.cz")
                .setConverter(new GsonConverter(gson)).setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(ApiDescriptionCollectInfo.class);
    }

    public static void invalidate() {
        apiDescriptionCollectInfo = null;
    }

}
