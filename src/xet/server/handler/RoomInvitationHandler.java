package xet.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import xet.server.rooms.Room;
import xet.server.Server;
import xet.server.rooms.RoomsManager;
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
        String identifier = params.get("state");
        String response;
        Room room;

        switch (operation){
            case "getCode":
                room = RoomsManager.Get().getRoomFromClientIdentifier(identifier);
                if (room != null) response = room.getInvitationCode();
                else response = "rejected";
                break;
            case "join":
                String code = params.get("code");
                room = RoomsManager.Get().getRoomFromInvitationCode(code);
                if (room != null) response = room.getName();
                else response = "rejected";
                break;
            case "newCode":
                room = RoomsManager.Get().getRoomFromClientIdentifier(identifier);
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
