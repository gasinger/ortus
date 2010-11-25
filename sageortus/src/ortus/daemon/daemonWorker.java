/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.daemon;

import ortus.vars;

/**
 *
 * @author jphipps
 */
public class daemonWorker extends vars implements Runnable {
  String[] command;

  public daemonWorker(String command) {
    this.command = command.split(":");
  }

  //public
  //void finalize() {
  //System.err.println("-------------------------Finalize called");
  // System.err.flush();
  //}

  public void run() {
      
    ortus.api.DebugLog(LogLevel.Trace, Thread.currentThread().getName() + " : Running");

    for ( String cmd : command) 
        ortus.api.DebugLog(LogLevel.Trace, " command: " + cmd);

    if ( command[0].equals("runindex")) {
           if ( command.length == 3) {
                ortus.onlinescrapper.api.indexMediaAll(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
            } else {
                ortus.onlinescrapper.api.indexMedia();
            }
    }

//    if ( command[0].equals("missingfanart")) {
//        ortus.onlinescrapper.api.GetMissingFanart();
//    }
//    if ( command[0].equals("scanfanart")) {
//        ortus.onlinescrapper.api.GetScanFanart();
//    }
    if ( command[0].equals("indexrecording")) {
        ortus.onlinescrapper.api.indexMediaRecordings();
    }
    if ( command[0].equals("cleanmedia")) {
        ortus.onlinescrapper.api.cleanMedia();
    }

    ortus.api.DebugLog(LogLevel.Trace, "daemonWorkder: Completed " + command[0]);
    return;
  }
}
