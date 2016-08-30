package cz.ackee.androidskeleton.utils;

import android.content.Context;
import android.content.SharedPreferences;

import cz.ackee.androidskeleton.App;

/**
 * Class that handles storing persistent small data Created by David Bilik[david.bilik@ackee.cz] on
 * {16. 2. 2015}
 */
public class Storage {
  public static final String TAG = Storage.class.getName();

  private static final String SP_NAME = "data";
  private static final String NAME_KEY = "name";
  private static final String PASS_KEY = "pass";
  private static final String CREDENTIALS_KEY = "credentials";
  private static final String SERVER_URL_KEY = "server";
  private static final String IS_LOGGED_KEY = "isLogged";
  private static final String ACCOUNT_TYPE = "account_type";
  public static final String QUERY_FILTER = "query_filter";
  public static final String QUERY_FILTER_NAME = "query_filter_name";

  private static SharedPreferences getSP() {
    return App.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
  }

  public static void storeName(String name) {
    getSP().edit().putString(NAME_KEY, name).commit();
  }

  public static String getName() {
    return getSP().getString(NAME_KEY, "");
  }

  public static void storePass(String pass) {
    getSP().edit().putString(PASS_KEY, pass).commit();
  }

  public static void storeURL(String url) {
    getSP().edit().putString(SERVER_URL_KEY, url).commit();
  }

  public static void storeCredentials(String credentials) {
    getSP().edit().putString(CREDENTIALS_KEY, credentials).commit();
  }

  public static boolean isLogged() {
    return getSP().getBoolean(IS_LOGGED_KEY, false);
  }

  public static void setLogged(boolean logged) {
    getSP().edit().putBoolean(IS_LOGGED_KEY, logged).commit();
  }

  public static String getCredentials() {
    return getSP().getString(CREDENTIALS_KEY, "");
  }

  public static void setCredentials(String credentials) {
    getSP().edit().putString(CREDENTIALS_KEY, credentials).commit();
  }

  public static String getURL() {
    return getSP().getString(SERVER_URL_KEY, "");
  }

  public static void setAccountType(AccountType type) {
    int value;
    if (type == null) {
      value = -1;
    } else {
      value = type.value;
    }
    getSP().edit().putInt(ACCOUNT_TYPE, value).commit();
  }

  public static int getAccountType() {
    return getSP().getInt(ACCOUNT_TYPE, -1);
  }

  public static void setFilter(int queryId) {
    getSP().edit().putString(QUERY_FILTER, "query_id=" + queryId).commit();
  }

  public static void setFilter(String query) {
    getSP().edit().putString(QUERY_FILTER, query).commit();
  }

  public static String getFilter() {
    return getSP().getString(QUERY_FILTER, "");
  }

  public static void setFilterName(String query) {
    getSP().edit().putString(QUERY_FILTER_NAME, query).commit();
  }

  public static String getFilterName() {
    return getSP().getString(QUERY_FILTER_NAME, "");
  }

}
