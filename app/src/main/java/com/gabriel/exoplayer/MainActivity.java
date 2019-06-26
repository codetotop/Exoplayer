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
import android.widget.Toast;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

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
    mExoController.playVideo(mLinks.get(mIndexVideo), 0);
  }

  private void addControlls() {
    rootPlayer = findViewById(R.id.fl_player_view);
    exoPlayer = findViewById(R.id.simple_player_view);
    progressBar = findViewById(R.id.common_progress_bar);
    ivFullScreen = findViewById(R.id.ivFullScreen);
    mExoPlay = findViewById(R.id.exo_play);
    mExoPause = findViewById(R.id.exo_pause);
    mExoPrev = findViewById(R.id.exo_prev);
    mExoNext = findViewById(R.id.exo_next);
    mExoReplay = findViewById(R.id.exo_replay);
    mExoController = new VideoController(this, exoPlayer);


    mLinks.add("http://www.html5videoplayer.net/videos/toystory.mp4");
    mLinks.add("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
  }

  private void addEvents() {

    ivFullScreen.setOnClickListener(this);

    mExoPrev.setOnClickListener(this);

    mExoNext.setOnClickListener(this);

    mExoReplay.setOnClickListener(this);

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
      case R.id.exo_next:
        mIndexVideo = (mIndexVideo + mLinks.size()) / mLinks.size();
      case R.id.exo_prev:
        mIndexVideo = (mLinks.size() - mIndexVideo) / mLinks.size();
      case R.id.exo_replay:
        mExoController.playVideo(mLinks.get(mIndexVideo), 0);
        break;
      default:
        break;

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
