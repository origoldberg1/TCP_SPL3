package bgu.spl.net.impl.tftp;

import java.io.IOException;
import java.nio.file.Path;

enum STATE{
    RRQ,
    WRQ,
    DIRQ,
    LOGRQ,
    DELRQ,
    DISC,
    Unoccupied
}

public class CurrentCommand {
    private Path filePath;
    private STATE state;
    private SendData sendData;
    private ReceiveData receiveData;
    

    public synchronized Path getFilePath() {
        return filePath;
    }
    
    public synchronized void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
    
    public synchronized STATE getState() {
        return state;
    }
    
    public synchronized void setState(STATE state) {
        this.state = state;
    }

    public synchronized SendData getSendData() {
        return sendData;
    }

    public synchronized void setSendData(SendData sendData) {
        this.sendData = sendData;
    }

    public synchronized ReceiveData getReceiveData() {
        return receiveData;
    }

    public synchronized void setReceiveData(ReceiveData receiveData) {
        this.receiveData = receiveData;
    }

    public synchronized void resetFields(){
        filePath = null;
        state = STATE.Unoccupied;    
        sendData = null;
        receiveData = null;
    }

    public synchronized void deleteRRQFile() throws IOException {
        receiveData.deleteFile();
    }

}
