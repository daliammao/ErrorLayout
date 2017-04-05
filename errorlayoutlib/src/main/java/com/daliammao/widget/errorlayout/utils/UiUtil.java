package com.daliammao.widget.errorlayout.utils;

import android.content.Context;

/**
 * @author: zhoupengwei
 * @time: 16/4/6-下午4:02
 * @Email: 496946423@qq.com
 * @desc:
 */
public class UiUtil {
    /**
     * dip转换px
     */
    public static int dip2px(Context context, int dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }
}
