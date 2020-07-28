package com.jiek.jglide;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

public class JDispacher extends Thread {
    //使用 RequestManager 中管理的链表阻塞队列，管理加载任务
    private LinkedBlockingQueue<JRequest> requestQueue;

    private Handler handler = new Handler(Looper.getMainLooper());

    public JDispacher(LinkedBlockingQueue<JRequest> requestQueue) {
        this.requestQueue = requestQueue;
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            JRequest request = null;
            try {
                request = requestQueue.take();
                new Exception().printStackTrace();
                showImage(request, request.getPlaceholder());

                Bitmap bitmap = findBitmap(request);
                if (bitmap == null) {
                    continue;
                }
//                显示网络下载的图片 Bitmap
                showImage(request, bitmap);
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (request != null && request.getjRequestListener() != null) {
                    request.getjRequestListener().onFail("图片下载失败");
                }
            }
        }
    }

    // 占位与错误显示图
    private void showImage(JRequest request, final int res_id) {
        if (request != null && res_id > 0) {
            final ImageView imageView = request.getImageView();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageResource(res_id);
                }
            });
        }
    }

    // 显示下载并处理好的图片
    private void showImage(JRequest request, final Bitmap bitmap) {
        JRequestListener jRequestListener = request.getjRequestListener();
        if (request != null && bitmap != null &&
                request.getImageView().getTag().equals(request.getUrlMd5())
        ) {
            final ImageView imageView = request.getImageView();
            System.out.println("ready to show image： " + imageView);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bitmap);
                }
            });
        } else {
            if (jRequestListener != null) {
                jRequestListener.onFail("下载失败");
            }
        }
    }

    private Bitmap findBitmap(JRequest request) {
        String url = request.getUrl();
        boolean memoryCache = RequestManager.getInstance().hasMemoryCache(url);
        Bitmap tBitmap;
        JRequestListener jRequestListener = request.getjRequestListener();
        if (memoryCache) {
            tBitmap = RequestManager.getInstance().getMemoryBitmap(url);
            if (jRequestListener != null) {
                jRequestListener.onSuccess(tBitmap, SourceType.SOURCE_MEMORY);
            }
        } else {
            tBitmap = downloadImage(request.getUrl());
            if (tBitmap != null) {
//                System.out.println("下载完成，并等待2s");
//                //调试延时显示
//                SystemClock.sleep(2000);
                if (jRequestListener != null) {
                    jRequestListener.onSuccess(tBitmap, SourceType.SOURCE_NET);
                }
            } else {
                if (jRequestListener != null) {
                    jRequestListener.onFail("下载失败： " + request.getUrl());
                }
            }
        }
        return tBitmap;
    }

    private Bitmap downloadImage(String uri) {
        FileOutputStream fos = null;
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            is = conn.getInputStream();
            bitmap = RequestManager.getInstance().saveInputStrem(uri, is);
//            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
