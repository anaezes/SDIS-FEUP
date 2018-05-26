package xet.server;
import com.sun.net.httpserver.HttpServer;
import xet.room.Room;
import xet.server.handler.*;
import xet.server.handler.auth.AuthFacebookHandler;
import xet.server.handler.auth.AuthGuestHandler;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * a simple static http xet.server
 */
public class Server {
    public final static String SERVER_BASE_URL = "http://localhost";
    public final static int SERVER_PORT = 8000;
    public final static String SERVER_URL = SERVER_BASE_URL + ":" + SERVER_PORT;

    public final static String URL_HANDSHAKE = "/handshake";
    public final static String URL_ROOM = "/room";
    public final static String URL_MESSAGE = "/message";
    public final static String URL_UPDATE = "/update";
    public final static String URL_AUTH_FACEBOOK = "/auth/fb";
    public final static String URL_AUTH_GUEST = "/auth/guest";

    private int socketPort = 6000;
    private HttpServer server;

    private final HashMap<String, Room> rooms = new HashMap<>();
    //private final HashMap<String, ServerSocket> socketsClients = new HashMap<>();

    private SSLServerSocketFactory sslServerSocketFactory;
    //private ServerSocket sslServerSocket;

    public Server() throws IOException {

        rooms.put("general", new Room("general"));
        rooms.put("games", new Room("games"));

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
        //server.createContext(URL_UPDATE, new UpdateHandler(this));
        server.createContext(URL_AUTH_FACEBOOK, new AuthFacebookHandler(this));
        server.createContext(URL_AUTH_GUEST, new AuthGuestHandler(this));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public ArrayList<String> getAvailableRooms() {
        ArrayList<String> availableRooms = new ArrayList<>();

        for(Map.Entry<String, Room> room : rooms.entrySet()) {
            availableRooms.add(room.getKey());
        }

        return availableRooms;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public void incrementPort() {
        this.socketPort += 1;
    }

    public void makeSSLConnection(String username, String room) {

        try {
            ServerSocket sslServerSocket = sslServerSocketFactory.createServerSocket(socketPort);
            System.out.println("SSL ServerSocket started");
            System.out.println(sslServerSocket.toString());

            Socket socket = sslServerSocket.accept();
            System.out.println("ServerSocket accepted");

            Room r = rooms.get(room);
            r.addClientToRoom(username, socket);

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

        incrementPort();
    }

    public static void main(String[] args) throws Exception {
        new Server().start();
    }

    public void updateRooms(String user, String room, String message) {
        System.out.println("Update Rooms");
        Room r = rooms.get(room);
        System.out.println("pum");
        r.update(user, message);
    }
}