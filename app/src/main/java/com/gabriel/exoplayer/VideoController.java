package com.gabriel.exoplayer;

import android.content.Context;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

public class VideoController {
  private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
  private static final int UPDATE_PROGRESS_INTERVAL = 1000;

  private Context mContext;
  private SimpleExoPlayerView mVideoView;
  private SimpleExoPlayer mVideoPlayer;
  private DataSource.Factory mMediaDataSourceFactory;
  private DefaultTrackSelector mTrackSelector;
  private Handler mMainHandler;
  private String mUserAgent;
  private String mCurrentUrl;
  private long mInitialPosition;
  private long mCurrentPosition;
  private OnVideoPlayerEventListener mListener;
  private SubtitleView mSubtitles;


  private Handler mUpdateProgressHandler = new Handler();
  private Runnable mUpdateProgressTask = new Runnable() {
    @Override
    public void run() {
      if (mVideoPlayer != null && mListener != null) {
        if (mVideoPlayer.getPlayWhenReady()) {
          mCurrentPosition = mVideoPlayer.getCurrentPosition();
          mListener.onProgressChanged((int) mCurrentPosition, (int) mVideoPlayer.getDuration());
          mUpdateProgressHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL);
        }
        if (mVideoPlayer.isLoading())
          mListener.onPlayerLoading((int) mVideoPlayer.getBufferedPosition());
      }
    }
  };

  public VideoController(Context context, SimpleExoPlayerView videoView) {
    mContext = context;
    mVideoView = videoView;
    mUserAgent = Util.getUserAgent(context, "Lingo");
    init();
  }

  public void setSpeed(float speed, float pitch) {
    if (mVideoPlayer != null) {
      PlaybackParameters param = new PlaybackParameters(speed, pitch);
      mVideoPlayer.setPlaybackParameters(param);
    }
  }

  public void setVideoPlayerEventListener(OnVideoPlayerEventListener listener) {
    mListener = listener;
  }

  public long getCurrentPosition() {
    return mCurrentPosition;
  }

  public String getCurrentUrl() {
    return mCurrentUrl;
  }

  public void playVideo(String url, int initialPosition, SubtitleView subtitles) {
    stop();
    this.mSubtitles = subtitles;
    initVideoPlayer(subtitles);
    mCurrentUrl = url;
    mInitialPosition = initialPosition;
    prepareVideo(url);
  }

  public void playVideo(String currentUrl, String previousUrl, int initialPosition, SubtitleView subtitles) {
    playVideo(currentUrl, initialPosition, subtitles);
  }

  public void startOver() {
    if (mVideoPlayer != null) {
      mCurrentPosition = 0;
      mVideoPlayer.seekTo(0);
      mVideoView.postDelayed(() -> {
        if (!mVideoPlayer.getPlayWhenReady())
          mVideoPlayer.setPlayWhenReady(true);
      }, 100);
    }
  }

  public void restart(String url) {
    if (mVideoPlayer != null) {
      mVideoPlayer.stop();
      mVideoPlayer.release();
    }

    initVideoPlayer(this.mSubtitles);
    prepareVideo(url);
  }

  public void resume() {
    if (mVideoPlayer != null && !mVideoPlayer.getPlayWhenReady())
      mVideoPlayer.setPlayWhenReady(true);
  }

  public void pause() {
    if (mVideoPlayer != null && mVideoPlayer.getPlayWhenReady()) {
      mVideoPlayer.setPlayWhenReady(false);
      mCurrentPosition = mVideoPlayer.getCurrentPosition();
    }
  }

  public void stopAndSavePosition() {
    if (mVideoPlayer != null) {
      mVideoPlayer.stop();
      mCurrentPosition = mVideoPlayer.getCurrentPosition();
      mVideoPlayer.release();
    }
  }

  public void stop() {
    if (mVideoPlayer != null) {
      mVideoPlayer.stop();
      mCurrentPosition = 0;
      mVideoPlayer.release();
    }
  }

  public void releasePlayer() {
    if (mVideoPlayer != null) {
      mVideoPlayer.release();
      mVideoPlayer = null;
      mTrackSelector = null;
    }
  }

  public void seekTo(int position) {
    if (mVideoPlayer != null) {
      mVideoPlayer.seekTo(position);
      mVideoPlayer.setPlayWhenReady(true);
    }
  }

  public long getDuration() {
    if (mVideoPlayer != null)
      return mVideoPlayer.getDuration();
    return 0;
  }

  public boolean isPlaying() {
    return mVideoPlayer != null && mVideoPlayer.getPlayWhenReady();
  }

  private void init() {
    mMainHandler = new Handler();
    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(mMainHandler, (elapsedMs, bytes, bitrate) -> {

    });
    TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
    mTrackSelector =
        new DefaultTrackSelector(videoTrackSelectionFactory);
    mTrackSelector.setParameters(mTrackSelector.getParameters().withMaxVideoSize(3086, 2160));
    mMediaDataSourceFactory = new DefaultDataSourceFactory(mContext, mUserAgent, (DefaultBandwidthMeter) bandwidthMeter);
  }

  private void initVideoPlayer(SubtitleView subtitles) {
    mVideoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, mTrackSelector);
    mVideoView.setPlayer(mVideoPlayer);

    mVideoPlayer.addTextOutput(new TextOutput() {
      @Override
      public void onCues(List<Cue> cues) {
        if (subtitles != null)
          subtitles.onCues(cues);
      }
    });
    mVideoPlayer.addListener(new Player.DefaultEventListener() {
      @Override
      public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
          case PlaybackState.STATE_PAUSED:
            if (mListener != null) {
              mListener.onPlayerBuffering();
            }
            break;
          case PlaybackState.STATE_PLAYING:
            if (playWhenReady) {
              mUpdateProgressHandler.post(mUpdateProgressTask);
              if (mListener != null) {
                mListener.onPlayerPlay();
              }
            } else {
              mUpdateProgressHandler.removeCallbacks(mUpdateProgressTask);
              if (mListener != null) {
                mListener.onPlayerPause();
              }
            }
            break;
          case Player.STATE_ENDED:
            if (playWhenReady) {
              mUpdateProgressHandler.removeCallbacks(mUpdateProgressTask);
              mVideoPlayer.setPlayWhenReady(false);
              if (mListener != null) {
                mListener.onPlayerFinish();
              }
            }
            break;
        }
      }

      @Override
      public void onPlayerError(ExoPlaybackException error) {
        if (mListener != null) {
          mListener.onPlayerError();
        }
      }
    });
  }

  private void prepareVideo(String videoUrl) {
    Uri uri = Uri.parse(videoUrl);
    MediaSource mediaSource;
    mediaSource = buildMediaSource(uri, "");
    mVideoPlayer.prepare(mediaSource);
    if (mInitialPosition > 0)
      mVideoPlayer.seekTo(mInitialPosition);
    mVideoPlayer.setPlayWhenReady(true);

  }

  private MediaSource buildMediaSource(Uri uri, String overrideExtension) {

    /*DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(mContext,
        Util.getUserAgent(mContext, "exo-demo"));

    MediaSource mediaSource[] = new MediaSource[2];

    MediaSource contentMediaSource = new ExtractorMediaSource(uri, dataSourceFactory, new DefaultExtractorsFactory(),
        mMainHandler, null);
    mediaSource[0] = contentMediaSource;
    // For subtitles
    String srt_link = "/Users/gem/Documents/Demo/Exoplayer/app/src/main/java/com/gabriel/exoplayer/WebVTTExample.vtt";
    *//*if ((srt_link == null || srt_link == "false" || srt_link.isEmpty())) {
      return mediaSource;

    }*//*

    Uri uriSubtitle = Uri.parse(srt_link);
    *//*Format textFormat = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP,
        Format.NO_VALUE, "hi");
    MediaSource subtitleSource = new SingleSampleMediaSource(uriSubtitle, dataSourceFactory, textFormat, C.TIME_UNSET);*//*

    SingleSampleMediaSource subtitleSource = new SingleSampleMediaSource(uriSubtitle, dataSourceFactory,
        Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP, Format.NO_VALUE, "en", null),
        C.TIME_UNSET);
    mediaSource[1] = subtitleSource;

    return new MergingMediaSource(mediaSource);*/


    int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
        : Util.inferContentType("." + overrideExtension);
    switch (type) {
      case C.TYPE_DASH:
        return new DashMediaSource(uri, buildDataSourceFactory(false),
            new DefaultDashChunkSource.Factory(mMediaDataSourceFactory), mMainHandler, null);
      case C.TYPE_HLS:
        return new HlsMediaSource(uri, mMediaDataSourceFactory, mMainHandler, null);
      case C.TYPE_OTHER:
        return new ExtractorMediaSource(uri, mMediaDataSourceFactory, new DefaultExtractorsFactory(),
            mMainHandler, null);
      default: {
        throw new IllegalStateException("Unsupported type: " + type);
      }
    }


  }

  private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
    return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
  }

  private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
    return buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
  }

  private DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
    return new DefaultDataSourceFactory(mContext, bandwidthMeter,
        buildHttpDataSourceFactory(bandwidthMeter));
  }

  private HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
    return new DefaultHttpDataSourceFactory(mUserAgent, bandwidthMeter);
  }

  public interface OnVideoPlayerEventListener {
    void onProgressChanged(int progress, int duration);

    void onPlayerLoading(int progress);

    void onPlayerBuffering();

    void onPlayerPlay();

    void onPlayerPause();

    void onPlayerFinish();

    void onPlayerError();
  }
}
