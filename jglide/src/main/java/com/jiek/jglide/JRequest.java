package com.jiek.jglide;

import android.content.Context;
import android.widget.ImageView;

import com.twmacinta.util.MD5;

import java.lang.ref.SoftReference;

public class JRequest {

    //    上下文
    private Context context;
    //    网络图片地址
    private String url;
    //    防图片引用错乱，为url 生成md5,作 View.setTag
    private String urlMd5;
    //    软引用 ImageView
    private SoftReference<ImageView> imageView;
    //    结果回调
    private JRequestListener jRequestListener;

    //    图片资源缓存策略，默认无缓存
    private int souceType = SourceType.SOURCE_NO_CACHE;

    private int cropType = CropType.CROP_NONE;

    private int placeholder;
    private int error;

    public JRequest(Context context) {
        this.context = context;
    }

    public JRequest load(String url) {
        this.url = url;
        this.urlMd5 = new MD5(url).asHex();
        return this;
    }

    public JRequest placeholder(int res_id) {
        this.placeholder = res_id;
        return this;
    }

    public JRequest error(int res_id) {
        this.error = res_id;
        return this;
    }

    public void into(ImageView imageView) {
        this.imageView = new SoftReference<>(imageView);
        imageView.setTag(urlMd5);
    }

    public String getUrl() {
        return url;
    }

    public int getPlaceholder() {
        return placeholder;
    }

    public int getError() {
        return error;
    }

    public ImageView getImageView() {
        return imageView.get();
    }

    public String getUrlMd5() {
        return this.urlMd5;
    }

    public JRequestListener getjRequestListener() {
        return jRequestListener;
    }

    public JRequest setjRequestListener(JRequestListener jRequestListener) {
        this.jRequestListener = jRequestListener;
        return this;
    }

    public int getSouceType() {
        return souceType;
    }

    //    设置缓存策略
    public void setSouceType(int souceType) {
        this.souceType = souceType;
    }

    public void centerCrop() {
        cropType = CropType.CROP_CENTER;
    }
}
