/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.daemon;

/**
 *
 * @author jphipps
 */
public class daemonProcess extends ortus.vars implements Runnable {
  String methd = null;
  Object[] args = null;

  public daemonProcess(String methd, Object args) {
    this.methd = methd;
    this.args = (Object[])args;
  }

  public void run() {

//    DebugLog(TRACE, Thread.currentThread().getName() + " : Running method: " + args[0]);

    ortus.process.execute(methd,args);
    
//    DebugLog(TRACE, "daemonWorkder: Completed");
    return;
  }
}
