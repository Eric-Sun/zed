package com.j13.zed.util.thread;

import java.util.concurrent.ExecutorService;

/**
 * Created by aaronliu on 15-7-2.
 */
public class CustomThreadPool {

    private static ThreadPoolManager sManager = ThreadPoolManager.getInstance();

    public static void asyncWork(Runnable r) {
        ExecutorService service = sManager.getPool(ThreadPoolManager.POOL_TYPE.CACHE, 0);
        service.execute(r);
    }

    public static void asyncWork(Runnable r, ExecutorService service) {
        if(service != null)
            service.execute(r);
    }
}
