package xet.server.handler;

import xet.server.Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by ana on 5/24/18.
 */
public class HelloHandler implements HttpHandler {
    private Server server;

    public HelloHandler(Server server) {
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

        byte [] response = (server.getPort()+";" + server.getAvailableRooms().toString()).getBytes();

        t.sendResponseHeaders(200, response.length);
        OutputStream os = t.getResponseBody();
        os.write(response);
        os.close();
    }
}