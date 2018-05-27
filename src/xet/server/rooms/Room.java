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
    private String ownerId;
    private boolean isPrivate = false;
    private ArrayList<String> invitedUsers = new ArrayList<>();
    private ArrayList<String> invitedGuests = new ArrayList<>();

    private final HashMap<String, Socket> socketsClients = new HashMap<>();

    public Room(String name) {
        this.name = name;
        this.ownerId = "server";
        generateNewInvitationCode();
    }

    public Room(String name, String ownerId, boolean isPrivate) {
        this.name = name;
        this.ownerId = ownerId;
        this.isPrivate = isPrivate;
        generateNewInvitationCode();
    }

    public Room(RoomSave roomSave) {
        name = roomSave.name;
        ownerId = roomSave.ownerId;
        isPrivate = roomSave.isPrivate;
        invitationCode = roomSave.invitationCode;
        invitedUsers = roomSave.invitedUsers;
    }

    public String getName() {
        return name;
    }

    public String getOwnerId() {
        return ownerId;
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

    public ArrayList<String> getInvitedUsers() {
        return invitedUsers;
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

    public void addInvitedGuest(String userId) {
        if (!invitedGuests.contains(userId)) invitedGuests.add(userId);
    }

    public boolean isGuestInvited(String userId) {
        return invitedGuests.contains(userId);
    }

    public void kickGuestUser(String userId) {
        invitedGuests.remove(userId);
    }
}
