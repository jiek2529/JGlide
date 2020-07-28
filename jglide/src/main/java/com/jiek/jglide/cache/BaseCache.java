package com.jiek.jglide.cache;

/**
 * 缓存接口
 *
 * @param <K>
 * @param <V>
 */
public interface BaseCache<K, V> {

    /**
     * 缓存数据
     *
     * @param key
     * @param val
     */
    void put(K key, V val);

    /**
     * 获取缓存
     *
     * @param key
     */
    V get(K key);

    /**
     * 清除缓存
     *
     * @param key
     */
    void remove(K key);

    /**
     * 清除所有缓存
     */
    void removeAll();
}
