package com.example.italker.frags.main;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.common.app.Fragment;
import com.example.common.app.widget.GalleryView;
import com.example.italker.R;

import butterknife.BindView;

/*
继承非系统的v4包，而是引用自定义的common中的命名空间import com.example.common.app.Fragment;
 */
public class ActiveFragment extends Fragment {
    @BindView(R.id.galleryView)
    GalleryView mGalley;

    public ActiveFragment() {
        // Required empty public constructor
    }

    /**
     * 调用common包中封装的Fragment中的getCgetContentLayouId
     * 调用当前包中的fragment中的xml文件
     *
     * @return
     */
    @Override
    protected int getContentLayouId() {
        return R.layout.fragment_active;
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initData() {
        super.initData();
        mGalley.setup(getLoaderManager(), new GalleryView.SelectedChangerListener() {
            @Override
            public void onSelectedCountChanged(int count) {

            }
        });
    }
}
