package xet.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import xet.server.Server;
import xet.server.rooms.RoomsManager;
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
        Map<String, String> params = Utils.requestBodyToMap(t.getRequestBody());
        String id = params.get("identification");
        String room = params.get("room");

        //create new room if don't exist
        // TODO change to different param
        if(!RoomsManager.Get().getAvailableRooms().contains(room)) {
            RoomsManager.Get().addRoom(room);
        }

        new Thread(() -> {
            server.makeSSLConnection(id, room);
        }).start();

        String response = "Room chosen!";
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
