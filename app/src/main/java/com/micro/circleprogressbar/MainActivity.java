package com.micro.circleprogressbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.micro.lib.CircleProgressBar;

public class MainActivity extends AppCompatActivity {

    private CircleProgressBar mProgressBar;
    private Button mStartBtn, mPauseBtn;
    private EditText mDurationText;

    // 当前的进度
    private int mCurrentProgress = 0;
    // 模拟渐进时间，默认25ms
    private long mSpeed = DEFAULT_SPEED;

    private static final long DEFAULT_SPEED = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (CircleProgressBar) findViewById(R.id.progressBar);
        mStartBtn = (Button) findViewById(R.id.start);
        mPauseBtn = (Button) findViewById(R.id.pause);
        mDurationText = (EditText) findViewById(R.id.et_duration);

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress();
            }
        });

        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseOrResume();
            }
        });
    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    Handler mHandler = new Handler() {

        // mock progress increase
        Runnable mockProgressRunnable = new Runnable() {
            private final int MAX_PROGRESS = 100;

            @Override
            public void run() {
                if (mCurrentProgress < MAX_PROGRESS) {
                    mCurrentProgress += 1;
                    mProgressBar.setProgress(mCurrentProgress);

                    postDelayed(this, mSpeed);
                }
            }
        };

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START:
                case MSG_RESUME:
                    this.post(mockProgressRunnable);
                    break;
                case MSG_STOP:
                    this.removeCallbacks(mockProgressRunnable);
                    break;
            }
        }
    };

    /**
     * 开始或者重新开始
     */
    private void startProgress() {
        // reset mCurrentProgress
        mCurrentProgress = 0;

        // fetch customer duration
        String durationText = mDurationText.getText().toString().trim();
        try {
            mSpeed = Long.valueOf(durationText);
        } catch (Exception e) {
            mSpeed = DEFAULT_SPEED;
        }

        mHandler.sendEmptyMessage(MSG_STOP);
        mHandler.sendEmptyMessage(MSG_START);
    }

    /**
     * 暂停或者恢复状态
     */
    private void pauseOrResume() {
        pauseProgress();
    }

    /**
     * 暂停状态
     */
    private void pauseProgress() {
        mHandler.sendEmptyMessage(MSG_STOP);
    }

    /**
     * 恢复状态
     */
    private void resumeProgress() {
        mHandler.sendEmptyMessage(MSG_RESUME);
    }

    private static final int MSG_START = 0x10;
    private static final int MSG_STOP = 0x20;
    private static final int MSG_RESUME = 0x30;
}
