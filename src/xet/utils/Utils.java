package xet.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static int GetAvailablePort() {
        try {
            ServerSocket socket = new ServerSocket(0);
            int port = socket.getLocalPort();
            socket.close();
            return port;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }

    public static String RandomDataBase64url(int length)
    {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64urlencodeNoPadding(bytes);
    }

    public static String Base64urlencodeNoPadding(byte[] buffer)
    {
        String base64 = Base64.getEncoder().encodeToString(buffer);
        // Converts base64 to base64url.
        base64 = base64.replace("+", "-");
        base64 = base64.replace("/", "_");
        // Strips padding.
        base64 = base64.replace("=", "");
        return base64;
    }

    public static byte[] Sha256(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.US_ASCII);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algorithm not found: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return bytes;
    }

    public static String SendPost(String url, String bodyParams) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Accept", "Accept=text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            byte[] _byteVersion = bodyParams.getBytes(StandardCharsets.US_ASCII);
            con.setRequestProperty("Content-Length", Integer.toString(_byteVersion.length));

            //con.setRequestProperty("User-Agent", "Mozilla/5.0");
            //con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(_byteVersion, 0, _byteVersion.length);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + bodyParams);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            System.err.println(e);
        }
        return "";
    }

    public static String SendGet(String url) {
        try {
            StringBuilder result = new StringBuilder();
            URL url_ = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url_.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            return result.toString();
        } catch (Exception e) {
            System.err.println(e);
        }
        return "";
    }

    public static String SimpleJsonParser(String JSON, String key, boolean withQuotes) {
        key = "\"" + key + "\":";
        if (JSON.contains(key)) {
            if (withQuotes) {
                String value = JSON.substring(JSON.indexOf(key) + key.length() + 1,
                        JSON.indexOf("\"", JSON.indexOf(key) + key.length() + 1));
                return value;
            } else {
                String value = JSON.substring(JSON.indexOf(key) + key.length() + 1,
                        JSON.indexOf(",", JSON.indexOf(key) + key.length() + 1));
                return value;
            }
        }
        return "";
    }

    public static String SimpleJsonParser(String JSON, String key) {
        return SimpleJsonParser(JSON, key, true);
    }
}
