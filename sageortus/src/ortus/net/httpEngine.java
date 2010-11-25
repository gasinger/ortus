/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.net;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import ortus.mq.EventListener;
import ortus.mq.OrtusEvent;
import ortus.vars.LogLevel;

/**
 *
 * @author jphipps
 */
public class httpEngine extends EventListener {
    HttpServer server = null;
    private boolean running = true;

    public httpEngine() {
        super();
        
        ortus.api.DebugLog(LogLevel.Trace, "httpEngine: Starting");

        try {
            InetSocketAddress addr = new InetSocketAddress(8989);
            server = HttpServer.create(addr,0);
            server.createContext("/", new httpHandler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
        } catch(Exception e) {}
    }

   public void run() {
	ortus.api.DebugLog(ortus.vars.LogLevel.Debug, "httpEngine: Running");

        while(running) {
                try {
                        Thread.sleep(1000);
                } catch (Exception ex) {
                        ortus.api.DebugLog(ortus.vars.LogLevel.Error, "httpEngine: exception: " + ex);
                }
        }
        server.stop(MIN_PRIORITY);
        ortus.api.DebugLog(ortus.vars.LogLevel.Debug,"httpEngine: Stopping");
	}

   @OrtusEvent("Shutdown")
   public void Shutdown() {
       running = false;
   }

}
