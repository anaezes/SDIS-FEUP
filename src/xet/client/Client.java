package xet.client;
import xet.providers.Facebook;
import xet.providers.Guest;
import xet.server.Server;

import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client extends JFrame {
    private static final String serverAddress = "http://localhost:8000";
    public static volatile boolean running = true;

    private HttpURLConnection con;
    private String username;
    private int socketPort;
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

    private JComboBox<String> roomsList;

    private SSLSocketFactory sslSocketFactory;
    private Socket socket;

    public Client(String username) {
        this.username = username;

        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        System.setProperty("javax.net.ssl.trustStore", System.getProperty("user.dir") + File.separator + "keyStore" + File.separator + "truststore");
        System.setProperty("javax.net.ssl.trustStorePassword","123456");
    }

    private void initGUI() {
        this.setVisible(true);
        this.setSize(new Dimension(600,600));
        this.setTitle("XET - Best chat in the World");
        contentPanel.setSize(new Dimension(600,600));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setBackground(Color.darkGray);

        userPanel.setPreferredSize(new Dimension(550, 200));
        userPanel.setBackground(Color.darkGray);

        readPanel.setLayout(new BoxLayout(readPanel, BoxLayout.LINE_AXIS));
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

        send.addActionListener(actionEvent -> {
            String message = writeArea.getText();
            if(message == null || message.isEmpty())
                return;

            writeArea.selectAll();
            writeArea.replaceSelection("");

            String urlParameters = "username=" + username + "&room=" + room + "&message="+message;
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

            try {
                makeHttpRequest(getUrl(Server.URL_MESSAGE), postData);
                String content = readServerAnswer();
                System.out.println(content);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                running = false;
        }});

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

    private ArrayList<String> makeConnectionToServer() {

        String urlParameters = "username="+username;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);


        try {
            makeHttpRequest(getUrl(Server.URL_HANDSHAKE), postData);

            StringBuilder content;

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
                in.close();
            return parseConnectionMessage(content.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<String> parseConnectionMessage(String s) {
        String[] parts = s.split(";");

        String port = parts[0];
        System.out.println("PORT: "  + port);
        this.socketPort = Integer.parseInt(port);

        String rooms = parts[1];
        System.out.println("ROOMS: " + rooms);

        rooms = rooms.toString().replace("[", "");
        rooms = rooms.toString().replace("]", "");

        return new ArrayList<String>(Arrays.asList(rooms.split(",")));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String id = "";

        System.out.println("Select login method: ");
        System.out.println("1 - Guest Login");
        System.out.println("2 - Facebook Login");
        String option = scanner.nextLine();
        System.out.println(option);

        if (option.contains("1")) {
            System.out.println("Guest Login");
            System.out.println("Enter desired username:");
            String username = scanner.nextLine();
            id = Guest.doLogin(username);
        } else if (option.contains("2")) {
            System.out.println("Facebook Login");
            id = Facebook.doLogin(Server.SERVER_URL);
        } else {
            System.err.println("Option not recognized");
            System.exit(1);
        }

        Client client = new Client(id);

        try {
            //make request and read response
            ArrayList<String> response = client.makeConnectionToServer();
            if(response == null) {
                System.out.print("Error to connect to server!!!");
                return;
            }


            //todo choose room - verify if choosen room is available
            client.chooseRoom(scanner, response);

            client.initGUI();


            //make ssl connection
            client.makeSSLconection();

            //update messages from room
            client.startUpdateThread();

            while(running);

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
            socket = sslSocketFactory.createSocket("localhost", socketPort);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    private void startUpdateThread() {

        this.updateThread = new Thread() {
            public void run() {
                while(true) {

                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String line;

                        while((line = bufferedReader.readLine()) != null){
                            messageArea.append(line + "\n");
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
    private boolean chooseRoom(Scanner scanner, ArrayList<String> rooms) throws IOException {

        roomsList = new JComboBox(rooms.toArray());

        String[] options = { "OK", "Cancel"};

        String title = "Choose a room";
        int selection = JOptionPane.showOptionDialog(null, roomsList, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
                options[0]);

        if (selection == 1) {
            System.out.println("selection is: " + options[selection]);
            System.exit(0);
        }

        String room = (String)roomsList.getSelectedItem();
        if (room != null) {
            System.out.println("room: " + room);
        }

        this.room = room;

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