/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.web.resources;

import org.apache.log4j.Logger;

/**
 *
 * @author jphipps
 */
public class fanartResource {
    Logger log = Logger.getLogger(this.getClass());

    String fanartFolder = ortus.api.GetFanartFolder();
    
    public fanartResource() {
        log.trace("fanartResource: Loaded");
    }

    public String getPosterThumb(int mediaid) {
        return ortus.api.GetFanartPosterThumb(mediaid);
    }
    public String getPosterCover(int mediaid) {
        return ortus.api.GetFanartPosterCover(mediaid);
    }
    public String getPosterHigh(int mediaid) {
        return ortus.api.GetFanartPosterHigh(mediaid);
    }
    public String getBackgroundThumb(int mediaid) {
        return ortus.api.GetFanartBackgroundThumb(mediaid);
    }
    public String getBackgroundCover(int mediaid) {
        return ortus.api.GetFanartBackgroundCover(mediaid);
    }
    public String getBackgroundHigh(int mediaid) {
        return ortus.api.GetFanartBackgroundHigh(mediaid);
    }
    public String getBanner(int mediaid) {
        return ortus.api.GetFanartBanner(mediaid);
    }
}
