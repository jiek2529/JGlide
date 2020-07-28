package com.jiek.jglide;

import android.widget.ImageView;

import com.twmacinta.util.MD5;

import java.lang.ref.SoftReference;

/**
 * JRequestBuilder 的构造，必须从 load 开始，into结束
 */
public class JRequestBuilder {

    private String url;
    private String urlMd5;
    private int placeholder;
    private int error;
    private SoftReference<ImageView> imageView;
    //    结果回调
    private JRequestListener jRequestListener;

    //    图片资源缓存策略，默认无缓存
    private int souceType = SourceType.SOURCE_NO_CACHE;

    private int cropType = CropType.CROP_NONE;

    public JRequestBuilder load(String url) {
        this.url = url;
        this.urlMd5 = new MD5(url).asHex();
        return this;
    }

    public JRequestBuilder placeholder(int res_id) {
        this.placeholder = res_id;
        return this;
    }

    public JRequestBuilder error(int res_id) {
        this.error = res_id;
        return this;
    }

    public JRequestBuilder setjRequestListener(JRequestListener jRequestListener) {
        this.jRequestListener = jRequestListener;
        return this;
    }

    public void into(ImageView imageView) {
        this.imageView = new SoftReference<>(imageView);
        imageView.setTag(urlMd5);
        JRequest request = new JRequest(imageView.getContext());
        request.load(url).error(error).placeholder(placeholder).setjRequestListener(jRequestListener).into(imageView);
        RequestManager.getInstance().addRequest(request);
    }
}
