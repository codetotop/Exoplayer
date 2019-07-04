package com.gabriel.exoplayer;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NotificationReceiver.NotificationListener {

  private static final int LANDSCAPE = 0;
  private static final int PORTRAIT = 1;

  private ConstraintLayout rootPlayer;
  private SimpleExoPlayerView exoPlayer;
  private ProgressBar progressBar;
  private ImageView ivFullScreen;
  public static final String CHANNEL_ID = "Gabriel";
  private SeekBar mSbSpeed;
  private SubtitleView mSubtitles;
  private ImageButton mExoPrev, mExoNext, mExoPlay, mExoPause, mExoReplay, mExoReplay10, mExoForward10;
  private VideoController mExoController;
  private int mScreenMode = PORTRAIT;
  private List<String> mLinks = new ArrayList<>();
  private int mIndexVideo = 0;
  private TextView mTvSpeed1, mTvSpeed2;
  private ConstraintLayout mContainerSpeed;
  NotificationUtils mNotificationUtils;
  private String subTitleURL = "https://www.iandevlin.com/html5test/webvtt/upc-video-subtitles-en.vtt";
  private ConstraintLayout mPlaybackContainer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    NotificationReceiver receiver = new NotificationReceiver(this);
    registerReceiver(receiver, new IntentFilter());
    addControlls();
    addEvents();
    playingVideo();
  }

  private void playingVideo() {
    mExoController.playVideo(mLinks.get(mIndexVideo), subTitleURL, 0, mSubtitles);
    mNotificationUtils = new NotificationUtils(this);
    mNotificationUtils.createMediaCustomNotification(CHANNEL_ID);
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
    mTvSpeed1 = findViewById(R.id.tvSpeed1);
    mTvSpeed2 = findViewById(R.id.tvSpeed2);
    mContainerSpeed = findViewById(R.id.containerSpeed);
    mPlaybackContainer = findViewById(R.id.playbackContainer);
    //mLinks.add("http://edge-token-download-plldsigb.bbt757.com.edgesuite.net/mp4/224k/29333_224k.mp4?strtoken=1562129148_f0bcb9bdddab606a1af3e86535cbb2ba&ext=da39a3ee5e6b4b0d3255bfef95601890afd80709");
    //mLinks.add("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
    //mLinks.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
    mLinks.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
    mLinks.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4");
    //setUpControls();
    mTvSpeed1.setText(getResources().getString(R.string.speed_default));
    mTvSpeed2.setText(getResources().getString(R.string.speed_default));
    setUpVisibilityPausePlay(View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
    mExoController = new VideoController(this, exoPlayer);
  }

  @SuppressLint("ClickableViewAccessibility")
  private void addEvents() {

    ivFullScreen.setOnClickListener(this);
    mExoPrev.setOnClickListener(this);
    mExoNext.setOnClickListener(this);
    mExoPlay.setOnClickListener(this);
    mExoPause.setOnClickListener(this);
    mExoReplay.setOnClickListener(this);
    mExoReplay10.setOnClickListener(this);
    mExoForward10.setOnClickListener(this);
    mTvSpeed1.setOnClickListener(this);
    mPlaybackContainer.setOnTouchListener((v, event) -> {
      mContainerSpeed.setVisibility(View.GONE);
      return false;
    });
    mSbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float speed = progress / 100f;
        speed += 0.5f;
        DecimalFormat format = new DecimalFormat("#.#");
        mTvSpeed1.setText("x" + format.format(speed));
        mTvSpeed2.setText("x" + format.format(speed));
        mExoController.setSpeed(speed, speed);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        mContainerSpeed.setVisibility(View.GONE);
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
        setUpVisibilityPausePlay(View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
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
        setUpVisibilityPausePlay(View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
        mExoController.resume();
        break;
      case R.id.exo_pause1:
        setUpVisibilityPausePlay(View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
        mExoController.pause();
        break;
      case R.id.ivFullScreen:
        setUpFullScreen();
        break;
      case R.id.exo_next1:
        mIndexVideo++;
        playingVideo();
        break;
      case R.id.exo_prev1:
        mIndexVideo--;
        playingVideo();
        break;
      case R.id.exo_replay:
        setUpVisibilityPausePlay(View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
        mExoController.restart(mLinks.get(mIndexVideo), subTitleURL);
        break;
      case R.id.exo_replay_10:

        if (mExoController.getCurrentPosition() > 10000) {
          mExoController.seekTo(mExoController.getCurrentPosition() - 10000);
        } else {
          mExoController.seekTo(0);
        }
        setUpVisibilityPausePlay(View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
        break;
      case R.id.exo_forward_10:
        if (mExoController.getCurrentPosition() < mExoController.getDuration() - 10000) {
          mExoController.seekTo(mExoController.getCurrentPosition() + 10000);
          setUpVisibilityPausePlay(View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
        } else {
          mExoController.seekTo(mExoController.getDuration());
          setUpVisibilityPausePlay(View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
        }
        break;
      case R.id.tvSpeed1:
        mContainerSpeed.setVisibility(View.VISIBLE);
        break;
      default:
        break;
    }

  }

  private void setUpVisibilityPausePlay(int play, int pause, int replay) {
    mExoPlay.setVisibility(play);
    mExoPause.setVisibility(pause);
    mExoReplay.setVisibility(replay);
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

  @Override
  public void onPausePlay() {
    if (mExoController.isPlaying()) {
      mExoController.pause();
    } else {
      mExoController.resume();
    }
  }
}
