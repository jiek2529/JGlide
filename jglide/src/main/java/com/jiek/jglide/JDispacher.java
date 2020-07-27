package com.jiek.jglide;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
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
                showImage(request, request.getPlaceholder());

                Bitmap bitmap = findBitmapFromInternet(request);
//                显示网络下载的图片 Bitmap
                showImage(request, bitmap);
            } catch (InterruptedException e) {
                e.printStackTrace();
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

    // 占位与错误显示图
    private void showImage(JRequest request, final Bitmap bitmap) {
        if (request != null && bitmap != null &&
                request.getImageView().getTag().equals(request.getUrlMd5())
        ) {
            System.out.println("下载完成，并等待2s");
            SystemClock.sleep(2000);
            final ImageView imageView = request.getImageView();
            System.out.println("ready to show image： " + imageView);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bitmap);
                }
            });
        }
    }

    private Bitmap findBitmapFromInternet(JRequest request) {
        return downloadImage(request.getUrl());
    }

    private Bitmap downloadImage(String uri) {
        FileOutputStream fos = null;
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
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
