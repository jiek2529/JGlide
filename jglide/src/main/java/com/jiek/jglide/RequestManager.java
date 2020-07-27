package com.jiek.jglide;

import java.util.concurrent.LinkedBlockingQueue;

public class RequestManager {
    private static volatile RequestManager instance;
    //阻塞与线程安全
    private LinkedBlockingQueue<JRequest> requestQueue = new LinkedBlockingQueue<>();
    private JDispacher[] jDispachers;

    public RequestManager() {
        stop();
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

    private void createAndStartDispachers() {
//        获取可以处理器，构建最大线程分发数
        int threadCount = Runtime.getRuntime().availableProcessors();
        jDispachers = new JDispacher[threadCount];
        for (int i = 0; i < threadCount; i++) {
            JDispacher jDispacher = new JDispacher(requestQueue);
            jDispacher.start();
            jDispachers[i] = jDispacher;
        }
    }

}
