/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.daemon;

import ortus.Ortus;

/**
 *
 * @author jphipps
 */
public class Loader extends ortus.vars implements Runnable {

    public Loader() {
	System.out.println("Ortus.daemon.Loader: Bootstrapping Ortus");
        Ortus.getInstance();
    }

    public void run() {
//	    while(true) {
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException ex) {
//			ortus.api.DebugLog(LogLevel.Error,"daemonServer: sleep exception: " + ex);
//		}
//	    }
    }
}
