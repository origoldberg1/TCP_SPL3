package bgu.spl.net.impl.tftp;

import java.io.IOException;
import java.io.OutputStream;

import bgu.spl.net.api.Command;

public class DELRQ implements Command{
    OutputStream outputStream;
    String fileName;
    CurrentCommand currentCommand;
    

    public DELRQ(OutputStream outputStream, String fileName, CurrentCommand currentCommand) {
        this.outputStream = outputStream;
        this.fileName = fileName;
        this.currentCommand = currentCommand;
    }

    @Override
    public void execute() {
        byte[] fileNameBytes = fileName.getBytes(); 
        currentCommand.setState(STATE.DELRQ);
        byte[] msg = Util.padPacketEndZero((byte)8, fileNameBytes);
        try {
            outputStream.write(msg);
            outputStream.flush();
        } catch (IOException e) {}
    }

}
