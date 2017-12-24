package org.weweb.rmi.registry;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class SingleRegistry {
    private static final Object WAITOBJECT=new Object();

    public static void main(String[] args) throws RemoteException, InterruptedException {
        LocateRegistry.createRegistry(1099);
        synchronized (WAITOBJECT){
            WAITOBJECT.wait();
        }
    }
}
