package com.jiek.jglide;

public interface SourceType {
    //无缓存
    int SOURCE_NO_CACHE = 0;
    //内存缓存
    int SOURCE_MEMORY = 1;
    //    磁盘原图缓存
    int SOURCE_DISC = 1 << 1;
    //    磁盘压缩缓存
//    int SOURCE_DISC_CROP = 1 << 2;
    //网络
    int SOURCE_NET = 1 << 4;
}
