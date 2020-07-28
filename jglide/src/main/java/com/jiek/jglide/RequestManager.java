package com.jiek.jglide;

import android.graphics.Bitmap;

import com.jiek.jglide.cache.DispatchLruCache;
import com.jiek.logger.Logger;

import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * RequestManager 确切应该与 Activity、FragmentActivity、Fagment 等进行绑定，以在生命周期内可进行逻辑控制
 */
public class RequestManager {
    private static volatile RequestManager instance;

    private DispatchLruCache<String, Bitmap> dispatchLruCache;

    //阻塞与线程安全
    private LinkedBlockingQueue<JRequest> requestQueue = new LinkedBlockingQueue<>();
    private JDispacher[] jDispachers;

    public RequestManager() {
        stop();
        dispatchLruCache = new DispatchLruCache();
        createAndStartDispachers();
    }

    public static RequestManager getInstance() {
        if (instance == null) {
            synchronized (RequestManager.class) {
                if (instance == null) {
                    instance = new RequestManager();
                }
            }
        }
        return instance;
    }

    public void addRequest(JRequest request) {
        if (!requestQueue.contains(request)) {
            requestQueue.add(request);
        }
    }

    public void stop() {
        if (jDispachers == null || jDispachers.length < 1) {
            return;
        }
        for (JDispacher jDispacher : jDispachers) {
            if (!jDispacher.isInterrupted()) {
                jDispacher.interrupt();
            }
        }
    }

    public void clearCache() {
        dispatchLruCache.removeAll();
    }

    private void createAndStartDispachers() {
//        获取可以处理器，构建最大线程分发数
        int threadCount = Runtime.getRuntime().availableProcessors();
        Logger.e("开启线程：" + threadCount);
        jDispachers = new JDispacher[threadCount];
        for (int i = 0; i < threadCount; i++) {
            JDispacher jDispacher = new JDispacher(requestQueue);
            jDispacher.start();
            jDispachers[i] = jDispacher;
        }
    }

    public Bitmap getMemoryBitmap(String url) {
        return (Bitmap) dispatchLruCache.get(url);
    }

    public boolean hasMemoryCache(String url) {
        if (getMemoryBitmap(url) != null) {
            return true;
        }
        return false;
    }

    public Bitmap saveInputStrem(String url, InputStream inputStream) {
        dispatchLruCache.put(url, inputStream);
        return (Bitmap) dispatchLruCache.get(url);
    }

    public JRequestBuilder load(String url) {
        return new JRequestBuilder().load(url);
    }
}
