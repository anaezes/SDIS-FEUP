package xet.client;
import xet.server.Server;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;


public class Client extends JFrame {
    private static final String serverAddress = "http://localhost:8000";

    private HttpURLConnection con;
    private int idClient;
    private String username;
    private String room;
    private Thread updateThread;

    private final JPanel contentPanel = new JPanel();
    private final JTextPane writeArea = new JTextPane();
    private final JScrollPane jScrollPane1 = new JScrollPane(writeArea);
    private final JTextArea messageArea = new JTextArea();
    private final JScrollPane jScrollPane2 = new JScrollPane(messageArea);
    private final JButton send = new JButton("SEND");
    private final JPanel userPanel = new JPanel();
    private final JPanel readPanel = new JPanel();

    public Client(String username) {
        Random ran = new Random();
        this.idClient = ran.nextInt();
        this.username = username;

        initGUI();
    }

    private void initGUI() {
        this.setVisible(true);
        this.setSize(new Dimension(600,600));
        contentPanel.setSize(new Dimension(600,600));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setBackground(Color.darkGray);

        userPanel.setPreferredSize(new Dimension(550, 200));
        userPanel.setBackground(Color.darkGray);

        readPanel.setPreferredSize(new Dimension(550,400));

        writeArea.setPreferredSize(new Dimension(400, 200));
        writeArea.setEditable(true);
        jScrollPane1.setPreferredSize(new Dimension(400, 200));

        messageArea.setSize(new Dimension(550, 400));
        messageArea.setEditable(false);
        jScrollPane2.setSize(new Dimension(550, 400));

        send.setPreferredSize(new Dimension(100,50));

        userPanel.add(jScrollPane1);
        userPanel.add(send);
        readPanel.add(jScrollPane2);

        contentPanel.add(readPanel);
        contentPanel.add(userPanel);

        messageArea.setBackground(Color.white);
        messageArea.setLineWrap(true);
        this.add(contentPanel);
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

            client.startUpdateThread();

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

        System.exit(0);
    }

    private void startUpdateThread() {

        this.updateThread = new Thread() {
            public void run() {
                while(true) {
                    try {

                        URL myurl = new URL(getUrl(Server.URL_UPDATE));
                        con = (HttpURLConnection) myurl.openConnection();

                        con.setRequestMethod("GET");
                        con.setRequestProperty("User-Agent", "Java xet.client");
                        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                        int responseCode = con.getResponseCode();

                        if (responseCode != 200) {
                            System.out.println("Error to update messages!!!");
                            Thread.sleep(2000);
                            continue;
                        }

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        //print result
                        System.out.println(response.toString());

                        Thread.sleep(1000);
                    } catch (InterruptedException v) {
                        System.out.println(v);
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        updateThread.start();
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
        this.room = scanner.nextLine();

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