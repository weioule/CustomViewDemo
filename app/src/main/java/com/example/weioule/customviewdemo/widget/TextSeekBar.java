package com.example.weioule.customviewdemo.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.example.weioule.customviewdemo.Util;

/**
 * Author by weioule.
 * Date on 2018/12/26.
 */
@SuppressLint("AppCompatCustomView")
public class TextSeekBar extends SeekBar {

    private TextPaint mTextPaint;

    public TextSeekBar(Context context) {
        this(context, null);
    }

    public TextSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(Util.dp2px(getContext(), 8));
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect bounds = new Rect();
        String progressText = generateTime(getProgress()) + "/" + generateTime(getMax());
        mTextPaint.getTextBounds(progressText, 0, progressText.length(), bounds);

        Drawable thumb = getThumb();
        int intrinsicWidth = thumb.getIntrinsicWidth();
        int intrinsicHeight = thumb.getIntrinsicHeight();
        int thumbX = thumb.getBounds().left + getPaddingLeft() + intrinsicWidth / 2;
        int thumbY = thumb.getBounds().top + getPaddingTop() + intrinsicHeight / 2 + bounds.height() / 2;
        canvas.drawText(progressText, thumbX, thumbY, mTextPaint);
    }

    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int hour = totalSeconds / 3600;
        int min = (totalSeconds % 3600) / 60;
        int sec = totalSeconds % 60;
        StringBuffer sb = new StringBuffer("");
        sb.append(hour == 0 ? "" : hour + ":").append(min < 10 ? "0" + min : min).append(":").append(sec < 10 ? "0" + sec : sec);
        return sb.toString();
    }

}
