package com.example.weioule.customviewdemo;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.weioule.customviewdemo.widget.ScalingSwitch;
import com.example.weioule.customviewdemo.widget.SpectrumView;
import com.example.weioule.customviewdemo.widget.TextSeekBar;

import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener {

    private String url = "http://re01.sycdn.kuwo.cn/resource/n1/55/80/1359120575.mp3";
    private final int DELAY_MILLIS = 1000;
    private static Handler handler = new Handler();
    private SpectrumView mSpectrumView;
    private TextSeekBar mTextSeekbar;
    private ScalingSwitch mScalingSwitch;
    private MediaPlayer mMediaPlayer;
    private Visualizer visualizer;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer.getCurrentPosition() > mMediaPlayer.getDuration()) {
                handler.removeCallbacks(mRunnable);
                return;
            }
            mTextSeekbar.setProgress(mMediaPlayer.getCurrentPosition());
            handler.postDelayed(mRunnable, 1000);
        }
    };
    private ImageView mPlayBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        mSpectrumView = findViewById(R.id.spectrum_View);
        mPlayBtn = findViewById(R.id.play_btn);
        mTextSeekbar = findViewById(R.id.text_seekbar);
        mScalingSwitch = findViewById(R.id.scaling_switch);
        mTextSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                 if (null != mMediaPlayer)
                    mMediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        mScalingSwitch.setSwitchClicklistener(new ScalingSwitch.SwitchClicklistener() {
            @Override
            public void switchClick(boolean isOpen) {
                //you can do some thing
            }
        });

        mPlayBtn.setOnClickListener(this);
    }

    private void initData() {
        mScalingSwitch.setHintText(true, "2000人气");
    }

    @Override
    public void onClick(View v) {
        v.setSelected(!v.isSelected());
        if (v.isSelected()) {
            if (null == mMediaPlayer) {
                initMediaPlayer();
            } else {
                mMediaPlayer.start();
                mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition());
                handler.postDelayed(mRunnable, DELAY_MILLIS);
            }
        } else {
            if (null != mMediaPlayer) {
                mMediaPlayer.pause();
                handler.removeCallbacks(mRunnable);
            }
        }
    }

    private void initMediaPlayer() {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                    mTextSeekbar.setMax(mMediaPlayer.getDuration());
                    handler.postDelayed(mRunnable, DELAY_MILLIS);
                    initVisualizer();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayBtn.setSelected(false);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initVisualizer() {
        try {
            if (visualizer != null) {
                visualizer.release();
            }
            visualizer = new Visualizer(mMediaPlayer.getAudioSessionId());

            int captureSize = Visualizer.getCaptureSizeRange()[1];
            int captureRate = Visualizer.getMaxCaptureRate() * 2 / 4;

            visualizer.setCaptureSize(captureSize);
            visualizer.setDataCaptureListener(dataCaptureListener, captureRate, true, true);
            visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
            visualizer.setEnabled(true);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private Visualizer.OnDataCaptureListener dataCaptureListener = new Visualizer.OnDataCaptureListener() {
        @Override
        public void onWaveFormDataCapture(Visualizer visualizer, final byte[] waveform, int samplingRate) {
            //声波
        }

        @Override
        public void onFftDataCapture(Visualizer visualizer, final byte[] fft, int samplingRate) {
            //声频
            mSpectrumView.post(new Runnable() {
                @Override
                public void run() {
                    mSpectrumView.setWaveData(fft);
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            // 释放所有对象
            visualizer.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (null != handler) {
            handler.removeCallbacks(mRunnable);
            handler = null;
        }
    }
}
