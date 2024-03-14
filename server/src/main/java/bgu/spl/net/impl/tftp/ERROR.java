package bgu.spl.net.impl.tftp;
import java.io.UnsupportedEncodingException;

public class ERROR 
{
  final int INDENT=4;
  private final byte [][] msgList;
  private byte [] errorInByte;

    public ERROR(int value)
    {
        msgList = new byte[8][];
        try{
          msgList[0] = "Not defined".getBytes("UTF-8");
          msgList[1] = "File not found".getBytes("UTF-8");
          msgList[2] = "Access violation – File cannot be written, read or deleted".getBytes("UTF-8");
          msgList[3] = "Disk full or allocation exceeded – No room in disk".getBytes("UTF-8");
          msgList[4] = "Illegal TFTP operation".getBytes("UTF-8");
          msgList[5] = "File already exists".getBytes("UTF-8");
          msgList[6] = "User not logged in".getBytes("UTF-8");
          msgList[7] = "User already logged in".getBytes("UTF-8");
    }catch(UnsupportedEncodingException e){}

        //Building the apropiate error
        errorInByte= new byte [msgList[value].length+5];
        //filling opcode field
        byte [] opcodeField=Util.intToTwoByte(5);
        errorInByte[0]=opcodeField[0];
        errorInByte[1]=opcodeField[1];
        //filling errorCode field
        byte [] errorCodeField=Util.intToTwoByte(value);      
        errorInByte[2]=errorCodeField[0];
        errorInByte[3]=errorCodeField[1];
        //filling last byte by zero
        errorInByte[errorInByte.length-1]=(byte)0x00;
        //filling ErrMsg field
        for(int i=0; i<msgList[value].length; i++)
        {
             errorInByte[i+INDENT]=msgList[value][i];
        }

    }

    public byte [] getError()
    {
      return errorInByte;
    }
}

    
    