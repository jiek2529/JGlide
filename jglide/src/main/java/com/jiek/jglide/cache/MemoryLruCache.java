package com.jiek.jglide.cache;

import android.util.LruCache;

public class MemoryLruCache<V> implements BaseCache {
    //    锁
    private static final byte[] lock = new byte[0];
    private static volatile MemoryLruCache instance;
    private LruCache<String, V> lruCache;

    public MemoryLruCache() {
        int maxMemorySize = (int) (Runtime.getRuntime().maxMemory() / 16);
        if (maxMemorySize < 0) {
            maxMemorySize = 10 * (1 << 20);
        }
        lruCache = new LruCache<String, V>(maxMemorySize) {
            @Override
            protected int sizeOf(String key, V value) {
                return super.sizeOf(key, value);
            }
        };
    }

    public static MemoryLruCache getInstance() {
        if (instance == null) {
            synchronized (MemoryLruCache.class) {
                if (instance == null) {
                    instance = new MemoryLruCache();
                }
            }
        }
        return instance;
    }

    @Override
    public void put(Object key, Object val) {
        if (val == null) {
            lruCache.put((String) key, (V) val);
        }
    }

    @Override
    public Object get(Object key) {
        return lruCache.get((String) key);
    }

    @Override
    public void remove(Object key) {
        lruCache.remove((String) key);
    }

    @Override
    public void removeAll() {
        lruCache.evictAll();
    }
}
