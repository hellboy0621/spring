package com.xtransformers.utils;

import cn.hutool.core.lang.UUID;
import java.util.concurrent.TimeUnit;

class StringBasedMutexLockTest {

    public static void main(String[] args) {
        StringBasedMutexLockTest test = new StringBasedMutexLockTest();
        final String uuid = UUID.randomUUID().toString();
        for (int i = 0; i < 2; i++) {
            // 10 个线程并发调用
            new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    // 每个线程，再调用10次，使用悲观锁强制串行化
                    test.service(uuid, j);
                }
            }).start();
        }
    }

    public void service(String lockStr, int index) {
        StringBasedMutexLock.lock(lockStr);
        try {
            System.out.println(System.currentTimeMillis() + " -> " + Thread.currentThread().getName() + " -> " + index);
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } finally {
            StringBasedMutexLock.unlock(lockStr);
        }
    }

}