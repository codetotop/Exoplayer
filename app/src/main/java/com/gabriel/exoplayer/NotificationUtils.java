package com.gabriel.exoplayer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public class NotificationUtils {

  private Context mContext;

  public NotificationUtils(Context context) {
    mContext = context;
  }

  public Context getContext() {
    return mContext;
  }

  public void setContext(Context context) {
    mContext = context;
  }

  public void createMediaCustomNotification(String channelID) {
    // NotificationTargetActivity is the activity opened when user click notification.
    Intent openActivityIntent = new Intent(mContext, MainActivity.class);
    openActivityIntent.setAction(Intent.ACTION_MAIN);
    openActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    openActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, openActivityIntent, 0);

    RemoteViews notificationMediaSmallLayout = new RemoteViews(getContext().getApplicationContext().getPackageName(), R.layout.notification_media_small);
    RemoteViews notificationMediaLargeLayout = new RemoteViews(getContext().getApplicationContext().getPackageName(), R.layout.notification_media_large);
    notificationMediaLargeLayout.setOnClickPendingIntent(R.id.imgPlayPause, onButtonNotificationClick(R.id.imgPlayPause));
    notificationMediaLargeLayout.setOnClickPendingIntent(R.id.imgCloseNotification, onButtonNotificationClick(R.id.imgCloseNotification));

    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelID)
        .setSmallIcon(R.drawable.son_tung_mtp)
        .setShowWhen(false)
        .setAutoCancel(false)
        .setOngoing(true)//disable swipe remove notification.
        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())// không setStyle() để remove icon, tên app, time mặc định trên notification .
        .setCustomContentView(notificationMediaSmallLayout)
        .setCustomBigContentView(notificationMediaLargeLayout)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    createNotificationChannel(notificationManager, channelID);
    notificationManager.notify(6, builder.build());
  }

  private PendingIntent onButtonNotificationClick(@IdRes int id) {
    Intent intent = new Intent();
    intent.putExtra(Constant.BUTTON_CLICKED, id);
    return PendingIntent.getBroadcast(mContext, id, intent, 0);
  }

  private void createNotificationChannel(NotificationManager notificationManager, String channelID) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = mContext.getString(R.string.channel_name);
      String description = mContext.getString(R.string.channel_description);
      int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(channelID, name, importance);
      channel.setDescription(description);
      notificationManager.createNotificationChannel(channel);
    }
  }


}
