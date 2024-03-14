package bgu.spl.net.impl.tftp;

import java.io.IOException;
import java.io.OutputStream;

import bgu.spl.net.api.Command;

public class DIRQ implements Command{
    OutputStream outputStream;
    CurrentCommand currentCommand;
    
    public DIRQ(OutputStream outputStream, CurrentCommand currentCommand) {
        this.outputStream = outputStream;
        this.currentCommand = currentCommand;
    }

    @Override
    public void execute() {
        currentCommand.setReceiveData(new ReceiveData());
        currentCommand.setState(STATE.DIRQ);
        try {
            outputStream.write(new byte[]{0,6});
            outputStream.flush();
        } catch (IOException e) {}
    }

}
