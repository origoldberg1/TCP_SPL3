package bgu.spl.net.impl.tftp;

public class BCAST{ 
  
    private byte [] bcastMsg;

    public BCAST(byte [] fileName, byte deletedOrAdded){
        bcastMsg= new byte [fileName.length+4];
        bcastMsg[0]=(byte)0;
        bcastMsg[1]=(byte)9;
        bcastMsg[2]=(byte)deletedOrAdded;
        bcastMsg[bcastMsg.length-1]=(byte)0;
        int indent = 3;
        for(int i = 0; i < fileName.length; i++)//filling the file Name was deleted or added
        {
            bcastMsg[i + indent] = fileName[i]; //fileName starts in third cell according to BCAST Packet format
        }
    }

    public byte [] getBcastMsg(){
        return bcastMsg;
    }
}

    
    