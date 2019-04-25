package com.example.common.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

public abstract class Activity extends AppCompatActivity {

    /*
        重写protected中的onCreate方法
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWidows();
        if (initArgs(getIntent().getExtras())) {
          int LayoutId = getContentLayoutId();
          setContentView(LayoutId);
            initWidget();
            initData();
        } else {
            finish();
        }
    }

    protected boolean initArgs(Bundle bundle) {
        return true;
    }

    protected void initWidows() {

    }

    /**
     * 得到当前页面的资源文件Id
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    @Override
    public boolean onSupportNavigateUp() {
//        当点击界面返回时，finish当前界面
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // 得到当前Activity下的所有Fragment
//        @SuppressLint("RestrictedApi")
//        List<android.support.v4.app.Fragment> fragments = getSupportFragmentManager().getFragments();
//        // 判断是否为空
//        if (fragments != null && fragments.size() > 0) {
//            for (Fragment fragment : fragments) {
//                // 判断是否为我们能够处理的Fragment类型
//                if (fragment instanceof net.qiujuer.italker.common.app.Fragment) {
//                    // 判断是否拦截了返回按钮
//                    if (((net.qiujuer.italker.common.app.Fragment) fragment).onBackPressed()) {
//                        // 如果有直接Return
//                        return;
//                    }
//                }
//            }
//        }

        super.onBackPressed();
        finish();
    }
}