package com.jiek.jglide.cache;

import com.jiek.jglide.JGlide;
import com.twmacinta.util.MD5;

import java.io.InputStream;

/**
 * 管理内存与磁盘缓存
 *
 * @param <K>
 * @param <V>
 */
public class DispatchLruCache<K, V> implements BaseCache {

    private MemoryLruCache memoryLruCache;
    private DiskCache diskCache;

    public DispatchLruCache() {
        diskCache = DiskCache.getInstance(JGlide.getApplicationContext());
        memoryLruCache = MemoryLruCache.getInstance();
    }

    @Override
    public void put(Object url, Object val) {
        if (val instanceof InputStream) {
            diskCache.put(md5(url), (InputStream) val);
        } else {
//            memoryLruCache.put(key, val);
//            diskCache.put(key, val);
        }
    }

    @Override
    public Object get(Object url) {
        String md5Url = md5(url);
        Object val = memoryLruCache.get(md5Url);
        if (val == null) {
            val = diskCache.get(md5Url);
            if (val != null) {
                memoryLruCache.put(md5Url, val);
            }
        }
        return val;
    }

    @Override
    public void remove(Object url) {
        String md5Url = md5(url);
        memoryLruCache.remove(md5Url);
        diskCache.remove(md5Url);
    }

    @Override
    public void removeAll() {
        memoryLruCache.removeAll();
//        磁盘清理可酌情处理
//        diskCache.removeAll();
    }

    /**
     * 将原始 url 转MD5为文件名
     *
     * @param key
     * @return
     */
    private String md5(Object key) {
        return new MD5(key.toString()).asHex();
    }
}
