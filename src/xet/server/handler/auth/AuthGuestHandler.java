package xet.server.handler.auth;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import xet.server.Server;
import xet.server.users.User;
import xet.server.users.UsersManager;
import xet.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AuthGuestHandler implements HttpHandler {
    private Server server;

    public AuthGuestHandler(Server server) {
        this.server = server;
    }

    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = Utils.queryToMap(t.getRequestURI().getQuery());

        String response;
        String username = params.get("username");
        username = URLDecoder.decode(username, StandardCharsets.UTF_8.name());

        if (UsersManager.Get().addUser(new User(params.get("state"), "guest", username)))
            response = "accepted";
        else response = "rejected";

        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
