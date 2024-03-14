package bgu.spl.net.impl.tftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Util {

    public static byte[] intToTwoByte(int a){
        return new byte []{(byte)( a >> 8) , ( byte )( a & 0xff ) };
    }
    
    public static int twoByteToInt(byte [] bytes){
        return (short) (((short) bytes[0]) << 8 | (short) (bytes[1]) & 0x00ff);
        //return ( int ) ((( int ) bytes [0]) << 8 | ( int ) ( bytes [1]) );
    }

    public static byte[] readFile(String fileFullPath) throws IOException {
        FileInputStream fis = new FileInputStream(fileFullPath);
        //extracting file size (in bytes)
        long fileSize= new File(fileFullPath).length();
        //reads all data to buffer
        byte[] fileBytes = new byte[(int) fileSize];
        fis.read(fileBytes);
        fis.close();
        return fileBytes;
    }

    public static void writeFile(String fileFullPath, byte[] bytes) throws FileNotFoundException, IOException {

        try{
            FileOutputStream fos = new FileOutputStream(fileFullPath);
            fos.write(bytes);
            fos.close();
        }catch(IOException e){}
    }

    public static byte [] convertListToArr( List<Byte> byteList)
    {
        byte[] byteArray = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) 
        {
            byteArray[i] = byteList.get(i);
        }
        return byteArray;
    }

    public static byte[] padDataPacket(byte[] packet, int blockNumber){
        byte[] res = new byte[packet.length + 6];
        res[0] = 0;
        res[1] = 3;

        byte[] packetSizeBytes = Util.intToTwoByte(packet.length);
        res[2] = packetSizeBytes[0];
        res[3] = packetSizeBytes[1];

        byte[] blockNumberBytes = Util.intToTwoByte(blockNumber);
        res[4] = blockNumberBytes[0];
        res[5] = blockNumberBytes[1];

        for (int i = 0; i < packet.length; i++) {
            res[i + 6] = packet[i];
        }
        return res;
    }
}
