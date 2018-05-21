package main.java.client;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import main.java.utils.Utils;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class GoogleLogin {
    private static final String clientID = "249929325899-euiefbmuh0b4vc50095vjl1okjmm703m.apps.googleusercontent.com";
    private static final String clientSecret = "XdnV9XfwTdkt5xvKBMhv-fRu";
    private static final String authorizationEndpoint = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String tokenEndpoint = "https://www.googleapis.com/oauth2/v4/token";
    private static final String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";

    public static void StartLogin() {
        try {
            int port = Utils.GetAvailablePort();

            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            System.out.println("server started at " + port);
            server.createContext("/auth", new AuthorizationHandler());
            server.createContext("/token", new TokenHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
            makeRequest(String.format("http://%s:%s/", "127.0.0.1", Integer.toString(port)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void makeRequest(String redirectUrl) throws IOException, URISyntaxException {
        // Creates the OAuth 2.0 authorization request.
        String state = "ASOIJDAOsifjaoidjasoidJOSJAS";
        String url = redirectUrl;
        System.out.println(url);

        String authorizationRequest = String.format("%s?response_type=code&scope=openid%%20profile&redirect_uri=%s&client_id=%s&state=%s",
                authorizationEndpoint,
                redirectUrl,
                clientID,
                state);
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(authorizationRequest));
        }
        /*String authorizationRequest = String.format("{0}?response_type=code&scope=openid%20profile&redirect_uri={1}&client_id={2}&state={3}&code_challenge={4}&code_challenge_method={5}",
                authorizationEndpoint,
                new URL(redirectUrl).toString(),
                clientID,
                state,
                code_challenge,
                code_challenge_method);*/
    }

    static class AuthorizationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, String> params = Utils.queryToMap(t.getRequestURI().getQuery());

            // TODO Generate
            String response = "";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class TokenHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, String> params = Utils.queryToMap(t.getRequestURI().getQuery());

            String response = String.format("%s %s, %s",
                    params.get("code"),
                    clientID,
                    clientSecret);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
