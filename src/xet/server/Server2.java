package xet.server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server2 {

    //private int port;
    //private String host;

    static final int port = 8000;

    private SSLSocket sslSocket;
    private SSLServerSocket sslServerSocket;
    private SSLSocketFactory sslSocketFactory;
    private SSLServerSocketFactory sslServerSocketFactory;

    public static void main(String[] args) {

     /*   if (args.length < 2) {
            System.out.println("Usage: java Server <port> <host>");
            return;
        }*/

        Server2 server = new Server2();
    }

    public Server2() {

        System.setProperty("javax.net.ssl.keyStoreType","JKS");
        System.setProperty("javax.net.ssl.keyStore", System.getProperty("user.dir") + File.separator + "keyStore" + File.separator + "xet.server.keys");
        System.setProperty("javax.net.ssl.keyStorePassword","123456");

        sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            ServerSocket sslServerSocket = sslServerSocketFactory.createServerSocket(port);
            System.out.println("SSL ServerSocket started");
            System.out.println(sslServerSocket.toString());

            Socket socket = sslServerSocket.accept();
            System.out.println("ServerSocket accepted");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            try (BufferedReader bufferedReader =
                         new BufferedReader(
                                 new InputStreamReader(socket.getInputStream()))) {
                String line;
                while((line = bufferedReader.readLine()) != null){
                    System.out.println(line);
                    out.println(line);
                }
            }
            System.out.println("Closed");

        } catch (IOException ex) {
            Logger.getLogger(Server2.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}
