package com.micro.circleprogressbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.micro.lib.CircleProgressBar;

public class MainActivity extends AppCompatActivity {

    private CircleProgressBar mProgressBar;
    private EditText mDurationText;

    // 当前的进度
    private int mCurrentProgress = 0;
    // 渐进时间，默认100ms
    private long mSpeed = DEFAULT_SPEED;

    private static final long DEFAULT_SPEED = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (CircleProgressBar) findViewById(R.id.progressBar);
        mDurationText = (EditText) findViewById(R.id.et_duration);
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
                    this.post(mockProgressRunnable);
                    break;
                case MSG_STOP:
                    this.removeCallbacks(mockProgressRunnable);
                    break;
            }
        }
    };

    public void startProgress(View view) {
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

    public void stopProgress(View view) {
        mHandler.sendEmptyMessage(MSG_STOP);
    }

    private final int MSG_START = 0x10;
    private final int MSG_STOP = 0x20;
}
