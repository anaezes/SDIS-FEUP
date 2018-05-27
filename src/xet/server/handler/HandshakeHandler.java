package xet.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import xet.server.Server;
import xet.server.rooms.RoomsManager;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by ana on 5/24/18.
 */
public class HandshakeHandler implements HttpHandler {
    private Server server;

    public HandshakeHandler(Server server) {
        this.server = server;
    }

    public void handle(HttpExchange t) throws IOException {
       /* InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        int b;
        StringBuilder buf = new StringBuilder(512);
        while ((b = br.read()) != -1) {
            buf.append((char) b);
        }*/

        byte [] response = (server.getSocketPort()+";" + RoomsManager.Get().getPublicRooms().toString()).getBytes();

        t.sendResponseHeaders(200, response.length);
        OutputStream os = t.getResponseBody();
        os.write(response);
        os.close();
    }
}