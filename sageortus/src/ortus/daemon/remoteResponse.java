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
public class remoteResponse extends ortus.vars implements Serializable {
	Object response = null;
	public remoteResponse(Object response) {
//		DebugLog(TRACE2,"remoteResponse: build with " + response);
		this.response = response;
	}

	public Object GetResponse() {
//		DebugLog(TRACE2,"remoteResponse: GetResponse: " + response);
		return response;
	}
}
