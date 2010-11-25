package ortus.daemon;
/*
 * Parser.java
 *
 * Created on April 19, 2006, 6:38 PM by Greg Kusnick
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import sage.SageTV;

/**
 * Ortus Parser plugin for new file additions
 * @author jphipps
 */
public class parser extends ortus.vars implements sage.MediaFileMetadataParser
	{
	/**
	 * Creates a new instance of Parser.
	 */
	public parser()
        {
//		try { ortus.api.DebugLog(TRACE2,  "Ortus Parser - Loading..."); } catch (Exception e2) {}

	}

	/**
	 * Finalizes Parser.  No real work to do here, just debug tracing to
	 * track object lifetimes.
	 */
	public void finalize()
	{
//		try { ortus.api.DebugLog(TRACE2, "Ortus Parser - unLoading..." ); } catch (Exception e2) {}
	}

	/**
	 * Extract metadata from the specified file.
	 * @param file The file path.
	 * @param szPrefix The 'prefix' to prepend to the name of this media file for
	 * hierarchical purposes.
	 * @return An Airing object representing the metadata.
	 */
	public Object extractMetadata(File file, String szPrefix)
	{
        	try
		{
                	String szFile = file.getAbsolutePath();
			try { ortus.api.DebugLog(LogLevel.Info, "Ortus Parser - Processing: " + szFile ); } catch (Exception e2) {}

			ortus.api.AddMetadataQueue("ortus.onlinescrapper.api.AutoFileMatch", new Object[] {szFile} );

                        return null;

                }catch (Exception e){//Catch exception if any
                	try { SageTV.api("DebugLog", new Object[] { "Ortus Parser - Failure: " + e } ); } catch (Exception e2) {}
                }
		return null;
		}
	}