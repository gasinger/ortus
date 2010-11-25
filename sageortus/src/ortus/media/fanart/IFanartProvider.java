/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media.fanart;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jphipps
 */
public interface IFanartProvider {
    public String GetFanartFolder();
    public void SetFanartFolder(String folder);
    public String GetFanartPoster(Object mediafile);
    public String GetFanartPosterThumb(Object mediafile);
    public String GetFanartPosterCover(Object mediafile);
    public String GetFanartPosterHigh(Object mediafile);
    public List<Object> GetFanartPosters(Object mediafile);
    public String GetFanartBanner(Object mediafile);
    public List<Object> GetFanartBanners(Object mediafile);
    public String GetFanartBackground(Object mediafile);
    public String GetFanartBackgroundThumb(Object mediafile);
    public String GetFanartBackgroundCover(Object mediafile);
    public String GetFanartBackgroundHigh(Object mediafile);
    public String GetFanartBackgroundHighRandom();
    public List<Object> GetFanartBackgrounds(Object mediafile);
    public List<Object> GetFanartRandom(List<Object> fanart);
    public String GetCastFanartPoster(String castname);
    public String GetSeasonFanartPoster(Object MediaObject);
    public String GetSeasonFanartBanner(Object MediaObject);
    public List<Object> GetSeasonFanartPosters(Object MediaObject);
    public List<Object> GetSeasonFanartBanners(Object MediaObject);
    public List<String> GetMenuBackgrounds(String Menutype);
    public String GetMenuBackground(String MenuType);
    public void CreateFanartFromJPG(Object[] MediaObjects);
    public Map<String,List> GetFanartCleanupList(Object[] MediaObjects,String Type);
    public void FanartCleanupMove (HashMap<String,List> FoldersMap,String Type);
    public String GetShowThumbnail(Object MediaObject);
    public void FanartCleanupDelete ( HashMap<String,List> FoldersMap);
    public void copy(File src, File dst);
}
