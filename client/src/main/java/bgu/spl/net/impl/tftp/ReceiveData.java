package bgu.spl.net.impl.tftp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

public class ReceiveData {
    int blockNumber; 
    byte[] data;
    final int DEFAULT_PACKET_SIZE = 512;
    final String fileName;
     
    
    public ReceiveData() {
        this.blockNumber = 0;
        this.data = new byte[0];
        fileName = null;
    }

    public ReceiveData(String fileName) {
        this.blockNumber = 0;
        this.data = new byte[0];
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean processPacket(byte[] packet){
        packet = extractData(packet);
        int indent = data.length;
        resize(packet.length);
        for (int i = 0; i < packet.length; i++) {
            data[indent + i] = packet[i];
        }
        if(packet.length < DEFAULT_PACKET_SIZE) {
            if(fileName != null){
                try {
                    Util.writeFile(fileName, data);
                } catch (FileNotFoundException e) {} catch (IOException e) {}
                return false;
            }
            else{
                printDIRQData();
                return false;
            }
        }
        blockNumber ++;
        return true;
    }
    
    private void resize(int addSize){
        byte[] tmp = new byte[addSize + data.length];
        for (int i = 0; i < data.length; i++) {
            tmp[i] = data[i];
        }
        data = tmp;
    }

    private byte[] extractData(byte[] packet){
        byte[] res = new byte[packet.length - 6];
        int indent = 6;
        for (int i = 0; i < res.length; i++) {
            res[i] = packet[i+indent];
        }
        return res;
    }

    public void deleteFile() throws IOException {
        Util.deleteFile(Paths.get(System.getProperty("user.dir")).resolve(fileName));
    }

    private void printDIRQData(){
        String [] fileNames = Util.convertDIRQDataToStringArr(data);
        for (String fileName : fileNames) {
            System.out.println(fileName);
        }
    }
}
