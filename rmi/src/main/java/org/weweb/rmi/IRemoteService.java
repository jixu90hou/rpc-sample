package org.weweb.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IRemoteService extends Remote{
    List<UserInfo> queryAllUserInfo() throws RemoteException;
}
