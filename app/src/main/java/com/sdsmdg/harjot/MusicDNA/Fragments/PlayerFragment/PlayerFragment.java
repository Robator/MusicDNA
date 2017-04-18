package com.sdsmdg.harjot.MusicDNA.Fragments.PlayerFragment;


import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.DbmHandler;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.sdsmdg.harjot.MusicDNA.Activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.ClickItemTouchListener.ClickItemTouchListener;
import com.sdsmdg.harjot.MusicDNA.Config;
import com.sdsmdg.harjot.MusicDNA.CustomRecyclerView.CustomAdapter;
import com.sdsmdg.harjot.MusicDNA.CustomRecyclerView.SnappyRecyclerView;
import com.sdsmdg.harjot.MusicDNA.CustomViews.CustomProgressBar;
import com.sdsmdg.harjot.MusicDNA.Models.LocalTrack;
import com.sdsmdg.harjot.MusicDNA.Models.Track;
import com.sdsmdg.harjot.MusicDNA.Models.UnifiedTrack;
import com.sdsmdg.harjot.MusicDNA.MusicDNAApplication;
import com.sdsmdg.harjot.MusicDNA.NotificationManager.AudioPlayerBroadcastReceiver;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.VisualizerViews.VisualizerView;
import com.sdsmdg.harjot.MusicDNA.imageLoader.ImageLoader;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment implements
        AudioPlayerBroadcastReceiver.onCallbackListener {

    public SnappyRecyclerView snappyRecyclerView;
    CustomAdapter customAdapter;
    private AudioVisualization audioVisualization;
    public static VisualizerView mVisualizerView;
    public static MediaPlayer mMediaPlayer;
    public static Visualizer mVisualizer;
    public static Equalizer mEqualizer;
    public static BassBoost bassBoost;
    public static PresetReverb presetReverb;

    public static boolean isReplayIconVisible = false;

    public static boolean isPrepared = false;

    AVLoadingIndicatorView bufferingIndicator;

    public static CustomProgressBar cpb;

    Pair<String, String> temp;

    TextView currTime, totalTime;

    public ImageView repeatController;
    public ImageView shuffleController;

    public ImageView equalizerIcon;
    public ImageView mainTrackController;
    public ImageView nextTrackController;
    public ImageView previousTrackController;
    public ImageView favouriteIcon;
    public ImageView queueIcon;

    boolean isFav = false;

    public RelativeLayout bottomContainer;
    public RelativeLayout seekBarContainer;
    public RelativeLayout toggleContainer;

    public ImageView selected_track_image;
    public TextView selected_track_title;
    public TextView selected_track_artist;
    public ImageView player_controller;

    public RelativeLayout smallPlayer;

    ImageView favControllerSp, nextControllerSp;

    ImageLoader imgLoader;

    public SeekBar progressBar;

    public static int durationInMilliSec;
    static boolean completed = false;
    public boolean pauseClicked = false;
    boolean isTracking = false;

    public static boolean localIsPlaying = false;

    public Timer timer;

    public static Track track;
    public static LocalTrack localTrack;

    static boolean isRefreshed = false;

    public PlayerFragmentCallbackListener mCallback;
    public onPlayPauseListener mCallback7;

    public interface PlayerFragmentCallbackListener {
        void onComplete();

        void onPreviousTrack();

        void onEqualizerClicked();

        void onQueueClicked();

        void onPrepared();

        void onFullScreen();

        void onSettingsClicked();

        void onAddedtoFavfromPlayer();

        void onShuffleEnabled();

        void onShuffleDisabled();

        void onSmallPlayerTouched();
    }

    public interface onPlayPauseListener{
        void onPlayPause();
    }

    HomeActivity homeActivity;
    public static Context ctx;

    public RelativeLayout spToolbar;
    ImageView overflowMenuAB;
    ImageView spImgAB;
    TextView spTitleAB;
    TextView spArtistAB;

    public boolean isStart = true;

    ShowcaseView showCase;

    long startTrack;
    long endTrack;

    public PlayerFragment() {
        // Required empty public constructor
    }

    public void setupVisualizerFxAndUI() {

        try {
            mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
            mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
            mEqualizer.setEnabled(true);

            try {
                bassBoost = new BassBoost(0, mMediaPlayer.getAudioSessionId());
                bassBoost.setEnabled(false);
                BassBoost.Settings bassBoostSettingTemp = bassBoost.getProperties();
                BassBoost.Settings bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
                bassBoostSetting.strength = (1000 / 19);
                bassBoost.setProperties(bassBoostSetting);
                mMediaPlayer.setAuxEffectSendLevel(1.0f);

                presetReverb = new PresetReverb(0, mMediaPlayer.getAudioSessionId());
                presetReverb.setPreset(PresetReverb.PRESET_NONE);
                presetReverb.setEnabled(false);
                mMediaPlayer.setAuxEffectSendLevel(1.0f);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (homeActivity.isEqualizerEnabled) {
            try {
                bassBoost.setEnabled(true);
                BassBoost.Settings bassBoostSettingTemp = bassBoost.getProperties();
                BassBoost.Settings bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
                if (homeActivity.bassStrength == -1) {
                    bassBoostSetting.strength = (1000 / 19);
                } else {
                    bassBoostSetting.strength = homeActivity.bassStrength;
                }
                bassBoost.setProperties(bassBoostSetting);
                mMediaPlayer.setAuxEffectSendLevel(1.0f);

                if (homeActivity.reverbPreset == -1) {
                    presetReverb.setPreset(PresetReverb.PRESET_NONE);
                } else {
                    presetReverb.setPreset(homeActivity.reverbPreset);
                }
                presetReverb.setEnabled(true);
                mMediaPlayer.setAuxEffectSendLevel(1.0f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (homeActivity.isEqualizerEnabled && homeActivity.isEqualizerReloaded) {
            try {
                homeActivity.isEqualizerEnabled = true;
                int pos = homeActivity.presetPos;
                if (pos != 0) {
                    mEqualizer.usePreset((short) (pos - 1));
                } else {
                    for (short i = 0; i < 5; i++) {
                        mEqualizer.setBandLevel(i, (short) homeActivity.seekbarpos[i]);
                    }
                }
                if (homeActivity.bassStrength != -1 && homeActivity.reverbPreset != -1) {
                    bassBoost.setEnabled(true);
                    bassBoost.setStrength(homeActivity.bassStrength);
                    presetReverb.setEnabled(true);
                    presetReverb.setPreset(homeActivity.reverbPreset);
                }
                mMediaPlayer.setAuxEffectSendLevel(1.0f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        try {
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            mVisualizer.setDataCaptureListener(
                    new Visualizer.OnDataCaptureListener() {
                        public void onWaveFormDataCapture(Visualizer visualizer,
                                                          byte[] bytes, int samplingRate) {
                        }

                        public void onFftDataCapture(Visualizer visualizer,
                                                     byte[] bytes, int samplingRate) {
                            homeActivity.updateVisualizer(bytes);
                        }
                    }, Visualizer.getMaxCaptureRate() / 2, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void togglePlayPause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (homeActivity.isPlayerVisible) {
                mainTrackController.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
                isReplayIconVisible = false;
            } else {
                player_controller.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                mainTrackController.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                isReplayIconVisible = false;
            }
            if (mVisualizer != null)
                mVisualizer.setEnabled(false);
            if (!isStart && mCallback != null)
                mCallback7.onPlayPause();
        } else {
            if (!completed) {
                setupVisualizerFxAndUI();
                if (mVisualizer != null)
                    mVisualizer.setEnabled(true);
                if (homeActivity.isPlayerVisible) {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
                    isReplayIconVisible = false;
                } else {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                    isReplayIconVisible = false;
                }
                mMediaPlayer.start();
                if (!isStart && mCallback != null)
                    mCallback7.onPlayPause();
            } else {
                mMediaPlayer.seekTo(0);
                setupVisualizerFxAndUI();
                if (mVisualizer != null)
                    mVisualizer.setEnabled(true);
                mMediaPlayer.start();
                completed = false;
                if (homeActivity.isPlayerVisible) {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                    isReplayIconVisible = false;
                } else {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                    isReplayIconVisible = false;
                }
            }
        }
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        homeActivity = (HomeActivity) context;
        ctx = context;
        try {
            mCallback = (PlayerFragmentCallbackListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                completed = false;
                pauseClicked = false;
                isPrepared = true;
                mCallback.onPrepared();
                if (homeActivity.isPlayerVisible) {
                    player_controller.setVisibility(View.VISIBLE);
                    player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
                } else {
                    player_controller.setVisibility(View.VISIBLE);
                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                }
                togglePlayPause();
                togglePlayPause();
                togglePlayPause();
                bufferingIndicator.setVisibility(View.GONE);
                mainTrackController.setVisibility(View.VISIBLE);
                equalizerIcon.setVisibility(View.VISIBLE);

                snappyRecyclerView.setCurrentPosition(HomeActivity.queueCurrentIndex);
                snappyRecyclerView.setTransparency();

                new HomeActivity.SaveQueue().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                new HomeActivity.SaveRecents().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                completed = true;
                if (homeActivity.isPlayerVisible) {
                    mainTrackController.setImageResource(R.drawable.ic_replay_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_replay_white_48dp);
                    isReplayIconVisible = true;
                } else {
                    player_controller.setImageResource(R.drawable.ic_replay_white_48dp);
                    mainTrackController.setImageResource(R.drawable.ic_replay_white_48dp);
                    isReplayIconVisible = true;
                }
            }
        });

        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                double ratio = percent / 100.0;
                double bufferingLevel = (int) (mp.getDuration() * ratio);
                if (progressBar != null) {
                    progressBar.setSecondaryProgress((int) bufferingLevel);
                }
            }
        });

        mMediaPlayer.setOnErrorListener(
                new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        return true;
                    }
                }
        );

        mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        bufferingIndicator.setVisibility(View.VISIBLE);
                        mainTrackController.setVisibility(View.GONE);
                        isPrepared = false;
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        bufferingIndicator.setVisibility(View.GONE);
                        mainTrackController.setVisibility(View.VISIBLE);
                        isPrepared = true;
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        audioVisualization = (AudioVisualization)view.findViewById(R.id.visualizer_view);
        return view;
    }

    @Override
    public void onPause() {
        audioVisualization.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (snappyRecyclerView.getCurrentPosition() != HomeActivity.queueCurrentIndex) {
            snappyRecyclerView.scrollToPosition(HomeActivity.queueCurrentIndex);
            snappyRecyclerView.setCurrentPosition(HomeActivity.queueCurrentIndex);
            customAdapter.notifyDataSetChanged();
        }
        audioVisualization.onResume();
        snappyRecyclerView.setTransparency();
    }

    @Override
    public void onCallbackCalled(int i) {
        switch (i) {
            case 6:
                mCallback.onPrepared();
                break;
            case 3:
                if (homeActivity.queueCurrentIndex != 0) {
                    mMediaPlayer.pause();
                    mCallback.onPreviousTrack();
                }
                break;
            case 2:
                mMediaPlayer.pause();
                homeActivity.nextControllerClicked = true;
                mCallback.onComplete();
                break;
        }
    }

    @Override
    public void togglePLayPauseCallback() {
        togglePlayPause();
    }

    @Override
    public boolean getPauseClicked() {
        return pauseClicked;
    }

    @Override
    public void setPauseClicked(boolean bool) {
        pauseClicked = bool;
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    @Override
    public void onViewCreated(final View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        audioVisualization.linkTo(DbmHandler.Factory.newVisualizerHandler(getContext(),0));
        imgLoader = new ImageLoader(getContext());

        smallPlayer = (RelativeLayout) view.findViewById(R.id.smallPlayer);

        snappyRecyclerView = (SnappyRecyclerView) view.findViewById(R.id.visualizer_recycler);

        bufferingIndicator = (AVLoadingIndicatorView) view.findViewById(R.id.bufferingIndicator);
        currTime = (TextView) view.findViewById(R.id.currTime);
        totalTime = (TextView) view.findViewById(R.id.totalTime);

        repeatController = (ImageView) view.findViewById(R.id.repeat_controller);
        shuffleController = (ImageView) view.findViewById(R.id.shuffle_controller);

        spToolbar = (RelativeLayout) view.findViewById(R.id.smallPlayer_AB);

        overflowMenuAB = (ImageView) view.findViewById(R.id.menuIcon);
        spImgAB = (ImageView) view.findViewById(R.id.selected_track_image_sp_AB);
        spImgAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!homeActivity.isPlayerTransitioning && smallPlayer != null) {
                    homeActivity.hidePlayer();
//                    homeActivity.showTabs();
                    homeActivity.isPlayerVisible = false;
                }
            }
        });
        spTitleAB = (TextView) view.findViewById(R.id.selected_track_title_sp_AB);
        spTitleAB.setSelected(true);

        spArtistAB = (TextView) view.findViewById(R.id.selected_track_artist_sp_AB);

        if (homeActivity.shuffleEnabled) {
            shuffleController.setImageResource(R.drawable.ic_shuffle_filled);
        } else {
            shuffleController.setImageResource(R.drawable.ic_shuffle_alpha);
        }

        if (homeActivity.repeatEnabled) {
            repeatController.setImageResource(R.drawable.ic_repeat_filled);
        } else if (homeActivity.repeatOnceEnabled) {
            repeatController.setImageResource(R.drawable.ic_repeat_once);
        } else {
            repeatController.setImageResource(R.drawable.ic_repeat_alpha);
        }

        repeatController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeActivity.repeatOnceEnabled) {
                    homeActivity.repeatOnceEnabled = false;
                    repeatController.setImageResource(R.drawable.ic_repeat_alpha);
                } else if (homeActivity.repeatEnabled) {
                    homeActivity.repeatEnabled = false;
                    homeActivity.repeatOnceEnabled = true;
                    repeatController.setImageResource(R.drawable.ic_repeat_once);
                } else {
                    homeActivity.repeatEnabled = true;
                    repeatController.setImageResource(R.drawable.ic_repeat_filled);
                }
            }
        });

        shuffleController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeActivity.shuffleEnabled) {
                    homeActivity.shuffleEnabled = false;
                    shuffleController.setImageResource(R.drawable.ic_shuffle_alpha);
                    mCallback.onShuffleDisabled();
                } else {
                    homeActivity.shuffleEnabled = true;
                    shuffleController.setImageResource(R.drawable.ic_shuffle_filled);
                    mCallback.onShuffleEnabled();
                }
                snappyRecyclerView.scrollToPosition(HomeActivity.queueCurrentIndex);
                snappyRecyclerView.setCurrentPosition(HomeActivity.queueCurrentIndex);
                customAdapter.notifyDataSetChanged();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        snappyRecyclerView.setTransparency();
                    }
                }, 400);
            }
        });

        equalizerIcon = (ImageView) view.findViewById(R.id.equalizer_icon);
        equalizerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onEqualizerClicked();
            }
        });
        equalizerIcon.setVisibility(View.INVISIBLE);

        mainTrackController = (ImageView) view.findViewById(R.id.controller);
        nextTrackController = (ImageView) view.findViewById(R.id.next);
        previousTrackController = (ImageView) view.findViewById(R.id.previous);

        isFav = false;

        favouriteIcon = (ImageView) view.findViewById(R.id.fav_icon);
        favControllerSp = (ImageView) view.findViewById(R.id.fav_controller_sp);

        if (homeActivity.isFavourite) {
            favouriteIcon.setImageResource(R.drawable.ic_heart_filled_1);
            favControllerSp.setImageResource(R.drawable.ic_heart_filled_1);
            isFav = true;
        } else {
            favouriteIcon.setImageResource(R.drawable.ic_heart_alpha);
            favControllerSp.setImageResource(R.drawable.ic_heart_alpha);
            isFav = false;
        }

        favouriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    favouriteIcon.setImageResource(R.drawable.ic_heart_alpha);
                    favControllerSp.setImageResource(R.drawable.ic_heart_alpha);
                    isFav = false;
                    removeFromFavourite();
                } else {
                    favouriteIcon.setImageResource(R.drawable.ic_heart_filled_1);
                    favControllerSp.setImageResource(R.drawable.ic_heart_filled_1);
                    isFav = true;
                    addToFavourite();
                }
                new HomeActivity.SaveFavourites().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        favControllerSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    favouriteIcon.setImageResource(R.drawable.ic_heart_alpha);
                    favControllerSp.setImageResource(R.drawable.ic_heart_alpha);
                    isFav = false;
                    removeFromFavourite();
                } else {
                    favouriteIcon.setImageResource(R.drawable.ic_heart_filled_1);
                    favControllerSp.setImageResource(R.drawable.ic_heart_filled_1);
                    isFav = true;
                    addToFavourite();
                }
                new HomeActivity.SaveFavourites().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        queueIcon = (ImageView) view.findViewById(R.id.queue_icon);
        queueIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onQueueClicked();
            }
        });

        selected_track_image = (ImageView) view.findViewById(R.id.selected_track_image_sp);
        selected_track_title = (TextView) view.findViewById(R.id.selected_track_title_sp);
        selected_track_artist = (TextView) view.findViewById(R.id.selected_track_artist_sp);
        player_controller = (ImageView) view.findViewById(R.id.player_control_sp);

        nextControllerSp = (ImageView) view.findViewById(R.id.next_controller_sp);

        nextControllerSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.pause();
                homeActivity.nextControllerClicked = true;
                mCallback.onComplete();
            }
        });

        bottomContainer = (RelativeLayout) view.findViewById(R.id.mainControllerContainer);
        seekBarContainer = (RelativeLayout) view.findViewById(R.id.seekBarContainer);
        toggleContainer = (RelativeLayout) view.findViewById(R.id.toggleContainer);

        mVisualizerView = (VisualizerView) view.findViewById(R.id.myvisualizerview);

        VisualizerView.w = HomeActivity.screen_width;
        VisualizerView.h = HomeActivity.screen_height;
        VisualizerView.conf = Bitmap.Config.ARGB_8888;
        VisualizerView.bmp = Bitmap.createBitmap(VisualizerView.w, VisualizerView.h, VisualizerView.conf);
        HomeActivity.cacheCanvas = new Canvas(VisualizerView.bmp);

        if (HomeActivity.queue != null) {
            customAdapter = new CustomAdapter(getContext(), snappyRecyclerView, HomeActivity.queue.getQueue());
        } else {
            customAdapter = new CustomAdapter(getContext(), snappyRecyclerView, new ArrayList<UnifiedTrack>());
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        snappyRecyclerView.setLayoutManager(linearLayoutManager);
        snappyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        snappyRecyclerView.setAdapter(customAdapter);
        snappyRecyclerView.setActivity(homeActivity);

        snappyRecyclerView.addOnItemTouchListener(new ClickItemTouchListener(snappyRecyclerView) {
            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                return false;
            }

            @Override
            public boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                if (homeActivity.isFullScreenEnabled) {
                    homeActivity.isFullScreenEnabled = false;
                    bottomContainer.setVisibility(View.VISIBLE);
                    seekBarContainer.setVisibility(View.VISIBLE);
                    toggleContainer.setVisibility(View.VISIBLE);
                    spToolbar.setVisibility(View.VISIBLE);
                    mCallback.onFullScreen();
                } else {
                    homeActivity.isFullScreenEnabled = true;
                    bottomContainer.setVisibility(View.INVISIBLE);
                    seekBarContainer.setVisibility(View.INVISIBLE);
                    toggleContainer.setVisibility(View.INVISIBLE);
                    spToolbar.setVisibility(View.INVISIBLE);
                    mCallback.onFullScreen();
                }
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


        cpb = (CustomProgressBar) view.findViewById(R.id.customProgress);

        smallPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSmallPlayerTouched();
            }
        });


        track = homeActivity.selectedTrack;
        localTrack = homeActivity.localSelectedTrack;

        if (homeActivity.streamSelected) {
            try {
                durationInMilliSec = track.getDuration();
            } catch (Exception e) {

            }
            if (track.getArtworkURL() != null) {
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(selected_track_image);
            } else {
                selected_track_image.setImageResource(R.drawable.ic_default);
            }
            try {
                spTitleAB.setText(track.getTitle());
                selected_track_title.setText(track.getTitle());
                selected_track_artist.setText("");
                spArtistAB.setText("");
            } catch (Exception e) {
            }

        } else {
            try {
                durationInMilliSec = (int) localTrack.getDuration();
            } catch (Exception e) {

            }
            try {
                imgLoader.DisplayImage(localTrack.getPath(), selected_track_image);
            } catch (Exception e) {

            }
            try {
                spTitleAB.setText(localTrack.getTitle());
                selected_track_title.setText(localTrack.getTitle());
                selected_track_artist.setText(localTrack.getArtist());
                spArtistAB.setText(localTrack.getArtist());
            } catch (Exception e) {

            }

        }

        temp = getTime(durationInMilliSec);
        totalTime.setText(temp.first + ":" + temp.second);

        homeActivity.mWifi = homeActivity.connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        try {
            if (homeActivity.streamSelected) {
                if ((homeActivity.settings.isStreamOnlyOnWifiEnabled() && homeActivity.mWifi.isConnected()) || !homeActivity.settings.isStreamOnlyOnWifiEnabled()) {
                    isPrepared = false;
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(track.getStreamURL() + "?client_id=" + Config.CLIENT_ID);
                    mMediaPlayer.prepareAsync();
                } else {
                    mMediaPlayer.pause();
                    homeActivity.nextControllerClicked = true;
                    mCallback.onComplete();
                }
            } else {
                isPrepared = false;
                mMediaPlayer.reset();
                File f = new File(localTrack.getPath());
                if (f.exists()) {
                    mMediaPlayer.setDataSource(localTrack.getPath());
                    mMediaPlayer.prepareAsync();
                } else {
                    Toast.makeText(getContext(), "Error playing " + localTrack.getTitle() + ". Skipping to next track", Toast.LENGTH_SHORT).show();
                    mMediaPlayer.pause();
                    homeActivity.nextControllerClicked = true;
                    mCallback.onComplete();
                }
            }
            bufferingIndicator.setVisibility(View.VISIBLE);
            mainTrackController.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        overflowMenuAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popMenu = new PopupMenu(getContext(), overflowMenuAB);
                popMenu.getMenuInflater().inflate(R.menu.player_overflow_menu, popMenu.getMenu());

                popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Full Screen")) {
                            if (homeActivity.isFullScreenEnabled) {
                                homeActivity.isFullScreenEnabled = false;
                                bottomContainer.setVisibility(View.VISIBLE);
                                seekBarContainer.setVisibility(View.VISIBLE);
                                toggleContainer.setVisibility(View.VISIBLE);
                                spToolbar.setVisibility(View.VISIBLE);
                                mCallback.onFullScreen();
                            } else {
                                homeActivity.isFullScreenEnabled = true;
                                bottomContainer.setVisibility(View.INVISIBLE);
                                seekBarContainer.setVisibility(View.INVISIBLE);
                                toggleContainer.setVisibility(View.INVISIBLE);
                                spToolbar.setVisibility(View.INVISIBLE);
                                mCallback.onFullScreen();
                            }
                        } else if (item.getTitle().equals("Settings")) {
                            mCallback.onSettingsClicked();
                        }
                        return true;
                    }
                });

                popMenu.show();
            }
        });


        player_controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReplayIconVisible) {
                    homeActivity.hasQueueEnded = true;
                    mCallback.onComplete();
                } else {
                    if (!pauseClicked) {
                        pauseClicked = true;
                    }
                    togglePlayPause();
                }
            }
        });

        mainTrackController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReplayIconVisible) {
                    homeActivity.hasQueueEnded = true;
                    mCallback.onComplete();
                } else {
                    if (!pauseClicked) {
                        pauseClicked = true;
                    }
                    togglePlayPause();
                }
            }
        });

        nextTrackController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.pause();
                homeActivity.nextControllerClicked = true;
                mCallback.onComplete();
            }
        });

        previousTrackController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeActivity.queueCurrentIndex != 0) {
                    mMediaPlayer.pause();
                    mCallback.onPreviousTrack();
                }
            }
        });

        progressBar = (SeekBar) view.findViewById(R.id.progressBar);
        progressBar.setMax(durationInMilliSec);

        timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        if (isPrepared && !isTracking && getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    float[] hsv = new float[3];
                                    hsv[0] = homeActivity.seekBarColor;
                                    hsv[1] = (float) 0.8;
                                    hsv[2] = (float) 0.5;
                                    progressBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.HSVToColor(hsv), PorterDuff.Mode.SRC_IN));
                                    cpb.update();
                                }
                            });
                            try {
                                temp = getTime(mMediaPlayer.getCurrentPosition());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        currTime.setText(temp.first + ":" + temp.second);
                                    }
                                });
                                progressBar.setProgress(mMediaPlayer.getCurrentPosition());
                            } catch (Exception e) {
                                Log.e("MEDIA", e.getMessage() + ":");
                            }
                        }
                    }
                }, 0, 50);

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                temp = getTime(progress);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currTime.setText(temp.first + ":" + temp.second);
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(seekBar.getProgress());
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.start();
                isTracking = false;
            }

        });

        final Button mEndButton = new Button(getContext());
        mEndButton.setBackgroundColor(homeActivity.themeColor);
        mEndButton.setTextColor(Color.WHITE);

        Handler handler = new Handler();
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        showCase = new ShowcaseView.Builder(getActivity())
                                .blockAllTouches()
                                .singleShot(2)
                                .setStyle(R.style.CustomShowcaseTheme)
                                .useDecorViewAsParent()
                                .replaceEndButton(mEndButton)
                                .setContentTitlePaint(homeActivity.tp)
                                .setTarget(new ViewTarget(mVisualizerView.getId(), getActivity()))
                                .setContentTitle("The DNA")
                                .setContentText("The DNA of the currently playing song.")
                                .build();
                        showCase.setButtonText("Next");
                        showCase.setButtonPosition(homeActivity.lps);
                        showCase.overrideButtonClick(new View.OnClickListener() {
                            int count1 = -1;

                            @Override
                            public void onClick(View v) {
                                count1++;
                                switch (count1) {
                                    case 0:
                                        showCase.setTarget(new ViewTarget(mVisualizerView.getId(), getActivity()));
                                        showCase.setContentTitle("The DNA");
                                        showCase.setContentText("Swipe Left or Right to change Song." +
                                                "Long Press for fullscreen");
                                        showCase.setButtonPosition(homeActivity.lps);
                                        showCase.setButtonText("Next");
                                        break;
                                    case 1:
                                        showCase.setTarget(new ViewTarget(R.id.toggleContainer, getActivity()));
                                        showCase.setContentTitle("The Controls");
                                        showCase.setContentText("Equalizer \n" +
                                                "Save DNA toggle\n" +
                                                "Add to Favourites \n" +
                                                "Queue");
                                        showCase.setButtonPosition(homeActivity.lps);
                                        showCase.setButtonText("Done");
                                        break;
                                    case 2:
                                        showCase.hide();
                                        break;
                                }
                            }

                        });
                    }
                }, 500);

    }

    public void addToFavourite() {

        UnifiedTrack ut;

        if (homeActivity.localSelected)
            ut = new UnifiedTrack(true, localTrack, null);
        else
            ut = new UnifiedTrack(false, null, track);

        homeActivity.favouriteTracks.getFavourite().add(ut);
        mCallback.onAddedtoFavfromPlayer();
    }

    public void removeFromFavourite() {

        UnifiedTrack ut;

        if (homeActivity.localSelected)
            ut = new UnifiedTrack(true, localTrack, null);
        else
            ut = new UnifiedTrack(false, null, track);

        for (int i = 0; i < homeActivity.favouriteTracks.getFavourite().size(); i++) {
            UnifiedTrack ut1 = homeActivity.favouriteTracks.getFavourite().get(i);
            if (ut.getType() && ut1.getType()) {
                if (ut.getLocalTrack().getTitle().equals(ut1.getLocalTrack().getTitle())) {
                    homeActivity.favouriteTracks.getFavourite().remove(i);
                    break;
                }
            } else if (!ut.getType() && !ut1.getType()) {
                if (ut.getStreamTrack().getTitle().equals(ut1.getStreamTrack().getTitle())) {
                    homeActivity.favouriteTracks.getFavourite().remove(i);
                    break;
                }
            }
        }
        mCallback.onAddedtoFavfromPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        super.onDestroy();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mVisualizer != null) {
            mVisualizer.release();
        }
        audioVisualization.release();
    }

    public void refresh() {

        isRefreshed = true;

        pauseClicked = false;
        completed = false;
        isTracking = false;

        if (snappyRecyclerView.getCurrentPosition() != HomeActivity.queueCurrentIndex) {
            snappyRecyclerView.scrollToPosition(HomeActivity.queueCurrentIndex);
            snappyRecyclerView.setCurrentPosition(HomeActivity.queueCurrentIndex);
            customAdapter.notifyDataSetChanged();
        }

        VisualizerView.w = HomeActivity.screen_width;
        VisualizerView.h = HomeActivity.screen_height;
        VisualizerView.conf = Bitmap.Config.ARGB_8888;
        VisualizerView.bmp = Bitmap.createBitmap(VisualizerView.w, VisualizerView.h, VisualizerView.conf);
        HomeActivity.cacheCanvas = new Canvas(VisualizerView.bmp);

        if (homeActivity.isPlayerVisible) {
            player_controller.setVisibility(View.VISIBLE);
            player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
        } else {
            player_controller.setVisibility(View.VISIBLE);
            player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
        }

        isFav = false;

        if (homeActivity.isFavourite) {
            favouriteIcon.setImageResource(R.drawable.ic_heart_filled_1);
            favControllerSp.setImageResource(R.drawable.ic_heart_filled_1);
            isFav = true;
        } else {
            favouriteIcon.setImageResource(R.drawable.ic_heart_alpha);
            favControllerSp.setImageResource(R.drawable.ic_heart_alpha);
            isFav = false;
        }

        if (homeActivity.shuffleEnabled) {
            shuffleController.setImageResource(R.drawable.ic_shuffle_filled);
        } else {
            shuffleController.setImageResource(R.drawable.ic_shuffle_alpha);
        }

        if (homeActivity.repeatEnabled) {
            repeatController.setImageResource(R.drawable.ic_repeat_filled);
        } else if (homeActivity.repeatOnceEnabled) {
            repeatController.setImageResource(R.drawable.ic_repeat_once);
        } else {
            repeatController.setImageResource(R.drawable.ic_repeat_alpha);
        }


        equalizerIcon.setVisibility(View.INVISIBLE);

        track = homeActivity.selectedTrack;
        localTrack = homeActivity.localSelectedTrack;

        if (homeActivity.streamSelected) {
            durationInMilliSec = track.getDuration();
            if (track.getArtworkURL() != null) {
                Picasso.with(getActivity()).load(track.getArtworkURL()).resize(100, 100).into(selected_track_image);
            } else {
                selected_track_image.setImageResource(R.drawable.ic_default);
            }
            try {
                spTitleAB.setText(track.getTitle());
                selected_track_title.setText(track.getTitle());
                selected_track_artist.setText("");
                spArtistAB.setText("");
            } catch (Exception e) {
            }
        } else {
            try {
                durationInMilliSec = (int) localTrack.getDuration();
            } catch (Exception e) {

            }
            try {
                imgLoader.DisplayImage(localTrack.getPath(), selected_track_image);
            } catch (Exception e) {

            }
            try {
                spTitleAB.setText(localTrack.getTitle());
                selected_track_title.setText(localTrack.getTitle());
                selected_track_artist.setText(localTrack.getArtist());
                spArtistAB.setText(localTrack.getArtist());
            } catch (Exception e) {

            }
        }

        temp = getTime(durationInMilliSec);
        totalTime.setText(temp.first + ":" + temp.second);

        homeActivity.mWifi = homeActivity.connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        try {
            if (homeActivity.streamSelected) {
                if ((homeActivity.settings.isStreamOnlyOnWifiEnabled() && homeActivity.mWifi.isConnected()) || !homeActivity.settings.isStreamOnlyOnWifiEnabled()) {
                    isPrepared = false;
                    mMediaPlayer.reset();
                    progressBar.setProgress(0);
                    progressBar.setSecondaryProgress(0);
                    mMediaPlayer.setDataSource(track.getStreamURL() + "?client_id=" + Config.CLIENT_ID);
                    mMediaPlayer.prepareAsync();
                } else {
                    mMediaPlayer.pause();
                    homeActivity.nextControllerClicked = true;
                    mCallback.onComplete();
                }
            } else {
                isPrepared = false;
                mMediaPlayer.reset();
                progressBar.setProgress(0);
                progressBar.setSecondaryProgress(0);
                File f = new File(localTrack.getPath());
                if (f.exists()) {
                    mMediaPlayer.setDataSource(localTrack.getPath());
                    mMediaPlayer.prepareAsync();
                } else {
                    Toast.makeText(getContext(), "Error playing " + localTrack.getTitle() + ". Skipping to next track", Toast.LENGTH_SHORT).show();
                    mMediaPlayer.pause();
                    homeActivity.nextControllerClicked = true;
                    mCallback.onComplete();
                }
            }
            bufferingIndicator.setVisibility(View.VISIBLE);
            mainTrackController.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        progressBar.setMax(durationInMilliSec);

        timer.cancel();
        timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        if (isPrepared && !isTracking && getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    float[] hsv = new float[3];
                                    hsv[0] = homeActivity.seekBarColor;
                                    hsv[1] = (float) 0.8;
                                    hsv[2] = (float) 0.5;
                                    progressBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.HSVToColor(hsv), PorterDuff.Mode.SRC_IN));
                                    cpb.update();
                                }
                            });
                            try {
                                temp = getTime(mMediaPlayer.getCurrentPosition());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        currTime.setText(temp.first + ":" + temp.second);
                                    }
                                });
                                progressBar.setProgress(mMediaPlayer.getCurrentPosition());
                            } catch (Exception e) {
                                Log.e("MEDIA", e.getMessage() + ":");
                            }
                        }
                    }
                }, 0, 50);

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                temp = getTime(progress);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currTime.setText(temp.first + ":" + temp.second);
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                startTrack = System.currentTimeMillis();
                isTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                endTrack = System.currentTimeMillis();
                mMediaPlayer.seekTo(seekBar.getProgress());
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.start();
                isTracking = false;
            }

        });
    }

    public Pair<String, String> getTime(int millsec) {
        int min, sec;
        sec = millsec / 1000;
        min = sec / 60;
        sec = sec % 60;
        String minS, secS;
        minS = String.valueOf(min);
        secS = String.valueOf(sec);
        if (sec < 10) {
            secS = "0" + secS;
        }
        Pair<String, String> pair = Pair.create(minS, secS);
        return pair;
    }

    public boolean isShowcaseVisible() {
        return (showCase != null && showCase.isShowing());
    }

    public void hideShowcase() {
        showCase.hide();
    }

    public void toggleAlbumArtBackground() {
        snappyRecyclerView.setTransparency();
    }

    public String getBase64encodedBitmap(Bitmap image) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

}
