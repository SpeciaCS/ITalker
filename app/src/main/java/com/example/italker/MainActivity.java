package com.example.italker;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.common.app.Activity;
import com.example.common.app.widget.PortraitView;
import com.example.italker.activities.AccountActivity;
import com.example.italker.frags.main.ActiveFragment;
import com.example.italker.frags.main.ContactFragment;
import com.example.italker.frags.main.GroupFragment;
import com.example.italker.helper.NavHelper;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

//继承Common包下封装的的Activity  implements BottomNavigationView.OnNavigationItemSelectedListener
public class MainActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener,
            NavHelper.OnTabChangedListener<Integer>{

    @BindView(R.id.appbar)
    View mLayAppbar;

    @BindView(R.id.im_portrait)
    PortraitView mPortraitView;

    @BindView(R.id.txt_title)
    TextView mTitle;

    @BindView(R.id.lay_container)
    FrameLayout mContainer;

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @BindView(R.id.btn_action)
    FloatActionButton mAction;


    private NavHelper<Integer> mNavHelper;

    /**
     * 封装好的，直接跳转到布局页面
     * @return
     */
    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;

    }

    protected void initWidget() {
        super.initWidget();
//        初始化底部辅助工具类   调用NavHelper封装类中NavHelper构造方法  按照顺序排列
        mNavHelper = new NavHelper<>(getSupportFragmentManager(), R.id.lay_container,this,this);
        mNavHelper.add(R.id.action_home,new NavHelper.Tab<>(ActiveFragment.class,R.string.title_home))
                .add(R.id.action_group,new NavHelper.Tab<>(GroupFragment.class,R.string.title_group))
                .add(R.id.action_contact,new NavHelper.Tab<>(ContactFragment.class,R.string.title_contact));


//        添加导航对底部的监听
        mNavigation.setOnNavigationItemSelectedListener(this);

        Glide.with(this)
                .load(R.drawable.bg_src_morning)
                .centerCrop()
                .into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });
    }

    protected void initData() {
        super.initData();
//        从底部导入接管我们的Menu，然后进行手动的触发第一次点击
        Menu menu = mNavigation.getMenu();
//        触发首次选中的Home
        menu.performIdentifierAction(R.id.action_home,0);
    }

    /**
     * 点击事件title标题更换
     */
    @OnClick(R.id.im_search)
    void onSearchMenuClick() {

    }
//AccountActivity类账户显示布局传递到MainActivity类中
    @OnClick(R.id.btn_action)
    void onActionClick() {
        AccountActivity.show(this);
    }

    /**
     * 选中状态切换的时候，调用的回调方法menuItem
     * 当我们的底部导航被点击的时候触发
     * @param menuItem
     * @return True 代表我们能够处理这个点击
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //调用工具类中的perfromClickMenu方法
        return mNavHelper.performClickMenu(menuItem.getItemId());
    }

    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        mTitle.setText(newTab.extra);
//动画实现  浮动按钮进行隐藏与实现
        float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.title_home)) {
            // 主界面时隐藏
            transY = Ui.dipToPx(getResources(), 76);
        } else {
//            transY  默认为0 则显示
            if (Objects.equals(newTab.extra, R.string.title_group)) {
//                群
                mAction.setImageResource(R.drawable.ic_group_add);
//                旋转
                rotation = -360;
            } else {
//               联系人
                mAction.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }
        }
//                开始动画
//                旋转 Y轴位移 弹性差值器  时间
        mAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();

    }

}



