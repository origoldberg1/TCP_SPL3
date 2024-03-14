package bgu.spl.net.impl.tftp;

import java.io.IOException;
import java.io.OutputStream;

import bgu.spl.net.api.Command;

public class ACK implements Command{
    OutputStream outputStream;
    int blockNumber;
    byte[] blockNumberBytes;

    public ACK(OutputStream outputStream, int blockNumber) {
        this.outputStream = outputStream;
        this.blockNumber = blockNumber;
    }

    public ACK(OutputStream outputStream, byte[] msg) {
        this.outputStream = outputStream;
        this.blockNumberBytes = new byte[]{msg[4], msg[5]};
    }

    @Override
    public void execute() {
        if(blockNumberBytes == null){
            blockNumberBytes = Util.intToTwoByte(blockNumber);
        }
        try {
            outputStream.write(new byte[]{0,4, blockNumberBytes[0], blockNumberBytes[1]});
            outputStream.flush();
        } catch (IOException e) {}
    }

}
