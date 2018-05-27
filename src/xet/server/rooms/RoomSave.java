package xet.server.rooms;

import java.io.Serializable;
import java.util.ArrayList;

public class RoomSave implements Serializable {
    public String key;
    public String name;
    public String invitationCode;
    public String ownerId;
    public boolean isPrivate = false;
    public ArrayList<String> invitedUsers = new ArrayList<>();
}
