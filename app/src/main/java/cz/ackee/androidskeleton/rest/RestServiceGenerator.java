package cz.ackee.androidskeleton.rest;

import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.ackee.androidskeleton.model.IssueFieldsHash;
import cz.ackee.androidskeleton.model.IssueHash;
import cz.ackee.androidskeleton.model.TimeEntry;
import cz.ackee.androidskeleton.model.base.IssueHashFieldsSerializer;
import cz.ackee.androidskeleton.model.base.IssueHashSerializer;
import cz.ackee.androidskeleton.model.base.TimeEntrySerializer;
import cz.ackee.androidskeleton.utils.Storage;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.UrlConnectionClient;
import retrofit.converter.GsonConverter;

/**
 * Class that generates api description class Created by David Bilik[david.bilik@ackee.cz] on {16.
 * 2. 2015}
 */
public class RestServiceGenerator {
    public static final String TAG = RestServiceGenerator.class.getName();
    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(IssueHash.class, new IssueHashSerializer())
            .registerTypeAdapter(TimeEntry.class, new TimeEntrySerializer())
            .registerTypeAdapter(IssueFieldsHash.class, new IssueHashFieldsSerializer())
            .serializeNulls()
            .create();

    static ApiDescription sApiDescription;

    /**
     * This should be used for singleton usage
     *
     * @return
     */
    public static ApiDescription getApiService() {
        if (sApiDescription == null) {
            sApiDescription = createApiDescription();
        }
        return sApiDescription;
    }

    /**
     * This will always create new instance of api description
     *
     * @return
     */
    public static ApiDescription createApiDescription() {
        disableSSLCertificateVerification();
        return new RestAdapter.Builder()
                .setEndpoint(Storage.getURL())
                .setRequestInterceptor(
                        new RequestInterceptor() {
                            @Override
                            public void intercept(RequestFacade request) {
                                if (!TextUtils.isEmpty(Storage.getCredentials())) {
                                    String string = "Basic " + Storage.getCredentials();
                                    request.addHeader("Authorization", string);
//                            request.addHeader("Content-Type", "");
                                }
                            }
                        })
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(getClient()).build()
                .create(ApiDescription.class);
    }

    private static void disableSSLCertificateVerification() {

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    private static Client getClient() {

        return new UrlConnectionClient();
//    OkHttpClient client = new OkHttpClient();
//    client.setConnectTimeout(60, TimeUnit.SECONDS);
//    client.setWriteTimeout(60, TimeUnit.SECONDS);
//    client.setReadTimeout(60, TimeUnit.SECONDS);
//    return new OkClient(client);

    }

    public static void invalidate() {
        sApiDescription = null;
    }

}
