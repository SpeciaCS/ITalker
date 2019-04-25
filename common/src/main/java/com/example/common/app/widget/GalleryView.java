package com.example.common.app.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.common.R;
import com.example.common.app.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 自定义新建layout文件  调用系统图片文件，选择不同文件做头像的类
 */
public class GalleryView extends RecyclerView {
    private static final int LOADER_ID = 0x0100;
    private static final int MAX_IMAGE_COUNT = 3; //最大的选中图片数量
    private static final int MIN_IMAGE_FILE_SIZE = 10*1024; //最小的图片大小
    private LoaderCallback loaderCallback = new LoaderCallback();
    //    选择v7默认的适配器
    private Adapter madapter = new Adapter();
    private List<Image> mSelectedImages = new LinkedList<>();
    private SelectedChangerListener mListener;

    public GalleryView(Context context) {
        super(context);
        init(null, 0);
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    //获取布局管理器
    private void init(AttributeSet attrs, int defStyle) {
        setLayoutManager(new GridLayoutManager(getContext(), 4));
        setAdapter(madapter);
        madapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Image>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image) {
                // Cell点击操作，如果说我们的点击是允许的，那么更新对应的Cell的状态
                // 然后更新界面，同理；如果说不能允许点击(已经达到最大的选中数量)那么就不刷新界面
                if (onItemSelectClick(image)) {
                    holder.updateData(image);
                }
            }


        });
    }

    /**
     * @param loaderManager loader管理器
     * @return 任何一个LOADER_ID，可用于销毁Loader
     */
    public int setup(LoaderManager loaderManager, SelectedChangerListener listener) {
        mListener = listener;
        loaderManager.initLoader(LOADER_ID, null, loaderCallback);
        return LOADER_ID;
    }

    /**
     * Cell点击的具体逻辑
     *
     * @param image
     * @return Ture ,代表我进行了数据更新，你需要刷新；反之不刷新
     */
    protected boolean onItemSelectClick(Image image) {
        boolean notifyRefresh;
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
            image.isSelect = false;
            notifyRefresh = true;
        } else {
            if (mSelectedImages.size() >= MAX_IMAGE_COUNT) {
                // Toast 一个提示
                notifyRefresh = true;
            } else {
                mSelectedImages.add(image);
                image.isSelect = true;
                notifyRefresh = true;
            }
        }
        // 如果数据有改变，那么我们需要通知外面的监听者我们的数据选中改变了
        if (notifyRefresh)
            notifySelectChanged();
        return true;
    }

    /**
     * 得到选中的图片的全部地址
     *
     * @return 返回一个数组
     */
    public String[] getSelectedPath() {
        String[] paths = new String[mSelectedImages.size()];
        int index = 0;
        for (Image image : mSelectedImages) {
            paths[index++] = image.path;
        }
        return paths;
    }

    public void clear() {
        for (Image image : mSelectedImages) {
//            一定要先重置状态
            image.isSelect = false;
        }
        mSelectedImages.clear();
        madapter.notifyDataSetChanged();
    }

    /**
     * 通知选中状态改变
     */
    private void notifySelectChanged() {
        // 得到监听者，并判断是否有监听者，然后进行回调数量变化
        SelectedChangerListener listener = mListener;
        if (listener != null) {
            listener.onSelectedCountChanged(mSelectedImages.size());
        }
    }

    /**
     * 通知adapter数据更改方法
     * @param images 新的数据
     */
    private void updateSource(List<Image> images){
        madapter.replace(images);
    }

    /**
     * 用于实际的数据加载Loader Callback
     */
    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        private final String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID,// Id
                MediaStore.Images.Media.DATA, //图片路径
                MediaStore.Images.Media.DATE_ADDED //图片的创建时间
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            //创建了一个Loader
            if (id == LOADER_ID) {
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2] + " DESC");//倒序查询

            }
            return null;
        }

        @Override
        // 当loader加载完成时
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            List<Image>images = new ArrayList<>();
            if (data!=null){
                int count = data.getCount();
                if (count>0){
                    //移动游标开始
                    data.moveToFirst();
                    //得到对应的列的Index坐标
                    int indexId = data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPach = data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate = data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                    do {
                        //循环读取，直到没有下一条数据
                        int id = data.getInt(indexId);
                        String path = data.getString(indexPach);
                        long dateTime = data.getLong(indexDate);

                        File file = new File(path);
                        if (!file.exists()||file.length()<MIN_IMAGE_FILE_SIZE){
                            continue;
                        }
                        //添加一条新的数据
                        Image image = new Image();
                        image.id = id;
                        image.path = path;
                        image.date = dateTime;
                        images.add(image);

                    }while (data.moveToFirst());
                }
            }
            updateSource(images);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            //当Loader销毁或者重置了,进行界面清空
            updateSource(null);
        }
    }

    /**
     * 内部的数据结构
     */
    private static class Image {
        int id;       //    数据的ID
        String path;  //    图片的路径
        long date;    //    图片的创建日期
        boolean isSelect;   // 是否选中

        //右键Generate——equals()and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Image image = (Image) o;
            return Objects.equals(path, image.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path);
        }
    }

    /**
     * 适配器
     */
    private class Adapter extends RecyclerAdapter<Image> {
        public void update(Image image, ViewHolder<Image> holder) {

        }

        @Override
        protected int getItemViewType(int position, Image image) {
            return R.layout.cell_galley;
        }

        @Override
        protected ViewHolder<Image> onCreateViewHolder(View root, int viewType) {
            return new GalleryView.ViewHolder(root);
        }

    }

    /**
     * Cell 对应的Holder 界面
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image> {
        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            mPic = (ImageView)itemView.findViewById(R.id.im_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelected = (CheckBox)itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Image image) {
            Glide.with(getContext())
                    .load(image.path)//加载路径
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存，直接从原图加载
                    .centerCrop()//居中剪切
                    .placeholder(R.color.green_200)//默认颜色
                    .into(mPic);

            mShade.setVisibility(image.isSelect?VISIBLE:INVISIBLE);
            mSelected.setChecked(image.isSelect);
        }
    }

    /**
     * 对外的一个监听器
     */
    public interface SelectedChangerListener {
        void onSelectedCountChanged(int count);
    }

}