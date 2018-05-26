package xet.room;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {

    private String name;
    private final HashMap<String, Socket> socketsClients = new HashMap<>();

    public Room(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addClientToRoom(String username, Socket serverSocket) {

        socketsClients.put(username, serverSocket);
    }

    public ArrayList<String> getClientsOfRoom(){
        ArrayList<String> clients = new ArrayList<>();

        for(Map.Entry<String, Socket> room : socketsClients.entrySet()) {
            clients.add(room.getKey());
        }

        return clients;
    }

    public void update(String user, String message) {

        System.out.println("update!!!!!");

        for(Map.Entry<String, Socket> room : socketsClients.entrySet()) {

            Socket socket = room.getValue();
            try {
                PrintWriter in = new PrintWriter(socket.getOutputStream(), true);
                in.println(user + "> " + message);
                System.out.println("pim!!!!!");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
