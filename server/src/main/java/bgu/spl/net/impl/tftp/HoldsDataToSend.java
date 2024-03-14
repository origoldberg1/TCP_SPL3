package bgu.spl.net.impl.tftp;
import java.util.ArrayList;
import java.util.List;
import bgu.spl.net.srv.BlockingConnectionHandler;

//This class implements sending packets mechanism as a result of RRQ request
public class HoldsDataToSend 
{
    
    private int block;
    private byte [] dataToSend;
    private TftpConnections connectionsObj;
    private boolean sendEmptyPacket;
    private TftpProtocol protocol;
    private final int PACKET_SIZE=512;

    
    public boolean errorFound(byte[] arg)
    {
        // error 6- user not logged in
        if(arg[1] != 7 && protocol.getUserName()==null){ 
            connectionsObj.send(protocol.getId(), new ERROR(6).getError());
            return true;
        }
        return false;   
    }

    public HoldsDataToSend(TftpConnections connectionsObj,byte[] dataToSend, TftpProtocol protocol)
    {
       this.connectionsObj=connectionsObj;
       this.dataToSend=dataToSend;
       sendEmptyPacket=false;
       this.protocol=protocol;  
       this.block = 1;
    }

    public void sendPacket(byte [] message)
    { 
        if(!errorFound(message))
        {
            int firstIndex=(block-1)*PACKET_SIZE;
            int leftBytes=dataToSend.length-(block-1)*PACKET_SIZE;
            if(leftBytes==0){
                sendEmptyPacket();
                leftBytes=-1; 
            }
            if(leftBytes>0){
                List<Byte> byteList = new ArrayList<>();
                byte [] packetArr;
                //filling opcode field
                byte [] opcodeField=Util.intToTwoByte(3);
                byteList.add(opcodeField[0]); 
                byteList.add(opcodeField[1]); 
                //filiing size field
                byte [] sizeField; 
                if(leftBytes<PACKET_SIZE){
                    sizeField=Util.intToTwoByte(leftBytes);

                }
                else{
                    sizeField=Util.intToTwoByte(PACKET_SIZE); 
                }
                byteList.add(sizeField[0]); 
                byteList.add(sizeField[1]);                  
                //filling block field
                byte [] blockField=Util.intToTwoByte(block);
                byteList.add(blockField[0]);
                byteList.add(blockField[1]);
                //filling data field
                for(int k=firstIndex; k<=firstIndex+PACKET_SIZE-1 && k<dataToSend.length; k++) 
                {
                    byteList.add(dataToSend[k]);
                }
                block++;
                packetArr=Util.convertListToArr(byteList);
                connectionsObj.send(protocol.getId(), packetArr); 
            }      
        }
    }

    public void sendEmptyPacket()
    {
        byte [] blockField=Util.intToTwoByte(block);
        byte [] emptyPacket= new byte[] {0,3,0,0,blockField[0], blockField[1]};
        connectionsObj.send(protocol.getId(), emptyPacket); 
    }
}

    
    