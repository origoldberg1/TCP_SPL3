package bgu.spl.net.impl.tftp;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import bgu.spl.net.srv.BlockingConnectionHandler;
public class LOGRQ implements Command<byte[]> 
{
    private boolean errorFound(byte[] arg, TftpProtocol protocol, TftpConnections connectionsObject)
    {
        //error 7- user already logged in
        if(protocol.getUserName()!=null)
        {
            connectionsObject.send(protocol.getId(), new ERROR(7).getError());
            return true;
        }
        byte [] bytesUserName= new byte[arg.length-2];
        final int INDENT = 2;
        for(int i = 0; i < bytesUserName.length; i++)
        {
            bytesUserName[i]=arg[i+INDENT];
        }
        String userName = new String(bytesUserName, StandardCharsets.UTF_8);
        if(connectionsObject.isExistByUserName(userName)){// means this userName is already logged-in
            connectionsObject.send(protocol.getId(), new ERROR(7).getError());
            return true;
        }
        return false;
    }   

    @Override
    public void execute(byte[] arg, TftpProtocol protocol, TftpConnections connectionsObject) 
    {
        if(!errorFound(arg, protocol, connectionsObject))
        {
            byte [] bytesUserName= new byte[arg.length-2];
            final int INDENT = 2;
            for(int i = 0; i < bytesUserName.length; i++)
            {
                bytesUserName[i]=arg[i+INDENT];
            }
            String userName = new String(bytesUserName, StandardCharsets.UTF_8);
            protocol.setUserName(userName);
            connectionsObject.send(protocol.getId(), new ACK(new byte[]{0,0}).getAck());
        }
    }
}
    

