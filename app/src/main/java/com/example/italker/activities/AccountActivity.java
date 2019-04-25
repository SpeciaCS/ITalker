package com.example.italker.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.common.app.Activity;
import com.example.common.app.Fragment;
import com.example.italker.R;
import com.example.italker.frags.main.account.UpdateInfoFragment;
import com.yalantis.ucrop.UCrop;

/**
 * 剪切图的实现，继承common下的Activity，调用getContentLayoutId()方法
 *
 */
public class AccountActivity extends Activity {
    private Fragment mCurFragment;
    /**
     * 账户activity显示的入口
     * @param context
     */
    public static void show(Context context){
        context.startActivity(new Intent(context,AccountActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
    }

    /**
     * 调用绑定UpdateInfoFragment中的布局显示在这个java里
     */
    @Override
    protected void initWidget() {
        super.initWidget();
        mCurFragment = new UpdateInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container,new UpdateInfoFragment())
                .commit();
    }
    /**
     * Activity中收到剪切的图片，调用剪切库依赖包中的
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCurFragment.onActivityResult(requestCode,resultCode,data);
    }
}
