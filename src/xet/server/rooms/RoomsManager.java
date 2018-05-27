package xet.server.rooms;

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

    public ArrayList<String> getAvailableRooms() {
        ArrayList<String> availableRooms = new ArrayList<>();

        for(Map.Entry<String, Room> room : rooms.entrySet()) {
            availableRooms.add(room.getKey());
        }

        return availableRooms;
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

    public void updateRooms(String user, String room, String message) {
        Room r = rooms.get(room);
        r.update(user, message);
    }

    public void addRoom(String s) {
        rooms.put(s, new Room(s));
    }
}
