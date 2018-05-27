package xet.server.handler.auth;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import xet.providers.Facebook;
import xet.server.Server;
import xet.server.users.User;
import xet.server.users.UsersManager;
import xet.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class AuthFacebookHandler implements HttpHandler {
    private Server server;
    private final String CLIENT_SECRET = "d5f2b6160028317c1d968cce2dd5cdcb";

    public AuthFacebookHandler(Server server) {
        this.server = server;
    }

    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = Utils.queryToMap(t.getRequestURI().getQuery());
        String accessToken = Facebook.GetAccessToken(CLIENT_SECRET, params.get("code"));
        String identifier = params.get("state");

        String response;
        if (accessToken != "") {
            User user = Facebook.GetUserInfo(accessToken, identifier);
            if (user != null && user.getId().length() > 0){
                UsersManager.Get().addUser(user);
                response = "Welcome " + user.getName() + "!\nYou can close your browser and return to the application";
            }
            else response = "Error getting user info, please try again later";
        } else response = "Error getting access token, please try again later";

        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
