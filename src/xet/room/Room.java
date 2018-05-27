package xet.room;

import xet.utils.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {

    private String name;
    private String invitationCode;
    private final HashMap<String, Socket> socketsClients = new HashMap<>();

    public Room(String name) {
        this.name = name;
        generateNewInvitationCode();
    }

    public String getName() {
        return name;
    }

    public void addClientToRoom(String identifier, Socket serverSocket) {
        socketsClients.put(identifier, serverSocket);
    }

    public void update(String user, String message) {
        for(Map.Entry<String, Socket> room : socketsClients.entrySet()) {

            Socket socket = room.getValue();
            try {
                PrintWriter in = new PrintWriter(socket.getOutputStream(), true);
                in.println(user + "> " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean clientIsInRoom(String identifier) {
        return socketsClients.get(identifier) != null;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public String generateNewInvitationCode() {
        this.invitationCode = Utils.RandomDataBase64url(16);
        return getInvitationCode();
    }
}
