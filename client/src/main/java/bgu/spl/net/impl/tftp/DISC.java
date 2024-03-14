package bgu.spl.net.impl.tftp;

import java.io.IOException;
import java.io.OutputStream;

import bgu.spl.net.api.Command;

public class DISC implements Command{
    OutputStream outputStream;
    CurrentCommand currentCommand;
   

    public DISC(OutputStream outputStream, CurrentCommand currentCommand) {
        this.outputStream = outputStream;
        this.currentCommand = currentCommand;
    }

    @Override
    public void execute() {
        currentCommand.setState(STATE.DISC);
        try {
            outputStream.write(new byte[]{0,10});
            outputStream.flush();
        } catch (IOException e) {}
    }

}
