/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.daemon;

import java.io.Serializable;

/**
 *
 * @author jphipps
 */
public class remoteCommand implements Serializable {
	private static final long serialVersionUID = 1L;
	public int length = 0;
        String methd = null;
	Object[] rcmd = null;

	public remoteCommand(String methd, Object[] rcmd) {
                this.methd = methd;
		this.rcmd = rcmd;
                if ( rcmd == null)
                    this.length = 0;
                else
                    length = this.rcmd.length;
	}
	public String GetRemoteMethod() {
		return methd;
	}

	public Object[] GetRemoteArgs() {
		return rcmd;
	}

}
