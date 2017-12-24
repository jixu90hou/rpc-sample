package org.weweb.rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RemoteUnicastMain {
    public static void main(String[] args) throws Exception {
        LocateRegistry.createRegistry(1099);
        RemoteUnicastServiceImpl remoteUnicastService=new RemoteUnicastServiceImpl();
        String rmiUrl="rmi://127.0.0.1:1099/queryAllUserInfo";
        Naming.rebind(rmiUrl,remoteUnicastService);
    }
}
