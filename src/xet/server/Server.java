package xet.server;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import xet.server.handler.HelloHandler;

/*
 * a simple static http xet.server
 */
public class Server {
    public final static String URL_HELLO = "/hello";
    public final static String URL_ROOM = "/room";
    public final static String URL_MESSAGE = "/Message";
    public final static String URL_UPDATE = "/Update";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext(URL_HELLO, new HelloHandler());
        server.createContext(URL_ROOM, new RoomHandler());
        server.createContext(URL_MESSAGE, new MessageHandler());
        //server.createContext(URL_UPDATE, new UpdateHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }


    static class MessageHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            int b;
            StringBuilder buf = new StringBuilder(512);
            while ((b = br.read()) != -1) {
                buf.append((char) b);
            }
            System.out.println(query);
            byte [] response = "Got your xet.message".getBytes();
            t.sendResponseHeaders(200, response.length);
            OutputStream os = t.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    static class RoomHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            int b;
            StringBuilder buf = new StringBuilder(512);
            while ((b = br.read()) != -1) {
                buf.append((char) b);
            }
            System.out.println(query);
            byte [] response = "Room choosen!!!! ".getBytes();
            t.sendResponseHeaders(200, response.length);
            OutputStream os = t.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}