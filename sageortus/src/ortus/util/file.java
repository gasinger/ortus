/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.util;

import java.util.HashMap;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
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

	public static HashMap<String, List<Object>> GroupByPath(List<Object> MediaFiles, String fspos) {
//                 ortus.api.DebugLog(TRACE, "GroupByPath Starting");
		HashMap<String, List<Object>> medialist = new HashMap<String, List<Object>>();
		File[] VLP = Configuration.GetVideoLibraryImportPaths();
		for (Object x : MediaFiles) {
       			if ( x == null)
				continue;

                        Object obj = null;
                        if ( x instanceof OrtusMedia)
                            obj = MediaFileAPI.GetMediaFileForID(((OrtusMedia)x).GetMediaID());
                        else
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
//                        ortus.api.DebugLog(LogLevel.Trace,"GroupByPath: " + ShowTitle );
			if (ShowTitle.startsWith(fspos)) {
                                String RelativePath=null;
                                if ( fspos.isEmpty())
                                    RelativePath = ShowTitle.substring(fspos.length());
                                else
                                    RelativePath = ShowTitle.substring(fspos.length()+1);

//                                ortus.api.DebugLog(LogLevel.Trace,"RelativePath: " + RelativePath);
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
									List<Object> ma = new ArrayList<Object>();
									ma.add(obj);
//									ortus.api.DebugLog(LogLevel.Trace,"Adding1: " + MediaTitle);
									medialist.put(MediaTitle+" copy no " + dupindex, ma);
									break;
								} else
									dupindex++;
							}					
						} else {
							List<Object> ma = new ArrayList<Object>();
							ma.add(obj);
//							ortus.api.DebugLog(LogLevel.Trace,"Adding2: " + MediaTitle);
							medialist.put(MediaTitle, ma);
						}
					} else {
						if (medialist.get(Folder) != null) {
							List<Object> ma = medialist.get(Folder);
//							ortus.api.DebugLog(LogLevel.Trace,"Adding3: " + Folder);
							ma.add(obj);
						} else {
							List<Object> ma = new ArrayList<Object>();
							ma.add(obj);
//							ortus.api.DebugLog(LogLevel.Trace,"Adding4: " + Folder);
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
								List<Object> ma = new ArrayList<Object>();
								ma.add(obj);
//								ortus.api.DebugLog(LogLevel.Trace,"Adding5: " + MediaTitle);
								medialist.put(MediaTitle+" copy no " + dupindex, ma);
								break;
							} else
								dupindex++;
						}
					} else {
						List<Object> ma = new ArrayList<Object>();
						ma.add(obj);
//						ortus.api.DebugLog(LogLevel.Trace,"Adding6: " + MediaTitle);
						medialist.put(MediaTitle, ma);
					}
				}
			}
		}

		return medialist;
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
//                                ortus.api.DebugLogTrace("GroupByGenre: " + x);
                                List<String> workgenre = ortus.api.GetMediaGenre(x);
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
                                List<String> workgenre = ortus.api.GetMediaGenre(x);
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
