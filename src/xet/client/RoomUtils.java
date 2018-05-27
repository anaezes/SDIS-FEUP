package xet.client;

import xet.server.Server;
import xet.utils.Utils;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RoomUtils {
    private Client client;
    private ArrayList<String> rooms = new ArrayList<>();

    public RoomUtils(Client client) {
        this.client = client;
    }

    public void chooseRoom(ArrayList<String> rooms) throws IOException {
        if (rooms == null) rooms = this.rooms;
        else this.rooms = rooms;

        JComboBox<String> roomsList = new JComboBox(rooms.toArray());

        String[] options = {"Join Room", "Create Room", "Invite Code", "Cancel"};

        String title = "Choose a room";
        int selection = JOptionPane.showOptionDialog(null, roomsList, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
                options[0]);

        switch (selection) {
            case 0: // Join Room
                joinRoom((String) roomsList.getSelectedItem());
                break;
            case 1: // Create Room
                createRoom();
                break;
            case 2: // Invite Code
                joinByInvitation();
                break;
            case 3: // Cancel
                System.exit(0);
        }
    }


    private void createRoom() throws IOException {
        String title = "Create Room";
        String[] options = {"Public Room", "Private Room", "Cancel"};

        JPanel panel = new JPanel();
        panel.add(new JLabel("Enter a name for the room: "));
        JTextField textField = new JTextField(16);
        panel.add(textField);

        int selection = JOptionPane.showOptionDialog(null, panel, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
                options[0]);

        String type = "";
        switch (selection) {
            case 0: // Public Room
                type = "public";
                break;
            case 1: // Private Room
                type = "private";
                break;
            default: // Cancel
                chooseRoom(null);
                return;
        }

        String room = textField.getText();
        if (room.length() == 0) {
            JOptionPane.showMessageDialog(null, "The name cannot be empty",
                    "Error Creating Room", JOptionPane.ERROR_MESSAGE);
            createRoom();
            return;
        }

        String urlParameters = "identification=" + client.getIdentification() +
                "&room=" + room +
                "&type=" + type +
                "&op=create";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

        client.sendHttpRequest(Server.BuildUrl(Server.URL_ROOM), postData);
        String content = client.readServerAnswer();

        if (content.contains("rejected")) {
            String motive = content.substring(content.indexOf("rejected - ") + "rejected - ".length());
            JOptionPane.showMessageDialog(null, motive, "Error Creating Room", JOptionPane.ERROR_MESSAGE);
            createRoom();
            return;
        }

        joinRoom(room);
    }

    private void joinRoom(String room) throws IOException {
        room = room.trim();
        client.setRoom(room);
        System.out.println(room);

        String urlParameters = "identification=" + client.getIdentification() +
                "&room=" + room +
                "&op=join";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

        client.sendHttpRequest(Server.BuildUrl(Server.URL_ROOM), postData);
        String content = client.readServerAnswer();

        if (content.contains("rejected")) {
            String motive = content.substring(content.indexOf("rejected - ") + "rejected - ".length());
            JOptionPane.showMessageDialog(null, motive, "Error Joining Room", JOptionPane.ERROR_MESSAGE);
            chooseRoom(null);
        }
        System.out.println(content);
    }

    private void joinByInvitation() throws IOException {
        String code = JOptionPane.showInputDialog("Enter invitation code");
        String response = Utils.SendGet(Server.SERVER_URL + Server.URL_ROOM_INVITATION + "?" +
                "identification=" + client.getIdentification() +
                "&op=" + "join" +
                "&code=" + code);
        if (response.contains("reject")) {
            JOptionPane.showMessageDialog(null,
                    "The invitation code entered isn't valid or has expired",
                    "Invalid invitation code",
                    JOptionPane.ERROR_MESSAGE);
            chooseRoom(null);
        } else {
            String room = response;
            System.out.println("Room name:" + room + "-");
            joinRoom(room);
        }
    }
}
