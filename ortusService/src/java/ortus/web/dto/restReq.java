/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.web.dto;

import org.apache.log4j.Logger;

/**
 *
 * @author jphipps
 */
public class restReq {
    Logger log = Logger.getLogger(this.getClass());
    boolean valid = false;
    private String service = "";
    private String serviceType = "";
    private String args = "";
    private int mediaid=0;

    public restReq(String pathInfo) {
        String[] req = pathInfo.split("/");

        if ( req.length < 2) {
            return;
        }

        service = req[1];
        log.debug("restReq: Service: " + service);
        valid = true;

        if ( req.length < 3)
            return;

        int x = 0;
        try {
            x = Integer.parseInt(req[2]);
        } catch (Exception ex) {}
        mediaid = x;
        log.debug("restReq: Mediaid: " + mediaid);

        if ( req.length < 4)
            return;

        serviceType = req[3];

        log.debug("restReq: Service Type: " + serviceType);

        if ( req.length < 5)
            return;

        args = req[4];

        log.debug("restReq: Args: " + args);

    }

    public boolean isValid() {
        return valid;
    }

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * @return the mediaid
     */
    public int getMediaid() {
        return mediaid;
    }

    /**
     * @return the args
     */
    public String getArgs() {
        return args;
    }
}
