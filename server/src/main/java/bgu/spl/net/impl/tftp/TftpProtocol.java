package bgu.spl.net.impl.tftp;
import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.Connections;

public class TftpProtocol implements BidiMessagingProtocol <byte[]>  {
    
    private BlockingConnectionHandler <byte[]> handler;
    private TftpConnections connectionsObj;
    private volatile boolean shouldTerminate=false;  
    private HoldsDataToSend dataToSend;
    private int id;
    private volatile String userName;
    private HoldsDataToWrite dataToWrite;
    
    public HoldsDataToSend packetToSend() //we add that
    {
        return dataToSend; 
    }

    public void setDataToWrite(HoldsDataToWrite  dataToWrite) //we add that
    {
        this.dataToWrite=dataToWrite;
    }

    public String getFileToWritePath()
    {
        if(dataToWrite==null)
        {
            return null;
        }
        return dataToWrite.getFileToWritePath();
    }
    
    @Override
    public void start(int id, Connections <byte[]> connections) {
        this.connectionsObj= (TftpConnections) connections;
        this.id=id;
        this.userName=null;
        this.dataToSend=null;
        this.dataToWrite=null;
        dataToWrite=null;
    }


    @Override
    public void process(byte[] message) 
    {
        switch (message[1])
        {
            case 1:
                new RRQ().execute(message, this, connectionsObj);
                break;
            case 2:
                new WRQ().execute(message, this, connectionsObj);
                break;
            case 3:
                if(dataToWrite==null){break;};
                dataToWrite.execute(message, this, connectionsObj);   
                break;
            case 4:
                if(dataToSend==null){break;};
                dataToSend.sendPacket(message); 
                break;                
            case 6:
                new DIRQ().execute(message, this, connectionsObj);
                break;
            case 7:
                new LOGRQ().execute(message, this, connectionsObj);
                break;
            case 8:
                new DELRQ().execute(message, this, connectionsObj);
                break;
            case 10:
                new DISC().execute(message, this, connectionsObj);
                break;
            default:
            connectionsObj.send(getId(),new ERROR (4).getError());
        }
    } 
    

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    //getters 
    public int getId()
    {
        return id;
    }

    public String getUserName()
    {
        return userName;
    }

    public HoldsDataToSend getHoldsDataToSend()
    {
        return dataToSend;
    }

    public HoldsDataToWrite getHoldsDataToWrite()
    {
        return dataToWrite;
    }

    //setters

    public void setShouldTerminate()
    {
        shouldTerminate=true;
    }

    public void setHoldsDataToSend(HoldsDataToSend dataToSend)
    {
        this.dataToSend=dataToSend;
    }

    public void setHoldsDataToWrite(HoldsDataToWrite dataToWrite)
    {
        this.dataToWrite=dataToWrite;
    }

    public void setUserName(String userName)
    {
        this.userName=userName;
    }



}