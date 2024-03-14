package bgu.spl.net.impl.tftp;

import java.util.Arrays;

import bgu.spl.net.api.MessageEncoderDecoder;

public class TftpEncoderDecoder implements MessageEncoderDecoder<byte[]> {
    //TODO: Implement here the TFTP encoder and decoder

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private int size = -1;

    @Override
    public byte[] decodeNextByte(byte nextByte) {
        byte[] res = null;
        if(len >= 2){
            switch (bytes[1]) {
                case 1: case 2: case 7: case 8:
                    if(decodeNextByteUntilZero(nextByte)) {res = bytes;}
                    break;
                case 3:
                    if(decodeNextByteKnownPacketLength(nextByte, 6)) {res = bytes;}
                    break;
                case 4:
                    size = 4;
                    if(decodeNextByteFixedSize(nextByte)) {res = bytes;}
                    break;
                case 5:
                    if(decodeNextByteError(nextByte)) {res = bytes;}
                    break;
                 case 9:
                 if(decodeNextByteBcast(nextByte)) {res = bytes;}
                    break;
                default:
                    break; //TODO: indicate error - non existing opcode
            }
        }
        else{
            pushByte(nextByte);
            if(len == 2 && (bytes[1] == 6 || bytes[1] == 10)){
                res = bytes;
            }
        }
        if(res != null) {
            byte[] trimed = trim();
            reset();
            return trimed;
        }
        return null;
    }

    
    @Override
    public byte[] encode(byte[] message) {
        return message;
    }
    
    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }
    
    private boolean decodeNextByteUntilZero(byte nextByte){
        if(nextByte == 0) {return true;}
        pushByte(nextByte);
        return false;
    }
    
    private boolean decodeNextByteKnownPacketLength(byte nextByte, int opcodeLength){
        if(len == 4){
            size =  opcodeLength + Util.twoByteToInt(new byte[]{bytes[2], bytes[3]});
        }
        pushByte(nextByte);
        return size == len;
    }

    private boolean decodeNextByteFixedSize(byte nextByte){
        pushByte(nextByte);
        return size == len;        
    }

    private boolean decodeNextByteBcast(byte nextByte){
        if(len >= 3 && nextByte == 0) {return true;}
        pushByte(nextByte);
        return false;
    }

    private boolean decodeNextByteError(byte nextByte){
        if(len >= 4 && nextByte == 0) {return true;}
        pushByte(nextByte);
        return false;
    }

    private byte[] trim(){
        byte[] res = new byte[len];
        for (int i = 0; i < res.length; i++) {
            res[i] = bytes[i];
        }
        return res;
    }

    private void reset(){
        len = 0;
        size = -1;
        bytes = new byte[1 << 10];
    }

}