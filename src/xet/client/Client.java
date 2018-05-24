package xet.client;
import xet.server.Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;


public class Client {
    private static final String serverAddress = "http://localhost:8000";

    private HttpURLConnection con;
    private int idClient;
    private String username;

    public Client(String username) {
        Random ran = new Random();
        this.idClient = ran.nextInt();
        this.username = username;
    }

    public static String getUrl( String url) {
        return serverAddress + url;
    }

    private void makeHttpRequest(String url, byte[] params) throws IOException {
        URL myurl = new URL(url);
        con = (HttpURLConnection) myurl.openConnection();

        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Java xet.client");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        DataOutputStream wr;
        wr = new DataOutputStream(con.getOutputStream()) ;
        wr.write(params);
        wr.flush();
        wr.close();
    }

    private boolean makeConnectionToServer() {

        String urlParameters = "id=" + idClient + "&xet.username="+username;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);


        try {
            makeHttpRequest(getUrl(Server.URL_HELLO), postData);

            StringBuilder content;

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
                in.close();

            System.out.println(content.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void writeData(String data) {

    }

    public static void main(String[] args) {



        //todo login here
        Scanner scanner = new Scanner(System.in);
        System.out.print("Username:");
        String name = scanner.nextLine();

        Client client = new Client(name);

        try {
            //make request and read response
            if(!client.makeConnectionToServer()) {
                System.out.print("Error to connect to server!!!");
                return;
            }

            //todo choose room
            client.chooseRoom(scanner);

            while(true) {
                System.out.print("xet> ");
                String message = scanner.nextLine();
                if(message.equals("exit")){
                    break;
                }

                String urlParameters = "id=" + client.idClient + "&xet.message="+message;
                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                //necessary - make new http request
                client.makeHttpRequest(getUrl(Server.URL_MESSAGE), postData);

                String content = client.readServerAnswer();
                System.out.println(content);
            }
            scanner.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readServerAnswer() throws IOException {

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

        return content.toString();
    }


    private boolean chooseRoom(Scanner scanner) throws IOException {

        System.out.print("Choose room: ");
        String  room = scanner.nextLine();

        String urlParameters = "id=" + idClient + "&xet.room="+room;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

        try {
            makeHttpRequest(getUrl(Server.URL_ROOM), postData);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        String content = readServerAnswer();
        System.out.println(content);

        return true;
    }
}