package com.xtransformers.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于字符串的悲观锁
 *
 * @author hellboy0621
 * @date 2022-05-01
 */
public class StringBasedMutexLock {

    private static final Logger log = LoggerFactory.getLogger(StringBasedMutexLock.class);

    /**
     * 字符串锁管理器
     * 锁，只发生在同一个字符串并发更新时
     */
    private static final Map<String, CountDownLatch> LOCK_KEY_HOLDER = new ConcurrentHashMap<>();

    /**
     * 基于字符串 lockKey 加锁
     *
     * @param lockKey 指定字符串
     */
    public static void lock(String lockKey) {
        while (!tryLock(lockKey)) {
            try {
                log.debug("StringBasedMutexLock 锁升级 {}", lockKey);
                blockOnSecondLevelLock(lockKey);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.debug("StringBasedMutexLock 中断异常 " + lockKey, e);
                break;
            }
        }
    }

    /**
     * 释放基于字符串 lockKey 的锁
     *
     * @param lockKey
     * @return
     */
    public static boolean unlock(String lockKey) {
        CountDownLatch realLock = getAndReleaseLock(lockKey);
        releaseSecondLevelLock(realLock);
        return true;
    }

    /**
     * 尝试给指定字符串加锁
     *
     * @param lockKey 指定字符
     * @return 加锁成功返回true，否则返回false
     */
    private static boolean tryLock(String lockKey) {
        return LOCK_KEY_HOLDER.putIfAbsent(lockKey, new CountDownLatch(1)) == null;
    }

    /**
     * 锁升级
     * 二级锁锁定
     *
     * @param lockKey 指定字符
     * @throws InterruptedException
     */
    private static void blockOnSecondLevelLock(String lockKey) throws InterruptedException {
        CountDownLatch realLock = LOCK_KEY_HOLDER.get(lockKey);
        // realLock 如果为 null，说明锁已被删除
        if (realLock != null) {
            realLock.await();
        }
    }

    /**
     * 根据指定字符串获取并解锁
     *
     * @param lockKey 指定字符串
     * @return
     */
    private static CountDownLatch getAndReleaseLock(String lockKey) {
        return LOCK_KEY_HOLDER.remove(lockKey);
    }

    private static void releaseSecondLevelLock(CountDownLatch readLock) {
        if (readLock != null) {
            readLock.countDown();
        }
    }
}
