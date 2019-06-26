package com.gabriel.exoplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.logging.Logger;


/**
 * Device Utils
 * Created by neo on 2/16/2016.
 */
@SuppressLint("MissingPermission")
public class DeviceUtils {
  public static String getDeviceId(Context context) {
    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    return telephonyManager.getDeviceId();
  }

  public static int getAppVersionCode(Context context) {
    int versionCode;
    try {
      versionCode = context.getPackageManager().getPackageInfo(
              context.getPackageName(), 0).versionCode;
    } catch (Exception e) {
      versionCode = 0;
    }
    return versionCode;
  }

  public static Point getDeviceSize(Activity context) {
    Display display = context.getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    return size;
  }

  public static Point getDeviceSizePortrait(Activity context) {
    Display display = context.getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);

    int x = Math.min(size.x, size.y);
    int y = Math.max(size.x, size.y);
    return new Point(x, y);
  }

  public static int getDpi(Context context) {
    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    return (int) (metrics.density * 160f);
  }

  public static boolean isLandscape(Activity activity) {
    return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
  }

  public static boolean isActivityAutoRotate(Activity activity) {
    return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_UNDEFINED;
  }

  /**
   * Force set the orientation of activity
   *
   * @param activity    target activity
   * @param orientation 1 of those values
   *                    Configuration.ORIENTATION_LANDSCAPE
   *                    or Configuration.ORIENTATION_PORTRAIT
   *                    or Configuration.ORIENTATION_UNDEFINED
   */
  public static void forceRotateScreen(Activity activity, int orientation) {
    switch (orientation) {
      case Configuration.ORIENTATION_LANDSCAPE:
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        break;
      case Configuration.ORIENTATION_PORTRAIT:
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        break;
      case Configuration.ORIENTATION_UNDEFINED:
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        break;
    }
  }

  public static boolean isDeviceLockRotate(Context context) {
    final int rotationState = Settings.System.getInt(
            context.getContentResolver(),
            Settings.System.ACCELEROMETER_ROTATION, 0
    );

    return rotationState == 0;
  }

  public static String getPhoneNumber(Context context) {
    TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    return tMgr.getLine1Number();
  }

  public static void openAppInStore(Context context) {
    final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
    try {
      context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
    } catch (Exception e) {
      context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
    }
  }

  public static int getScreenOrientation(Activity activity) {
    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    DisplayMetrics dm = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels;
    int height = dm.heightPixels;
    int orientation;
    // if the device's natural orientation is portrait:
    if ((rotation == Surface.ROTATION_0
            || rotation == Surface.ROTATION_180) && height > width ||
            (rotation == Surface.ROTATION_90
                    || rotation == Surface.ROTATION_270) && width > height) {
      switch (rotation) {
//                case Surface.ROTATION_0:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                    break;
        case Surface.ROTATION_90:
          orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
          break;
        case Surface.ROTATION_180:
          orientation =
                  ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
          break;
        case Surface.ROTATION_270:
          orientation =
                  ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
          break;
        case Surface.ROTATION_0:
        default:

          orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
          break;
      }
    }
    // if the device's natural orientation is landscape or if the device
    // is square:
    else {
      switch (rotation) {
//                case Surface.ROTATION_0:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//                    break;
        case Surface.ROTATION_90:
          orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
          break;
        case Surface.ROTATION_180:
          orientation =
                  ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
          break;
        case Surface.ROTATION_270:
          orientation =
                  ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
          break;
        case Surface.ROTATION_0:
        default:

          orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
          break;
      }
    }

    return orientation;
  }

  public static int getScreenOrientationEx(Activity activity) {
    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    int orientation = activity.getResources().getConfiguration().orientation;
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
      if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270) {
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
      } else {
        return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
      }
    }
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
      if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
        return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
      } else {
        return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
      }
    }
    return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
  }

  public static void hideSoftKeyboard(Activity activity) {
    InputMethodManager inputMethodManager =
            (InputMethodManager) activity.getSystemService(
                    Activity.INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(
            activity.getCurrentFocus().getWindowToken(), 0);
  }

  public static String getVersionName(Context context) {
    try {
      PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      return pInfo.versionName;
    } catch (Exception e) {
      return null;
    }
  }

  public static int getScreenWidth(Activity activity) {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    return displayMetrics.widthPixels;
  }

  public static int getScreenHeight(Activity activity) {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    return displayMetrics.heightPixels;
  }

  public static void hideNavigationBar(Activity activity) {
    activity.getWindow().getDecorView().setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE);
    Log.e("ABCDE", "hideNavigationBar: " );
  }

  public static void showNavigationBar(Activity activity) {
    activity.getWindow()
        .getDecorView()
        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
  }
}
