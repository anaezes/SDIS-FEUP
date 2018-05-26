package xet.providers;

import xet.server.Server;
import xet.utils.Utils;

public class Guest {
    public static String doLogin(String username) {
        String id = Utils.RandomDataBase64url(32);
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
