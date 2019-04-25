package com.example.common.app.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 调用相册的正方形图片java文件
 */
public class SquareLayout extends FrameLayout {
    public SquareLayout(@NonNull Context context) {
        super(context);
    }

    public SquareLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
//    onMeasure()是测量方法
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        高宽给父类传递的测量值为宽度，那么就是基于宽度的正方形控件了
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
