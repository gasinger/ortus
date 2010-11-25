/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.daemon;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author jphipps
 */
class serverWorker extends ortus.vars implements Runnable {
  Socket socket;
  ObjectInputStream ois;
  ObjectOutputStream oos;

  public serverWorker(Socket socket) {
    this.socket = socket;
  }

  public void run() {
    String command;

    try {
      ois = new ObjectInputStream(socket.getInputStream());
    } catch(Exception e) {
      ortus.api.DebugLog(LogLevel.Error,"serverWorker: Could not open ObjectInputStream to " + socket + e);
    }
    try {
      if (ois != null) {
          remoteCommand rcmd = (remoteCommand)ois.readObject();
          oos = new ObjectOutputStream(socket.getOutputStream());
	  Object xyz = ortus.process.execute(rcmd.GetRemoteMethod(), rcmd.GetRemoteArgs());
	  remoteResponse ret = new remoteResponse(xyz);
          oos.writeObject(ret);
      }
    } catch(Exception e) {
      ortus.api.DebugLog(LogLevel.Info,"serverWorker: Unexpected exception. Closing conneciton." + e);
    } finally {
         try {
	    if (ois != null)
               ois.close();
	    if ( oos != null)
		oos.close();
	    if ( socket != null)
		socket.close();
         } catch(Exception e) {
            ortus.api.DebugLog(LogLevel.Info,"serverWorker: Could not close connection." + e);
         }      
    }
  }
}

