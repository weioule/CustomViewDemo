package com.example.weioule.customviewdemo;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Author by weioule.
 * Date on 2019/2/20.
 */
public class Util {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        final float scale = dm.density;
        return (int) (dpValue * scale + 0.5f);
    }

}
