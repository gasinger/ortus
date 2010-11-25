/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.util;

import ortus.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import sagex.api.Database;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;
import java.io.File;
import org.apache.commons.io.FileUtils;
import sagex.UIContext;

/**
 *
 * @author Aaron
 */
public class dump extends vars{
	public static String DumpObject(Object o1)
	{
		try {
			ortus.api.DebugLog(new UIContext("SAGETV_PROCESS_LOCAL_UI"), "DumpObject: " + o1);

		} catch (Exception e) {
		}
		return "a";
	}

	/**
	 * Dump all MediaFiles to the sage directory in TitleFile.dat file
	 * @return
	 */
	public String DumpMedia()
	{
		return this.DumpMedia("TitleFile.dat");
	}

	/**
	 * Dump all MediaFiles to a custom directory
	 * @param outFile Path to dump the media database
	 * @return
	 */
	public String DumpMedia(String outFile)
	{
		BufferedWriter out;
		int noTitles = 0;
		UIContext UIC = new UIContext("SAGETV_PROCESS_LOCAL_UI");
//		API sageapi = new API();
//		MediaFileAPI mf = new MediaFileAPI(sageapi);
//		MediaFileAPI.List mfl = mf.GetMediaFiles();

		Object[] mfl = sagex.api.MediaFileAPI.GetMediaFiles(UIC);

		noTitles = mfl.length;

		try {
			out = new BufferedWriter(new FileWriter(outFile));
		} catch (IOException e) {
			return "Failed to open file";
		}

		for (int x = 0; x < mfl.length; x++) {
			MediaFileAPI obj = (MediaFileAPI) mfl[x];
			try {
				out.write(MediaFileAPI.GetMediaTitle(UIC, obj) + "\r\n");
			} catch (IOException e) {
				return "Failed to write file";
			}
		}

		try {
			out.close();
		} catch (IOException e) {
			return "Failed to Close file";
		}

		return "Wrote " + noTitles + " to file " + outFile;

	}

	/**
	 * Dump all imported videos to a file TitleFile.dat under the sage directory
	 * @return
	 */
	public static String DumpVideo()
	{
		return DumpVideo(ortus.api.GetProperty("ortus/basepath") + java.io.File.separator + "sagemedia.dmp");
	}

//		public void TestDirectory() {
//			import java.io.File;
//			import java.io.IOException;
//			public class FileUtil {
//			  public static void main(String[] a)throws IOException{
//			    showDir(1, new File("d:\\Java_Dev"));
//			  }
//			  static void showDir(int indent, File file) throws IOException {
//			    for (int i = 0; i < indent; i++)
//			      System.out.print('-');
//			    System.out.println(file.getName());
//			    if (file.isDirectory()) {
//			      File[] files = file.listFiles();
//			      for (int i = 0; i < files.length; i++)
//			        showDir(indent + 4, files[i]);
//			    }
//			  }
//			}
//		}
	/**
	 * Dump all imported videos to a custom file
	 * @param outFile File name to dump the imported list
	 * @return
	 */
	public static String DumpVideo(String outFile)
	{
		BufferedWriter out;
		int noTitles = 0;
		Object[] mfl = MediaFileAPI.GetMediaFiles();
//		Database.FilterByBoolMethod(UIC, mfl, "IsVideoFile|IsDVD", true);
//		Database.FilterByBoolMethod(UIC, mfl, "IsLibraryFile", true);
//		Database.FilterByBoolMethod(UIC, mfl, "IsTVFile", false);
//		Database.SortLexical(mfl, false, "GetMediaTitle");
		noTitles = mfl.length;

		try {
			out = new BufferedWriter(new FileWriter(outFile));
		} catch (IOException e) {
			return "Failed to open file";
		}

		try {
		for (Object obj : mfl) {
//			ortus.api.DebugLog(TRACE2,"Object: " + obj);
//				AiringAPI.Airing a = obj.GetMediaFileAiring();
//				ShowAPI.Show s = a.GetShow();
			String title = MediaFileAPI.GetMediaTitle(obj);
			if ( title.isEmpty())
				continue;
			// String Desc = s.GetShowDescription();
//			ortus.api.DebugLog(TRACE2,"Getting file info");
			File mfile;
			String fileloc = "";
			try {
				mfile = MediaFileAPI.GetFileForSegment(obj, 0);
				fileloc = mfile.getAbsolutePath();
			} catch ( Exception e ) {}
//			if (MediaFileAPI.IsDVD(obj)) {
//				filetype = "DVD";
//			} else {
//			ortus.api.DebugLog(TRACE2,"Getting format desc");
			String filetype = MediaFileAPI.GetMediaFileFormatDescription(obj);
//			}
			// String fileloc = s.GetShowTitle();
			long showlen = 0;
			try {
				showlen = MediaFileAPI.GetFileDuration(obj);
			} catch ( Exception e) {}

			long filesiz = 0;
			try {
				filesiz = MediaFileAPI.GetSize(obj);
			} catch ( Exception e ) {}
//			ortus.api.DebugLog(TRACE2,"Wring data");
			String outrec = MediaFileAPI.GetMediaFileID(obj) + ";" + title + ";" + filetype + ";" + fileloc + ";" + showlen + ";" + filesiz + "\r\n";
			try {
				out.write(outrec);
			} catch (IOException e) {
				return "Failed to write file";
			}
		}
			out.close();
		} catch (IOException e) {
			return "Failed to Close file";
		}

		return "Wrote " + noTitles + " to file " + outFile;

	}

	static public List<String> DumpLogFile(String logfile)
	{
		File ifile = new File(logfile);
		List<String> LogEntries = new ArrayList<String>();

		try {
			if( ifile.exists())
				LogEntries = FileUtils.readLines(ifile);
			return LogEntries;
		} catch (Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "DumpLogFile: " + logfile + " not found");
			return LogEntries;
		}
	}
}
