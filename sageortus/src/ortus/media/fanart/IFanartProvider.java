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

    public Object GetTVFanart(String seriesid, String type, String quality);
    public Object GetTVFanartRandom(String seriesid, String type, String quality);
    public List<Object> GetTVFanartAll(String seriesid, String type, String quality);
    public Object GetFanartPoster(Object mediafile);
    public List<Object> GetFanartPosterAll(Object mediafile);
    public Object GetFanartPosterRandom(Object mediafile);
    public Object GetRandom(List<Object> fitems);

    public Object GetFanartBackground(Object mediafile);
    public List<Object> GetFanartBackgroundAll(Object mediafile);
    public Object GetFanartBackgroundRandom(Object mediafile);

    public Object GetFanartBanner(Object mediafile);
    public List<Object> GetFanartBannerAll(Object mediafile);
    public Object GetFanartBannerRandom(Object mediafile);
    
    public Object GetFanartPosterLow(Object mediafile);
    public List<Object> GetFanartPosterLowAll(Object mediafile);
    public Object GetFanartPosterLowRandom(Object mediafile);
    public Object GetFanartPosterMedium(Object mediafile);
    public List<Object> GetFanartPosterMediumAll(Object mediafile);
    public Object GetFanartPosterMediumRandom(Object mediafile);
    public Object GetFanartPosterHigh(Object mediafile);
    public List<Object> GetFanartPosterHighAll(Object mediafile);
    public Object GetFanartPosterHighRandom(Object mediafile);
    
    public Object GetFanartBackgroundLow(Object mediafile);
    public List<Object> GetFanartBackgroundLowAll(Object mediafile);
    public Object GetFanartBackgroundLowRandom(Object mediafile);
    public Object GetFanartBackgroundMedium(Object mediafile);
    public List<Object> GetFanartBackgroundMediumAll(Object mediafile);
    public Object GetFanartBackgroundMediumRandom(Object mediafile);
    public Object GetFanartBackgroundHigh(Object mediafile);
    public List<Object> GetFanartBackgroundHighAll(Object mediafile);
    public Object GetFanartBackgroundHighRandom(Object mediafile);

    public Object GetFanartBannerLow(Object mediafile);
    public List<Object> GetFanartBannerLowAll(Object mediafile);
    public Object GetFanartBannerLowRandom(Object mediafile);
    public Object GetFanartBannerMedium(Object mediafile);
    public List<Object> GetFanartBannerMediumAll(Object mediafile);
    public Object GetFanartBannerMediumRandom(Object mediafile);
    public Object GetFanartBannerHigh(Object mediafile);
    public List<Object> GetFanartBannerHighAll(Object mediafile);
    public Object GetFanartBannerHighRandom(Object mediafile);

    public Object GetFanartEpisodeLow(Object mediafile);
    public List<Object> GetFanartEpisodeLowAll(Object mediafile);
    public Object GetFanartEpisodeLowRandom(Object mediafile);
    public Object GetFanartEpisodeMedium(Object mediafile);
    public List<Object> GetFanartEpisodeMediumAll(Object mediafile);
    public Object GetFanartEpisodeMediumRandom(Object mediafile);
    public Object GetFanartEpisodeHigh(Object mediafile);
    public List<Object> GetFanartEpisodeHighAll(Object mediafile);
    public Object GetFanartEpisodeHighRandom(Object mediafile);
    
    public Object GetCastFanartLow(String castname);
    public Object GetCastFanartMedium(String castname);
    public Object GetCastFanartHigh(String castname);

    public Object GetSeasonFanartPoster(Object MediaObject);
    public List<Object> GetSeasonFanartPosterAll(Object MediaObject);
    public Object GetSeasonFanartBanner(Object MediaObject);
    public List<Object> GetSeasonFanartBannerAll(Object MediaObject);

    public Object GetFanartForID(int id, String resolution);
    
    public List<String> GetMenuBackgrounds(String Menutype);
    public String GetMenuBackground(String MenuType);
    public void CreateFanartFromJPG(Object[] MediaObjects);
    public Map<String,List> GetFanartCleanupList(Object[] MediaObjects,String Type);
    public void FanartCleanupMove (HashMap<String,List> FoldersMap,String Type);
    public String GetShowThumbnail(Object MediaObject);
    public void FanartCleanupDelete ( HashMap<String,List> FoldersMap);
    public void copy(File src, File dst);
}
