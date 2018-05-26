package xet.server.handler;
import xet.server.Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import xet.server.UsersManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by ana on 5/24/18.
 */
public class RoomHandler implements HttpHandler {
    private Server server;

    public RoomHandler(Server server) {
        this.server = server;
    }

    public void handle(HttpExchange t) throws IOException {
        InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        int b;
        StringBuilder buf = new StringBuilder(512);
        while ((b = br.read()) != -1) {
            buf.append((char) b);
        }

        String[] parts = query.split("&");

        String username = parts[0];
        String[] user = username.split("=");

        String room = parts[1];
        String[] r = room.split("=");

        System.out.println("username: " + UsersManager.Get().getUserName(user[1]));
        System.out.println("room: " + r[1]);

        //create new room if don't exist
        if(!server.getAvailableRooms().contains(r[1])) {
            server.addRoom(r[1]);
        }

        new Thread(() -> {
            server.makeSSLConnection(user[1], r[1]);
        }).start();

        byte [] response = "Room choosen! ".getBytes();
        t.sendResponseHeaders(200, response.length);
        OutputStream os = t.getResponseBody();
        os.write(response);
        os.close();
    }
}
