// package bgu.spl.net.impl.echo;

// import bgu.spl.net.api.MessagingProtocol;
// import java.time.LocalDateTime;

// public class EchoProtocol implements MessagingProtocol<String> {

//     private boolean shouldTerminate = false;

//     public EchoProtocol() { //added 
//         shouldTerminate = false;
//         System.out.println("connected");
//     }

//     @Override
//     public String process(String msg) {
//         shouldTerminate = msg.startsWith("bye");
//         System.out.println("[" + LocalDateTime.now() + "]: " + msg);
//         return createEcho(msg);
//     }

//     private String createEcho(String message) {
//         String echoPart = message.substring(Math.max(message.length() - 2, 0), message.length());
//         return message + " .. " + echoPart + " .. " + echoPart + " ..";
//     }

//     @Override
//     public boolean shouldTerminate() {
//         return shouldTerminate;
//     }
// }
