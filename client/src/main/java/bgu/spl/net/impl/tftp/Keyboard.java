package bgu.spl.net.impl.tftp;

import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import bgu.spl.net.api.Command;

public class Keyboard implements Runnable{
    volatile boolean terminate = false;
    Util.State state = Util.State.Simple; 
    CommandParser commandParser;
    OutputStream outputStream;
    CurrentCommand currentCommand;
    Object waitOnObject;


    public Keyboard(CommandParser commandParser, OutputStream outputStream, CurrentCommand currentCommand, Object waitOnObject) {
        this.commandParser = commandParser;
        this.outputStream = outputStream;
        this.currentCommand = currentCommand;
        this.waitOnObject = waitOnObject;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        Scanner scanner = new Scanner(System.in);
        while (! terminate) {
            if (scanner.hasNextLine()) {
                String userInput = scanner.nextLine();
                Command command = null;
                int opcode = Util.getOpcodeValue(userInput.split(" ")[0]);
                switch (opcode){
                    case 1:
                        command = new RRQ(outputStream, Util.getFileName(userInput), currentCommand);
                        break;
                    case 2:
                        command = new WRQ(outputStream, Util.getFileName(userInput), currentCommand); 
                        break;
                    case 6:
                        command = new DIRQ(outputStream, currentCommand);
                        break;
                    case 7:
                        command = new LOGRQ(outputStream, userInput.split(" ")[1], currentCommand);
                        break;
                    case 8:
                        command = new DELRQ(outputStream, Util.getFileName(userInput), currentCommand);
                        break;
                    case 10:
                        command = new DISC(outputStream, currentCommand);
                        terminate = true;
                        break;
                    default:
                        currentCommand.resetFields();
                }
                if(command != null){
                    command.execute();
                }
                if(!terminate && !currentCommand.getState().equals(STATE.Unoccupied)){
                    synchronized(waitOnObject){
                        try {
                            waitOnObject.wait();
                        } catch (InterruptedException e) {};
                    }
                }
            }
        }
        scanner.close();

    }

}
