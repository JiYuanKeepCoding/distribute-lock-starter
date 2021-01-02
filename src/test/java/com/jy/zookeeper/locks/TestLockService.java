package com.jy.zookeeper.locks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.locks.LockSupport;

@SpringBootTest(classes = DistributeLockFactory.class)
class TestLockService {

    @Autowired
    LockService lockService;

    @Test
    void test() {
        Runnable r = () -> {
            try {
                LockContext lockContext = lockService.lock("/lock", "/job1");
                System.out.println("executing job ...");
                Thread.sleep(1000L);
                lockService.unLock(lockContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        for (int i = 0; i < 3; i ++) {
            Thread t = new Thread(r);
            t.start();
        }
        LockSupport.parkNanos(1000*1000*5);
    }

}