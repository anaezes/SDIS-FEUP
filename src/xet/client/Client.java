package xet.client;
import xet.providers.Facebook;
import xet.providers.Guest;
import xet.server.Server;
import xet.utils.Utils;

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
    private String identification;
    private int socketPort;
    private String room;
    private Thread updateThread;

    private final JPanel contentPanel = new JPanel();
    private final JTextPane writeArea = new JTextPane();
    private final JScrollPane jScrollPane1 = new JScrollPane(writeArea);
    private final JTextArea messageArea = new JTextArea();
    private final JScrollPane jScrollPane2 = new JScrollPane(messageArea);
    private final JButton send = new JButton("SEND");
    private final JButton inviteFriends = new JButton("Invite Friends");
    private final JPanel userPanel = new JPanel();
    private final JPanel readPanel = new JPanel();

    private JComboBox<String> roomsList;

    private SSLSocketFactory sslSocketFactory;
    private Socket socket;

    public Client(String identification) {
        this.identification = identification;

        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        System.setProperty("javax.net.ssl.trustStore", System.getProperty("user.dir") + File.separator + "keyStore" + File.separator + "truststore");
        System.setProperty("javax.net.ssl.trustStorePassword","123456");
    }

    private void initGUI() {
        this.setVisible(true);
        this.setSize(new Dimension(750,700));
        this.setTitle("XET - Best chat in the World [" + this.room + "]");
        contentPanel.setSize(new Dimension(800,600));
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
        inviteFriends.setPreferredSize(new Dimension(150,50));

        userPanel.add(jScrollPane1);
        userPanel.add(send);
        userPanel.add(inviteFriends);
        readPanel.add(jScrollPane2);

        contentPanel.add(readPanel);
        contentPanel.add(userPanel);

        messageArea.setBackground(Color.white);
        messageArea.setLineWrap(true);
        this.add(contentPanel);

        send.addActionListener(actionEvent -> {
            String message = writeArea.getText();
            if(message == null || message.isEmpty()) {
                System.out.println("PUMMM");
                return;
            }

            writeArea.selectAll();
            writeArea.replaceSelection("");

            message = message.replace("\n", " ");
            String urlParameters = "username=" + identification + "&room=" + room + "&message="+message;

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

        inviteFriends.addActionListener(actionEvent -> {
            showInviteFriendsDialog();
        });
    }

    private void showInviteFriendsDialog() {
        // Gets invitation code
        String invitationCode = Utils.SendGet(Server.SERVER_URL + Server.URL_ROOM_INVITATION + "?" +
                "state=" + this.identification +
                "&op=getCode"
        );

        String[] options = { "Generate new code", "Ok"};
        JTextField textField = new JTextField(16);
        textField.setText(invitationCode);
        int selection = JOptionPane.showOptionDialog(null, textField, "Invitation Code",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
                options[0]);

        switch (selection) {
            case 0: // Generate new code
                Utils.SendGet(Server.SERVER_URL + Server.URL_ROOM_INVITATION + "?" +
                        "state=" + this.identification +
                        "&op=newCode"
                );
                showInviteFriendsDialog();
                return;
            case 1: // Ok
                break;

        }
    }

    public static String getUrl( String url) {
        return serverAddress + url;
    }

    private void makeHttpRequest(String url, byte[] params) throws IOException {
        URL myUrl = new URL(url);
        con = (HttpURLConnection) myUrl.openConnection();

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

        String urlParameters = "username="+ identification;
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
        String id = doLogin();
        Client client = new Client(id);

        try {
            //make request and read response
            ArrayList<String> response = client.makeConnectionToServer();
            if(response == null) {
                System.out.print("Error to connect to server!!!");
                return;
            }

            client.chooseRoom(scanner, response);

            client.initGUI();

            //make ssl connection
            client.makeSslConnection();

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

    private static String doLogin() {
        String[] options = { "Guest", "Facebook", "Quit"};
        int selection = JOptionPane.showOptionDialog(null, "Select a login method", "Xet - Login",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
                options[0]);
        switch (selection)  {
            case 0: // Guest
                String username;
                do {
                    username = JOptionPane.showInputDialog("Enter username");
                } while (username.length() == 0);
                return Guest.doLogin(username);
            case 1: // Facebook
                return Facebook.doLogin(Server.SERVER_URL);
            default:
                System.exit(0);
        }
        return "";
    }

    private void makeSslConnection() {

        try {
            sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            socket = sslSocketFactory.createSocket("localhost", socketPort);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    private void startUpdateThread() {

        this.updateThread = new Thread(() -> {
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
        });

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

    private boolean chooseRoom(Scanner scanner, ArrayList<String> rooms) throws IOException {

        roomsList = new JComboBox(rooms.toArray());

        String[] options = {"Join Room", "Create Room", "Invite Code", "Cancel"};

        String title = "Choose a room";
        int selection = JOptionPane.showOptionDialog(null, roomsList, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
                options[0]);

        if (selection == 3) // Cancel
            System.exit(0);

        String room = "";
        if (selection == 1) // Create Room
            room = JOptionPane.showInputDialog("Name of room:");
        else if (selection == 0) // Join Room
            room = (String) roomsList.getSelectedItem();
        else if (selection == 2) { // Invite Code
            String code = JOptionPane.showInputDialog("Enter invitation code");
            String response = Utils.SendGet(Server.SERVER_URL + Server.URL_ROOM_INVITATION + "?" +
                "state=" + this.identification +
                "&op=" + "join" +
                "&code=" + code);
            if (response.contains("reject")) {
                JOptionPane.showMessageDialog(null,
                        "The invitation code entered isn't valid or has expired",
                        "Invalid invitation code",
                        JOptionPane.ERROR_MESSAGE);
                return chooseRoom(scanner, rooms);
            } else {
                room = response;
            }
        }

        this.room = room.trim();

        String urlParameters = "identification=" + identification + "&room=" + this.room;
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