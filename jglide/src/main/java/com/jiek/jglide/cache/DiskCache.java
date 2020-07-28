package com.jiek.jglide.cache;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.jiek.jglide.cache.disk.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 磁盘缓存
 */
public class DiskCache<K, V> implements BaseCache {
    private static final int MB = 1 << 20;
    private static volatile DiskCache instance;
    //缓存最大存储容量
    private int maxDiskSize = 50 * MB;
    private String cachePath = "jglide";
    private DiskLruCache lruCache;
    //    锁
//    private static final byte[] lock = new byte[0];

    public DiskCache(Context context) {
        File cacheFile = getCacheFile(context, cachePath);
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        try {
            lruCache = DiskLruCache.open(cacheFile, getAppVersion(context), 1, maxDiskSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DiskCache getInstance(Context context) {
        if (instance == null) {
            synchronized (DiskCache.class) {
                if (instance == null) {
                    instance = new DiskCache(context);
                }
            }
        }
        return instance;
    }

    private int getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private File getCacheFile(Context mContext, String cachePath) {
        //此处需要
        return new File(Environment.getExternalStorageDirectory(), cachePath);
    }

    /**
     * @param key 原始 url
     * @param val
     */
    @Override
    public void put(Object key, Object val) {
        try {
            boolean saveFlag = false;
            DiskLruCache.Editor editor = lruCache.edit(key.toString());
            if (editor != null && val instanceof InputStream) {
                OutputStream outputStream = editor.newOutputStream(0);
                saveFlag = saveInputStream((InputStream) val, outputStream);
                editor.commit();
            }
            if (!saveFlag) {
                editor.abort();
            }
            lruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将网络请求数据写入指定文件
     *
     * @param inputStream  网络输入流
     * @param outputStream 文件写入流
     * @return
     */
    private boolean saveInputStream(InputStream inputStream, OutputStream outputStream) {
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(inputStream, 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        try {
            DiskLruCache.Snapshot snapShot = lruCache.get((String) key);
            if (snapShot != null) {
                InputStream is = snapShot.getInputStream(0);
                return BitmapFactory.decodeStream(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void remove(Object key) {
        try {
            lruCache.remove((String) key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeAll() {
    }

}
