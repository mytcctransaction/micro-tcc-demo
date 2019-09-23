package org.micro.tcc.demo.common.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
*@author jeff.liu
*@desc   定长和定时清理的缓存
*@date 2019/8/27
*/
public class FixSizeCacheMap {

    private static volatile Cache<String, Object> cache = CacheBuilder.newBuilder().initialCapacity(100).maximumSize(10000).expireAfterAccess(30L, TimeUnit.SECONDS).build();

    private static volatile FixSizeCacheMap fixSizeCacheMap =new FixSizeCacheMap();

    public static FixSizeCacheMap get(){
        return fixSizeCacheMap;
    }

    public  void add(String key,Object value){
        cache.put(key,value);
    }

    public Object peek(String key){
        return cache.getIfPresent(key);
    }

    public void del(String key){
        cache.invalidate(key);
    }

}
