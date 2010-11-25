/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.net;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jphipps
 */
public class httpHandler  implements HttpHandler {

    @Override
    public void handle(HttpExchange he) throws IOException {
        String requestMethod = he.getRequestMethod();
        if ( requestMethod.equalsIgnoreCase("get")) {


            Headers responseHeaders = he.getResponseHeaders();
            responseHeaders.set("Content-Type","text/html");
            he.sendResponseHeaders(200,0);

            OutputStream responseBody = he.getResponseBody();
            Headers requestHeaders = he.getRequestHeaders();
            URI uri = he.getRequestURI();
            String x = "<b> URI: " + uri.toString();
            responseBody.write(x.getBytes());
            
            Set<String> keySet = requestHeaders.keySet();
            Iterator<String> iter = keySet.iterator();
            while( iter.hasNext()) {
                String key = iter.next();
                List values = requestHeaders.get(key);
                String s = "<b> " + key + " = " + values.toString();
                responseBody.write(s.getBytes());
            }
            responseBody.close();
        }
    }
}
