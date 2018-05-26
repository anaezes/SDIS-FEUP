package xet.client;
import xet.server.Server;

import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client extends JFrame {
    private static final String serverAddress = "http://localhost:8000";

    private HttpURLConnection con;
    private String username;
    private int port;
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

    private SSLSocketFactory sslSocketFactory;
    private Socket socket;
    //private PrintWriter out;

    public Client(String username) {
        this.username = username;

        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        System.setProperty("javax.net.ssl.trustStore", System.getProperty("user.dir") + File.separator + "keyStore" + File.separator + "truststore");
        System.setProperty("javax.net.ssl.trustStorePassword","123456");

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

        String urlParameters = "username="+username;
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

            parseConnectionMessage(content.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void parseConnectionMessage(String s) {
        String[] parts = s.split(";");

        String port = parts[0];
        System.out.println("PORT: "  + port);
        this.port = Integer.parseInt(port);

        String rooms = parts[1];
        System.out.println("ROOMS: " + rooms);
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

            System.out.println("pim!!!");

            //todo choose room - verify if choosen room is available
            client.chooseRoom(scanner);

            //make ssl connection
            client.makeSSLconection();

            client.startUpdateThread();

            while(true) {
                System.out.print("xet> ");
                String message = scanner.nextLine();
                if(message.equals("exit")){
                    break;
                }

                String urlParameters = "username=" + client.username + "&room=" + client.room + "&message="+message;
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

    private void makeSSLconection() {

        try {
            sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            socket = sslSocketFactory.createSocket("localhost", port);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    private void startUpdateThread() {

        this.updateThread = new Thread() {
            public void run() {
                while(true) {
                    PrintWriter out = null;
                    try {
                        out = new PrintWriter(socket.getOutputStream(), true);

                   BufferedReader bufferedReader = new BufferedReader(
                                         new InputStreamReader(socket.getInputStream()));
                        String line;
                        while((line = bufferedReader.readLine()) != null){
                            System.out.println(line);
                            out.println("RECEBEU!!!" + line);
                        }

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


    //todo verify if input is valid!!!!
    private boolean chooseRoom(Scanner scanner) throws IOException {

        System.out.print("Choose room: ");
        this.room = scanner.nextLine();

        String urlParameters = "username=" + username + "&room="+room;
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