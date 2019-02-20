package com.example.weioule.customviewdemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.weioule.customviewdemo.R;
import com.example.weioule.customviewdemo.Util;

import org.jetbrains.annotations.Nullable;

/**
 * 音频频谱控件
 * Author by weioule.
 * Date on 2018/12/14.
 */
public class SpectrumView extends View {

    Path wavePath = new Path();
    private byte[] waveData;
    private Paint lumpPaint;
    private int mCount = 15;
    private int mGroupWidth;
    private int mGroupHeight;
    private int mActualWidth;
    private int mMinHeight;
    private int mRadius;
    private int mSpace;
    private int mColor;
    private int mWidth;


    public SpectrumView(Context context) {
        this(context, null);
    }

    public SpectrumView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpectrumView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @SuppressWarnings("deprecation")
    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AudioSpectruView);
        mColor = a.getColor(R.styleable.AudioSpectruView_RectangularBarColor, getResources().getColor(android.R.color.white));
        mSpace = a.getDimensionPixelSize(R.styleable.AudioSpectruView_RectangularBarSpace, Util.dp2px(getContext(), 3));
        mWidth = a.getDimensionPixelSize(R.styleable.AudioSpectruView_RectangularBarWidth, Util.dp2px(getContext(), 2));
        mRadius = a.getDimensionPixelSize(R.styleable.AudioSpectruView_RectangularBarRadius, Util.dp2px(getContext(), 8));
        mMinHeight = a.getDimensionPixelSize(R.styleable.AudioSpectruView_RectangularBarMinHeight, Util.dp2px(getContext(), 8));
        a.recycle();
        initPaint();
    }

    @SuppressLint("ResourceAsColor")
    private void initPaint() {
        lumpPaint = new Paint();
        lumpPaint.setAntiAlias(true);
        lumpPaint.setColor(mColor);

        lumpPaint.setStrokeWidth(2);
        lumpPaint.setStyle(Paint.Style.FILL);
    }

    public void setWaveData(byte[] data) {
        this.waveData = readyData(data);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
        mCount = (mGroupWidth - getPaddingLeft() - getPaddingRight() + mSpace) / (mSpace + mWidth);
        mActualWidth = getPaddingLeft() + mSpace * (mCount - 1) + mWidth * mCount + getPaddingRight();
    }

    private int measureHeight(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            mGroupHeight = size;
        } else {
            //默认高度100dp
            mGroupHeight = getPaddingTop() + Util.dp2px(getContext(), 100) + getPaddingBottom();
            if (mode == MeasureSpec.AT_MOST) {
                mGroupHeight = Math.min(mGroupHeight, size);
            }
        }
        return mGroupHeight;
    }

    private int measureWidth(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            this.mGroupWidth = size;
        } else {
            this.mGroupWidth = getPaddingLeft() + mSpace * (mCount - 1) + mWidth * mCount + getPaddingRight();
            if (mode == MeasureSpec.AT_MOST) {
                this.mGroupWidth = Math.min(this.mGroupWidth, size);
            }
        }
        return this.mGroupWidth;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        wavePath.reset();

        for (int i = 0; i < mCount; i++) {
            if (waveData == null) {
                drawRectF(canvas, i, mGroupHeight - getPaddingBottom() - mMinHeight);
                continue;
            }

            drawLump(canvas, i, false);
        }
    }

    /**
     * 预处理数据
     *
     * @return
     */
    private byte[] readyData(byte[] fft) {
        byte[] newData = new byte[mCount];
        byte abs;
        for (int i = 0; i < mCount; i++) {
            abs = (byte) Math.abs(fft[i]);
            //描述：Math.abs -128时越界
            newData[i] = abs < 0 ? 127 : abs;
        }
        return newData;
    }

    /**
     * 绘制矩形条
     */
    private void drawLump(Canvas canvas, int i, boolean reversal) {
        if (i >= waveData.length) return;
        int minus = reversal ? -1 : 1;
        float top = mGroupHeight - (mMinHeight + getPaddingBottom() + waveData[i] * mGroupHeight / 40) * minus;
        drawRectF(canvas, i, top);
    }

    private void drawRectF(Canvas canvas, int i, float top) {
        int lumpSize = mWidth + mSpace;
        int left = lumpSize * i + (mGroupWidth - mActualWidth) / 2 + getPaddingLeft();
        int right = left + mWidth;

        //超出边界则不再绘制
        if (right > getX() + getWidth()) return;

        //设置顶部PaddingTop
        top = Math.max(top, getPaddingTop());

        // 设置个新的长方形
        RectF oval3 = new RectF(left, top, right, mGroupHeight - getPaddingBottom());
        //第二个参数是x半径，第三个参数是y半径
        canvas.drawRoundRect(oval3, mRadius, mRadius, lumpPaint);
    }
}

