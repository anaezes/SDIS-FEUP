package xet.server.rooms;

import xet.server.users.UsersManager;
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
    private String OwnerId;
    private boolean isPrivate = false;
    private ArrayList<String> invitedUsers = new ArrayList<>();

    private final HashMap<String, Socket> socketsClients = new HashMap<>();

    public Room(String name) {
        this.name = name;
        generateNewInvitationCode();
    }

    public Room(String name, String ownerId, boolean isPrivate) {
        this.name = name;
        this.OwnerId = ownerId;
        this.isPrivate = isPrivate;
        generateNewInvitationCode();
    }

    public String getName() {
        return name;
    }

    public String getOwnerId() {
        return OwnerId;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void addClientToRoom(String identifier, Socket serverSocket) {
        socketsClients.put(identifier, serverSocket);
    }

    public void update(String userId, String message) {
        for(Map.Entry<String, Socket> room : socketsClients.entrySet()) {

            Socket socket = room.getValue();
            try {
                PrintWriter in = new PrintWriter(socket.getOutputStream(), true);
                String user = UsersManager.Get().getUserName(userId);
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

    public void addInvitedUser(String userId) {
        if (!invitedUsers.contains(userId)) invitedUsers.add(userId);
    }

    public boolean isUserInvited(String userId) {
        return invitedUsers.contains(userId);
    }

    public void kickInvitedUser(String userId) {
        invitedUsers.remove(userId);
    }
}
