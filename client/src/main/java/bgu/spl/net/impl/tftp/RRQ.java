package bgu.spl.net.impl.tftp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

import bgu.spl.net.api.Command;

public class RRQ implements Command{
    OutputStream outputStream;
    String fileName;
    CurrentCommand currentCommand;

    public RRQ(OutputStream outputStream, String fileName, CurrentCommand currentCommand) {
        this.outputStream = outputStream;
        this.fileName = fileName;
        this.currentCommand = currentCommand;
    }
    @Override
    public void execute() {
        File file = new File(Paths.get(System.getProperty("user.dir")).resolve(fileName).toString());
        if(file.exists()){ //file already exists
            System.out.println("file already exists");
            currentCommand.resetFields();
            return;
        }

        try {
            file.createNewFile();
        } catch (IOException e) {}
        
        currentCommand.setState(STATE.RRQ);
        currentCommand.setReceiveData(new ReceiveData(fileName));
        
        byte[] fileNameBytes = fileName.getBytes();
        byte[] packet = Util.padPacketEndZero((byte)1, fileNameBytes);
        try {
            outputStream.write(packet);
            outputStream.flush();
        } catch (IOException e) {}
    }

}
