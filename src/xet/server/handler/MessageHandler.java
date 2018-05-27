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

public class MessageHandler implements HttpHandler {
    private Server server;

    public MessageHandler(Server server) {
        this.server = server;
    }

    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = Utils.RequestBodyToMap(t.getRequestBody());
        String id = params.get("identification");
        String room = params.get("room");
        String message = params.get("message");

        RoomsManager.Get().updateRooms(id, room, message);

        byte [] response = "Got your message".getBytes();
        t.sendResponseHeaders(200, response.length);
        OutputStream os = t.getResponseBody();
        os.write(response);
        os.close();
    }
}

