package xet.providers;

import xet.server.Server;
import xet.server.User;
import xet.utils.Utils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Facebook {
    private static final String CLIENT_ID = "379423662558959";
    private static final String AUTHORIZATION_ENDPOINT = "https://www.facebook.com/v3.0/dialog/oauth";
    private static final String TOKEN_ENDPOINT = "https://graph.facebook.com/v3.0/oauth/access_token";
    private static final String ME_ENDPOINT = "https://graph.facebook.com/v3.0/me";

    public static String doLogin(String serverUrl) {
        if (!Desktop.isDesktopSupported()) {
            return "";
        }
        String id = Utils.RandomDataBase64url(32);

        String authorizationRequestUrl = AUTHORIZATION_ENDPOINT + "?" +
                "&redirect_uri=" + serverUrl + Server.URL_AUTH_FACEBOOK +
                "&client_id=" + CLIENT_ID +
                "&state=" + id;
        try {
            Desktop.getDesktop().browse(new URI(authorizationRequestUrl));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static String GetAccessToken(String clientSecret, String code) {
        String authorizationRequestUrl = TOKEN_ENDPOINT + "?" +
                "&redirect_uri=" + Server.SERVER_URL + Server.URL_AUTH_FACEBOOK +
                "&client_id=" + CLIENT_ID +
                "&client_secret=" + clientSecret +
                "&code=" + code;
        String response = Utils.SendGet(authorizationRequestUrl);
        String accessToken = getAccessTokenFromResponse(response);
        return accessToken;
    }

    public static User GetUserInfo(String accessToken, String id) {
        String meRequestUrl = ME_ENDPOINT + "?" +
                "fields=id%2Cname%2Cpicture%7Burl%2Cheight%2Cwidth%7D" +
                "&access_token=" + accessToken;
        String response = Utils.SendGet(meRequestUrl);

        String providerId = Utils.SimpleJsonParser(response, "id");
        String name = Utils.SimpleJsonParser(response, "name");
        String pictureUrl = Utils.SimpleJsonParser(response, "url");

        User user = new User(id, providerId, name, pictureUrl);
        return user;
    }

    private static String getAccessTokenFromResponse(String response) {
        String accessTokenIdentifier = "access_token";
        return Utils.SimpleJsonParser(response, accessTokenIdentifier);
    }


}
