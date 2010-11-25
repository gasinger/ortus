/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media.favoriteexcluder;

import ortus.vars;

/**
 *
 * @author jphipps
 */
class FavExcItem extends vars {
	private String Exctype;
	private String Title;
	private String Action;
	private String Favorite;
	private String ScheduleType;

	public FavExcItem(String paction, String pexctype, String ptitle, String pschedtype, String pfavorite) {
		Exctype = pexctype.toUpperCase();
		Title = ptitle.toUpperCase();
		Action = paction.toUpperCase();
		Favorite = pfavorite.toUpperCase();
		ScheduleType = pschedtype.toUpperCase();
		ortus.api.DebugLog(LogLevel.Trace ,  "Loaded FE: " + Action + "-" + Exctype + "-" + Title + "-" + ScheduleType + "-" + Favorite );
	}

	public String toString() {
		return Action + "-" + Exctype + "-" + Title + "-" + ScheduleType + "-" + Favorite;
	}

	public String getAction() {
		return Action;
	}

	public String getExcType() {
		return Exctype;
	}

	public String getScheduleType() {
		return ScheduleType;
	}

	public boolean isChannel() {
		if ( Exctype.equals("CHANNEL"))
			return true;
		else
			return false;
	}
	public boolean isTitle() {
		if ( Exctype.equals("TITLE"))
			return true;
		else
			return false;
	}
	public boolean isTime() {
		if ( Exctype.equals("TIME"))
			return true;
		else
			return false;
	}
	public boolean isKeyword() {
		if ( Exctype.equals("KEYWORD"))
			return true;
		else
			return false;
	}
	public boolean isCatagory() {
		if ( Exctype.equals("CATAGORY"))
			return true;
		else
			return false;
	}

	public String getTitle() {
		return Title;
	}

	public boolean isWatched() {
		if ( Action.equals("WD"))
			return true;
		else
			return false;
	}

	public boolean isDontLike() {
		if ( Action.equals("DL"))
			return true;
		else
			return false;
	}

	public String getFavorite() {
		return Favorite;
	}

	public boolean isFavKeyword() {
		if ( ScheduleType.equals("FAVK"))
			return true;
		else
			return false;
	}
	public boolean isFavTitle() {
		if ( ScheduleType.equals("FAVT"))
			return true;
		else
			return false;
	}
}
