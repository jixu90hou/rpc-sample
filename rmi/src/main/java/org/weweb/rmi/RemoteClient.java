package org.weweb.rmi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class RemoteClient {
    static{
        BasicConfigurator.configure();
    }
    private static final Log LOG= LogFactory.getLog(RemoteClient.class);

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        String rmiUrl="rmi://192.168.1.111:1099/queryAllUserInfo";
        IRemoteService remoteService= (IRemoteService) Naming.lookup(rmiUrl);
        List<UserInfo> userInfos=remoteService.queryAllUserInfo();
        RemoteClient.LOG.info("users.size()="+userInfos.size());
    }

}
