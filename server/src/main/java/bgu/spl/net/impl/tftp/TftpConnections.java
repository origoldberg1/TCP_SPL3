package bgu.spl.net.impl.tftp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

public class TftpConnections implements Connections<byte[]>{
    ConcurrentHashMap<Integer, BlockingConnectionHandler<byte[]>> connections = new ConcurrentHashMap<>(); //we think integer is the id and the other one is the connectionHandler

    @Override
    public synchronized boolean connect(int connectionId, BlockingConnectionHandler<byte[]> handler) {
        connections.put(connectionId, handler);
        return true;
    }

    @Override
    public synchronized boolean send(int connectionId, byte[] msg)  {
        ConnectionHandler<byte[]> handler = connections.get(connectionId);
        if(handler == null){
            return false;
        }
        handler.send(msg);
        return true;      
    }

    @Override
    public synchronized void disconnect(int connectionId){ 
        BlockingConnectionHandler<byte[]> handlerToDisc=connections.get(connectionId);
        connections.remove(connectionId); //"remove" client from logged-in list
        ((TftpProtocol)handlerToDisc.getProtocol()).setShouldTerminate(); //in order to finish procees gracefully
        handlerToDisc.disconnect(); //changes connected to false;
    }

    public synchronized boolean isExistByUserName(String userName){ //we add this method in order to check if this userName is already connected
        for(Map.Entry<Integer, BlockingConnectionHandler<byte[]>> ch: connections.entrySet()){
            TftpProtocol protocol=(TftpProtocol)ch.getValue().getProtocol();
            if(protocol.getUserName() != null &&protocol.getUserName().equals(userName))
            {
                  return true;                
            }
        }
        return false;
    }

    public synchronized boolean isExistById(int id){ //we add this method in order to check if this userName is already connected
        return connections.containsKey(id);
    }

    public synchronized ConcurrentHashMap<Integer, BlockingConnectionHandler<byte[]>> getConnectionsHash(){
        return connections;
    }

    public synchronized ConcurrentHashMap<Integer, BlockingConnectionHandler<byte[]>> getCopyHashMap()
    {
        return new ConcurrentHashMap<>(connections);
    }

}