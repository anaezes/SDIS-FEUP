package xet.server;
import xet.room.Room;
import xet.server.handler.HelloHandler;
import xet.server.handler.RoomHandler;
import xet.server.handler.MessageHandler;
import xet.server.handler.UpdateHandler;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * a simple static http xet.server
 */
public class Server {
    public final static String URL_HELLO = "/hello";
    public final static String URL_ROOM = "/room";
    public final static String URL_MESSAGE = "/message";
    public final static String URL_UPDATE = "/update";

    private HttpServer server;

    private final HashMap<String, Room> rooms = new HashMap<>();

    public Server() throws IOException {

        rooms.put("general", new Room("general"));
        rooms.put("games", new Room("games"));

        server = HttpServer.create(new InetSocketAddress(8000), 0);
    }

    public void start() {
        server.createContext(URL_HELLO, new HelloHandler(this));
        server.createContext(URL_ROOM, new RoomHandler(this));
        server.createContext(URL_MESSAGE, new MessageHandler(this));
        server.createContext(URL_UPDATE, new UpdateHandler(this));
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

    public static void main(String[] args) throws Exception {
         new Server().start();

    }
}