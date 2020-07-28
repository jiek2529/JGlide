package com.jiek.jglide;

import android.graphics.Bitmap;

public interface JRequestListener {
    /**
     * Bitmap 获取成功
     *
     * @param bitmap
     * @param sourceType 图片源类型
     */
    void onSuccess(Bitmap bitmap, int sourceType);

    void onFail(String msg);
}
