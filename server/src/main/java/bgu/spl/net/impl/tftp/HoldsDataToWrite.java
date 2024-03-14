package bgu.spl.net.impl.tftp;
import bgu.spl.net.srv.BlockingConnectionHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//this class implements all about writing to files- after WRQ opcode
public class HoldsDataToWrite implements Command<byte[]> {
    
    List<Byte> byteList;
    String fileToWritePath;
    byte [] bcastMsg;

    public HoldsDataToWrite(String fileToWritePath, byte [] bytesFileName)
    { 
        this.byteList= new ArrayList<>();
        this.fileToWritePath=fileToWritePath;
        BCAST bcast = new BCAST(bytesFileName, (byte)0x01);
        bcastMsg= bcast.getBcastMsg();
        
    }
    
    public String getFileToWritePath()
    {
        return fileToWritePath;
    }


    public boolean errorFound(byte[] arg, TftpProtocol protocol, TftpConnections connectionsObject)
    {
        Path filePath = Paths.get(fileToWritePath);
        //error 1- File not found 
        if(!Files.exists(filePath)) {
              connectionsObject.send(protocol.getId(),new ERROR (1).getError());
              return true;
        }
        //error 2- access violation 
        if(!Files.isWritable(filePath)){
            connectionsObject.send(protocol.getId(),new ERROR (2).getError());
            return true;
        }
        //error 4- illegal TFTP operation- unknown opcode
        if((arg[1]>7 || arg[1]<1 ||arg.length <= 1)){ 
            connectionsObject.send(protocol.getId(),new ERROR (4).getError());
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
    public void execute(byte[] packet, TftpProtocol protocol, TftpConnections connectionsObject) 
    {
        if(!errorFound(packet, protocol, connectionsObject))
        {  
            for (int i=0; i<packet.length-6; i++) {
                byteList.add(packet[i+6]); //data information starts at sixth cell in Data Opcode
            }
            if(packet.length-6<512){ //means this is the last packet to write
                //writes to file
                byte [] buffer= Util.convertListToArr(byteList);
                try{ Util.writeFile(fileToWritePath, buffer);}
                catch(IOException e){}
                protocol.setDataToWrite(null);
                //sending ACK
                byte [] ackMsg=new ACK(new byte[]{packet[4],packet[5]}).getAck();
                connectionsObject.send(protocol.getId(),ackMsg);
                //Sending broadccast to all loggedin users
                sendBroadcast(packet, protocol, connectionsObject);
            }
            else{ //means this is not the last packet to write
                byte [] ackMsg=new ACK(new byte[]{packet[4],packet[5]}).getAck();
                connectionsObject.send(protocol.getId(),ackMsg);
            }
        }  
    }

    private void sendBroadcast(byte[] packet, TftpProtocol protocol, TftpConnections connectionsObject)
    {
        ConcurrentHashMap <Integer, BlockingConnectionHandler<byte[]>> connectionsHash =connectionsObject.getCopyHashMap();                
        for(Map.Entry<Integer, BlockingConnectionHandler<byte[]>> entry : connectionsHash.entrySet())
        {
            TftpProtocol chProtocol=(TftpProtocol)entry.getValue().getProtocol();
            String userName=chProtocol.getUserName();
            if(userName!=null) //means this CH is logged in
            {
                int id=chProtocol.getId();
                connectionsObject.send(id,this.bcastMsg);
            }
        }
    }

}