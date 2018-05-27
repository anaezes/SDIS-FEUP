package xet.server.rooms;

import xet.server.users.UsersManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoomsManager {
    private static final RoomsManager instance = new RoomsManager();;
    public static RoomsManager Get() {
        return instance;
    }

    private final HashMap<String, Room> rooms = new HashMap<>();

    public void add(String name, Room room) {
        rooms.put(name, room);
    }

    public Room get(String name) {
        return rooms.get(name);
    }

    public ArrayList<String> getAvailableRooms(String userId) {
        ArrayList<String> rooms = new ArrayList<>();

        for(Map.Entry<String, Room> room : this.rooms.entrySet()) {
            String providerId = UsersManager.Get().getUser(userId).getProviderId();
            if (!room.getValue().isPrivate() ||                         // If room is public
                    room.getValue().getOwnerId().equals(providerId) ||  // If user is owner
                    room.getValue().isUserInvited(providerId))          // If user is invited
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
        System.out.println("public:" + rooms.get(s).getOwnerId());
    }

    public void addPrivateRoom(String s, String ownerId) {
        rooms.put(s, new Room(s, ownerId, true));
        System.out.println( "private:" + rooms.get(s).getOwnerId());
    }
}
