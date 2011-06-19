/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.util;

import java.util.HashMap;
import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import ortus.media.OrtusMedia;

import sagex.api.MediaFileAPI;
import sagex.api.Configuration;

import ortus.vars;
/**
 *
 * @author Aaron
 */
public class file extends vars{

	//declare integeres needed for foldersize call.
    int totalFolder=0;
    int totalFile=0;

	private static float Round(float Rval, int Rpl){
		return ortus.util.math.Round(Rval, Rpl);
	}

	public static boolean IsFileExist(String filename) {
		File x = new File(filename);
		if ( x.exists())
			return true;
		else
			return false;
	}

        public static String filterFileSeperator(String filename) {
            String newName = filename;

            if ( java.io.File.separator.equals("/")) {
                newName.replaceAll("\\\\","/");
            } else {
                newName.replaceAll("/", "\\\\");
            }

            return newName;
        }
        
	public static void CreateDirectory(String path) {
		File d = new File(path);
		if ( ! d.exists()) {
			try {
			d.mkdirs();
			} catch ( Exception e) {
				ortus.api.DebugLog(LogLevel.Error, "CreateDirectory: " + path);
				ortus.api.DebugLog(LogLevel.Error,"CreateDirectory: Exception:  " + e);
			}
		}
	}
	public static List GetFoldersDetails(Object Array) {
		Object[] Folders = ortus.util.array.toArray(Array);
		long fileSizeByte = 0;
		int foldercount = 0;
		int filecount = 0;
		for (int i = 0; i < Folders.length; i++) {
			String folder = Folders[i].toString();
			try {
				file size = new file();
				fileSizeByte = fileSizeByte + size.getFileSize(new File(folder));
				foldercount = foldercount + size.getTotalFolder();
				ortus.api.DebugLog(5, "Total Number of Folders: " + size.getTotalFolder());
				filecount = filecount + size.getTotalFile();
				ortus.api.DebugLog(5, "Total Number of Files: " + size.getTotalFile());
			} catch (Exception e) {
			}
		}
		float rounded = Round((fileSizeByte / 1024), 4);
		rounded = Round(rounded / 1024, 2);
		List details = new ArrayList();
		details.add(foldercount);
		details.add(filecount);
		details.add(rounded);
		return details;
	}

	public static List GetFolderDetail(String folder) {
		long fileSizeByte = 0;
		int foldercount = 0;
		int filecount = 0;
		try {
			file size = new file();
			fileSizeByte = fileSizeByte + size.getFileSize(new File(folder));
			foldercount = foldercount + size.getTotalFolder();
			ortus.api.DebugLog(5, "Total Number of Folders: " + size.getTotalFolder());
			filecount = filecount + size.getTotalFile();
			ortus.api.DebugLog(5, "Total Number of Files: " + size.getTotalFile());
		} catch (Exception e) {
		}
		float rounded = Round((fileSizeByte / 1024), 4);
		rounded = Round(rounded / 1024, 2);
		List details = new ArrayList();
		details.add(foldercount);
		details.add(filecount);
		details.add(rounded);

		return details;
	}

	private Long getFileSize(File folder) {
		totalFolder++;
		ortus.api.DebugLog(5, "Folder: " + folder.getName());
		long foldersize = 0;

		File[] filelist = folder.listFiles();
		for (int i = 0; i < filelist.length; i++) {
			if (filelist[i].isDirectory()) {
				foldersize += getFileSize(filelist[i]);
			} else {
				totalFile++;
				foldersize += filelist[i].length();
			}
		}
		return foldersize;
	}

	private int getTotalFolder() {
		return totalFolder;
	}

	private int getTotalFile() {
		return totalFile;
	}

	public static boolean IsFilePath(String path){
		File fp = new File(path);
		return fp.isFile();
	}

	public static String GetFilePathForSegment(Object mf, int segment){
		File fp = MediaFileAPI.GetFileForSegment(mf, segment);
		String fpstr = fp.getPath();
		return fpstr;

	}

        public static void DeleteBackupFiles() {
                String backupDir = Configuration.GetServerProperty("ortus/backup/folder", ortus.api.GetOrtusBasePath() + java.io.File.separator + "backups");
                long backupDays = Long.parseLong(Configuration.GetServerProperty("ortus/backup/limit", "30"));
                File dir = new File(backupDir);
                if ( ! dir.exists()) {
                    ortus.api.DebugLogError("DeleteBackupFiles: Path not found: " + backupDir);
                    return;
                }

                String[] files = dir.list(new backupFilter());
                ortus.api.DebugLogTrace("DeleteBackupFiles: Scanning directory: " + backupDir + " for files over " + backupDays + " days old");

                for ( String file : files ){
                     File g = new File(backupDir + java.io.File.separator + file);
                     long fileage = (System.currentTimeMillis() - g.lastModified())/86400000;
                     ortus.api.DebugLogTrace("DeleteBackupFile: checking file: " + g.getAbsolutePath() + " age: " + fileage);
                     if ( fileage > backupDays) {
                        ortus.api.DebugLogTrace(("DeleteBackupFiles: Deleting file: " + g.getAbsolutePath()));
//                        g.delete();
                     } else {
                        ortus.api.DebugLogTrace("DeleteBackupFiles: Bypassing file: " + g.getAbsolutePath());
                    }
                 }
        }

	public static LinkedHashMap<String, LinkedList<Object>> GroupByPath(List<Object> MediaFiles, String fspos) {
//                 ortus.api.DebugLog(TRACE, "GroupByPath Starting");
                boolean webGrouping=false;

     //           if ( fspos.endsWith(java.io.File.separator))  {
     //               int index = fspos.lastIndexOf(java.io.File.separator);
     //               fspos=fspos.substring(0, index - 1);
     //           }


                try {
                    LinkedHashMap<String, LinkedList<Object>> medialist = new LinkedHashMap<String, LinkedList<Object>>();
                    File[] VLP = Configuration.GetVideoLibraryImportPaths();
                    for (Object x : MediaFiles) {
                            if ( x == null)
                                    continue;

                            Object obj = null;
                            if ( x instanceof OrtusMedia)
                                obj = MediaFileAPI.GetMediaFileForID(((OrtusMedia)x).GetMediaID());
                            else if ( x instanceof HashMap) {
                                webGrouping = true;
                                obj = MediaFileAPI.GetMediaFileForID((Integer)((HashMap)x).get("mediaid"));
                            } else
                                obj = x;
                            String ShowTitle = "";
                            try {
                                    ShowTitle = GetRelativePath(VLP, obj);
                            } catch (Exception e) {
                                    ortus.api.DebugLog(LogLevel.Error, "GroupByPath: Exception: " + e);
                                    ortus.api.DebugLog(LogLevel.Error,"Processing: " + obj);
                            }
                            if (ShowTitle == null) {
                                    ShowTitle = "";
                            }
  //                          ortus.api.DebugLog(LogLevel.Trace,"GroupByPath: " + ShowTitle );
                            if (ShowTitle.startsWith(fspos)) {
                                    String RelativePath=null;
                                    if ( fspos.isEmpty())
                                        RelativePath = ShowTitle.substring(fspos.length());
                                    else
                                        RelativePath = ShowTitle.substring(fspos.length()+1);

//                                    ortus.api.DebugLog(LogLevel.Trace,"RelativePath: " + RelativePath);
                                    if (RelativePath.indexOf(java.io.File.separator) > 0) {
                                            String Folder = RelativePath.substring(0, RelativePath.indexOf(java.io.File.separator));
                                            if ( RelativePath.substring(RelativePath.indexOf(java.io.File.separator)+1).startsWith("VIDEO_TS") ||
                                                 RelativePath.substring(RelativePath.indexOf(java.io.File.separator)+1).startsWith("video_ts") ||
                                                 RelativePath.substring(RelativePath.indexOf(java.io.File.separator)+1).startsWith("BDMV")) {
                                                    String MediaTitle = ortus.api.GetMediaTitle(obj);
                                                    if (medialist.get(MediaTitle) != null) {
                                                            int dupindex = 2;
                                                            while( true ) {
                                                                    if ( medialist.get(MediaTitle+" copy no " + dupindex) == null) {
                                                                            LinkedList<Object> ma = new LinkedList<Object>();
                                                                            if ( webGrouping )
                                                                                ma.add(x);
                                                                            else
                                                                                ma.add(obj);
//        								    ortus.api.DebugLog(LogLevel.Trace,"Adding1: " + MediaTitle);
                                                                            medialist.put(MediaTitle+" copy no " + dupindex, ma);
                                                                            break;
                                                                    } else
                                                                            dupindex++;
                                                            }
                                                    } else {
                                                            LinkedList<Object> ma = new LinkedList<Object>();
                                                            if ( webGrouping )
                                                                ma.add(x);
                                                            else
                                                                ma.add(obj);
 //       						    ortus.api.DebugLog(LogLevel.Trace,"Adding2: " + MediaTitle);
                                                            medialist.put(MediaTitle, ma);
                                                    }
                                            } else {
                                                    if (medialist.get(Folder) != null) {
                                                            LinkedList<Object> ma = medialist.get(Folder);
   // 							    ortus.api.DebugLog(LogLevel.Trace,"Adding3: " + Folder);
                                                            if ( webGrouping )
                                                                ma.add(x);
                                                            else
                                                                ma.add(obj);
                                                    } else {
                                                            LinkedList<Object> ma = new LinkedList<Object>();
                                                            if ( webGrouping )
                                                                ma.add(x);
                                                            else
                                                                ma.add(obj);
    //							    ortus.api.DebugLog(LogLevel.Trace,"Adding4: " + Folder);
                                                            medialist.put(Folder, ma);
                                                    }
                                            }
                                    } else {
    //					String MediaTitle = MediaFileAPI.GetMediaTitle(obj);
                                            String MediaTitle = ortus.api.GetMediaTitle(obj);
                                            if (medialist.get(MediaTitle) != null) {
                                                    int dupindex = 2;
                                                    while( true ) {
                                                            if ( medialist.get(MediaTitle+" copy no " + dupindex) == null) {
                                                                    LinkedList<Object> ma = new LinkedList<Object>();
                                                                    if ( webGrouping )
                                                                        ma.add(x);
                                                                    else
                                                                        ma.add(obj);
    //								ortus.api.DebugLog(LogLevel.Trace,"Adding5: " + MediaTitle);
                                                                    medialist.put(MediaTitle+" copy no " + dupindex, ma);
                                                                    break;
                                                            } else
                                                                    dupindex++;
                                                    }
                                            } else {
                                                    LinkedList<Object> ma = new LinkedList<Object>();
                                                    if ( webGrouping )
                                                        ma.add(x);
                                                    else
                                                        ma.add(obj);
    //						ortus.api.DebugLog(LogLevel.Trace,"Adding6: " + MediaTitle);
                                                    medialist.put(MediaTitle, ma);
                                            }
                                    }
                            }
                    }

                    return medialist;
            } catch ( Exception e) {
                ortus.api.DebugLog(LogLevel.Error,"GroupByPath: Exception",e);
                return null;
            }
	}

	public static String GetRelativePath(File[] importfolders, Object mediaobject) {

		File mediafile = MediaFileAPI.GetFileForSegment(mediaobject, 0);
		String mediafilepath = mediafile.getAbsolutePath();
		if (mediafilepath.startsWith("\\\\livingroom\\movies")) {
//		       ortus.api.DebugLog(INFO," found: " + mediafilepath);
			String x = mediafilepath.substring(14);
			x = "\\\\livingroom\\M" + x;
			mediafilepath = x;
//			ortus.api.DebugLog(INFO," changed:" + mediafilepath);
		}
		for (File impfold : importfolders) {
			String importpathname = impfold.getAbsolutePath();
			if (mediafilepath.startsWith(importpathname)) {
				String relativepath = mediafilepath.substring(importpathname.length() + 1);
				return relativepath;
			}
		}
		return mediafilepath;
	}

	public static Object GroupByGenre(List<Object>mediafiles, String filepos) {
            
                if ( filepos.isEmpty()) {
        		HashMap<Object,List<Object>> mfl = new HashMap<Object,List<Object>>();

                        for ( Object x : mediafiles) {
                                List<String> workgenre;
//                                ortus.api.DebugLogTrace("GroupByGenre: " + x);
                                if ( x instanceof HashMap) {
                                    workgenre = (List)((HashMap)x).get("genre");
                                } else {
                                    workgenre = ortus.api.GetMediaGenre(x);
                                }
                                
                                for ( String wg : workgenre) {
                                    if ( mfl.get(wg) == null) {
                                            List<Object> gl = new ArrayList<Object>();
                                            gl.add(x);
                                            mfl.put(wg,gl);
                                    } else {
                                            mfl.get(wg).add(x);
                                    }
                                }
                        }

                        return mfl;
                    
                } else {
                        List<Object> mfl = new ArrayList<Object>();

                        for ( Object x : mediafiles) {
//                                ortus.api.DebugLogTrace("GroupByGenre: " + x);
                                List<String> workgenre;
                                if ( x instanceof HashMap) {
                                    workgenre = (List)((HashMap)x).get("genre");
                                } else {
                                    workgenre = ortus.api.GetMediaGenre(x);
                                }
                                for ( String wg : workgenre) {
                                    if ( filepos.equalsIgnoreCase(wg)) {
                                            String title = ortus.api.GetMediaTitle(x);
                                            mfl.add(x);
                                    }
                                }
                        }

                        return mfl;
                }
	}       
}


