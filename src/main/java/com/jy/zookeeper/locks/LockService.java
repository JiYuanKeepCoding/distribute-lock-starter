package com.jy.zookeeper.locks;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class LockService {

    private Thread t;

    private ZooKeeper zk;

    public LockService(String endpoint, int sessionTimeout) throws IOException {
        zk = new ZooKeeper(endpoint, sessionTimeout, (WatchedEvent event) -> {
            Watcher.Event.KeeperState state = event.getState();

            switch (state) {
                case Disconnected:
                    System.out.println("Disconnected...c...new...");
                    break;
                case SyncConnected:
                    System.out.println("Connected...c...ok...");
                    LockSupport.unpark(t);
                    break;
            }
        });
        t = Thread.currentThread();
        LockSupport.park();
    }

    public LockContext lock(String dir, String name) throws Exception {
        final LockContext lockContext = new LockContext(dir, name);
        lockContext.setCreateCallBack((int rc, String path, Object ctx, String name1) -> {
            //每个线程启动后创建锁，然后get锁目录的所有孩子，不注册watch在锁目录
            System.out.println("create path: "+ name1);
            lockContext.setLockName(name1.replace(dir + "/", ""));
            zk.getChildren(dir, false, lockContext.getGetChildrenCallBack(), ctx );
        });

        lockContext.setWatchExistCallback((WatchedEvent event) -> {
            Watcher.Event.EventType type = event.getType();
            switch (type) {
                case NodeDeleted:
                    zk.getChildren(dir, false, lockContext.getGetChildrenCallBack(), "");
                    break;
                case NodeChildrenChanged:
                    break;
            }
        });

        lockContext.setGetChildrenCallBack((int rc, String path, Object ctx, List<String> children, Stat stat) -> {
            String lockName = lockContext.getLockName();
            if(children == null){
                System.out.println(ctx.toString() + "list null");
            }else{
                try {
                    Collections.sort(children);
                    int i = children.indexOf(lockName);
                    if(i<1){
                        LockSupport.unpark(lockContext.getWaiterThread());
                    }else{
                        zk.exists(lockContext.getDir()+children.get(i-1), lockContext.getWatchExistCallback());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        String hostName = InetAddress.getLocalHost().getHostName();

        zk.create(dir + name, hostName.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,  CreateMode.EPHEMERAL_SEQUENTIAL,
                lockContext.getCreateCallBack(), null );
        lockContext.setWaiterThread(Thread.currentThread());
        LockSupport.park();
        return lockContext;
    }

    public void unLock(LockContext lockContext){
        try {
            zk.delete(lockContext.getDir() + lockContext.getName(),-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

}
