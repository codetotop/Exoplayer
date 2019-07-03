package com.gabriel.exoplayer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private static final int LANDSCAPE = 0;
  private static final int PORTRAIT = 1;

  private ConstraintLayout rootPlayer;
  private SimpleExoPlayerView exoPlayer;
  private ProgressBar progressBar;
  private ImageView ivFullScreen;
  private static final String CHANNEL_ID = "Gabriel";
  private SeekBar mSbSpeed;
  private SubtitleView mSubtitles;
  private ImageButton mExoPrev, mExoNext, mExoPlay, mExoPause, mExoReplay, mExoReplay10, mExoForward10;
  private VideoController mExoController;
  private int mScreenMode = PORTRAIT;
  private List<String> mLinks = new ArrayList<>();
  private int mIndexVideo = 0;
  private TextView mTvSpeed;
  private String subTitleURL = "https://www.iandevlin.com/html5test/webvtt/upc-video-subtitles-en.vtt";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    addControlls();
    addEvents();
    playingVideo();
  }

  private void playingVideo() {
    mExoController.playVideo(mLinks.get(mIndexVideo), subTitleURL, 0, mSubtitles);
    //createNotification();
  }

  private void createNotification() {
    Bitmap lagreIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_smile);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_smile)
        .setContentTitle("Gabriel")
        .setContentText("Gabriel !")
        .setLargeIcon(lagreIcon)
        .addAction(new NotificationCompat.Action(R.drawable.ic_previous, "previous", null))
        .addAction(new NotificationCompat.Action(R.drawable.ic_pause, "pause play", null))
        .addAction(new NotificationCompat.Action(R.drawable.ic_next, "next", null))
        .setStyle(new MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)
            .setShowCancelButton(true)
            .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(MainActivity.this, PlaybackStateCompat.ACTION_STOP)))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    createNotificationChannel(notificationManager);

    notificationManager.notify(6, builder.build());
  }

  private void createNotificationChannel(NotificationManager notificationManager) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = getString(R.string.channel_name);
      String description = getString(R.string.channel_description);
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
      channel.setDescription(description);
      notificationManager.createNotificationChannel(channel);
    }
  }

  private void addControlls() {
    rootPlayer = findViewById(R.id.fl_player_view);
    exoPlayer = findViewById(R.id.simple_player_view);
    progressBar = findViewById(R.id.common_progress_bar);
    ivFullScreen = findViewById(R.id.ivFullScreen);
    mExoPlay = findViewById(R.id.exo_play1);
    mExoPause = findViewById(R.id.exo_pause1);
    mExoPrev = findViewById(R.id.exo_prev1);
    mExoNext = findViewById(R.id.exo_next1);
    mExoReplay = findViewById(R.id.exo_replay);
    mExoReplay10 = findViewById(R.id.exo_replay_10);
    mExoForward10 = findViewById(R.id.exo_forward_10);
    mSubtitles = findViewById(R.id.subtitle);
    mSbSpeed = findViewById(R.id.sbSpeed);
    mTvSpeed = findViewById(R.id.tvSpeed);
    mExoController = new VideoController(this, exoPlayer);
    mLinks.add("http://www.html5videoplayer.net/videos/toystory.mp4");
    mLinks.add("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
    mLinks.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
    mLinks.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
    mLinks.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4");
    //setUpControls();
    setUpVisibilityPausePlay(View.GONE, View.VISIBLE);
  }

  private void addEvents() {

    ivFullScreen.setOnClickListener(this);
    mExoPrev.setOnClickListener(this);
    mExoNext.setOnClickListener(this);
    mExoPlay.setOnClickListener(this);
    mExoPause.setOnClickListener(this);
    mExoReplay.setOnClickListener(this);
    mExoReplay10.setOnClickListener(this);
    mExoForward10.setOnClickListener(this);
    mSbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float speed = progress / 100f;
        speed += 0.5f;
        mTvSpeed.setText("x" + speed);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });
    mExoController.setVideoPlayerEventListener(new VideoController.OnVideoPlayerEventListener() {
      @Override
      public void onProgressChanged(int progress, int duration) {
        progressBar.setVisibility(View.GONE);
      }

      @Override
      public void onPlayerLoading(int progress) {
        // Do nothing
      }

      @Override
      public void onPlayerBuffering() {
        progressBar.setVisibility(View.VISIBLE);
        mExoReplay.setVisibility(View.INVISIBLE);
      }

      @Override
      public void onPlayerPlay() {
        // Do nothing
      }

      @Override
      public void onPlayerPause() {
        // Do nothing
        progressBar.setVisibility(View.GONE);
      }

      @Override
      public void onPlayerFinish() {
        mExoReplay.setVisibility(View.VISIBLE);
        mExoPause.setVisibility(View.GONE);
        mExoPlay.setVisibility(View.GONE);
        // Do nothing
      }

      @Override
      public void onPlayerError() {
        if (!ConnectionHelper.isConnectedOrConnecting(MainActivity.this))
          Toast.makeText(MainActivity.this, getString(R.string.offline), Toast.LENGTH_SHORT).show();
        else
          Toast.makeText(MainActivity.this, getString(R.string.occurred), Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      //actionBar.setVisibility(View.GONE);
      if (MainActivity.this.getWindow() != null)
        MainActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
      ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) rootPlayer.getLayoutParams();
      params.width = params.MATCH_PARENT;
      params.height = params.MATCH_PARENT;
      rootPlayer.setLayoutParams(params);
    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
      //actionBar.setVisibility(View.VISIBLE);
      if (MainActivity.this.getWindow() != null)
        MainActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
      ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) rootPlayer.getLayoutParams();
      params.width = params.MATCH_PARENT;
      params.height = params.WRAP_CONTENT;
      rootPlayer.setLayoutParams(params);
    }
  }


  @Override
  protected void onDestroy() {
    if (mExoController != null) {
      mExoController.releasePlayer();
      mExoController = null;
    }
    super.onDestroy();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.exo_play1:
        setUpVisibilityPausePlay(View.GONE, View.VISIBLE);
        mExoController.resume();
        break;
      case R.id.exo_pause1:
        setUpVisibilityPausePlay(View.VISIBLE, View.GONE);
        mExoController.pause();
        break;
      case R.id.ivFullScreen:
        setUpFullScreen();
        break;
      case R.id.exo_next1:
        mIndexVideo++;
        //setUpControls();
        playingVideo();
        break;
      case R.id.exo_prev1:
        mIndexVideo--;
        //setUpControls();
        playingVideo();
        break;
      case R.id.exo_replay:
        setUpVisibilityPausePlay(View.GONE, View.VISIBLE);
        mExoController.restart(mLinks.get(mIndexVideo), subTitleURL);
        break;
      case R.id.exo_replay_10:
        setUpVisibilityPausePlay(View.GONE, View.VISIBLE);
        if (mExoController.getCurrentPosition() >= 10000)
          mExoController.seekTo(mExoController.getCurrentPosition() - 10000);
        else
          mExoController.seekTo(0);
        break;
      case R.id.exo_forward_10:
        setUpVisibilityPausePlay(View.GONE, View.VISIBLE);
        if (mExoController.getCurrentPosition() <= mExoController.getDuration() - 10000)
          mExoController.seekTo(mExoController.getCurrentPosition() + 10000);
        else
          mExoController.seekTo(mExoController.getDuration());
        break;
      default:
        break;
    }

  }

  private void setUpVisibilityPausePlay(int play, int pause) {
    mExoPlay.setVisibility(play);
    mExoPause.setVisibility(pause);
  }

  private void setUpControls() {
    if (mLinks.size() == 1) {
      mExoPrev.setClickable(false);
      mExoNext.setClickable(false);
      mExoPrev.setVisibility(View.GONE);
      mExoNext.setVisibility(View.GONE);
    } else if (mIndexVideo == 0) {
      mExoPrev.setClickable(false);
      mExoNext.setClickable(true);
      mExoPrev.setVisibility(View.GONE);
      mExoNext.setVisibility(View.VISIBLE);
    } else if (mIndexVideo == mLinks.size() - 1) {
      mExoPrev.setClickable(true);
      mExoNext.setClickable(false);
      mExoPrev.setVisibility(View.VISIBLE);
      mExoNext.setVisibility(View.GONE);
    } else {
      mExoPrev.setClickable(true);
      mExoNext.setClickable(true);
      mExoPrev.setVisibility(View.VISIBLE);
      mExoNext.setVisibility(View.VISIBLE);
    }
  }


  private void setUpFullScreen() {
    if (mScreenMode == LANDSCAPE) {
      MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      mScreenMode = PORTRAIT;
    } else {
      MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
      mScreenMode = LANDSCAPE;
    }
  }
}
