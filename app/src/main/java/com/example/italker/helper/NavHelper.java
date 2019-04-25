package com.example.italker.helper;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.support.v4.app.Fragment;

/**
 * 解决对Fragment的调度与重用问题
 * 达到最优的Fragment切换
 */
public class NavHelper<T> {
    //所有的Tab集合
    private final SparseArray<Tab<T>> tabs = new SparseArray<>();
    private final FragmentManager fragmentManager;
    private final int containerId;
    private final Context context;
    private final OnTabChangedListener<T> listener;
    //    当前的一个选中的Tab
    private Tab currentTab;

    /**
     * 初始化四个的构造方法
     * @param context
     * @param fragmentManager
     * @param containerId
     * @param listener
     */

// getSupportFragmentManager，布局id，this，this
    public NavHelper(FragmentManager fragmentManager, int containerId, Context context, OnTabChangedListener<T> listener) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        this.context = context;
        this.listener = listener;
    }
    /**
     * 添加Tab
     * @param menuId Tab对应的菜单Id
     * @param tab
     */
    public NavHelper<T> add(int menuId, Tab tab){
        tabs.put(menuId,tab);
        return this;
    }

    /**
     * 获取当前的显示的Tab
     * @return
     */
    public Tab getCurrentTab(){

        return currentTab;
    }
    /**
     * z执行点击菜单
     * @param menuId
     * @return 是否能够处理这个点击
     */
    public boolean performClickMenu(int menuId){
        Tab tab = tabs.get(menuId);
        if (tab!=null){
            doSelect(tab);
            return true;
        }
        return false;
    }

    /**
     * 进行真实的Tab基础操作
     * @param tab
     */
    private void doSelect(Tab<T> tab){
        Tab<T> oldTab = null;
        if (currentTab!= null){
            oldTab = currentTab;
            if (oldTab == tab){
//                如果当前的Tab就是点的Tab
//                那么我们不做处理
                notifyTabReselect(tab);
                return;
            }
        }
        currentTab = tab;
        doTabChanged(currentTab,oldTab);
    }

    /**
     * 进行Fragment的真实调度操作
     * @param newTab 新的
     * @param oldTab 旧的
     */
    private void doTabChanged(Tab<T> newTab,Tab<T> oldTab){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (oldTab!=null){
            if (oldTab.fragment!=null){
                transaction.detach(oldTab.fragment);
            }
        }
        if (newTab!=null){
            if (newTab.fragment == null){
//                首次新建
                Fragment fragment =  Fragment.instantiate( context,newTab.clx.getName(),null);
//                缓存起来
                newTab.fragment = fragment;
//                提交到FragmentManger
                transaction.add(currentTab.fragment,newTab.clx.getName());
            }else {
//                从FragmentManger的缓存空间中重新加载到界面中
                transaction.attach(newTab.fragment);
            }
//            提交事务
            transaction.commit();
//            通知回调
            notifyTabSelect(newTab,oldTab);
        }
    }

    /**
     * 回调我们的监听器
     * @param newTab 新的Tab
     * @param oldTab 旧的Tab
     */
    private void notifyTabSelect(Tab<T> newTab,Tab<T> oldTab){
    if (listener!=null){
        listener.onTabChanged(newTab,oldTab);
      }
    }
    private void notifyTabReselect(Tab<T> tab){

    }
    /**
     * 我们的所有的Tab基础属性
     */
    public static class Tab<T> {
        public Tab(Class<?>clx,T extra){
            this.clx = clx;
            this.extra = extra;
        }
//        Fragment对于的Class信息
        public Class<?> clx;
//        额外的字段，用户自己设定需要使用
        public T extra;
//        内部缓存的对应的Fragment
        Fragment fragment;
    }
    public interface OnTabChangedListener<T>{
        void onTabChanged(Tab<T> newTab,Tab<T> oldTab);
    }
}
