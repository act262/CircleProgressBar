package com.micro.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * 圆形展示的进度条控件
 */
public class CircleProgressBar extends View {

    // 控件宽高
    private int width, height;
    // 圆心X,圆心Y
    private int centerX, centerY;

    // 从内到外的圆圈
    private int stroke1 = 10;
    private int stroke2 = 25;
    private int stroke3 = 40;
    private int stroke4 = 15;

    // 每个圈圈对应边界偏移量
    private int offset1 = 150;
    private int offset2 = 50;
    private int offset3 = 50;
    private int offset4 = 0;

    // 每个圈的半径
    private float radius1;
    private float radius2;
    private float radius3;
    private float radius4;

    private int color1 = Color.GRAY;
    private int color2 = Color.GRAY;

    private Shader shader3;
    private Shader shader4;
    private RectF rectF3 = new RectF();
    private RectF rectF4 = new RectF();
    // 颜色渐变
    private int[] shadowColors3 = {Color.parseColor("#FFE709"), Color.parseColor("#FFA754"), Color.parseColor("#FFE921"), Color.parseColor("#FFAE0E")};
    private int[] shadowColors4 = {Color.parseColor("#FF0000"), Color.parseColor("#F0F0F0"), Color.parseColor("#F0000F"), Color.parseColor("#FFAE0E")};
    private float[] shadowPositions1 = {0.2f, 0.5f, 0.8f, 1.0f};

    // 画圆圈的画笔
    private Paint mCirclePaint = new Paint();

    // 最大进度界线值
    private float mMaxProgress = 100;

    // 当前进度值
    private float mCurrentProgress = 0;

    // 火箭的图片
    private Bitmap bitmap;

    public CircleProgressBar(Context context) {
        this(context, null, 0, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mCirclePaint.setDither(true);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rocket);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        centerX = w / 2;
        centerY = h / 2;

        // 当前圆的半径
        float radius = w / 2;

        radius1 = (width - stroke1) / 2 - offset1;
        radius2 = (width - stroke2) / 2 - offset2;
        radius3 = (width - stroke3) / 2 - offset3;
        radius4 = (width - stroke4) / 2 - offset4;

        float offsetRect3 = radius - radius2;
        rectF3.set(offsetRect3, offsetRect3, width - offsetRect3, height - offsetRect3);

        float offsetRect4 = radius - radius4;
        rectF4.set(offsetRect4, offsetRect4, width - offsetRect4, height - offsetRect4);

        // 环形渐变效果
        shader3 = new RadialGradient(centerX, centerY, radius3, Color.RED, Color.BLUE, Shader.TileMode.CLAMP);
        shader4 = new RadialGradient(centerX, centerY, radius4, shadowColors4, shadowPositions1, Shader.TileMode.CLAMP);
        shader3 = new SweepGradient(centerX, centerY, shadowColors3, shadowPositions1);
//        shader4 = new LinearGradient(0, 0, w, h, shadowColors4, shadowPositions1, Shader.TileMode.CLAMP);
//        shader = new BitmapShader(BitmapFactory.decodeResource(getResources(), R.drawable.rocket), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas) {
        // 重置画笔样式
        mCirclePaint.setShader(null);
//        mCirclePaint.reset();

        // 画灰色圆圈1
        mCirclePaint.setColor(color1);
        mCirclePaint.setStrokeWidth(stroke1);
        canvas.drawCircle(centerX, centerY, radius1, mCirclePaint);

        // 画灰色圆圈2
        mCirclePaint.setColor(color2);
        mCirclePaint.setStrokeWidth(stroke2);
        canvas.drawCircle(centerX, centerY, radius2, mCirclePaint);

        // 画圆圈3（圆弧1）
        float startAngle = -90; // 从0点方向开始
        float sweepAngle = (mCurrentProgress / mMaxProgress) * 360;
        mCirclePaint.setStrokeWidth(stroke3);
        mCirclePaint.setShader(shader3);
        canvas.drawArc(rectF3, startAngle, sweepAngle, false, mCirclePaint);

        // 画圆圈4（圆弧2）
        mCirclePaint.setStrokeWidth(stroke4);
        mCirclePaint.setShader(shader4);
        canvas.drawArc(rectF4, startAngle, sweepAngle, false, mCirclePaint);

        // 画火箭
//        mCirclePaint.setShader(shader3);
    }


    /**
     * 设置可用最大值
     *
     * @param maxProgress 必须大于0
     */
    public void setMaxProgress(float maxProgress) {
        if (maxProgress <= 0) {
            throw new IllegalStateException("Max progress must >0");
        }
        this.mMaxProgress = maxProgress;
        postInvalidate();
    }

    /**
     * 设置当前进度值
     */
    public void setProgress(float progress) {
        if (0 <= progress && progress <= mMaxProgress) {
            this.mCurrentProgress = progress;
            postInvalidate();
        }
    }
}
