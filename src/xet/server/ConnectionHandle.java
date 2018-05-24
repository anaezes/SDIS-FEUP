package xet.server;

import javax.net.ssl.SSLSocket;

public class ConnectionHandle implements Runnable{
    private SSLSocket sslSocket;

    public ConnectionHandle(SSLSocket socket) {
        sslSocket = socket;
    }

    public void run(){
        System.out.println("Pum!");
    }
}
