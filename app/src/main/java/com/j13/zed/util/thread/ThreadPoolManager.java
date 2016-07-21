package com.j13.zed.util.thread;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by aaronliu on 15-7-2.
 */
public class ThreadPoolManager {

    static class ThreadPoolManagerHolder {
        static ThreadPoolManager sInstance = new ThreadPoolManager();
    }

    public static ThreadPoolManager getInstance() {
        return ThreadPoolManagerHolder.sInstance;
    }

    public enum POOL_TYPE {
        CACHE,
        FIX,
        SINGLE
    }


    private HashMap<POOL_TYPE,ExecutorService> mPoolMap;

    public ThreadPoolManager() {
        mPoolMap = new HashMap<POOL_TYPE, ExecutorService>();
    }

    public ExecutorService getPool(POOL_TYPE type, int threads) {
        ExecutorService pool = mPoolMap.get(type);
        if(pool != null && !pool.isTerminated() && !pool.isShutdown()) {
            return pool;
        }
        switch (type) {

            case FIX:
                mPoolMap.put(type, Executors.newFixedThreadPool(threads));
                break;
            case SINGLE:
                mPoolMap.put(type, Executors.newSingleThreadExecutor());
                break;
            case CACHE:
            default:
                mPoolMap.put(type, Executors.newCachedThreadPool());
                break;
        }
        return mPoolMap.get(type);
    }

    public void shutdownAll()
    {
        for(ExecutorService pool : mPoolMap.values()) {
            if(pool != null && !pool.isTerminated() && !pool.isShutdown()) {
                pool.shutdown();
            }
        }
    }

    public void shutdown(POOL_TYPE type)
    {
        ExecutorService pool = mPoolMap.get(type);
        if(pool != null && !pool.isTerminated() && !pool.isShutdown()) {
            pool.shutdown();
        }
    }
}
