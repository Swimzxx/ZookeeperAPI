package com.z.ZKUniqueID;

import com.z.confcenter.ZKConfCenter;
import org.apache.zookeeper.*;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: zxx
 * @Date: 2020/3/30 21:43
 * @Description: 生成唯一ID
 */
public class ZKUniqueID implements Watcher{
    // 计数器对象
    public static CountDownLatch countDownLatch=new CountDownLatch(1);
    // 连接对象
    public static ZooKeeper zooKeeper;

    private String IP = "192.168.21.141:2181";

    // 用户生成序号的节点
    String defaultPath = "/uniqueId";

    public ZKUniqueID() throws Exception {
        zooKeeper = new ZooKeeper(IP, 6000, this);
        countDownLatch.await();
    }

    public void process(WatchedEvent watchedEvent) {
        try {
            //EventType = None时
            if (watchedEvent.getType() == Watcher.Event.EventType.None){
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected){
                    System.out.println("连接成功");
                    countDownLatch.countDown();
                }else if (watchedEvent.getState() == Watcher.Event.KeeperState.Disconnected){
                    System.out.println("断开连接");
                }else if (watchedEvent.getState() == Watcher.Event.KeeperState.Expired){
                    System.out.println("会话超时");
                }else if (watchedEvent.getState() == Watcher.Event.KeeperState.AuthFailed){
                    System.out.println("认证失败");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getUniqueID()  {
        String path ="";
        //创建临时有序节点
        try {
            path = zooKeeper.create(defaultPath,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return path.substring(8);
    }

    @Test
    public void test() throws Exception {
        ZKUniqueID zkUniqueID = new ZKUniqueID();
        for(int i=0 ;i<3;i++){
            System.out.println(zkUniqueID.getUniqueID());
        }
    }


}
