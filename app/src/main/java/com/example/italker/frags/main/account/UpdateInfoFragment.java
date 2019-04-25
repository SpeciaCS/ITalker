package com.example.italker.frags.main.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.example.common.app.Application;
import com.example.common.app.Fragment;
import com.example.common.app.widget.PortraitView;
import com.example.italker.R;
import com.example.italker.frags.main.media.GalleryFragment;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 截图数据更新的Fragment
 */
public class UpdateInfoFragment extends Fragment {

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @Override
    protected int getContentLayouId() {
        return R.layout.fragment_update_info;
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        new GalleryFragment()
                .setListener(new GalleryFragment.OnSelectedListener() {
                    @Override
                    public void onSelectedImage(String path) {
                        UCrop.Options options = new UCrop.Options();
                        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                        //设置压缩后的图片精度
                        options.setCompressionQuality(96);

                        //调用common文件夹中的自定义Application类
                        File dPath = Application.getPortraitTmpFile();

                        UCrop.of(Uri.fromFile(new File(path)),Uri.fromFile(dPath))
                                 .withAspectRatio(1,1) //1比1比例
                                 .withMaxResultSize(520,520)  // 返回最大的尺寸
                                 .withOptions(options)  //相关参数
                                 .start(getActivity());
                    }
                })
                //show 的时候建议使用getChildFragmentManager
                .show(getChildFragmentManager(),GalleryFragment.class.getName());
    }

    /**
     * 调用剪切库依赖包中的
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //收到从Activity 传递过来的回调，然后取出其中的值进行图片加载
        //如果是我能够处理的类型
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            //通过UCrop得到对应的Uri
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri!=null){
                loadPortrait(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }
    private void loadPortrait(Uri uri){
        Glide.with(this)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);
    }
}
