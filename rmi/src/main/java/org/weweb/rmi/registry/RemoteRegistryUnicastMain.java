package org.weweb.rmi.registry;

import org.weweb.rmi.RemoteUnicastServiceImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteRegistryUnicastMain {
    public static void main(String[] args) throws RemoteException {
        String registryUrl="192.168.1.111";
        Registry registry= LocateRegistry.getRegistry(registryUrl,1099);
        //以下是向RMI注册表（绑定/重绑定）RMI Server的Stub
        //在RMI注册表的JVM-classpath下，一定要有这个RMI Server的Stub
        RemoteUnicastServiceImpl remoteUnicastService=new RemoteUnicastServiceImpl();
        //注册表存在192.168.1.111这个IP上
        registry.rebind("queryAllUserInfo",remoteUnicastService);
    }
}

