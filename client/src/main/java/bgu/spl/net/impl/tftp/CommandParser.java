package bgu.spl.net.impl.tftp;

import java.util.Arrays;

public class CommandParser {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;

    public byte[] bytesToOutput() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'decodeNextByte'");
    }

    public byte[] parse(String input) {
        // TODO Auto-generated method stub
        String[] words = input.split(" ");
        pushByte((byte)0);
        byte opcode = Util.getOpcodeValue(words[0]);
        pushByte(opcode);
        for (int i = 1; i < words.length; i++) {
            for (Byte nextByte : words[i].getBytes()) {
                pushByte(nextByte);
            }
            if(i != words.length - 1){
                for (Byte nextByte : (" ").getBytes()) {
                    pushByte(nextByte);
                }
            }
        }
        if(addZero(opcode)) {
            pushByte((byte)0);
        }
        return trim(bytes);
    }
    
    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private byte[] trim(byte[] bytes){
        byte[] res = new byte[len];
        for (int i = 0; i < res.length; i++) {
            res[i] = bytes[i];
        }
        return res;
    }

    private boolean addZero(byte opcode){
        return (opcode == 1 || opcode == 2 || opcode == 5 || opcode == 7 || opcode == 8 || opcode == 9);
    }

}
