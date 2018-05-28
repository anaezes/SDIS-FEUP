package xet.server;
import com.sun.net.httpserver.HttpServer;
import xet.server.handler.HandshakeHandler;
import xet.server.handler.MessageHandler;
import xet.server.handler.RoomHandler;
import xet.server.handler.RoomInvitationHandler;
import xet.server.handler.auth.AuthFacebookHandler;
import xet.server.handler.auth.AuthGuestHandler;
import xet.server.rooms.Room;
import xet.server.rooms.RoomsManager;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * a simple static http xet.server
 */
public class Server {
    public static String SERVER_BASE_URL = "localhost";
    public static int SERVER_PORT = 8000;
    public static String SERVER_URL = "http://" + SERVER_BASE_URL + ":" + SERVER_PORT;

    public final static String URL_HANDSHAKE = "/handshake";
    public final static String URL_ROOM = "/room";
    public final static String URL_MESSAGE = "/message";
    public final static String URL_ROOM_INVITATION = "/room/invitation";
    public final static String URL_AUTH_FACEBOOK = "/auth/fb";
    public final static String URL_AUTH_GUEST = "/auth/guest";

    private int socketPort = 6000;
    private HttpServer server;

    private SSLServerSocketFactory sslServerSocketFactory;

    public Server() throws IOException {

        if (!RoomsManager.Get().load()) {
            RoomsManager.Get().add("general", new Room("general"));
            RoomsManager.Get().add("games", new Room("games"));
        }

        server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);

        System.setProperty("javax.net.ssl.keyStoreType","JKS");
        System.setProperty("javax.net.ssl.keyStore", System.getProperty("user.dir") + File.separator + "keyStore" + File.separator + "server.keys");
        System.setProperty("javax.net.ssl.keyStorePassword","123456");

        sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
    }

    public void start() {
        server.createContext(URL_HANDSHAKE, new HandshakeHandler(this));
        server.createContext(URL_ROOM, new RoomHandler(this));
        server.createContext(URL_MESSAGE, new MessageHandler(this));
        server.createContext(URL_ROOM_INVITATION, new RoomInvitationHandler(this));
        server.createContext(URL_AUTH_FACEBOOK, new AuthFacebookHandler(this));
        server.createContext(URL_AUTH_GUEST, new AuthGuestHandler(this));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public int getSocketPort() {
        return socketPort;
    }

    public void incrementPort() {
        this.socketPort += 1;
    }

    public void makeSSLConnection(String identifier, String room) {

        try {
            ServerSocket sslServerSocket = sslServerSocketFactory.createServerSocket(socketPort);
            System.out.println("SSL ServerSocket started");
            System.out.println(sslServerSocket.toString());

            Socket socket = sslServerSocket.accept();
            System.out.println("ServerSocket accepted");

            Room r = RoomsManager.Get().get(room);
            r.addClientToRoom(identifier, socket);

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

        incrementPort();
    }

    public static String BuildUrl(String url) {
        return Server.SERVER_URL + url;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            SERVER_BASE_URL = args[0];
            SERVER_URL = "http://" + SERVER_BASE_URL + ":" + SERVER_PORT;
        }
        new Server().start();
    }
}