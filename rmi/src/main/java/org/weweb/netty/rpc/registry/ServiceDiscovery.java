package org.weweb.netty.rpc.registry;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class ServiceDiscovery {
    public String discovery(String serviceName) throws Exception {
        String samplePath = "/sample";
        //将数据写入到注册中心中
        String host = "192.168.1.111:";
        System.out.println("------------------15");
        int port = 2181;
        int timeout = 5000;
        final CountDownLatch latch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(host + port, timeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    latch.countDown();
                    System.out.println("handle " + event.getType());
                }
            }

        });
        latch.await();
        String path=samplePath + "/" + serviceName;
        Stat stat = zooKeeper.exists(path, true);
        byte[] data=zooKeeper.getData(path,true,stat);
        return new String(data);
    }

    public static void main(String[] args) throws Exception {
        ServiceDiscovery serviceDiscovery=new ServiceDiscovery();
        String serviceName="org.weweb.service.HelloService";
        String requestUrl=serviceDiscovery.discovery(serviceName);
        System.out.println("requestUrl:"+requestUrl);
    }
}
