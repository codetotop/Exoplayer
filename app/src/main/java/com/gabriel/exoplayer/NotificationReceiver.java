package com.gabriel.exoplayer;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
  private NotificationListener mListener;

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

    int id = intent.getIntExtra(Constant.BUTTON_CLICKED, -1);
    switch (id) {
      case R.id.imgPlayPause:
        mListener.onPausePlay();
        break;
      case R.id.imgCloseNotification:
        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(6);
        Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show();
        break;

    }
  }

  public interface NotificationListener {
    void onPausePlay();
  }
}
