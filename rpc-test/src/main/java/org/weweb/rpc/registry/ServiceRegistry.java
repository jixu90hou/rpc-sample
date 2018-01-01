package org.weweb.rpc.registry;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class ServiceRegistry {
    public void registry(String serviceName, String requestUrl) throws Exception {
        String samplePath = "/sample";
        //将数据写入到注册中心中
        String host = "192.168.1.111:";
        host="localhost:";
        int port = 2181;
        int timeout = 5000;
        final CountDownLatch latch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(host + port, timeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                latch.countDown();
                System.out.println("handle " + event.getType());
            }
        });
        latch.await();
        String path=samplePath + "/" + serviceName;
        Stat stat=zooKeeper.exists(path,true);
        if(stat==null){
            zooKeeper.create(path, requestUrl.getBytes()
                    , ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

       // zooKeeper.setData(samplePath, serviceName.getBytes(), 0);
    }

    public static void main(String[] args) throws Exception {
        ServiceRegistry serviceRegistry = new ServiceRegistry();
        String serviceName="org.weweb.service.HelloService";
        serviceRegistry.registry(serviceName,"127.0.0.1:8080");
    }
}
