package com.z.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: zxx
 * @Date: 2020/3/29 22:05
 * @Description: watcher机制
 * 通知状态 KeeperState:
 * SyncConnected:客户端与服务器正常连接时
 * Disconnected:客户端与服务器断开连接时
 * Expired:会话session失效时
 * AuthFailed:身份认证失败时
 * <p>
 * 事件类型 EventType none
 */
public class ZKConnectWatcher implements Watcher {
    // 计数器对象
    public static CountDownLatch countDownLatch=new CountDownLatch(1);
    // 连接对象
    public static ZooKeeper zooKeeper;

    private String ip = "192.168.21.141:2181";

    public void process(WatchedEvent watchedEvent) {
        try {
            //EventType = None时
            if (watchedEvent.getType() == Event.EventType.None){
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("连接成功");
                    countDownLatch.countDown();
                }else if (watchedEvent.getState() == Event.KeeperState.Disconnected){
                    System.out.println("断开连接");
                }else if (watchedEvent.getState() == Event.KeeperState.Expired){
                    System.out.println("会话超时");
                }else if (watchedEvent.getState() == Event.KeeperState.AuthFailed){
                    System.out.println("认证失败");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        try {
            zooKeeper=new ZooKeeper(ip, 5000, new ZKConnectWatcher());
            // 阻塞线程等待连接的创建
            countDownLatch.await();
            // 会话id
            System.out.println(zooKeeper.getSessionId());
            Thread.sleep(50000);
            zooKeeper.close();
            System.out.println("结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }
