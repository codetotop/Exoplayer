package com.gabriel.exoplayer;

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
  private ImageButton mExoPrev, mExoNext, mExoPlay, mExoPause, mExoReplay;
  private TextView mTvSpeedSlow, mTvSpeedNormal, mTvSpeedFast;
  private SubtitleView mSubtitles;

  private VideoController mExoController;
  private int mScreenMode = PORTRAIT;
  private List<String> mLinks = new ArrayList<>();
  private int mIndexVideo = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    addControlls();
    addEvents();
    playingVideo();
  }

  private void playingVideo() {
    mExoController.playVideo(mLinks.get(mIndexVideo), 0, mSubtitles);
  }

  private void addControlls() {
    rootPlayer = findViewById(R.id.fl_player_view);
    exoPlayer = findViewById(R.id.simple_player_view);
    progressBar = findViewById(R.id.common_progress_bar);
    ivFullScreen = findViewById(R.id.ivFullScreen);
    mExoPlay = findViewById(R.id.exo_play);
    mExoPause = findViewById(R.id.exo_pause);
    mExoPrev = findViewById(R.id.exo_prev1);
    mExoNext = findViewById(R.id.exo_next1);
    mExoReplay = findViewById(R.id.exo_replay);
    mTvSpeedSlow = findViewById(R.id.speedSlow);
    mTvSpeedNormal = findViewById(R.id.speedNormal);
    mTvSpeedFast = findViewById(R.id.speedFast);
    mSubtitles = findViewById(R.id.subtitle);
    mExoController = new VideoController(this, exoPlayer);
    mLinks.add("http://www.html5videoplayer.net/videos/toystory.mp4");
    mLinks.add("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
    mLinks.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
    mLinks.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
    mLinks.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4");
    setUpControls();
  }

  private void addEvents() {

    ivFullScreen.setOnClickListener(this);

    mExoPrev.setOnClickListener(this);

    mExoNext.setOnClickListener(this);

    mExoReplay.setOnClickListener(this);
    mTvSpeedSlow.setOnClickListener(this);
    mTvSpeedNormal.setOnClickListener(this);
    mTvSpeedFast.setOnClickListener(this);

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
        mExoReplay.setVisibility(View.GONE);
      }

      @Override
      public void onPlayerPlay() {
        // Do nothing
      }

      @Override
      public void onPlayerPause() {
        // Do nothing
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
      case R.id.ivFullScreen:
        setUpFullScreen();
        break;
      case R.id.exo_next1:
        mIndexVideo++;
        setUpControls();
        mExoController.playVideo(mLinks.get(mIndexVideo), 0, mSubtitles);
        break;
      case R.id.exo_prev1:
        mIndexVideo--;
        setUpControls();
        mExoController.playVideo(mLinks.get(mIndexVideo), 0, mSubtitles);
        break;
      case R.id.exo_replay:
        mExoController.playVideo(mLinks.get(mIndexVideo), 0, mSubtitles);
        break;
      case R.id.speedSlow:
        mExoController.setSpeed(0.5f, 0.5f);
        break;
      case R.id.speedNormal:
        mExoController.setSpeed(1f, 1f);
        break;
      case R.id.speedFast:
        mExoController.setSpeed(1.5f, 1.5f);
        break;
      default:
        break;
    }

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
