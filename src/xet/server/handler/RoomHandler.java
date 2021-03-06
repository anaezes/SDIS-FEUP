package xet.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import xet.server.Server;
import xet.server.rooms.Room;
import xet.server.rooms.RoomsManager;
import xet.server.users.User;
import xet.server.users.UsersManager;
import xet.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by ana on 5/24/18.
 */
public class RoomHandler implements HttpHandler {
    private Server server;

    public RoomHandler(Server server) {
        this.server = server;
    }

    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = Utils.RequestBodyToMap(t.getRequestBody());
        String id = params.get("identification");
        String room = params.get("room");
        String operation = params.get("op");
        String providerId = UsersManager.Get().getUser(id).getProviderId();
        String response = "";

        //create new room if don't exist
        switch (operation) {
            case "join": {
                if(!RoomsManager.Get().getAvailableRooms(id).contains(room)) {
                    response = "rejected - Room does not exist!";
                } else {
                    response = "accepted - Joined!";
                    User user = UsersManager.Get().getUser(id);
                    if (user != null && RoomsManager.Get().get(room).getOwnerId().equals(user.getProviderId())) {
                        response += "owner;" + response;
                    }
                    new Thread(() -> {
                        server.makeSSLConnection(id, room);
                    }).start();
                }
                break;
            }
            case "create": {
                if (providerId == "guest") {
                    response = "rejected - You don't have permissions to create rooms!";
                } else if(RoomsManager.Get().getAvailableRooms(id).contains(room)) {
                    response = "rejected - Room already exists!";
                } else {
                    String type = params.get("type");
                    if (type == null) {
                        response = "rejected - Room type not found!";
                    }
                    else if (type.equals("public")) {
                        RoomsManager.Get().addPublicRoom(room, providerId);
                        response = "accepted - Created public room!";
                    } else if (type.equals("private")) {
                        RoomsManager.Get().addPrivateRoom(room, providerId);
                        response = "accepted - Created private room!";
                    } else {
                        response = "rejected - Unrecognized room type " + type;
                    }
                }
                break;
            }
            case "delete": {
                Room r = RoomsManager.Get().get(room);
                User user = UsersManager.Get().getUser(id);
                if (r == null || user == null) {
                    response = "rejected - Action couldn't be completed!";
                    break;
                }

                if (r.getOwnerId().equals(user.getProviderId())) {
                    RoomsManager.Get().remove(room);
                } else {
                    response = "rejected - Only the owner can delete the room";
                }
                break;
            }
            case "options": {
                String options = "Join Room;Create Room;Invite Code;Cancel";
                User user = UsersManager.Get().getUser(id);
                if (user == null || user.getProviderId().equals("guest"))
                    options = options.replace("Create Room;", "");

                response = options;
                break;
            }
        }

        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
