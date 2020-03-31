package com.z.ZKLock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: zxx
 * @Date: 2020/3/30 21:51
 * @Description: 分布式锁实现
 * 1.每个客户端往/Locks下创建临时有序节点/Locks/Lock 000000001
 * 2.客户端取得/Locks下子节点，并进行排序，判断排在最前面的是否为自己，如果自己的 锁节点在第一位，代表获取锁成功
 * 3.如果自己的锁节点不在第一位，则监听自己前一位的锁节点。例如，自己锁节点 Lock 000000001
 * 4.当前一位锁节点（Lock 000000002）的逻辑
 * 5.监听客户端重新执行第2步逻辑，判断自己是否获得了锁
 */
public class ZKLock {

    // 计数器对象
    public static CountDownLatch countDownLatch=new CountDownLatch(1);
    // 连接对象
    public static ZooKeeper zooKeeper;

    private String IP = "192.168.21.141:2181";

    //锁的根节点路径
    private static final String LOCK_ROOT_PATH = "/Locks";
    private static final String LOCK_NODE_NAME = "Lock_";
    private String lockPath;

    public ZKLock() {
        try {
            zooKeeper = new ZooKeeper(IP, 5000, (event) -> {
                    if (event.getType() == Watcher.Event.EventType.None) {
                        if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                            System.out.println("连接成功!");
                            countDownLatch.countDown();
                        }
                    }

            });
            countDownLatch.await();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //获取锁
    public void acquireLock() throws Exception {
        //创建锁节点
        createLock();
        //尝试获取锁
        attemptLock();
    }



    //创建锁节点
    private void createLock() throws Exception {
        //判断Locks是否存在，不存在创建
        Stat stat = zooKeeper.exists(LOCK_ROOT_PATH, false);
        if (stat == null) {
            zooKeeper.create(LOCK_ROOT_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        // 创建临时有序节点
        lockPath = zooKeeper.create(LOCK_ROOT_PATH + "/" + LOCK_NODE_NAME, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("节点创建成功:" + lockPath);
    }
    //尝试获取锁
    private void attemptLock() throws Exception {
        // 获取Locks节点下的所有子节点
        List<String> list = zooKeeper.getChildren(LOCK_ROOT_PATH, false);
        // 对子节点进行排序
        Collections.sort(list);
        // /Locks/Lock_000000001
        int index = list.indexOf(lockPath.substring(LOCK_ROOT_PATH.length() + 1));
        if (index == 0) {
            //获取锁成功，执行相应的代码
            System.out.println("获取锁成功!");

            return;
        } else {
            // 上一个节点的路径
            String path = list.get(index - 1);
            Stat stat = zooKeeper.exists(LOCK_ROOT_PATH + "/" + path, watcher);
            if (stat == null) {
                attemptLock();
            } else {
                synchronized (watcher) {
                    watcher.wait();
                }
                attemptLock();
            }
        }

    }
    //监视器对象，监视上一个节点是否被删除
    Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.NodeDeleted) {
                synchronized (this) {
                    notifyAll();
                }
            }
        }
    };

    //释放锁
    public void releaseLock() throws Exception {
        //删除临时有序节点
        zooKeeper.delete(this.lockPath,-1);
        zooKeeper.close();
        System.out.println("锁已经释放:"+this.lockPath);
    }
    
}
