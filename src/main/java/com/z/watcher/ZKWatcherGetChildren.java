package com.z.watcher;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: zxx
 * @Date: 2020/3/30 19:57
 * @Description: zk.getChildren(path, watch)**
 */
public class ZKWatcherGetChildren {

    private String IP = "192.168.60.130:2181";

    public static ZooKeeper zooKeeper;

    //连接zookeeper
    @Before
    public void before() throws InterruptedException, IOException {
       final CountDownLatch countDownLatch=new CountDownLatch(1);
       zooKeeper = new ZooKeeper(IP, 6000, new Watcher() {
           public void process(WatchedEvent watchedEvent) {
               if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                   countDownLatch.countDown();
               }
               System.out.println("path=" + watchedEvent.getPath());
               System.out.println("eventType=" + watchedEvent.getType());
           }
       });
       countDownLatch.await();
    }

    @After
    public void after() throws InterruptedException {
        zooKeeper.close();
    }

    @Test
    public void watcherGetChildren1() throws KeeperException, InterruptedException {
        // arg1:节点的路径
        // arg2:使用连接对象中的watcher
        zooKeeper.getChildren("/watcher1", true);
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void watcherGetChildren2() throws KeeperException, InterruptedException {
        Watcher watcher = new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher");
                System.out.println("path=" + watchedEvent.getPath());
                System.out.println("eventType=" + watchedEvent.getType());
            }};
        zooKeeper.getChildren("/watcher1", watcher);
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void watcherGetChildren3() throws KeeperException, InterruptedException {
        zooKeeper.getChildren("/watcher1", new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher");
                System.out.println("path=" + watchedEvent.getPath());
                System.out.println("eventType=" + watchedEvent.getType());
            }
        });
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void watcherGetChildren4() throws KeeperException, InterruptedException {
        zooKeeper.getChildren("/watcher1", new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher1");
                System.out.println("path=" + watchedEvent.getPath());
                System.out.println("eventType=" + watchedEvent.getType());
            }
        });
        zooKeeper.getChildren("/watcher1", new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher2");
                System.out.println("path=" + watchedEvent.getPath());
                System.out.println("eventType=" + watchedEvent.getType());
            }
        });
        Thread.sleep(50000);
        System.out.println("结束");
    }
}
