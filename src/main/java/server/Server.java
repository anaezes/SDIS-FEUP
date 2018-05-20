package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/*
 * a simple static http server
 */
public class Server {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new HelloHandler());
        server.createContext("/chat", new MessageHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        while(true);
    }

    static class HelloHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            int b;
            StringBuilder buf = new StringBuilder(512);
            System.out.println("pim");
            while ((b = br.read()) != -1) {
                buf.append((char) b);
            }
            System.out.println(query);
            byte [] response = "Welcome Real's HowTo test page".getBytes();
            t.sendResponseHeaders(200, response.length);
            OutputStream os = t.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    static class MessageHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            int b;
            StringBuilder buf = new StringBuilder(512);
            System.out.println("pim");
            while ((b = br.read()) != -1) {
                buf.append((char) b);
            }
            System.out.println(query);
            byte [] response = "Got your message".getBytes();
            t.sendResponseHeaders(200, response.length);
            OutputStream os = t.getResponseBody();
            os.write(response);
            os.close();
        }
    }


}