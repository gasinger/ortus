/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.daemon;

import ortus.mq.EventListener;
import ortus.mq.OrtusEvent;

/**
 *
 * @author jphipps
 */
public class processEngine extends EventListener {
	private static Blocking_queue queue = null;
	private static Blocking_queue metadataqueue = null;
	private Thread_pool pool;

	public processEngine() {
		super();
		queue = new Blocking_queue();
		metadataqueue = new Blocking_queue();
		pool = new Thread_pool(10,10);
		ortus.api.DebugLog(ortus.vars.LogLevel.Debug, "processDaemon: Loading");
	}

	public void run() {
		ortus.api.DebugLog(ortus.vars.LogLevel.Debug, "processDaemon: Running");

		Thread metadataserver =
		new Thread()
		{	public void run()
			{
			  ortus.api.DebugLog(ortus.vars.LogLevel.Debug, "metadataDaemon: Running");
	                  try {
				Object medob;
				while( (medob=metadataqueue.dequeue()) != null ) {
				   if ( ((remoteCommand)medob).GetRemoteMethod().equalsIgnoreCase("shutdown"))
					   break;
				   ortus.process.execute(((remoteCommand)medob).GetRemoteMethod(), ((remoteCommand)medob).GetRemoteArgs());
				}	
			  } catch(Exception e) {
				ortus.api.DebugLog(ortus.vars.LogLevel.Error, "metadataServer: Command processor exception: " + e);
			  }
			}
		};

		metadataserver.start();

		try {
			Object s;
			while( (s=queue.dequeue()) != null ) {
			   if ( ((remoteCommand)s).GetRemoteMethod().equalsIgnoreCase("shutdown"))
				   break;
			   ortus.api.DebugLog(ortus.vars.LogLevel.Trace,"processDaemon: found " + ((remoteCommand)s).GetRemoteMethod());
			   pool.execute(new ortus.daemon.daemonProcess(((remoteCommand)s).GetRemoteMethod(),((remoteCommand)s).GetRemoteArgs()));
			}
		} catch (Exception ex) {
			ortus.api.DebugLog(ortus.vars.LogLevel.Error, "processDaemon: exception: " + ex);
		}
		ortus.api.DebugLog(ortus.vars.LogLevel.Debug,"processDaemon: Stopping");
	}

	@OrtusEvent("Shutdown")
	public void Shutdown() {
		AddQueue("Shutdown",null);
		AddMetadataQueue("Shutdown",null);
	}

	public void AddQueue(String methd, Object[] args) {
	    ortus.api.DebugLog(ortus.vars.LogLevel.Trace,"processDaemon: Queue: added: " + methd);
	    queue.enqueue(new remoteCommand(methd, args));
	}

	public void AddMetadataQueue(String methd, Object[] cmd) {
	    ortus.api.DebugLog(ortus.vars.LogLevel.Trace,"metadataDaemon: Queue: added: " + (String)((Object[])cmd)[0]);
	    metadataqueue.enqueue(cmd);
	}


}
