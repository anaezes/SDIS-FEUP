package server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

public class Server {

    private int port;
    private String host;

    private SSLSocket sslSocket;
    private SSLServerSocket sslServerSocket;
    private SSLSocketFactory sslSocketFactory;
    private SSLServerSocketFactory sslServerSocketFactory;

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: java Server <port> <host>");
            return;
        }

        Server server = new Server(Integer.parseInt(args[0]), args[1]);
    }

    public Server(int port, String host) {
        this.port = port;
        this.host = host;

        sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
        } catch (IOException e) {
            System.out.println("Failed to create sslServerSocket");
            e.printStackTrace();
        }

        listening();
    }

    private void listening() {
        while(true){
            try {
                System.out.println("Pim!");

                new ConnectionHandle((SSLSocket) sslServerSocket.accept());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
