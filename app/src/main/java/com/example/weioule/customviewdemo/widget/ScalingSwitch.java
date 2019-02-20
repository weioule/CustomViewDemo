package com.example.weioule.customviewdemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.weioule.customviewdemo.R;
import com.example.weioule.customviewdemo.Util;

import org.jetbrains.annotations.Nullable;

/**
 * Author by weioule.
 * Date on 2019/1/15.
 */
public class ScalingSwitch extends View {

    private final String[] switchTxts = {"弹", "关"};
    private String mBtnText = "", mHintText = "";
    private boolean mSwitchOn = true, clickable = true, defultOpen;
    private int mTextColor;
    private int mCircleBgColor;
    private int mHintTextBgColor;
    private int mWidth, hintBgLift;
    private int mRadius;
    private int mTextSize;
    private Runnable mRunnable;
    private Handler mHandler;

    private int defaultRadius = Util.dp2px(getContext(), 13);
    private int defaultTextSize = Util.dp2px(getContext(), 10);
    private int defaultTextMargin = Util.dp2px(getContext(), 10);
    private int defaultCircleBgColor = 0x57000000;
    private int defaultHintTextBgColor = 0x80000000;
    @SuppressWarnings("deprecation")
    private int defaultTextColor = getResources().getColor(android.R.color.white);
    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect hintTextBound, mCircleBound;
    private SwitchClicklistener listener;
    private Paint textPaint, bgPaint;
    private int mTextMargin;

    public ScalingSwitch(@Nullable Context context) {
        super(context, null);
    }

    public ScalingSwitch(@Nullable Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ScalingSwitch);
        mSwitchOn = a.getBoolean(R.styleable.ScalingSwitch_switchSwitchOn, true);
        mTextColor = a.getColor(R.styleable.ScalingSwitch_switchTextColor, defaultTextColor);
        mRadius = a.getDimensionPixelSize(R.styleable.ScalingSwitch_switchRadius, defaultRadius);
        mTextSize = a.getDimensionPixelSize(R.styleable.ScalingSwitch_switchTextSize, defaultTextSize);
        mCircleBgColor = a.getColor(R.styleable.ScalingSwitch_switchCircleColor, defaultCircleBgColor);
        mHintTextBgColor = a.getColor(R.styleable.ScalingSwitch_switchTextBgColor, defaultHintTextBgColor);
        mTextMargin = a.getDimensionPixelSize(R.styleable.ScalingSwitch_switchTextMargin, defaultTextMargin);
        a.recycle();
    }

    public void setHintText(boolean defultOpen, String hintText) {
        this.defultOpen = defultOpen;
        mBtnText = switchTxts[1];
        mHintText = hintText;
        initPaint();
        requestLayout();
        invalidate();
    }

    private void initPaint() {
        textPaint = new Paint();
        textPaint.setTextSize(mTextSize);
        textPaint.setAntiAlias(true);

        hintTextBound = new Rect();
        textPaint.getTextBounds(mHintText, 0, mHintText.length(), hintTextBound);

        mCircleBound = new Rect();
        textPaint.getTextBounds(mBtnText, 0, mBtnText.length(), mCircleBound);

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(getMeasuredWidth()), measureHeight(getMeasuredHeight()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == bgPaint) return;

        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (mRadius * 2 - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;

        //默认为关闭的话，就只绘制圆的部分，因为需要背景颜色一致，所以要与圆重叠
        if (!defultOpen) {
            mSwitchOn = false;
            hintBgLift = mWidth - getPaddingRight() - mRadius * 2;
            defultOpen = true;
        }

        bgPaint.setColor(mHintTextBgColor);
        RectF rec = new RectF(getPaddingLeft() + hintBgLift, getPaddingTop(), mWidth, mRadius * 2);
        canvas.drawRoundRect(rec, mRadius, mRadius, bgPaint);

        //状态为开时，才绘制文字
        if (defultOpen) {
            textPaint.setColor(mTextColor);
            String hintTxt = "";
            if (mSwitchOn && !clickable) {
                hintTxt = "";
            } else if (mSwitchOn && clickable) {
                hintTxt = mHintText;
            }
            canvas.drawText(hintTxt, getPaddingLeft() + mTextMargin, baseline, textPaint);
        }

        bgPaint.setColor(mCircleBgColor);
        canvas.drawCircle(this.mWidth - getPaddingRight() - mRadius, getPaddingTop() + mRadius, mRadius, bgPaint);

        textPaint.setColor(mTextColor);
        if (clickable) {
            if (mSwitchOn) {
                mBtnText = switchTxts[1];
            } else {
                mBtnText = switchTxts[0];
            }
        }
        canvas.drawText(mBtnText, this.mWidth - getPaddingRight() - (mRadius * 2 - ((mRadius * 2 - mCircleBound.width()) / 2)), baseline, textPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (clickable && x >= mWidth - getPaddingRight() - mRadius * 2 && x <= mWidth - getPaddingRight() && y >= getPaddingTop() && y <= getPaddingTop() + mRadius * 2) {
            startAnimations();
        }
        return super.onTouchEvent(event);
    }

    private void startAnimations() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                int hintBgRight = mWidth - getPaddingRight() - mRadius * 2;
                if (mSwitchOn) {
                    if (hintBgLift <= hintBgRight - hintBgRight / 10) {
                        clickable = false;
                        hintBgLift += hintBgRight / 10;
                        invalidate();
                    } else {
                        hintBgLift = hintBgRight;
                        clickable = true;
                        mSwitchOn = false;
                        invalidate();
                        if (null != listener) {
                            listener.switchClick(false);
                        }
                        return;
                    }
                } else {
                    if (hintBgLift >= getPaddingLeft() + hintBgRight / 10) {
                        clickable = false;
                        hintBgLift -= hintBgRight / 10;
                        invalidate();
                    } else {
                        hintBgLift = getPaddingLeft();
                        clickable = true;
                        mSwitchOn = true;
                        invalidate();
                        if (null != listener) {
                            listener.switchClick(true);
                        }
                        return;
                    }
                }
                mHandler.postDelayed(this, 20);
            }
        };
        mRunnable.run();
    }

    private int measureHeight(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int hight;
        if (mode == MeasureSpec.EXACTLY) {
            hight = size;
        } else {
            hight = getPaddingTop() + mRadius * 2 + getPaddingBottom();
            if (mode == MeasureSpec.AT_MOST) {
                hight = Math.min(hight, size);
            }
        }
        return hight;
    }

    private int measureWidth(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            this.mWidth = size;
        } else {
            if (hintTextBound == null) return 0;
            this.mWidth = getPaddingLeft() + mTextMargin + hintTextBound.width() + mTextMargin + mRadius * 2 + getPaddingRight();
            if (mode == MeasureSpec.AT_MOST) {
                this.mWidth = Math.min(this.mWidth, size);
            }
        }
        return this.mWidth;
    }

    public void setSwitchClicklistener(SwitchClicklistener listener) {
        this.listener = listener;
    }

    public interface SwitchClicklistener {
        void switchClick(boolean isOpen);
    }
}
