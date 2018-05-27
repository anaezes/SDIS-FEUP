package xet.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import xet.server.Server;
import xet.server.rooms.RoomsManager;
import xet.server.users.UsersManager;
import xet.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by ana on 5/24/18.
 */
public class HandshakeHandler implements HttpHandler {
    private Server server;

    public HandshakeHandler(Server server) {
        this.server = server;
    }

    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = Utils.RequestBodyToMap(t.getRequestBody());
        String userId = params.get("identification");

        byte [] response;
        if (UsersManager.Get().exists(userId)) {
            response = (server.getSocketPort()+";" + RoomsManager.Get().getAvailableRooms(userId).toString()).getBytes();
        } else {
            response = ("rejected - user not registered").getBytes();
        }

        t.sendResponseHeaders(200, response.length);
        OutputStream os = t.getResponseBody();
        os.write(response);
        os.close();
    }
}