package bgu.spl.net.impl.tftp;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.net.srv.BlockingConnectionHandler;

public class DELRQ implements Command<byte[]> 
{
    public boolean errorFound(byte[] arg, TftpProtocol protocol, TftpConnections connectionsObject)
    {
        //error 1- File not found
        byte [] bytesFileName= new byte[arg.length-2];//Acording to Ori we get args without the last byte 
        final int INDENT = 2;
        for(int i = 0; i < bytesFileName.length; i++)
        {
            bytesFileName[i]=arg[i+INDENT];
        }
        String fileName = new String(bytesFileName, StandardCharsets.UTF_8);
        Path filePath = Paths.get("Files/"+fileName);
        if(!Files.exists(filePath))
        {
            connectionsObject.send(protocol.getId() ,new ERROR(1).getError());
            return true;
        }
        //error 2- access violation 
        if(!Files.isWritable(filePath))
        {
            connectionsObject.send(protocol.getId(),new ERROR (2).getError());
            return true;
        }
        // error 6- user not logged in
        if(arg[1] != 7 && protocol.getUserName()==null){ 
            connectionsObject.send(protocol.getId(), new ERROR(6).getError());
            return true;
            }
        return false;   
    }

    @Override
    public void execute(byte[] arg, TftpProtocol protocol, TftpConnections connectionsObject) 
    {
        
        if(!errorFound(arg,protocol,connectionsObject))
        {
            byte [] bytesFileName= new byte[arg.length-2];//Acording to Ori we get args without the last byte 
            final int INDENT = 2;
            for(int i = 0; i < bytesFileName.length; i++)
            {
                bytesFileName[i]=arg[i+INDENT];
            }
            String fileName = new String(bytesFileName, StandardCharsets.UTF_8);
            try{
                //delete the file
                Path filePath = Paths.get("Files/"+fileName);
                Files.delete(filePath); 
                
                connectionsObject.send(protocol.getId(),new ACK(new byte[]{0,0}).getAck());
                
                //starting broadcast
                byte [] bcastMsg= new BCAST(bytesFileName, (byte)0x00).getBcastMsg();
                ConcurrentHashMap <Integer, BlockingConnectionHandler<byte[]>> connectionsHash =connectionsObject.getCopyHashMap();                
                for(Map.Entry<Integer, BlockingConnectionHandler<byte[]>> entry : connectionsHash.entrySet())
                {
                    TftpProtocol chProtocol=(TftpProtocol)entry.getValue().getProtocol();
                    if(chProtocol.getUserName()!=null) //means this CH is logged in
                    {
                        int id=chProtocol.getId();
                        connectionsObject.send(id,bcastMsg);
                    }

                    if(protocol.getFileToWritePath()!=null && protocol.getFileToWritePath()=="Files/"+fileName) 
                    {
                        protocol.setDataToWrite(null);
                    }
                }
                //finishing broadCast
            } catch(IOException e){connectionsObject.send(protocol.getId() ,new ERROR(1).getError());}
        }
    }   
}