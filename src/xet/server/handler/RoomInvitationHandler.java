package xet.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import xet.server.rooms.Room;
import xet.server.Server;
import xet.server.rooms.RoomsManager;
import xet.server.users.User;
import xet.server.users.UsersManager;
import xet.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class RoomInvitationHandler implements HttpHandler {
    private Server server;

    public RoomInvitationHandler(Server server) {
        this.server = server;
    }

    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = Utils.QueryToMap(t.getRequestURI().getQuery());

        String operation = params.get("op");
        String userId = params.get("identification");
        String response;
        Room room;

        switch (operation){
            case "getCode":
                room = RoomsManager.Get().getRoomFromClientIdentifier(userId);
                if (room != null) response = room.getInvitationCode();
                else response = "rejected";
                break;
            case "join":
                String code = params.get("code");
                room = RoomsManager.Get().getRoomFromInvitationCode(code);
                if (room != null) {
                    System.out.println(1);
                    response = room.getName();
                    System.out.println(2);
                    User user = UsersManager.Get().getUser(userId);
                    System.out.println(3);
                    System.out.println(user.getProviderId());
                    if (user != null && !user.getProviderId().equals("guest")) {
                        room.addInvitedUser(user.getProviderId());
                    }
                    System.out.println(4);

                } else {
                    response = "rejected";
                }
                break;
            case "newCode":
                room = RoomsManager.Get().getRoomFromClientIdentifier(userId);
                if (room != null) response = room.generateNewInvitationCode();
                else response = "rejected";
                break;
            default:
                response = "Invalid operation";
        }


        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
