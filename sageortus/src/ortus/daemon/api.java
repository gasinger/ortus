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
public class api extends ortus.vars {

 public static Object executecCMD(String host, String methd, Object[] command) {
        int Port = 6666;
        ObjectInputStream ois;
        ObjectOutputStream oos;
        Object result = null;

//	((String)command[0]).replaceAll("_",".");
//        ortus.api.DebugLog(TRACE2, "executeCMD: running: " + command[0]);
//	for ( int x = 1; x< command.length;x++) {
//		ortus.api.DebugLog(TRACE2,"executeCMD:  parm #" + x + " : " + command[x]);
//	}
	
        try {
		Socket so = new Socket(host,  Port);
		oos = new ObjectOutputStream( so.getOutputStream());
		oos.writeObject(new remoteCommand(methd, command));
		ois = new ObjectInputStream( so.getInputStream());

		Object res = null;

		try {
		    res = ois.readObject();
		} catch ( Exception e ) {
			ortus.api.DebugLog(LogLevel.Error, "executeCMD: read exception: " , e);
		}

		if ( ois != null)
			ois.close();
		if ( oos !=null)
			oos.close();
		if ( so != null)
			so.close();

//		ortus.api.DebugLog(TRACE2, "executeCMD: completed");
		return ((remoteResponse)res).GetResponse();
        } catch ( Exception e ) {
            ortus.api.DebugLog(LogLevel.Error, "executeCMD: socket Exception: " , e);
            return null;
        }
    }
}
