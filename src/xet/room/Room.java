package xet.room;

import xet.server.Server;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {

    private String name;
    private final HashMap<String, ServerSocket> socketsClients = new HashMap<>();

    public Room(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void addClientToRoom(String username, ServerSocket serverSocket) {
        socketsClients.put(username, serverSocket);
    }

    public ArrayList<String> getClientsOfRoom(){
        ArrayList<String> clients = new ArrayList<>();

        for(Map.Entry<String, ServerSocket> room : socketsClients.entrySet()) {
            clients.add(room.getKey());
        }

        return clients;
    }
}
