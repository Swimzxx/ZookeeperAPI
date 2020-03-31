package com.z.zookeeperapi;

import com.z.watcher.ZKConnectWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: zxx
 * @Date: 2020/3/30 20:43
 * @Description: 连接zookeeper
 */
public class ZKConnection {
    @Test
    public void zkconnect() throws IOException, InterruptedException {
        String ip="192.168.21.141:2181";
        // 计数器对象
        final CountDownLatch countDownLatch=new CountDownLatch(1);
        // 连接对象
        ZooKeeper zooKeeper=new ZooKeeper(ip, 5000, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                try {
                    //EventType = None时
                    if (watchedEvent.getType() == Watcher.Event.EventType.None) {
                        if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                            System.out.println("连接成功");
                            countDownLatch.countDown();
                        } else if (watchedEvent.getState() == Watcher.Event.KeeperState.Disconnected) {
                            System.out.println("断开连接");
                        } else if (watchedEvent.getState() == Watcher.Event.KeeperState.Expired) {
                            System.out.println("会话超时");
                        } else if (watchedEvent.getState() == Watcher.Event.KeeperState.AuthFailed) {
                            System.out.println("认证失败");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        countDownLatch.await();
        zooKeeper.close();
    }
}
