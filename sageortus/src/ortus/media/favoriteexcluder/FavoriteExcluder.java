package ortus.media.favoriteexcluder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import ortus.Ortus;
import ortus.mq.EventListener;
import ortus.mq.OrtusEvent;
import ortus.vars.LogLevel;
import sagex.api.AiringAPI;
import sagex.api.FavoriteAPI;
import sagex.api.Global;

/**
 * Favorite Excluder class
 * @author jphipps
 *
 */
public class FavoriteExcluder extends EventListener {
	private List<FavExcItem> FavExc = new ArrayList<FavExcItem>();
	
	public FavoriteExcluder() {
		super();
		
		ortus.api.DebugLog(LogLevel.Debug, "FavoriteExcluder: Loading");

		LoadFavoriteExcluderFile();

//		ortus.EventBus.eventEngine.getInstance().registerListener(this);

		ortus.api.DebugLog(LogLevel.Debug, "FavoriteExcluder: Load Completed");
	}

	@OrtusEvent("Shutdown")
	public void Shutdown() {
		ortus.api.DebugLog(LogLevel.Debug, " FavoriteExcluder: Shutdown");
			
		return;
	}
	/**
	 * Load the excluder.def file
	 */
	public void LoadFavoriteExcluderFile() {
		
		FavExc.clear();
		
		File ef = new File(Ortus.getInstance().getBasePath() + java.io.File.separator + "Configuration" + java.io.File.separator + "excluder.def");
		
		if ( ef.exists()) {
			try{
				FileInputStream fstream = new FileInputStream(ef);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				while ((strLine = br.readLine()) != null)   {
					String[] frec = strLine.split("-");
	
					FavExcItem fi = new FavExcItem(frec[0],frec[1],frec[2], frec[3], frec[4]);
					FavExc.add(fi);
				}
				in.close();
			}catch (Exception e){
				ortus.api.DebugLog(LogLevel.Error, "FavoriteExcluder exception: " + e );
				return;
			}
		}
	}
	/**
	 * Remove a excluder definition
	 * @param paction Action: WD - Watched, DL - Don't Like
	 * @param ptype Type: Title, Keyword, Channel
	 * @param ptitle Title
	 * @param pschedtype Schedule type: FAVK - Keyword favorite, FAVT - Title favorite
	 * @param pfavorite Favorite Name
	 */
	public void RemoveFavoriteExcluder(String favrule) {
		String[] frec = favrule.split("-");

		for ( Iterator<FavExcItem> iter = FavExc.iterator(); iter.hasNext();) {
			FavExcItem fe = iter.next();
			if ( fe.getAction().equals(frec[0]) &&
				 fe.getExcType().equals(frec[1]) &&
				 fe.getTitle().equals(frec[2]) &&
				 fe.getScheduleType().equals(frec[3]) &&
				 fe.getFavorite().equals(frec[4]))
				 iter.remove();
		}
		WriteExcluderFile();
	}
	/**
	 * Add a excluder definition
	 * @param paction Action: WD - Watched, DL - Don't Like
	 * @param ptype Type: Title, Keyword, Channel
	 * @param ptitle Title
	 * @param pschedtype Schedule type: FAVK - Keyword favorite, FAVT - Title favorite
	 * @param pfavorite Favorite Name
	 */
	public void AddFavoriteExcluder(String favrule) {
		String[] frec = favrule.split("-");

		FavExcItem fi = new FavExcItem(frec[0],frec[1],frec[2], frec[3], frec[4]);
		FavExc.add(fi);

		WriteExcluderFile();
	}

	public List GetFavoriteExcluder() {
		List<String> result = new ArrayList<String>();
		for ( Iterator<FavExcItem> iter = FavExc.iterator(); iter.hasNext();) {
			FavExcItem fe = iter.next();
			result.add(fe.toString());
		}
		return result;
	}
	
	/**
	 * Return true/false if system has a valid excluder.def file
	 * @return true/false
	 */
	public boolean ValidExcluderFile() {
		if ( FavExc.isEmpty() )
			return false;
		else
			return true;
	}
	
	private void WriteExcluderFile() {
		try{
			FileOutputStream fstream = new FileOutputStream(Ortus.getInstance().getBasePath() + java.io.File.separator + "Configuration" + java.io.File.separator + "excluder.def");
			DataOutputStream out = new DataOutputStream(fstream);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
			
			for ( Iterator<FavExcItem> iter = FavExc.iterator(); iter.hasNext();) {
				FavExcItem fe = iter.next();
				
				String outrec = fe.getAction() + "-" + fe.getExcType() + "-" + fe.getTitle() + "-" + fe.getScheduleType() + "-" + fe.getFavorite();
				bw.write(outrec);
			}
			out.close();
		}catch (Exception e){
			ortus.api.DebugLog(LogLevel.Error, "FavoriteExcluder exception: " + e );
			return;
		}
		
		return;
	}
	/**
	 * Mark all shows that match a excluder rule with the defined action
	 */
	@OrtusEvent("RunExcluder")
	public void RunExcluder() {
		 
		 ortus.api.DebugLog(LogLevel.Debug, "Favorite Excluder: Starting" );

		 int totalschedule = 0;
		 int totalsetwatched = 0;
		 int totalsetdontlike = 0;

		 List<Object> sched = new ArrayList<Object>(Arrays.asList(Global.GetScheduledRecordings()));

		 totalschedule = sched.size();

		 for( Iterator<Object> iter = sched.iterator(); iter.hasNext();) {
			 Object sa = iter.next();
			 
			 Object af = FavoriteAPI.GetFavoriteForAiring(sa);
			 
			 // rectype:   1 keyword, 2 title
			 int rectype = 0;
			 
			 if ( ! FavoriteAPI.GetFavoriteKeyword(af).isEmpty()) {
				 rectype = 1;
			 } else {
				 if ( ! FavoriteAPI.GetFavoriteTitle(af).isEmpty() ) {
					 rectype = 2;
				 }
			 }
					
			 boolean ExcludeAiring = false;
			 boolean SetWatched = true;
			 for ( Iterator<FavExcItem> favi = FavExc.iterator(); favi.hasNext(); ) {
				 FavExcItem fi = favi.next();
				 
				 if (( rectype == 1 && fi.isFavKeyword() && FavoriteAPI.GetFavoriteKeyword(af).toUpperCase().equals(fi.getFavorite())) || 
					   rectype == 2 && fi.isFavTitle() && FavoriteAPI.GetFavoriteTitle(af).toUpperCase().equals(fi.getFavorite())) {
					 
					 if ( fi.isChannel() && AiringAPI.GetAiringChannelNumber(sa).toUpperCase().equals(fi.getTitle())) {
						 ExcludeAiring = true;
						 if ( fi.isDontLike() )
							 SetWatched = false;
						 break;
					 }
					 if ( fi.isTitle() && AiringAPI.GetAiringTitle(sa).toUpperCase().equals(fi.getTitle())) {
						 ExcludeAiring = true;
						 if ( fi.isDontLike() )
							 SetWatched = false;
						 break;
					 }
					 if ( fi.isKeyword() && AiringAPI.GetAiringTitle(sa).toUpperCase().contains(fi.getTitle())) {
						 ExcludeAiring = true;
						 if ( fi.isDontLike() )
							 SetWatched = false;
						 break;
					 }
				 }
			 }
			 
			 if ( ExcludeAiring ) {
				 if ( SetWatched ) {
					 totalsetwatched++;
					ortus.api.DebugLog(LogLevel.Debug, "FE: marking " + AiringAPI.GetAiringTitle(sa) + " as watched" );
					AiringAPI.SetWatched(sa);
				 } else {
					 totalsetdontlike++;
					 ortus.api.DebugLog(LogLevel.Debug, "FE: marking " + AiringAPI.GetAiringTitle(sa) + " as dont like" );
					 AiringAPI.SetDontLike(sa);
				 }
			 }
		 }
		 ortus.api.DebugLog(LogLevel.Debug, "Total Airings: " + totalschedule + "  Marked: Watched: " + totalsetwatched + " DontLike: " + totalsetdontlike);
		 ortus.api.DebugLog(LogLevel.Debug, "Favorite Excluder: Completed" );
	 }
}

