package org.weweb.rmi.registry;

import org.weweb.rmi.IRemoteService;
import org.weweb.rmi.UserInfo;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class RmiRemoteClient {
    private static Object WAITOBJECT=new Object();

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException, InterruptedException {
        String rmiUrl="rmi://192.168.1.111/queryAllUserInfo";
        IRemoteService remoteService= (IRemoteService) Naming.lookup(rmiUrl);
        //保证连续请求，好观察SingleRegistry关闭后的请求情况
        for(;;){
            List<UserInfo> users=remoteService.queryAllUserInfo();
            System.out.println("users.size()="+users.size());
            synchronized (RmiRemoteClient.WAITOBJECT){
                RmiRemoteClient.WAITOBJECT.wait(1000);
            }

        }
    }
}
