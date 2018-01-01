package org.weweb.rpc.registry;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZookeeperRegistry {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        String host = "192.168.1.111:";
        int port = 2181;
        int timeout = 5000;
        final CountDownLatch latch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(host + port, timeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                latch.countDown();
                System.out.println("handle" + event.getType());
            }
        });
        String path = "/test";
        String value = "zhangsan";
        latch.await();
        Stat stat = zooKeeper.exists(path, true);
        if (stat == null) {
            zooKeeper.create(path, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } else {
            final String rootPath = "/test";
            List<String> strings = zooKeeper.getChildren(rootPath, true);
            for (String string : strings) {
                zooKeeper.delete(rootPath + "/" + string, -1);
            }
            zooKeeper.delete(rootPath, -1);
        }
        byte[] bytes = zooKeeper.getData(path, true, stat);
        String result = new String(bytes);
        System.out.println(result);
    }
}
