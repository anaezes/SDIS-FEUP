package client;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class Client {

    private static HttpURLConnection con;

    private static void makeHttpRequest(String url) throws IOException {
        URL myurl = new URL(url);
        con = (HttpURLConnection) myurl.openConnection();

        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Java client");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    }

    private static void makeConnectionToServer(String url, byte[] postData) throws IOException {

        makeHttpRequest(url);

        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(postData);
            wr.flush();
            wr.close();
        }

        StringBuilder content;

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {

            String line;
            content = new StringBuilder();

            while ((line = in.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
            in.close();
        }

        System.out.println(content.toString());
    }

    public static void main(String[] args) {
        String url = "http://localhost:8000/test";
        String urlParameters = "name=Jack&occupation=programmer";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

        try {
            //make request and read response
            makeConnectionToServer(url, postData);

            Scanner scanner = new Scanner(System.in);
            while(true) {
                System.out.println("Enter something:");
                String message = scanner.nextLine();
                if(message.equals("exit")){
                    break;
                }

                urlParameters = "message="+message;
                postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                //necessary - make new http request
                url = "http://localhost:8000/chat";
                makeHttpRequest(url);
                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.write(postData);
                    wr.close();
                }

                StringBuilder content;

                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()))) {

                    String line;
                    content = new StringBuilder();

                    while ((line = in.readLine()) != null) {
                        content.append(line);
                        content.append(System.lineSeparator());
                    }
                    in.close();
                }
            }
            scanner.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
    }
}