package xet.server.handler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import xet.server.Server;

import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by ana on 5/24/18.
 */
public class UpdateHandler implements HttpHandler {
    private Server server;

    public UpdateHandler(Server server) {
        this.server = server;
    }
    public void handle(HttpExchange t) throws IOException {
        /*InputStreamReader isr =  new InputStreamReader(t.getRequestBody(),"utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        int b;
        StringBuilder buf = new StringBuilder(512);
        while ((b = br.read()) != -1) {
            buf.append((char) b);
        }
        System.out.println(query);*/
        byte [] response = "Update".getBytes();
        t.sendResponseHeaders(200, response.length);
        OutputStream os = t.getResponseBody();
        os.write(response);
        os.close();
    }
}
