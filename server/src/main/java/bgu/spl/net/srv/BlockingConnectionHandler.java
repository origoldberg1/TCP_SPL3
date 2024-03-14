package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.tftp.TftpConnections;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final MessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private final Connections<T> connections;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    static volatile int connectionCounter = 0; 
    private volatile int id;


    public BlockingConnectionHandler(Socket sock, Connections<T> connections, MessageEncoderDecoder<T> reader, MessagingProtocol<T> protocol) {
        this.sock = sock;
        this.connections = connections;
        this.encdec = reader;
        this.protocol = protocol;
        this.id = connectionCounter ++ ; 
        this.connections.connect(id, this);

    }

    @Override
    public void run() {
        try (Socket sock = this.sock) {
            this.protocol.start(id, connections); //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            // out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) { //full packet acccepted
                    protocol.process(nextMessage);
                }
            }
        } catch (IOException ex) { 
            ex.printStackTrace();
        }
    }

    @Override
    public void close(){
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void send(T msg){ //implement if needed
        try{
            out = new BufferedOutputStream(sock.getOutputStream());
            byte[] msgEncoded = encdec.encode(msg);
            out.write(msgEncoded);
            out.flush();
        }catch(IOException e){}
    }

    public Connections<T> getConnectionsObject(){ //we add this method
        return connections;
    }
    public void disconnect(){
        connected=false;
    }

    public MessagingProtocol<T> getProtocol(){ //we add that
        return protocol;
    }
}
