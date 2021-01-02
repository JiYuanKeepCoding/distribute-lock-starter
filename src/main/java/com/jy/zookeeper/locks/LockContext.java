package com.jy.zookeeper.locks;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.Watcher;

public class LockContext {

    private String dir;

    private String name;

    private String lockName;

    private Watcher watchExistCallback;

    private AsyncCallback.Children2Callback getChildrenCallBack;

    private AsyncCallback.StringCallback createCallBack;

    private Thread waiterThread;

    public LockContext(String dir, String name) {
        this.dir = dir;
        this.name = name;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Thread getWaiterThread() {
        return waiterThread;
    }

    public void setWaiterThread(Thread waiterThread) {
        this.waiterThread = waiterThread;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public Watcher getWatchExistCallback() {
        return watchExistCallback;
    }

    public void setWatchExistCallback(Watcher watchExistCallback) {
        this.watchExistCallback = watchExistCallback;
    }

    public AsyncCallback.Children2Callback getGetChildrenCallBack() {
        return getChildrenCallBack;
    }

    public void setGetChildrenCallBack(AsyncCallback.Children2Callback getChildrenCallBack) {
        this.getChildrenCallBack = getChildrenCallBack;
    }

    public AsyncCallback.StringCallback getCreateCallBack() {
        return createCallBack;
    }

    public void setCreateCallBack(AsyncCallback.StringCallback createCallBack) {
        this.createCallBack = createCallBack;
    }
}
