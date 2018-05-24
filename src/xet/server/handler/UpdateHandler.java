package xet.server.handler;
import xet.server.Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;


/**
 * Created by ana on 5/24/18.
 */
public class UpdateHandler implements HttpHandler {
    private Server server;

    public UpdateHandler(Server server) {
        this.server = server;
    }
    public void handle(HttpExchange t) throws IOException {
        //todo
    }
}
