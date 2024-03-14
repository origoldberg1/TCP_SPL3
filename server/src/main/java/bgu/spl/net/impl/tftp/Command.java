package bgu.spl.net.impl.tftp;
import bgu.spl.net.srv.BlockingConnectionHandler;
public interface Command<T>{

    void execute(T arg, TftpProtocol protocol, TftpConnections connectionsObject);
}