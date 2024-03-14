package bgu.spl.net.impl.tftp;
public class ACK 
{
  private byte [] ackInByte;

    public ACK(byte[] blockNumber)
    {
        ackInByte= new byte[]{(byte)0x00, (byte)0x04, blockNumber[0], blockNumber[1]}; 
    }

    public byte [] getAck()
    {
        return ackInByte;
    }
}

    
    