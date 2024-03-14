package bgu.spl.net.impl.tftp;
import java.math.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import bgu.spl.net.api.MessageEncoderDecoder;

public class SendData {
    String fileName;
    byte[] fileBytes;
    final int DEFAULT_PACKET_SIZE = 512;
    int blockNumber = 0;
    OutputStream outputStream;

    
    public SendData(String fileName, OutputStream outputStream){
        this.fileName = fileName;
        try {
            fileBytes = Util.readFile(Paths.get(System.getProperty("user.dir")).resolve(fileName).toString());
        } catch (IOException e) {}
        this.outputStream = outputStream;
    }
        
    public String getFileName() {
        return fileName;
    }

    public byte[] makePacket(){
        if(blockNumber * DEFAULT_PACKET_SIZE > fileBytes.length) {
            return null;
        }
        int indent = blockNumber * DEFAULT_PACKET_SIZE;
        int i = 0;
        byte[] packet = new byte[DEFAULT_PACKET_SIZE];
        while(i < packet.length && (indent + i) < fileBytes.length){
            packet[i] = fileBytes[i + indent];
            i++;
        }
        if(i < DEFAULT_PACKET_SIZE){
            return(trim(packet, i));
        }
        return packet;
    }

    public boolean sendPacket(){
        byte[] packet = makePacket();
        if(packet == null) {return false;}
        try {
            outputStream.write(Util.padDataPacket(packet, blockNumber + 1));
            outputStream.flush();
        } catch (IOException e) {}
        blockNumber ++;
        return true;
    }


    private byte[] trim(byte[] packet, int len){
        byte[] tmp = new byte[len];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = packet[i];
        }
        return tmp;
    }

}
