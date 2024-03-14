package bgu.spl.net.impl.tftp;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import bgu.spl.net.srv.BlockingConnectionHandler;

public class DIRQ implements Command<byte[]> {
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
        if(!errorFound(arg, protocol, connectionsObject))
        {
            File folder = new File("Files");

            File[] files = folder.listFiles();
            //creating list with file names in byte (0 byte between each of them)
            List<Byte> bytesNameFiles = new ArrayList<>();
            for (File file: files) //passing each file
            {
                String fileName=file.getName();
                for(byte b: fileName.getBytes()) //passing each byte in fileName
                {
                    bytesNameFiles.add(b);   
                }
                bytesNameFiles.add((byte)0x00);
            }
            bytesNameFiles.remove(bytesNameFiles.size() - 1);
            //converting bytesNameFiles list to array
            byte [] data= Util.convertListToArr(bytesNameFiles);
            //creating the data to send
            HoldsDataToSend dataToSend= new HoldsDataToSend(connectionsObject, data, protocol);
            protocol.setHoldsDataToSend(dataToSend);
            dataToSend.sendPacket(new byte[] {0,4,0,0});

        }

    }
    
}