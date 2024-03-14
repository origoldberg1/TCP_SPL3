package bgu.spl.net.impl.tftp;
import bgu.spl.net.srv.BlockingConnectionHandler;
public class DISC implements Command<byte[]> {

    private boolean errorFound(byte[] arg, TftpProtocol protocol, TftpConnections connectionsObject)
    {
        //error 6- user not logged in
        if(arg[1] != 7 && protocol.getUserName()==null){
            connectionsObject.send(protocol.getId(), new ERROR(6).getError());
            return true;
        }
        return false;
    }

    @Override
    public void execute(byte[] arg, TftpProtocol protocol, TftpConnections connectionsObject) 
    {
        if(!errorFound(arg, protocol, connectionsObject)){
            connectionsObject.send(protocol.getId(), new ACK(new byte[]{0,0}).getAck());
        }
        connectionsObject.disconnect(protocol.getId());
        
    }
    
}