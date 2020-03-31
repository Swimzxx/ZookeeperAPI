package com.z.confcenter;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: zxx
 * @Date: 2020/3/30 21:26
 * @Description: 配置中心
 */
public class ZKConfCenter implements Watcher {

    // 计数器对象
    public static CountDownLatch countDownLatch=new CountDownLatch(1);
    // 连接对象
    public static ZooKeeper zooKeeper;

    private String IP = "192.168.21.141:2181";
    
    private String url;
    
    private String username;

    public ZKConfCenter() throws Exception {
        initZK();
    }

    private void initZK() throws Exception {
        zooKeeper = new ZooKeeper(IP, 6000,this);
        countDownLatch.await();
        //获取配置
        this.url = new String(zooKeeper.getData("/config/url",true,null),"utf-8");
        this.username = new String(zooKeeper.getData("/config/username",true,null),"utf-8");
    }

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
            }else if(watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                initZK();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url=url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username=username;
    }

    @Test
    public void test() throws Exception {
        ZKConfCenter zkConfCenter = new ZKConfCenter();
        for(int i=0 ;i<3;i++){
            Thread.sleep(10000);
            System.out.println(zkConfCenter.getUrl());
            System.out.println(zkConfCenter.getUsername());
        }
    }
   
}
