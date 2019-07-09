package com.gabriel.exoplayer;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationReceiver extends BroadcastReceiver {
  private NotificationListener mListener;

  public NotificationReceiver() {
  }

  public NotificationReceiver(NotificationListener listener) {
    mListener = listener;
  }

  public NotificationListener getListener() {
    return mListener;
  }

  public void setListener(NotificationListener listener) {
    mListener = listener;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    String actionID = intent.getAction();

    switch (actionID) {
      case Constants.NOTIFICATION_ACTION.PLAY:
        mListener.onPlayClick();
        break;
      case Constants.NOTIFICATION_ACTION.PAUSE:
        mListener.onPauseClick(false);
        break;
      case Constants.NOTIFICATION_ACTION.RE_PLAY:
        mListener.onRePlayClick();
        break;
      case Constants.NOTIFICATION_ACTION.CANCEL_NOTIFICATION:
        Integer notificationID = intent.getIntExtra(Constants.NOTIFICATION_ID, 6);
        NotificationManager nMgr = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          nMgr.deleteNotificationChannel(MainActivity.CHANNEL_ID);
        } else {
          nMgr.cancel(notificationID);
        }
        mListener.onPauseClick(true);
        break;
    }
  }

  public interface NotificationListener {
    void onPauseClick(boolean isCancel);

    void onPlayClick();

    void onRePlayClick();
  }
}
