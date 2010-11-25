/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ortus.media.metadata;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author jphipps
 */
public interface IMetadataProvider
{
        //Search
        public Object Search(String filter);
        public HashMap GetMetadata(Object mediafile);
        public HashMap GetMetadataFull(Object mediafile);
        public ortus.media.metadata.item.Media GetMetadataMedia(Object mediafile);
        public ortus.media.metadata.item.Episode GetMetadataEpisode(Object mediafile);
        public ortus.media.metadata.item.Series GetMetadataSeries(Object mediafile);
        public int GetMediaID(Object mediafile);
        public int GetEpisodeID(Object mediafile);
        public int GetSeriesID(Object mediafile);
        public boolean IsMetadataKeyMedia(String key);
        public boolean IsMetadataKeyEpisode(String key);
        public boolean IsMetadataKeySeries(String key);
        public int GetMetadataKeyValue(String key);

        public String GetFanartFolder();
        public void SetFanartFolder(String folder);

	//MediaType
	public int GetMediaType(Object mediafile);
        public void SetMediaType(Object mediafile, Object mediatype);
	public int GetMediaGroup(Object mediafile);
        public Object GetMediaForOrtusMedia(Object mediafile);
        public boolean IsOrtusMediaObject(Object mediafile);
        public long GetShowDuration(Object mediafile);
	public boolean IsTV(Object mediafile);
	public boolean IsTVMovie(Object mediafile);
	public boolean IsImportedTV(Object mediafile);
	public boolean IsRecorded(Object mediafile);
	public boolean IsImported(Object mediafile);
	public boolean IsSeries(Object mediafile);
	public boolean IsIntelligentRecorded(Object mediafile);
	public boolean IsFavorite(Object mediafile);
	public boolean IsHD(Object mediafile);
	public boolean IsSD(Object mediafile);
	public boolean IsMovie(Object mediafile);
	public boolean IsHDMovie(Object mediafile);
	public boolean IsSDMovie(Object mediafile);
	public boolean IsDVD(Object mediafile);
	public boolean IsBluRay(Object mediafile);

	//Common Metadata
        public HashMap GetMediaInfo(Object mediafile);
        public String GetAllmediaData(Object mediafile, String column);
	public String GetMediaTitle(Object mediafile);
	public void SetMediaTitle(Object mediafile, String newtitle);
	public String GetMediaFileID(Object mediafile);
	public String GetDescription(Object mediafile);
        public void SetDescription(Object mediafile, String title);
	public List<String> GetGenre(Object mediafile);
	public String GetImportDate(Object mediafile);
	public String GetPath(Object mediafile);
	public String GetMediaEncoding(Object mediafile);
	public String GetVideoEncoding(Object mediafile);
	public String GetAudioEncoding(Object mediafile);
	public String GetSubpicEncoding(Object mediafile);

	//TV Series Metadata
        public HashMap GetSeriesInfo(Object mediafile);
        public String GetSeriesData(Object mediafile, String column);
	public String GetSeriesTitle(Object mediafile);
	public String GetSeriesDescription(Object mediafile);
	public String GetSeriesNetwork(Object mediafile);
	public long GetSeriesFirstAirDate(Object mediafile);
	public long GetSeriesFinalAirDate(Object mediafile);
	public boolean IsSeriesStillRunning(Object mediafile);
	public String GetSeriesAirDay(Object mediafile);
	public long GetSeriesRunTime(Object mediafile);
	public long GetSeriesNextEpisodeDate(Object mediafile);
	public String GetSeriesTVRating(Object mediafile);
	public int GetSeriesTotalSeasons(Object mediafile);
	public int GetSeriesTotalEpisodes(Object mediafile);
	public int GetSeriesTotalEpisodesAvailable(Object mediafile);

	//TV Metadata
        public HashMap GetEpisodeInfo(Object mediafile);
        public String GetEpisodeData(Object mediafile, String column);
	public String GetShowTitle(Object mediafile);
	public String GetEpisodeTitle(Object mediafile);
        public String GetEpisodeDescription(Object mediafile);
	public int GetSeasonNumber(Object mediafile);
	public int GetEpisodeNumber(Object mediafile);
	public long GetOriginalAirDate(Object mediafile);

	//Movie Metadata
	public String GetReleaseDate(Object mediafile);
	public void SetReleaseDate(Object mediafile, String newreleasedate);
	public String GetMPAARating(Object mediafile);
	public void SetMPAARating(Object mediafile, String newmpaarating);
	public int GetUserRating(Object mediafile);
        public String GetUserRatingString(Object mediafile);
	public void SetUserRating(Object mediafile, String newuserrating);
	public int GetDiscNumber(Object mediafile);

	//Cast Metadata
	public List<HashMap> GetCast(Object mediafile,String job);
	public String GetDirector(Object mediafile);
	public String GetWriter(Object mediafile);
	public String GetProducer(Object mediafile);
	public List<String> GetActors(Object mediafile);
	public List<HashMap<String,String>> GetSeries(Object seriesid, boolean allepisodes, Object seasonno);

	// Music
	public List<HashMap> GetMusicByArtist(String filter);
	public List<HashMap> GetMusicByAlbum(String filter);
	public List<Object> GetMusicBySong(String filter);
}
