package ortus.media.fanart;

import java.util.logging.Level;
import java.util.logging.Logger;
import ortus.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Object;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ortus.media.metadata.item.Episode;
import ortus.media.metadata.item.Fanart;
import ortus.media.metadata.item.Media;
import ortus.media.metadata.item.Series;
import ortus.media.OrtusMedia;
import sagex.api.Configuration;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;

public class OrtusFanart extends ortus.vars implements IFanartProvider {

    private String seperator = File.separator;
    private String CentralTVFolder = GetFanartFolder() + seperator + "TV" + seperator;
	
	// All calls below are temporary until they are added to phoenix api. 
	// The return of Season calls returns series if no season is available. Always use phoenix
	// HasFanart calls before hand in studio to avoid nulls.



	@Override
	public String GetFanartFolder() {

		String fanartfolder = Configuration.GetProperty("ortus/fanart/folder", "None");

		if (fanartfolder.equalsIgnoreCase("none")) {
			fanartfolder = configurationEngine.getInstance().getBasePath() + seperator + "Fanart";
			File df = new File(fanartfolder);
			if (!df.exists()) {
				df.mkdir();
			}
		}

		return fanartfolder;
	}

	public void SetFanartFolder(String folder) {

		Configuration.SetProperty("ortus/fanart/folder", folder);

		File df = new File(folder);
		if (!df.exists()) {
			df.mkdir();
		}

		return;
	}


	public String GetFanartPosterFile(Object mediafile) {
                int mediaid = GetMediaID(mediafile);
//                ortus.api.DebugLogTrace("GetFanartPosterFile: ID: " + mediaid);
                Object medfil = MediaFileAPI.GetMediaFileForID(mediaid);
		File mf = MediaFileAPI.GetFileForSegment(medfil,0);
                String mfile = null;
                if ( mf.getAbsolutePath().contains("."))
		      mfile = mf.getAbsolutePath().substring(0, mf.getAbsolutePath().lastIndexOf("."));
                else
                      mfile = mf.getAbsolutePath() + java.io.File.separator + "folder";
//		ortus.api.DebugLog(LogLevel.Trace, "GetFanartPosterFile: " +  mfile + ".jpg");
		File faf = new File(mfile + ".jpg");
		if ( faf.exists())
			return faf.getAbsolutePath();
		else
			return null;
	}

        public int GetMediaID(Object mediafile) {
            int mediaid = 0;
            if (mediafile instanceof Integer) {
                mediaid = (Integer) mediafile;
            } else if ( mediafile instanceof String) {
                try { mediaid = Integer.parseInt((String)mediafile); } catch ( Exception e) {}
            } else if (mediafile instanceof OrtusMedia) {
                if (((OrtusMedia) mediafile).IsMediaFile() || ((OrtusMedia) mediafile).IsEpisode()) {
                    mediaid = ((OrtusMedia) mediafile).GetMediaID();
                }
            } else if (MediaFileAPI.IsMediaFileObject(mediafile)) {
                mediaid = MediaFileAPI.GetMediaFileID(mediafile);
            }

            return mediaid;
        }
   

        private String GetFanart(Object mediafile, String fanarttype, int imagetype) {
//                ortus.api.DebugLogTrace("Parms: Object: " + mediafile + " type: " + fanarttype + " ImageType: " + imagetype);
                List results = null;
                if ( mediafile instanceof OrtusMedia ) {
                    if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetFanart(fanarttype,imagetype);
                    } else if (((OrtusMedia) mediafile).IsSeries()) {
                        results = ((Series)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetFanart(fanarttype,imagetype);
                    } else if (((OrtusMedia)mediafile).IsEpisode()) {
                        int seriesid = ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getSeriesid();
                        results = ((Series)ortus.cache.cacheEngine.getInstance().GetCache("SR" + seriesid)).GetFanart(fanarttype,imagetype);
                    }
                } else if ( mediafile instanceof Integer ) {
                    results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + mediafile)).GetFanart(fanarttype,imagetype);
                } else {
                    results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetFanart(fanarttype,imagetype);
                }
                if (results.size() > 0) {
                    ortus.api.DebugLogTrace("Return: Fanart: " + ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)results.get(0)).getFile());
                    return ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)results.get(0)).getFile();
                } else {
                  return GetFanartPosterFile(mediafile);
//                ortus.api.DebugLogTrace("Return: null");

//                    return null;
      		}
	}
	private List<Object> GetFanartAll(Object mediafile, String fanarttype, int imagetype) {
              List results = null;
                if ( mediafile instanceof OrtusMedia ) {
                    if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetFanart(fanarttype,imagetype);
                    } else if (((OrtusMedia) mediafile).IsSeries()) {
                        results = ((Series)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetFanart(fanarttype,imagetype);
                    } else if (((OrtusMedia)mediafile).IsEpisode()) {
                        int seriesid = ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getSeriesid();
                        results = ((Series)ortus.cache.cacheEngine.getInstance().GetCache("SR" + seriesid)).GetFanart(fanarttype,imagetype);
                    }
                } else {
                    results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetFanart(fanarttype,imagetype);
                }
                if (results.size() > 0) {
                    List<Object> ret = new ArrayList<Object>();
                    for ( Object o : results) {
			ret.add(ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)o).getFile());
                    }
                    return ret;
                } else {
//                    return GetFanartPosterFile(mediafile);
                    return null;
      		}
	}

        public String GetFanartPoster(Object mediafile) {
            return GetFanart(mediafile, "Posters",2);
        }
        public List<Object> GetFanartPosters(Object mediafile) {
            return GetFanartAll(mediafile, "Posters",2);
        }

	public String GetFanartPosterThumb(Object mediafile) {
            return GetFanart(mediafile,"Posters",1);
	}

	public String GetFanartPosterCover(Object mediafile) {
            return GetFanart(mediafile,"Posters",2);
	}

	public String GetFanartPosterHigh(Object mediafile) {
            return GetFanart(mediafile,"Posters",3);
	}

	public String GetFanartBackgroundHighRandom() {
//		List<Object> result = ortus.api.executeSQLQuery("select * from sage.fanart where mediaid >= ( select rand() * max(mediaid) from sage.fanart where file is not null and imagetype  = 3 and type = 'Backgrounds') limit 1");
//		if (result.size() > 0) {
//			return ortus.api.GetFanartFolder() + java.io.File.separator + (String) result.get(0);
//		} else {
			return null;
//		}

	}

	public String GetFanartBanner(Object mediafile) {
            return GetFanart(mediafile,"Banners",3);
	}

	public List<Object> GetFanartBanners(Object mediafile) {
            return GetFanartAll(mediafile,"Banners",3);
	}

	public String GetFanartBackground(Object mediafile) {
            return GetFanart(mediafile,"Backgrounds",2);
	}
	public String GetFanartBackgroundThumb(Object mediafile) {
            return GetFanart(mediafile,"Backgrounds",1);
	}

	public String GetFanartBackgroundCover(Object mediafile) {
            return GetFanart(mediafile,"Backgrounds",2);
	}

	public String GetFanartBackgroundHigh(Object mediafile) {
            return GetFanart(mediafile,"Backgrounds",3);
	}

	public List<Object> GetFanartBackgrounds(Object mediafile) {
            return GetFanartAll(mediafile,"Backgrounds",2);
	}

	public List<Object> GetFanartRandom(List<Object> fanart) {
		Collections.shuffle(fanart);
		return fanart;
	}

	public String GetCastFanartPoster(String castname) {
		ortus.api.DebugLog(LogLevel.Trace2, " GetCastFanartPoster: fanart for: " + castname);
		List<List> result = ortus.api.executeSQLQueryArray("select file from sage.fanart where type = 'Cast-" + castname + "'");
		if ( result.size() > 1)
			return ortus.api.GetFanartFolder() + java.io.File.separator + (String)result.get(0).get(0);
		else
			return null;
	}

	public String GetSeasonFanartPoster(Object mediafile) {
                String SQL = null;
                int SageMediaID = 0;
                if ( mediafile instanceof OrtusMedia) {
                    if ( ((OrtusMedia)mediafile).IsEpisode()) {
                        List<Object> x = ortus.api.executeSQLQuery("select mediaid from sage.episode where episodeid = " + ((OrtusMedia)mediafile).GetID());
                        if ( x.size() > 0) {
                            try { SageMediaID = Integer.parseInt((String)x.get(0)); } catch ( Exception e) {}
                        } 
                    } else if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        SageMediaID = ((OrtusMedia)mediafile).GetID();
                    }
                } else {
                    SageMediaID = MediaFileAPI.GetMediaFileID(mediafile);
                }
		List<Object> sqlresult = ortus.api.executeSQLQueryCache("select seasonno from sage.episode as e, sage.series as s where e.seriesid = s.seriesid and e.mediaid = " + SageMediaID);
		if (sqlresult.size() < 1) {
			return null;
		}
		List<Object> result = (List) ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + SageMediaID)).GetFanart("Season-" + sqlresult.get(0) + "-Posters",3);

		if (result == null || result.size() == 0) {
                        return GetFanartPoster(SageMediaID);
		} else {
			return ortus.api.GetFanartFolder() + java.io.File.separator + (String) result.get(0);
		}
	}

	public List<Object> GetSeasonFanartPosters(Object mediafile) {
                String SQL = null;
                int SageMediaID = 0;
                if ( mediafile instanceof OrtusMedia) {
                    if ( ((OrtusMedia)mediafile).IsEpisode()) {
                        List<Object> x = ortus.api.executeSQLQuery("select mediaid from sage.episode where episodeid = " + ((OrtusMedia)mediafile).GetID());
                        if ( x.size() > 0) {
                            try { SageMediaID = Integer.parseInt((String)x.get(0)); } catch ( Exception e) {}
                        }
                    } else if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        SageMediaID = ((OrtusMedia)mediafile).GetID();
                    }
                } else {
                    SageMediaID = MediaFileAPI.GetMediaFileID(mediafile);
                }

		List<Object> sqlresult = ortus.api.executeSQLQueryCache("select seasonno from sage.episode as e, sage.series as s where e.seriesid = s.seriesid and e.mediaid = " + SageMediaID);
		if (sqlresult.size() < 1) {
			return null;
		}

		List<Object> result = (List) ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + SageMediaID)).GetFanart("Season-" + sqlresult.get(0) + "-Posters",3 );

		return result;
	}

	public String GetSeasonFanartBanner(Object mediafile) {
                String SQL = null;
                int SageMediaID = 0;
                if ( mediafile instanceof OrtusMedia) {
                    if ( ((OrtusMedia)mediafile).IsEpisode()) {
                        List<Object> x = ortus.api.executeSQLQuery("select mediaid from sage.episode where episodeid = " + ((OrtusMedia)mediafile).GetID());
                        if ( x.size() > 0) {
                            try { SageMediaID = Integer.parseInt((String)x.get(0)); } catch ( Exception e) {}
                        }
                    } else if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        SageMediaID = ((OrtusMedia)mediafile).GetID();
                    }
                } else {
                    SageMediaID = MediaFileAPI.GetMediaFileID(mediafile);
                }

		List<Object> sqlresult = ortus.api.executeSQLQueryCache("select seasonno from sage.episode as e, sage.series as s where e.seriesid = s.seriesid and e.mediaid = " + SageMediaID);
		if (sqlresult.size() < 1) {
			return null;
		}

		List<Object> result = (List) ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD"+SageMediaID)).GetFanart("Season-" + sqlresult.get(0) + "-Banners",3);

		if (result == null) {
			return null;
		} else {
			return ortus.api.GetFanartFolder() + java.io.File.separator + (String) result.get(0);
		}
	}

	public List<Object> GetSeasonFanartBanners(Object mediafile) {
                String SQL = null;
                int SageMediaID = 0;
                if ( mediafile instanceof OrtusMedia) {
                    if ( ((OrtusMedia)mediafile).IsEpisode()) {
                        List<Object> x = ortus.api.executeSQLQuery("select mediaid from sage.episode where episodeid = " + ((OrtusMedia)mediafile).GetID());
                        if ( x.size() > 0) {
                            try { SageMediaID = Integer.parseInt((String)x.get(0)); } catch ( Exception e) {}
                        }
                    } else if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        SageMediaID = ((OrtusMedia)mediafile).GetID();
                    }
                } else {
                    SageMediaID = MediaFileAPI.GetMediaFileID(mediafile);
                }

		List<Object> sqlresult = ortus.api.executeSQLQueryCache("select seasonno from sage.episode as e, sage.series as s where e.seriesid = s.seriesid and e.mediaid = " + SageMediaID);
		if (sqlresult.size() < 1) {
			return null;
		}

		List<Object> result = (List) ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD"+SageMediaID)).GetFanart("Season-" + sqlresult.get(0) + "-Banners",3);

		return result;
	}

	public List<String> GetMenuBackgrounds(String Menutype) {

		String Path = GetFanartFolder() + "\\" + Menutype;
		File directory = new File(Path);
		File[] AllDirectories = directory.listFiles(new OrtusDirectoryFilter());
		ArrayList<String> AllFanart = new ArrayList<String>();
		ortus.api.DebugLog(3, "Directory: " + Path + " Size of Subfolders:" + AllDirectories.length);
		if (AllDirectories != null) {
			for (int i = 0; i < AllDirectories.length - 3; i++) {
				String SubPath = AllDirectories[i].toString() + "\\Backgrounds";
				ortus.api.DebugLog(3, "Adding + SubDirectory: " + i + SubPath);
				File tempbglist = new File(SubPath);
				if (tempbglist.isDirectory()) {
					File[] tempbgs = tempbglist.listFiles(new OrtusImageFilter());
					if (tempbgs != null && tempbgs.length != 0) {
						AllFanart.add(tempbgs[0].toString());
					}
				}
			}

			return AllFanart;
		}

		ortus.api.DebugLog(1, "AllFanart equals null");
		return null;
	}

	public String GetMenuBackground(String MenuType) {
		if (MenuType.contains("tv")) {
			MenuType = "TV";
		}
		if (MenuType.contains("movie")) {
			MenuType = "Movies";
		}
		if (MenuType.contains("music")) {
			MenuType = "Music";
		}
		String Path = GetFanartFolder() + "\\" + MenuType;
		File directory = new File(Path);
		File[] AllDirectories = directory.listFiles(new OrtusDirectoryFilter());
		File[] AllFanart = null;
		ortus.api.DebugLog(3, "Directory: " + Path);
		if (AllDirectories != null) {
			for (int i = 0; AllFanart == null && i < AllDirectories.length; i++) {
				int randint = phoenix.api.GetRandomNumber(AllDirectories.length);
				String SubPath = AllDirectories[randint].toString() + "\\Backgrounds";
				ortus.api.DebugLog(3, "SubDirectory: " + SubPath);
				File subdirectory = new File(SubPath);
				AllFanart = subdirectory.listFiles(new OrtusImageFilter());
			}

			return AllFanart[0].toString();
		}
		ortus.api.DebugLog(1, "AllFanart equals null");

		return null;
	}

// Creates Fanart Posters for all folder.jpg's for a given Array of movies. Will check to make sure no fanart poster and baackgrounds
// exist and then will create Movie folder and poster folder based off @MediaTitle same method as BMI uses to create fanart.
// This allows those not wanting to use BMI to use newer stvi's which require the fanart structure.
// @ MediaObjects = Array of mediaobjects with possible folder.jpg's. Jpeg does not have to be called folder.jpg just must reside in main movie folder.
// Will move multiple jpg's if they exist. Background fanart must contain the word "background" in the name of the jpg.
	public void CreateFanartFromJPG(Object[] MediaObjects) {
		if (MediaObjects.length > 0) {
			for (int j = 0; j < MediaObjects.length; j++) {

				Object MediaObject = MediaObjects[j];

				String Title = GetSafeTitle(MediaObject);
				Boolean hasfanart = phoenix.api.HasFanartPoster(MediaObject);
				if (Title != null && Title.length() != 0 && hasfanart == false) {
					// commented out for thumbs maybe at a later date not complete need to evaluate further
					//String SThumb = MediaFileAPI.GetThumbnail(MediaObject).toString();
					//Global.ortus.api.DebugLog("Before Stringing  " + SThumb );
					//SThumb = SThumb.substring(SThumb.indexOf("[")+1,SThumb.indexOf("#"));
					//File FThumb = new File(SThumb);
					//Global.ortus.api.DebugLog("After Stringing" + SThumb);
					// Again commented out for thumbs
					//if (FThumb.isFile()){
					//Global.ortus.api.DebugLog("IsFile = true");
					//boolean success = FThumb.renameTo(new File(FDirectory,FThumb.getName()));}

					File PosterDirectory = new File(GetFanartFolder() + "\\Movies\\" + Title + "\\Posters");
					File BackgroundDirectory = new File(GetFanartFolder() + "\\Movies\\" + Title + "\\Backgrounds");
					File jpgfolder = new File(MediaFileAPI.GetParentDirectory(MediaObject).toString() + "\\");
					File[] jpegs = jpgfolder.listFiles(new OrtusImageFilter());
					if (jpegs.length != 0 && jpegs != null) {
						if (PosterDirectory.exists() == false) {
							ortus.api.DebugLog(3, "Making New Directory " + PosterDirectory);
							boolean success1 = PosterDirectory.mkdirs();
							if (success1 == true) {
								ortus.api.DebugLog(3, "Movie Directory made " + PosterDirectory);
							}
							if (success1 == false) {
								ortus.api.DebugLog(1, "Movie Directory failed creation " + PosterDirectory);
							}
						}
						for (int i = 0; i < jpegs.length; i++) {
							File Folderjpg = jpegs[i];
							ortus.api.DebugLog(3, "Folderjpg exist " + Folderjpg);
							if (PosterDirectory.exists() && !Folderjpg.toString().toLowerCase().contains("background") && !Folderjpg.toString().toLowerCase().contains("fanart")) {
								try {
									copy(Folderjpg, new File(PosterDirectory.toString() + Folderjpg.toString().substring(Folderjpg.toString().lastIndexOf("\\"))));
								} catch (Exception ex) {
								}
								boolean success3 = new File(PosterDirectory.toString() + Folderjpg.toString().substring(Folderjpg.toString().lastIndexOf("\\"))).exists();
								if (success3 == true);
								{
									ortus.api.DebugLog(3, "File copied");
								}
								if (success3 == false) {
									ortus.api.DebugLog(1, "File not copied");
								}
							} else if (Folderjpg.toString().toLowerCase().contains("background") || Folderjpg.toString().toLowerCase().contains("fanart")) {
								ortus.api.DebugLog(3, "jpg is background checking for background directory");
								if (BackgroundDirectory.exists() == false) {
									ortus.api.DebugLog(3, "Making New Background Directory " + BackgroundDirectory);
									boolean success1 = BackgroundDirectory.mkdirs();
									if (success1 == true) {
										ortus.api.DebugLog(3, "Movie Directory made " + BackgroundDirectory);
									}
									if (success1 == false) {
										ortus.api.DebugLog(1, "Movie Directory failed creation " + BackgroundDirectory);
									}
								}
								try {
									copy(Folderjpg, new File(BackgroundDirectory.toString() + Folderjpg.toString().substring(Folderjpg.toString().lastIndexOf("\\"))));
								} catch (Exception ex) {
								}
								boolean success3 = new File(PosterDirectory.toString() + Folderjpg.toString().substring(Folderjpg.toString().lastIndexOf("\\"))).exists();
								if (success3 == true);
								{
									ortus.api.DebugLog(3, "File copied");
								}
								if (success3 == false) {
									ortus.api.DebugLog(1, "File not copied");
								}
							}

							if (!PosterDirectory.exists()) {
								ortus.api.DebugLog(1, "Cannot Create Folder structure error in Title return");
							}
						}
					} else {
						ortus.api.DebugLog(1, "No folderjpegs found for title " + Title);
					}
				} else if (Title == null || Title.length() == 0 && hasfanart == false) {
					ortus.api.DebugLog(1, "No Poster Created for Movie not title found");
				} else if (hasfanart == true) {
					ortus.api.DebugLog(3, "Has Fanart Poster no jpeg conversion needed" + Title);
				}
			}
		}
	}

// Returns a ArrayList of strings of "MediaTitles" for fanart folders that exist but there is no matching title in the SageDB.
// Can then be passed to FanartCleanupMove to move this files.
//@ Object[] mediafiles passed from Sage ie. GetMediaFiles()
//@ String Type = "Movies" or "TV" for type of media files being passed.
	public Map<String, List> GetFanartCleanupList(Object[] MediaObjects, String Type) {
		ArrayList<String> Titles = new ArrayList<String>();
		for (int a = 0; a < MediaObjects.length; a++) {
			Titles.add(a, GetSafeTitle(MediaObjects[a]));
		}
		String Path = GetFanartFolder() + "\\" + Type;
		File directory = new File(Path);
		File[] AllDirectories = directory.listFiles(new OrtusDirectoryFilter());
		Map<String, List> NoFiles = new HashMapImpl();

		if (AllDirectories != null && AllDirectories.length != 0) {
			for (int i = 0; i < AllDirectories.length; i++) {
				String CurrDirectory = AllDirectories[i].toString();
				CurrDirectory = CurrDirectory.substring(CurrDirectory.lastIndexOf("\\") + 1);
				ortus.api.DebugLog(3, "Current Directory searching titles: " + CurrDirectory);
				boolean found = false;
				for (int j = 0; j < Titles.size() && found == false; j++) {
					String Title = Titles.get(j);
					if (Title.equalsIgnoreCase(CurrDirectory)) {
						ortus.api.DebugLog(3, "Title Found: " + CurrDirectory);
						Titles.remove(Titles.get(j));
						ortus.api.DebugLog(5, "Removing found title : " + Titles.size());
						found = true;
					} else {
						ortus.api.DebugLog(5, "Next Title  " + j);
					}
				}
				if (found == false) {
					ortus.api.DebugLog(3, "Fanart has no title associated added to list : " + CurrDirectory);
					String Key = AllDirectories[i].toString();
					NoFiles.put(Key, ortus.util.file.GetFolderDetail(Key));

				}
			}
		}

		return NoFiles;
	}

// Takes ArrayList from GetFanartCleanupList only!!! Moves those files to default folder of
//  "centralfanart folder//unused//ddyymm//type"
// Moves all folders passed but keeps original folder structure
//@ ArrayList<String> - accepts arraylist passed from GetFanartCleanupList
//@ Type - accepts type of media ("TV", "Movies")
	public void FanartCleanupMove(HashMap<String, List> FoldersMap, String Type) {
		Object[] Folders = api.toArray(FoldersMap);
		for (int i = 0; i < Folders.length; i++) {
			String Date = getDateTime();
			if (Type.equals("All")) {
				if (Folders[i].toString().contains("TV")) {
					Type = "TV";
				} else {
					Type = "Movies";
				}
			}
			String Folder = GetFanartFolder() + "\\unused\\" + Date + "\\" + Type;
			File Directory = new File(Folder);
			Directory.mkdirs();
			if (Directory.canRead() && Directory.canWrite()) {
				ortus.api.DebugLog(3, "Folders Files being moved to: " + Folder);
				File ODirectoryF = new File(Folders[i].toString());
				boolean success = ODirectoryF.renameTo(new File(Directory, ODirectoryF.getName()));
				if (success == true) {
					ortus.api.DebugLog(3, "Directories moved " + Folder);
				} else {
					ortus.api.DebugLog(3, "Directory failed to move : " + Folder);
				}
			} else {
				ortus.api.DebugLog(1, "Read/Write access denied");
			}
		}
	}

	public String GetShowThumbnail(Object MediaObject) {
		int MediaID = MediaFileAPI.GetMediaFileID(MediaObject);
		List results = ortus.api.executeSQLQuery("Select mediatitle,season,episode FROM sage.mediatv WHERE mediaid=" + MediaID);
		String path = "";
		if (!results.isEmpty()) {
			path = CentralTVFolder + results.get(0) + seperator + "Thumbnails" + results.get(0) + "S"
				+ results.get(2) + "E" + results.get(3) + ".jpg";
			File thumb = new File(path);
			if (!thumb.exists()) {
				path = "";
			}
		}
		return path;
	}

	public void FanartCleanupDelete(HashMap<String, List> FoldersMap) {
		Object[] Folders = api.toArray(FoldersMap);
		for (int i = 0; i < Folders.length; i++) {
			File CurrDirectory = new File(Folders[i].toString());
			ortus.api.DebugLog(3, "Directory to delete " + CurrDirectory);
			if (CurrDirectory.canRead() && CurrDirectory.canWrite()) {
				delete(CurrDirectory);
				CurrDirectory.delete();
			} else {
				ortus.api.DebugLog(1, "Cannot get read and write acess");
			}
		}
	}

	public void copy(File src, File dst) {
		InputStream in = null;
		try {
			in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception ex) {
			Logger.getLogger(OrtusFanart.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				in.close();
			} catch (Exception ex) {
				Logger.getLogger(OrtusFanart.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private static String GetSafeTitle(Object MediaObject) {
		String Title = api.GetMediaTitle(MediaObject);
		if (Title == null || Title.length() == 0) {
			Title = ShowAPI.GetShowEpisode(MediaObject);
		}
		return sagex.phoenix.fanart.FanartUtil.createSafeTitle(Title);
	}

	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		Date date = new Date();
		return dateFormat.format(date);
	}

	private void delete(File folder) {
		if (folder.exists()) {
			ortus.api.DebugLog(3, "Folder Exist");
			File[] files = folder.listFiles();
			ortus.api.DebugLog(3, "Size of files in Folder" + folder + folder.length());
			for (int i = 0; i < files.length; i++) {
				File oFileCur = files[i];
				if (oFileCur.isDirectory()) {
					// call itself to delete the contents of the current folder
					delete(oFileCur);
				}
				oFileCur.delete();
			}
		}

	}

	private class HashMapImpl extends HashMap<String, List> {

		public HashMapImpl() {
		}
	}
}

class OrtusImageFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.getName().contains("jpg");
	}
}

class OrtusDirectoryFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.isDirectory();
	}
}




