package com.gabriel.exoplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionHelper {

  private static ConnectionHelper mInstance;

  public static ConnectionHelper getInstance() {
    if (mInstance == null)
      mInstance = new ConnectionHelper();
    return mInstance;
  }

  private long lastNoConnectionTs = -1;
  private boolean isOnline = true;
  private boolean networkChangeNotified = true;

  public static boolean isConnected(Context context) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    @SuppressLint("MissingPermission") NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }

  public static boolean isConnectedOrConnecting(Context context) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    @SuppressLint("MissingPermission") NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnectedOrConnecting();
  }

  public boolean isNotified() {
    boolean notified = networkChangeNotified;
    if (!networkChangeNotified) networkChangeNotified = true;
    return notified;
  }

  public long getLastNoConnectionTs() {
    return lastNoConnectionTs;
  }

  public void setLastNoConnectionTs(long lastNoConnectionTs) {
    this.lastNoConnectionTs = lastNoConnectionTs;
  }

  public boolean isOnline() {
    return isOnline;
  }

  public void setOnline(boolean isOnline) {
    this.isOnline = isOnline;
  }

  public boolean isNetworkChangeNotified() {
    return networkChangeNotified;
  }

  public void setNetworkChangeNotified(boolean networkChangeNotified) {
    this.networkChangeNotified = networkChangeNotified;
  }
}
