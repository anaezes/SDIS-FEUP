package xet.providers;

import xet.server.Server;
import xet.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Guest {
    public static String doLogin(String username) {
        String id = Utils.RandomDataBase64url(32);
        try { // Encodes possible spaces in the username parameter
            username = URLEncoder.encode(username, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String identificationRequestUrl = Server.SERVER_URL + Server.URL_AUTH_GUEST + "?" +
                "&username=" + username +
                "&state=" + id;

        String response = Utils.SendGet(identificationRequestUrl);
        if (response.contains("accepted")) {
            return id;
        } else {
            return "";
        }
    }
}
