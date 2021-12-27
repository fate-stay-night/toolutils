package xyz.vimtool.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

/**
 * @author zhangzheng
 * @version 1.0.0
 * @date 2021/1/12
 */
public interface WorldClock extends Remote {

    LocalDateTime getLocalDateTime(String zoneId) throws RemoteException;
}
