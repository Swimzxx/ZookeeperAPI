package com.z.zookeeperapi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: zxx
 * @Date: 2020/3/30 20:49
 * @Description: 更新节点
 */
public class ZKUpdate {

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
    public void zkCreate1() throws KeeperException, InterruptedException {
        // arg1:节点的路径
        // arg2:节点的数据
        // arg3:版本号 -1代表版本号不作为修改条件
        Stat stat=zooKeeper.setData("/node1","nodedata".getBytes(),2);
        // 节点的版本号
        System.out.println(stat.getVersion());
        // 节点的创建时间
        System.out.println(stat.getCtime());
    }

    @Test
    public void zkCreate2() throws KeeperException, InterruptedException {
        // 异步方式修改节点
        zooKeeper.setData("/node2", "node2".getBytes(), -1, new AsyncCallback.StatCallback() {
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            // 0 代表修改成功
             System.out.println(rc);
            // 修改节点的路径
             System.out.println(path);
             // 上线文的参数对象
            System.out.println(ctx);
            // 版本信息
            System.out.println(stat.getVersion());
        } },"I am Context");
        Thread.sleep(50000);
        System.out.println("结束");
    }




}
