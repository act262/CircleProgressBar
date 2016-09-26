package com.micro.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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
    // 半径
    private int radius;
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
    private int[] shadowColors3 = {
            Color.parseColor("#FF6699"),
            Color.parseColor("#FF8088"),
            Color.parseColor("#FF9977"),
            Color.parseColor("#FFB366"),
            Color.parseColor("#FFCC55"),
            Color.parseColor("#FFCC55"),
            Color.parseColor("#FFB366"),
            Color.parseColor("#FF9977"),
            Color.parseColor("#FF8088"),
            Color.parseColor("#FF6699")
    };
    private int[] shadowColors4 = {
            Color.parseColor("#FF0033"),
            Color.parseColor("#FF132E"),
            Color.parseColor("#FF6F17"),
            Color.parseColor("#FF8213"),
            Color.parseColor("#FF940E"),
            Color.parseColor("#FFA709"),
            Color.parseColor("#FFB905"),
            Color.parseColor("#FFA709"),
            Color.parseColor("#FF940E"),
            Color.parseColor("#FF8213"),
            Color.parseColor("#FF6F17"),
            Color.parseColor("#FF132E"),
            Color.parseColor("#FF0033")
    };
    private float[] shadowPositions1 = {0.2f, 0.5f, 0.8f, 1.0f};

    // 画圆圈的画笔
    private Paint mPaint;
    // 旋转的火箭
    private Bitmap bitmap;
    // 控制火箭旋转矩阵
    private Matrix matrix = new Matrix();

    // 最大进度界线值
    private float mMaxProgress = 100;
    // 当前进度值
    private float mCurrentProgress = 0;

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
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE); // 描边专用
        mPaint.setStrokeCap(Paint.Cap.ROUND); // 圆形笔头
        mPaint.setStrokeJoin(Paint.Join.MITER);

        // 限定了火箭图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rocket, options);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        // 半径取宽高小值
//        radius = Math.min(w, h) / 2;

        centerX = w / 2;
        centerY = h / 2;
        // 当前圆的半径
        radius = w / 2;

        radius1 = (width - stroke1) / 2 - offset1;
        radius2 = (width - stroke2) / 2 - offset2;
        radius3 = (width - stroke3) / 2 - offset3;
        radius4 = (width - stroke4) / 2 - offset4;

        float offsetRect3 = radius - radius2;
        rectF3.set(offsetRect3, offsetRect3, width - offsetRect3, height - offsetRect3);

        float offsetRect4 = radius - radius4;
        rectF4.set(offsetRect4, offsetRect4, width - offsetRect4, height - offsetRect4);

        // 环形渐变效果
        shader3 = new SweepGradient(centerX, centerY, shadowColors3, null);
        shader4 = new SweepGradient(centerX, centerY, shadowColors4, null);
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawGuideLine(canvas);
        drawCircle(canvas);
    }

    // 画参考线
    private void drawGuideLine(Canvas canvas) {
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        // 画一个圆心
        canvas.drawCircle(centerX, centerY, 2, mPaint);
        // 外边框
        canvas.drawRect(0, 0, width, height, mPaint);
    }

    private void drawCircle(Canvas canvas) {
        // 重置画笔样式
        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.STROKE);

        // 画灰色圆圈1
        mPaint.setColor(color1);
        mPaint.setStrokeWidth(stroke1);
        canvas.drawCircle(centerX, centerY, radius1, mPaint);

        // 画灰色圆圈2
        mPaint.setColor(color2);
        mPaint.setStrokeWidth(stroke2);
        canvas.drawCircle(centerX, centerY, radius2, mPaint);

        // 画圆圈3（圆弧1）
        float startAngle = -90; // 从0点方向开始
        float sweepAngle = (mCurrentProgress / mMaxProgress) * 360;
        mPaint.setStrokeWidth(stroke3);
        mPaint.setShader(shader3);
        canvas.drawArc(rectF3, startAngle, sweepAngle, false, mPaint);

        // 画圆圈4（圆弧2）
        mPaint.setStrokeWidth(stroke4);
        mPaint.setShader(shader4);
        canvas.drawArc(rectF4, startAngle, sweepAngle, false, mPaint);

        // 画火箭
//        float x = (float) (centerX * Math.sin(sweepAngle / Math.PI / 2)) + centerX;
//        float y = (float) (centerX * Math.cos(sweepAngle / Math.PI / 2)) + centerX;
//        canvas.drawBitmap(bitmap, x - stroke4, y - stroke4, mPaint);

        // 旋转画布实现
//        canvas.save();
//        canvas.rotate(centerX, centerY, sweepAngle);
//        canvas.drawBitmap(bitmap, 0, 0, mPaint);
//        canvas.restore();

        // 旋转matrix操作实现
        matrix.reset();
        matrix.preTranslate(radius - stroke4 / 2 - bitmap.getWidth() / 2, -stroke4 / 2);
        matrix.postRotate(sweepAngle, centerX, centerY);
        canvas.drawBitmap(bitmap, matrix, mPaint);
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
    public void setProgress(int progress) {
        if (0 <= progress && progress <= mMaxProgress) {
            this.mCurrentProgress = progress;
            postInvalidate();
        }
    }
}
