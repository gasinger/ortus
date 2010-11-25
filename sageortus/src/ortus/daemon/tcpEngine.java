/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.daemon;

import java.net.ServerSocket;
import java.net.Socket;
import ortus.mq.EventListener;
import ortus.mq.OrtusEvent;

/**
 *
 * @author jphipps
 */
public class tcpEngine extends EventListener {
	private boolean tcp_enabled = true;
	private int Port = 6666;
	private Thread_pool pool;

	public tcpEngine() {
		super();
		pool = new Thread_pool(10,10);
		ortus.api.DebugLog(ortus.vars.LogLevel.Debug, "tcpDaemon: Loading");
//		ortus.EventBus.eventEngine.getInstance().registerListener(this);
	}

	public void run() {
		ortus.api.DebugLog(ortus.vars.LogLevel.Debug, "tcpDaemon: Running");
		ServerSocket serverSocket = null;
		try {
		     ortus.api.DebugLog(ortus.vars.LogLevel.Debug,"tcpDaemon: Command processor listening on port " + Port);
		     serverSocket = new ServerSocket(Port);
		     serverSocket.setSoTimeout(1000);
		     while(true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
			} catch (java.net.SocketTimeoutException e ) {}
			if ( socket != null )
				pool.execute(new serverWorker(socket));
			if ( ! tcp_enabled )
				break;
		      }
		} catch (Exception ex) {
			ortus.api.DebugLog(ortus.vars.LogLevel.Error, "tcpDaemon: exception: " + ex);
		} finally {
			if (serverSocket != null) 
				try { serverSocket.close(); } catch ( Exception e) {}
		}

		ortus.api.DebugLog(ortus.vars.LogLevel.Debug,"tcpDaemon: Stopping");
	}

	@OrtusEvent("Shutdown")
	public void Shutdown() {
		tcp_enabled = false;
	}
}
