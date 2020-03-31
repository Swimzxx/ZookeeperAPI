package com.z.zookeeperapi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
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
 * @Description: 新增节点
 */
public class ZKCreate {

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
        // arg3:权限列表 world:anyone:cdrwa
        // arg4:节点类型 持久化节点
        zooKeeper.create("/node1", "data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
    }

    @Test
    public void zkCreate2() throws KeeperException, InterruptedException {
        // arg1:节点的路径
        // arg2:节点的数据
        // arg3:权限列表 Ids.READ_ACL_UNSAFE world:anyone:r
        // arg4:节点类型 持久化节点
        zooKeeper.create("/node1", "data".getBytes(), ZooDefs.Ids.READ_ACL_UNSAFE,CreateMode.PERSISTENT);
    }

    @Test
    public void zkCreate3() throws KeeperException, InterruptedException {
        List<ACL> acls = new ArrayList<ACL>();
        //授权模式和对象
        Id id = new Id("world", "anyone");
        acls.add(new ACL(ZooDefs.Perms.READ, id));
        acls.add(new ACL(ZooDefs.Perms.CREATE, id));
        zooKeeper.create("/node1", "data".getBytes(), acls,CreateMode.PERSISTENT);
    }

    @Test
    public void zkCreate4() throws KeeperException, InterruptedException {
        //auth授权
        zooKeeper.addAuthInfo("digest", "username:123456".getBytes());
        zooKeeper.create("/node1", "data".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL,CreateMode.PERSISTENT);
    }

    @Test
    public void zkCreate5() throws Exception {
        // 持久化顺序节点
        // Ids.OPEN_ACL_UNSAFE world:anyone:cdrwa
        String result = zooKeeper.create("/create/node1", "node1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println(result);
    }


}
