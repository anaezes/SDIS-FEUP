package xet.client;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import xet.utils.Utils;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class GoogleLogin {
    private final String clientID = "249929325899-kdf2nn2ndaemri7nk13r0d6ct8n9jtej.apps.googleusercontent.com";
    private final String clientSecret = "N-Rx2Dt9aP3kGsfc0YXu0nkU";
    private final String authorizationEndpoint = "https://accounts.google.com/o/oauth2/v2/auth";
    private final String tokenEndpoint = "https://www.googleapis.com/oauth2/v4/token";
    private final String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";

    private final int port = 60217; //Utils.GetAvailablePort();
    private final String baseRedirectUrl = "127.0.0.1:" + port;
    private final String state = Utils.RandomDataBase64url(32);
    private final String codeVerifier = Utils.RandomDataBase64url(32);
    private final String codeChallenge = Utils.Base64urlencodeNoPadding(Utils.Sha256(codeVerifier));
    private final String codeChallengeMethod = "S256";

    private HttpServer server;

    public GoogleLogin() {
        // Creates HTTP Server
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/auth", new AuthorizationHandler());
            server.createContext("/token", new TokenHandler());
            server.setExecutor(null); // creates a default executor
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void StartLoginProcess() throws IOException, URISyntaxException {
        server.start();
        makeAuthRequest();
    }

    private void makeAuthRequest() throws IOException, URISyntaxException {
        // Creates the OAuth 2.0 authorization request.

        /*String authorizationRequest = String.format("%s?response_type=code&scope=openid%%20profile&redirect_uri=%s&client_id=%s&state=%s",
                authorizationEndpoint,
                url,
                clientID,
                state);*/
        String authorizationRequestUrl = authorizationEndpoint + "?" +
                "response_type=" + "code" +
                "&scope=" + "openid%20profile" +
                "&redirect_uri=" + getRedirectUrl(RedirectUrl.AUTH) +
                "&client_id=" + clientID +
                "&state=" + state +
                "&code_challenge=" + codeChallenge +
                "&code_challenge_method=" + codeChallengeMethod;

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(authorizationRequestUrl));
        }
    }

    private void performCodeExchange(String code) {
        try {
            String tokenRequestBody =
                    "code=" + code +
                    "&client_id=" + clientID +
                    "&client_secret=" + clientSecret +
                    "&redirect_uri=" + getRedirectUrl(RedirectUrl.TOKEN) +
                    "&grant_type=authorization_code";

            String result = Utils.SendPost(tokenEndpoint, tokenRequestBody);
            System.out.println(result);

        } catch (Exception e) {
            System.err.println("Couldn't perform code exchange:\n" + e.getLocalizedMessage());
        }
    }

    private String getRedirectUrl(RedirectUrl url) {
        if (url.equals(RedirectUrl.AUTH)) {
            return "http://" + baseRedirectUrl + "/auth";
        } else if (url.equals(RedirectUrl.TOKEN)) {
            return "http://" + baseRedirectUrl + "/token";
        }
        return baseRedirectUrl;
    }

    class AuthorizationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, String> params = Utils.queryToMap(t.getRequestURI().getQuery());
            //performCodeExchange(params.get("code"));

            String response = "You can close your browser and return to the app\n" + codeVerifier + "\n\n";
            for (int i = 0; i < params.size(); i++) {
                response += params.keySet().toArray()[i] + ": " + params.get(params.keySet().toArray()[i]) + "\n";
            }
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    class TokenHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, String> params = Utils.queryToMap(t.getRequestURI().getQuery());
            performCodeExchange(params.get("code"));

            String response = "";
            for (int i = 0; i < params.size(); i++) {
                response += params.keySet().toArray()[i] + ": " + params.get(params.keySet().toArray()[i]) + "\n";
            }
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private enum RedirectUrl {
        AUTH,
        TOKEN
    }

}
