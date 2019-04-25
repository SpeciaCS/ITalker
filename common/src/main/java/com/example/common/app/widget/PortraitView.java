package com.example.common.app.widget;

import android.content.Context;
import android.util.AttributeSet;

import de.hdodenhof.circleimageview.CircleImageView;

/*
继承 CircleImageView里的三个CircleImageView ，这个CircleImageView在app包中的activity_main.xml文件中展示
 */
public class PortraitView extends CircleImageView {
    public PortraitView(Context context) {
        super(context);
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}