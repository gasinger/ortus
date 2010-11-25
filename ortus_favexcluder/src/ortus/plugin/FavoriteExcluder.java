/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import sagex.UIContext;
import sagex.api.AiringAPI;
import sagex.api.Configuration;
import sagex.api.FavoriteAPI;
import sagex.api.Global;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;
/**
 *
 * @author jphipps
 */
public class FavoriteExcluder {
  public static String version = "2.7";
  private static final String DEF_FILE_NAME = "excluder.def";
  private static final String LOG_FILE_NAME = "excluder.log";
  private static final String FILE_DIR_PROPERTY = "plugins/df/favorite_excluder/file_dir";
  public static final String DONTLIKE_KEYWORD = "DL-KEYWORD";
  public static final String DONTLIKE_TITLE = "DL-TITLE";
  public static final String DONTLIKE_ACTOR = "DL-ACTOR";
  public static final String DONTLIKE_CATEGORY = "DL-CATEGORY";
  public static final String DONTLIKE_CHANNEL = "DL-CHANNEL";
  public static final String DONTLIKE_TIME = "DL-TIME";
  public static final String WATCHED_KEYWORD = "WD-KEYWORD";
  public static final String WATCHED_TITLE = "WD-TITLE";
  public static final String WATCHED_ACTOR = "WD-ACTOR";
  public static final String WATCHED_CATEGORY = "WD-CATEGORY";
  public static final String WATCHED_CHANNEL = "WD-CHANNEL";
  public static final String WATCHED_TIME = "WD-TIME";
  public static final String KEYWORD_FAV = "FAVK";
  public static final String TITLE_FAV = "FAVT";
  public static final String CATEGORY_FAV = "FAVC";
  public static final String ACTOR_FAV = "FAVA";
  private static final int MARK_WATCHED = 1;
  private static final int MARK_DONTLIKE = 2;
  private static final int KEYWORD = 1;
  private static final int TITLE = 2;
  private static final int ACTOR = 3;
  private static final int CATEGORY = 4;
  private static final int CHANNEL = 5;
  private static final int TIME = 6;
  private static final int MAX_LINES = 1000;
  private static Map<String, Tag> defMap = new HashMap();

  private static Map<String, Object> keywordFavorites = new HashMap();
  private static Map<String, Object> titleFavorites = new HashMap();
  private static Map<String, Object> categoryFavorites = new HashMap();
  private static Map<String, Object> actorFavorites = new HashMap();

  public static void init()
    throws InvocationTargetException
  {
    readDefinitions();
  }
  	public static List<String> getLogLines()
	{
		File ifile = new File(LOG_FILE_NAME);
		List<String> LogEntries = new ArrayList<String>();

		try {
			if( ifile.exists())
				LogEntries = FileUtils.readLines(ifile);
			return LogEntries;
		} catch (Exception e) {			
			return LogEntries;
		}
	}
	
  public static synchronized void excludeAirings() throws InvocationTargetException {

    BufferedWriter file = null;
    try
    {
      List oldLogs = getLogLines();

      String dir = Configuration.GetProperty("plugins/df/favorite_excluder/file_dir",".");

      file = new BufferedWriter(new FileWriter(dir + java.io.File.separator + "excluder.log"));

      int lookahead = Integer.parseInt(Configuration.GetProperty(new UIContext(Global.GetUIContextName()), "plugins/df/favorite_excluder/lookahead", "336"));
      String scan = Configuration.GetProperty("plugins/df/favorite_excluder/scan", "schedule");

      long now = System.currentTimeMillis();

      DateFormat dtf = DateFormat.getDateTimeInstance(2, 2);
      Object[] airings;
      if (scan.equalsIgnoreCase("schedule")) {
	Global.DebugLog("FavExc: Using context: " + Global.GetUIContextName() + " Lookahead: " + lookahead);
        airings = Global.GetScheduledRecordingsForTime(new UIContext(Global.GetUIContextName()), new Long(now), new Long(now + lookahead * 60 * 60 * 1000));
	Global.DebugLog("FavExc Found " + airings.length + " Shows");
        file.write(dtf.format(new Date()) + " Scanning scheduled recordings ...");
        file.newLine();
        Global.DebugLog("Scanning scheduled recordings ...");
      } else {
        airings = Global.GetSuggestedIntelligentRecordings();
        file.write(dtf.format(new Date()) + " Scanning IR suggestions ...");
        file.newLine();
        Global.DebugLog("Scanning IR suggestions ...");
      }

      file.write("");
      file.newLine();

      Global.DebugLog("FavoriteExcluder processing " + airings.length + " shows");

      int markCount = 0;
      Set invalidTags = new HashSet();

      DateFormat df = DateFormat.getDateInstance(3);
      DateFormat tf = DateFormat.getTimeInstance(3);

      Global.DebugLog("Scanning airings...");

      UIContext UIC = new UIContext(Global.GetUIContextName());

      for (int i = 0; i < airings.length; ++i)
      {
	Object show = airings[i];

        String title = ShowAPI.GetShowTitle(UIC, show).toUpperCase();
        String episode = ShowAPI.GetShowEpisode(UIC,show).toUpperCase();
        String description = ShowAPI.GetShowDescription(UIC,show).toUpperCase();
        String actors = ShowAPI.GetPeopleInShow(UIC,show).toUpperCase();
        String category = ShowAPI.GetShowCategory(UIC,show).toUpperCase() +
          "-" + ShowAPI.GetShowSubCategory(UIC, show).toUpperCase();
        String channelName = AiringAPI.GetAiringChannelName(UIC, airings[i]).toUpperCase();
        String channelNo = AiringAPI.GetAiringChannelNumber(UIC, airings[i]);

        long start = ((Long)AiringAPI.GetAiringStartTime(UIC, airings[i])).longValue();
        long end = ((Long)AiringAPI.GetAiringEndTime(UIC, airings[i])).longValue();

        Date startTime = new Date(start);
        Date endTime = new Date(end);

        String airingDetail = df.format(startTime) +
          " " + tf.format(startTime) +
          " - " + tf.format(endTime) +
          ", " + channelName;

        boolean isManual = AiringAPI.IsManualRecord(UIC, airings[i]);
        boolean isFavorite = AiringAPI.IsFavorite(UIC, airings[i]);
        boolean isRecording = AiringAPI.GetMediaFileForAiring(UIC, airings[i]) != null;

        if (isManual) {
          Global.DebugLog("Skipping manual recording " + title);
        }
        else if (isRecording) {
          Global.DebugLog("Skipping currently recording show " + title);
        }
        else
        {
          for (Tag tag : defMap.values())
          {
            Global.DebugLog("Trying to match " + title + " with " + tag.toString());

            if (isFavorite)
            {
              if ( tag instanceof FavTag ) {
	      } else {
                Global.DebugLog("Skipped");
                continue;
              }

              Object baseFavorite = ((FavTag)tag).baseFav;

              boolean showMatchedByFav = FavoriteAPI.DoesFavoriteMatchAiring(UIC, baseFavorite, airings[i]);

              if (!showMatchedByFav) {
                Global.DebugLog("Skipped");
                continue;
              }

              Global.DebugLog("Favorite recording " + title + ": base fav matches");
            }
            else if (tag instanceof FavTag) {
              Global.DebugLog("Skipped");
              continue;
            }

            boolean match = false;

            switch (tag.scope)
            {
            case 1:
              if ((title.indexOf(tag.match) != -1) ||
                (actors.indexOf(tag.match) != -1) ||
                (episode.indexOf(tag.match) != -1) ||
                (description.indexOf(tag.match) != -1))
              {
                match = true;
                Global.DebugLog(title + ": keyword matched in title/actors/episode/desc");
              }
              break;
            case 2:
              if (title.indexOf(tag.match) != -1)
              {
                match = true;
                Global.DebugLog(title + ": keyword matched in title");
              }
              break;
            case 3:
              if (actors.indexOf(tag.match) != -1)
              {
                match = true;
                Global.DebugLog(title + ": keyword matched in actors");
              }
              break;
            case 4:
              if (category.indexOf(tag.match) != -1)
              {
                match = true;
                Global.DebugLog(title + ": keyword matched in category");
              }
              break;
            case 5:
              if (channelNo.equals(tag.match))
              {
                match = true;
                Global.DebugLog(title + ": channel matched");
              }

              break;
            case 6:
              try
              {
                Date excludeFrom = null;
                Date excludeTo = null;

                if ((tag.match.indexOf("AM") != -1) || (tag.match.indexOf("PM") != -1))
                {
                  int index = tag.match.indexOf("-");

                  Date fromTime = tf.parse(tag.match.substring(0, index));
                  Calendar fromCal = Calendar.getInstance();
                  fromCal.setTime(fromTime);

                  Date toTime = tf.parse(tag.match.substring(index + 1));
                  Calendar toCal = Calendar.getInstance();
                  toCal.setTime(toTime);

                  Global.DebugLog("fromTime = " + fromCal.toString());
                  Global.DebugLog("toTime   = " + toCal.toString());

                  Calendar cal = Calendar.getInstance();
                  cal.setTime(startTime);
                  cal.set(11, fromCal.get(11));
                  cal.set(12, fromCal.get(12));

                  excludeFrom = cal.getTime();

                  cal.setTime(endTime);
                  cal.set(11, toCal.get(11));
                  cal.set(12, toCal.get(12));

                  excludeTo = cal.getTime();

                  Global.DebugLog("from = " + excludeFrom);
                  Global.DebugLog("to   = " + excludeTo);
                }
                else
                {
                  int hour = Integer.parseInt(tag.match.substring(0, 2));
                  int min = Integer.parseInt(tag.match.substring(3, 5));

                  Calendar cal = Calendar.getInstance();
                  cal.setTime(startTime);
                  cal.set(11, hour);
                  cal.set(12, min);

                  excludeFrom = cal.getTime();

                  hour = Integer.parseInt(tag.match.substring(6, 8));
                  min = Integer.parseInt(tag.match.substring(9));

                  cal.setTime(startTime);
                  cal.set(11, hour);
                  cal.set(12, min);

                  excludeTo = cal.getTime();
                }

                if ((startTime.after(excludeFrom)) && (startTime.before(excludeTo)))
                {
                  match = true;
                  Global.DebugLog(title + ": time matched");
                }
              }
              catch (Exception e)
              {
                if (!invalidTags.contains(tag)) {
                  file.write("Invalid exclude time '" + tag.match + "' in excluder definition");
                  file.newLine();
                  invalidTags.add(tag);
                }
              }

            }

            if (!match)
              continue;
            Global.DebugLog("Excluder matched: " + tag.toString());

            if (tag.action == 2)
            {
              Global.DebugLog("[Marking as DONTLIKE]: " + title);

              AiringAPI.SetDontLike(airings[i]);
              file.write(title + "(" + airingDetail + ") marked as 'Dont Like' (" + tag.getScope() + ")");
              file.newLine();
            }
            else
            {
              Global.DebugLog("[Marking as WATCHED]: " + title);

              AiringAPI.SetWatched(airings[i]);
              file.write(title + "(" + airingDetail + ") marked as 'Watched' (" + tag.getScope() + ")");
              file.newLine();
            }

            ++markCount;
          }
        }
      }

      switch (markCount)
      {
      case 0:
        file.write("No shows marked.");
        break;
      case 1:
        file.write("1 show marked.");
        break;
      default:
        file.write(markCount + " shows marked.");
      }

      file.newLine();

      file.write(" ");
      file.newLine();
      file.write(" ");
      file.newLine();
      file.write(" ");
      file.newLine();

      for (int i = 0; i < oldLogs.size(); ++i)
      {
        if (i > 1000) {
          break;
        }
        file.write((String)oldLogs.get(i));
        file.newLine();
      }

      file.flush();
    }
    catch (IOException e) {
      throw new InvocationTargetException(e);
    } finally {
      if (file != null)
        try {
          file.close();
        }
        catch (IOException localIOException1)
        {
        }
    } }

  public static boolean addDefinition(String def) throws InvocationTargetException {
    def = def.toUpperCase();

    Tag tag = parseDef(def, true);

    if (tag != null)
    {
      defMap.put(def, tag);
      Global.DebugLog("Added excluder definition: " + def);
      writeDefinitions();

      return true;
    }

    Global.DebugLog("Excluder definition " + def + " not valid");
    return false;
  }

  public static boolean removeDefinition(String def)
  {
    def = def.toUpperCase();

    Tag tag = (Tag)defMap.remove(def);

    if (tag != null)
    {
      Global.DebugLog("Removed excluder definition: " + def);
      writeDefinitions();

      return true;
    }

    Global.DebugLog("Excluder definition " + def + " not found");
    return false;
  }

  public static List<String> getDefinitions()
  {
    List list = new ArrayList();

    for (String def : defMap.keySet()) {
      list.add(def);
    }

    return list;
  }

  private static boolean readDefinitions() throws InvocationTargetException
  {
    BufferedReader r = null;
    defMap.clear();
    try
    {
      String dir = Configuration.GetProperty("plugins/df/favorite_excluder/file_dir",".");

      File file = new File(dir + "\\" + "excluder.def");

      Global.DebugLog("Reading excluder definitions from " + file.getPath());

      if (!file.exists()) {
        Global.DebugLog("Definitions does not exist");
        return false;
      }

      readFavorites();

      r = new BufferedReader(new FileReader(file));

      String def = r.readLine();

      while (def != null)
      {
        def = def.toUpperCase();

        if ((!def.startsWith("#")) && (def.length() != 0))
        {
          Tag tag = parseDef(def, false);

          if (tag != null)
            defMap.put(def, tag);
          else {
            Global.DebugLog("Invalid excluder definition " + def + " in file");
          }
        }

        def = r.readLine();
      }

      return true;
    }
    catch (IOException e) {
      return false;
    } finally {
      if (r != null)
        try {
          r.close();
        }
        catch (IOException localIOException4)
        {
        }
    }
  }

  private static boolean writeDefinitions() {
    BufferedWriter w = null;
    try
    {
      String dir = Configuration.GetProperty("plugins/df/favorite_excluder/file_dir", ".");

      File file = new File(dir + "\\" + "excluder.def");

      Global.DebugLog("Writing excluder definitions to " + file.getPath());

      w = new BufferedWriter(new FileWriter(file));

      for (String def : defMap.keySet()) {
        w.write(def);
        w.newLine();
      }

      return true;
    }
    catch (IOException e) {
      return false;
    } finally {
      if (w != null)
        try {
          w.close();
        }
        catch (IOException localIOException3)
        {
        }
    }
  }

  private static Tag parseDef(String def, boolean readFavs) throws InvocationTargetException {
    Global.DebugLog("parsing excluder definition " + def);

    if (readFavs) {
      readFavorites();
    }

    String match = def;

    int keywordFavxIndex = def.indexOf("FAVK");
    int titleFavxIndex = def.indexOf("FAVT");
    int categoryFavxIndex = def.indexOf("FAVC");
    int actorFavxIndex = def.indexOf("FAVA");

    String favxMatch = null;

    if (keywordFavxIndex != -1) {
      match = def.substring(0, keywordFavxIndex - 1);
      favxMatch = def.substring(keywordFavxIndex + 5);
    } else if (titleFavxIndex != -1) {
      match = def.substring(0, titleFavxIndex - 1);
      favxMatch = def.substring(titleFavxIndex + 5);
    } else if (actorFavxIndex != -1) {
      match = def.substring(0, actorFavxIndex - 1);
      favxMatch = def.substring(actorFavxIndex + 5);
    } else if (categoryFavxIndex != -1) {
      match = def.substring(0, categoryFavxIndex - 1);
      favxMatch = def.substring(categoryFavxIndex + 5);
    }

    String tagMatch = null;
    int tagScope = 0;
    int tagAction = 0;

    if (match.startsWith("DL-"))
    {
      if (match.startsWith("DL-KEYWORD"))
      {
        tagMatch = match.substring("DL-KEYWORD".length() + 1).toUpperCase();
        tagScope = 1;
        Global.DebugLog("Found dontlike keyword criteria: " + tagMatch);
      }
      else if (match.startsWith("DL-TITLE"))
      {
        tagMatch = match.substring("DL-TITLE".length() + 1).toUpperCase();
        tagScope = 2;
        Global.DebugLog("Found dontlike title criteria: " + tagMatch);
      }
      else if (match.startsWith("DL-ACTOR"))
      {
        tagMatch = match.substring("DL-ACTOR".length() + 1).toUpperCase();
        tagScope = 3;
        Global.DebugLog("Found dontlike actor criteria: " + tagMatch);
      }
      else if (match.startsWith("DL-CATEGORY"))
      {
        tagMatch = match.substring("DL-CATEGORY".length() + 1).toUpperCase();
        tagScope = 4;
        Global.DebugLog("Found dontlike category criteria: " + tagMatch);
      }
      else if (match.startsWith("DL-CHANNEL"))
      {
        tagMatch = match.substring("DL-CHANNEL".length() + 1).toUpperCase();
        tagScope = 5;
        Global.DebugLog("Found dontlike channel criteria: " + tagMatch);
      }
      else if (match.startsWith("DL-TIME"))
      {
        tagMatch = match.substring("DL-TIME".length() + 1).toUpperCase();
        tagScope = 6;
        Global.DebugLog("Found dontlike time criteria: " + tagMatch);
      }

      tagAction = 2;
    }
    else if (match.startsWith("WD-"))
    {
      if (match.startsWith("WD-KEYWORD"))
      {
        tagMatch = match.substring("WD-KEYWORD".length() + 1).toUpperCase();
        tagScope = 1;
        Global.DebugLog("Found dontlike keyword criteria: " + tagMatch);
      }
      else if (match.startsWith("WD-TITLE"))
      {
        tagMatch = match.substring("WD-TITLE".length() + 1).toUpperCase();
        tagScope = 2;
        Global.DebugLog("Found watched title criteria: " + tagMatch);
      }
      else if (match.startsWith("WD-ACTOR"))
      {
        tagMatch = match.substring("WD-ACTOR".length() + 1).toUpperCase();
        tagScope = 3;
        Global.DebugLog("Found watched actor criteria: " + tagMatch);
      }
      else if (match.startsWith("WD-CATEGORY"))
      {
        tagMatch = match.substring("WD-CATEGORY".length() + 1).toUpperCase();
        tagScope = 4;
        Global.DebugLog("Found watched category criteria: " + tagMatch);
      }
      else if (match.startsWith("WD-CHANNEL"))
      {
        tagMatch = match.substring("WD-CHANNEL".length() + 1).toUpperCase();
        tagScope = 5;
        Global.DebugLog("Found watched channel criteria: " + tagMatch);
      }
      else if (match.startsWith("WD-TIME"))
      {
        tagMatch = match.substring("WD-TIME".length() + 1).toUpperCase();
        tagScope = 6;
        Global.DebugLog("Found watched time criteria: " + tagMatch);
      }

      tagAction = 1;
    }

    if (tagMatch == null) {
      Global.DebugLog("Could not parse " + def);
      return null;
    }

    Tag result = null;

    if (favxMatch != null)
    {
      FavTag favTag = null;
      Object fav = null;

      if (keywordFavxIndex != -1) {
        favTag = new FavTag(tagMatch, tagScope, tagAction, favxMatch, 1);
        fav = keywordFavorites.get(favxMatch);
      } else if (titleFavxIndex != -1) {
        favTag = new FavTag(tagMatch, tagScope, tagAction, favxMatch, 2);
        fav = titleFavorites.get(favxMatch);
      } else if (actorFavxIndex != -1) {
        favTag = new FavTag(tagMatch, tagScope, tagAction, favxMatch, 3);
        fav = actorFavorites.get(favxMatch);
      } else if (categoryFavxIndex != -1) {
        favTag = new FavTag(tagMatch, tagScope, tagAction, favxMatch, 4);
        fav = categoryFavorites.get(favxMatch);
      }

      if (fav != null)
      {
        favTag.baseFav = fav;
        result = favTag;

        Global.DebugLog("Found existing base favorite: " +
          favTag.match + " - " + favTag.baseFavMatch);
      } else {
        Global.DebugLog("No base favorite found for: " +
          favTag.match + " - " + favTag.baseFavMatch);
      }
    }
    else {
      result = new Tag(tagMatch, tagScope, tagAction);
    }

    return result;
  }

  private static void readFavorites() throws InvocationTargetException
  {
    Global.DebugLog("Reading SageTV favorites ...");

    keywordFavorites.clear();
    titleFavorites.clear();
    categoryFavorites.clear();
    actorFavorites.clear();

    UIContext UIC = new UIContext(Global.GetUIContextName());

    Object[] favs = FavoriteAPI.GetFavorites(UIC);

    for (int i = 0; i < favs.length; ++i)
    {
      String keyword = FavoriteAPI.GetFavoriteKeyword(UIC, favs[i]).toUpperCase();
      String title = FavoriteAPI.GetFavoriteTitle(UIC,favs[i]).toUpperCase();
      String category = FavoriteAPI.GetFavoriteCategory(UIC,favs[i]).toUpperCase();
      String actor = FavoriteAPI.GetFavoritePerson(UIC,favs[i]).toUpperCase();

      if (keyword.length() > 0) {
        keywordFavorites.put(keyword, favs[i]);
        Global.DebugLog("fav keyword = " + keyword);
      }

      if (title.length() > 0) {
        titleFavorites.put(title, favs[i]);
        Global.DebugLog("fav title = " + title);
      }

      if (actor.length() > 0) {
        actorFavorites.put(actor, favs[i]);
        Global.DebugLog("fav actor = " + actor);
      }

      if (category.length() > 0) {
        categoryFavorites.put(category, favs[i]);
        Global.DebugLog("fav category = " + category);
      }
    }
  }

  private static class FavTag extends FavoriteExcluder.Tag
  {
    Object baseFav = null;
    String baseFavMatch = null;
    int baseFavScope;

    FavTag(String keyword, int scope, int action, String baseFavMatch, int baseFavScope)
    {
      super(keyword, scope, action);

      this.baseFavMatch = baseFavMatch;
      this.baseFavScope = baseFavScope;
    }

    public String toString()
    {
      StringBuffer buf = new StringBuffer();

      buf.append(super.toString());
      buf.append(" Favorite: ");

      switch (this.baseFavScope)
      {
      case 1:
        buf.append("KEYWORD: ");
        break;
      case 2:
        buf.append("TITLE: ");
        break;
      case 3:
        buf.append("ACTOR: ");
        break;
      case 4:
        buf.append("CATEGORY: ");
        break;
      case 5:
        buf.append("CHANNEL ");
      }

      buf.append(this.baseFavMatch);

      return buf.toString();
    }
  }

  private static class Tag
  {
    String match;
    int scope;
    int action;

    Tag(String match, int scope, int action)
    {
      this.match = match;
      this.scope = scope;
      this.action = action;
    }

    public String toString()
    {
      StringBuffer buf = new StringBuffer();

      switch (this.scope)
      {
      case 1:
        buf.append("KEYWORD: ");
        break;
      case 2:
        buf.append("TITLE: ");
        break;
      case 3:
        buf.append("ACTOR: ");
        break;
      case 4:
        buf.append("CATEGORY: ");
        break;
      case 5:
        buf.append("CHANNEL ");
        break;
      case 6:
        buf.append("TIME ");
      }

      buf.append(this.match);

      if (this.action == 1)
        buf.append(" -> WATCHED");
      else {
        buf.append(" -> DONTLIKE");
      }

      return buf.toString();
    }

    public String getScope()
    {
      switch (this.scope)
      {
      case 1:
        return "Keyword";
      case 2:
        return "Title";
      case 3:
        return "Actor";
      case 4:
        return "Category";
      case 5:
        return "Channel";
      case 6:
        return "Time";
      }

      return "";
    }
  }
}
