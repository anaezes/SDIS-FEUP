package xet.server.rooms;

import xet.server.users.UsersManager;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoomsManager {
    private static final RoomsManager instance = new RoomsManager();;
    public static RoomsManager Get() {
        return instance;
    }

    private HashMap<String, Room> rooms = new HashMap<>();

    public void add(String name, Room room) {
        rooms.put(name, room);
    }

    public Room get(String name) {
        return rooms.get(name);
    }

    public boolean remove(String name) {
        if (rooms.containsKey(name)) {
            rooms.remove(name);
            return true;
        }
        return false;
    }

    public ArrayList<String> getAvailableRooms(String userId) {
        ArrayList<String> rooms = new ArrayList<>();

        for(Map.Entry<String, Room> room : this.rooms.entrySet()) {
            String providerId = UsersManager.Get().getUser(userId).getProviderId();

            if (!room.getValue().isPrivate() ||                         // If room is public
                    room.getValue().getOwnerId().equals(providerId) ||  // If user is owner
                    room.getValue().isUserInvited(providerId) ||        // If user is invited
                    room.getValue().isGuestInvited(userId))             // If guest is invited
                rooms.add(room.getKey());
        }
        return rooms;
    }

    public ArrayList<String> getPublicRooms() {
        ArrayList<String> rooms = new ArrayList<>();

        for(Map.Entry<String, Room> room : this.rooms.entrySet()) {
            if (!room.getValue().isPrivate())
                rooms.add(room.getKey());
        }

        return rooms;
    }

    public ArrayList<String> getAllRooms() {
        ArrayList<String> rooms = new ArrayList<>();

        for(Map.Entry<String, Room> room : this.rooms.entrySet()) {
            rooms.add(room.getKey());
        }

        return rooms;
    }

    // Returns the room that the client with given identifier is
    public Room getRoomFromClientIdentifier(String identifier) {
        for(Map.Entry<String, Room> roomKV : rooms.entrySet()) {
            Room room = roomKV.getValue();
            if (room.clientIsInRoom(identifier)) {
                return room;
            }
        }
        return null;
    }

    // Returns the room that has the invitation code
    public Room getRoomFromInvitationCode(String code) {
        for(Map.Entry<String, Room> roomKV : rooms.entrySet()) {
            Room room = roomKV.getValue();
            if (room.getInvitationCode().equals(code)) {
                return room;
            }
        }
        return null;
    }

    public void updateRooms(String userId, String room, String message) {
        Room r = rooms.get(room);
        r.update(userId, message);
    }

    public void addPublicRoom(String s, String ownerId) {
        rooms.put(s, new Room(s, ownerId, false));
        save();
    }

    public void addPrivateRoom(String s, String ownerId) {
        rooms.put(s, new Room(s, ownerId, true));
        System.out.println( "private:" + rooms.get(s).getOwnerId());
        save();
    }

    public void save() {
        if (rooms == null || rooms.size() == 0) return;

        ArrayList<RoomSave> roomSaves = new ArrayList<>();
        for(Map.Entry<String, Room> room : rooms.entrySet()) {
            Room r = room.getValue();
            RoomSave rs = new RoomSave();
            rs.key = room.getKey();
            rs.name = r.getName();
            rs.invitationCode = r.getInvitationCode();
            rs.ownerId = r.getOwnerId();
            rs.isPrivate = r.isPrivate();
            rs.invitedUsers = r.getInvitedUsers();
            roomSaves.add(rs);
        }

        try {
            FileOutputStream saveFile = new FileOutputStream("rooms.sav");
            ObjectOutputStream save = new ObjectOutputStream(saveFile);
            save.writeObject(roomSaves);
            save.close();
            saveFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean load() {
        try {
            File f = new File("rooms.sav");
            if (!f.exists()) return false;

            FileInputStream saveFile = new FileInputStream("rooms.sav");
            ObjectInputStream save = new ObjectInputStream(saveFile);
            ArrayList<RoomSave> roomSaves = (ArrayList<RoomSave>) save.readObject();
            save.close();
            saveFile.close();

            for (int i = 0; i < roomSaves.size(); i++) {
                RoomSave rs = roomSaves.get(i);
                rooms.put(rs.key, new Room(rs));
            }
            return rooms.size() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
