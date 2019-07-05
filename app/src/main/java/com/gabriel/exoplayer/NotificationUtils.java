package com.gabriel.exoplayer;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

@SuppressLint("RestrictedApi")
public class NotificationUtils {

  private Context mContext;
  private NotificationCompat.Builder mBuilder;
  private NotificationManager mNotificationManager;

  public NotificationUtils(Context context) {
    mContext = context;
  }

  public Context getContext() {
    return mContext;
  }

  public void setContext(Context context) {
    mContext = context;
  }

  public void createMediaCustomNotification(Integer notificationID, String channelID) {
    // NotificationTargetActivity is the activity opened when user click notification.
    Intent openActivityIntent = new Intent(mContext, MainActivity.class);
    openActivityIntent.setAction(Intent.ACTION_MAIN);
    openActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    openActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, openActivityIntent, 0);

    RemoteViews notificationMediaSmallLayout = new RemoteViews(getContext().getApplicationContext().getPackageName(), R.layout.notification_media_small);
    RemoteViews notificationMediaLargeLayout = new RemoteViews(getContext().getApplicationContext().getPackageName(), R.layout.notification_media_large);

    mBuilder = new NotificationCompat.Builder(mContext, channelID)
        .setSmallIcon(R.drawable.son_tung_mtp)
        .setShowWhen(false)
        .setAutoCancel(false)
        .setOngoing(true)//disable swipe remove notification.
        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())// không setStyle() để remove icon, tên app, time mặc định trên notification .
        .setCustomContentView(notificationMediaSmallLayout)
        //.setCustomBigContentView(notificationMediaLargeLayout)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT);


    notificationMediaSmallLayout.setOnClickPendingIntent(R.id.imgPlay, onButtonPlayClick());
    notificationMediaSmallLayout.setOnClickPendingIntent(R.id.imgPause, onButtonPauseClick());
    notificationMediaSmallLayout.setOnClickPendingIntent(R.id.imgRePlay, onButtonRePlayClick());
    notificationMediaSmallLayout.setOnClickPendingIntent(R.id.imgCloseNotification, onButtonCancelClick(notificationID));
    /*notificationMediaLargeLayout.setOnClickPendingIntent(R.id.imgPlayPause, onButtonPlayPauseClick(mBuilder));
    notificationMediaLargeLayout.setOnClickPendingIntent(R.id.imgCloseNotification, onButtonCancelClick(notificationID));*/


    mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    createNotificationChannel(mNotificationManager, channelID);
    mNotificationManager.notify(notificationID, mBuilder.build());

  }

  private PendingIntent onButtonPlayClick() {
    Intent intent = new Intent(Constants.NOTIFICATION_ACTION.PLAY);
    return PendingIntent.getBroadcast(mContext, 0, intent, 0);
  }

  private PendingIntent onButtonPauseClick() {
    Intent intent = new Intent(Constants.NOTIFICATION_ACTION.PAUSE);
    return PendingIntent.getBroadcast(mContext, 0, intent, 0);
  }

  private PendingIntent onButtonRePlayClick() {
    Intent intent = new Intent(Constants.NOTIFICATION_ACTION.RE_PLAY);
    return PendingIntent.getBroadcast(mContext, 0, intent, 0);
  }

  private PendingIntent onButtonCancelClick(Integer notificationID) {
    Intent intent = new Intent(Constants.NOTIFICATION_ACTION.CANCEL_NOTIFICATION);
    intent.putExtra(Constants.NOTIFICATION_ID, notificationID);
    return PendingIntent.getBroadcast(mContext, 0, intent, 0);
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

  @SuppressLint("RestrictedApi")
  public void updateNotification(Integer notificationID, String action) {
    switch (action) {
      case Constants.NOTIFICATION_ACTION.PLAY:
        mBuilder.getContentView().setViewVisibility(R.id.imgPlay, View.GONE);
        mBuilder.getContentView().setViewVisibility(R.id.imgPause, View.VISIBLE);
        mBuilder.getContentView().setViewVisibility(R.id.imgRePlay, View.GONE);
        mNotificationManager.notify(notificationID, mBuilder.build());
        break;
      case Constants.NOTIFICATION_ACTION.PAUSE:
        mBuilder.getContentView().setViewVisibility(R.id.imgPlay, View.VISIBLE);
        mBuilder.getContentView().setViewVisibility(R.id.imgPause, View.GONE);
        mBuilder.getContentView().setViewVisibility(R.id.imgRePlay, View.GONE);
        mNotificationManager.notify(notificationID, mBuilder.build());
        break;
      case Constants.NOTIFICATION_ACTION.RE_PLAY:
        mBuilder.getContentView().setViewVisibility(R.id.imgPlay, View.GONE);
        mBuilder.getContentView().setViewVisibility(R.id.imgPause, View.VISIBLE);
        mBuilder.getContentView().setViewVisibility(R.id.imgRePlay, View.GONE);
        mNotificationManager.notify(notificationID, mBuilder.build());
        break;
      case Constants.NOTIFICATION_ACTION.FINISH:
        mBuilder.getContentView().setViewVisibility(R.id.imgPlay, View.GONE);
        mBuilder.getContentView().setViewVisibility(R.id.imgPause, View.GONE);
        mBuilder.getContentView().setViewVisibility(R.id.imgRePlay, View.VISIBLE);
        mNotificationManager.notify(notificationID, mBuilder.build());
        break;
      default:
        break;
    }
  }


}
