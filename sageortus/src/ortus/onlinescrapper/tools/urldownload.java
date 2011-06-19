/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.tools;

import java.io.*;
import java.net.*;
import org.apache.commons.io.IOUtils;
import sagex.api.Configuration;

public class urldownload extends ortus.vars {

	final static int size = 1024;

	public static String fileUrl(String fAddress, String localFileName, String destinationDir) {
		OutputStream outStream = null;
		HttpURLConnection uCon = null;
		InputStream is = null;
                String downloadfile = null;

		long t0 = System.currentTimeMillis();

		localFileName = ortus.util.string.ScrubFileName(localFileName);

                if ( destinationDir.trim().endsWith(java.io.File.separator))
                    downloadfile = destinationDir.trim() + localFileName;
                else
                    downloadfile = destinationDir.trim() + java.io.File.separator + localFileName;

		try {
			URL Url;
			byte[] buf;
			int ByteRead, ByteWritten = 0;
			Url = new URL(fAddress);

			File dd = new File(destinationDir.trim());

			if (!dd.exists()) {
				ortus.api.DebugLog(LogLevel.Trace2, "Path: " + destinationDir.trim() + " Not Found, Creating");
				dd.mkdirs();
			}

			File df = new File(downloadfile);
			if (df.exists()) {
				ortus.api.DebugLog(LogLevel.Info, "urldownload: file: " + df.getAbsolutePath() + " already exists, skipping download");
				return "OK";
			}

			if (ortus.api.GetSageProperty("ortus/proxy", null) != null) {
				String[] prox = ortus.api.GetSageProperty("ortus/proxy", null).split(":");
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(prox[0], Integer.parseInt(prox[1])));
				uCon = (HttpURLConnection) Url.openConnection(proxy);
			} else {
				uCon = (HttpURLConnection) Url.openConnection();
			}

			uCon.setInstanceFollowRedirects(true);
			uCon.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008072820 Firefox/3.0.1");
			uCon.connect();
                        uCon.setReadTimeout(60000);
                        uCon.setConnectTimeout(60000);

			if (uCon.getResponseCode() != uCon.HTTP_OK) {
				if (uCon.getResponseCode() == uCon.HTTP_NOT_FOUND) {
					ortus.api.DebugLog(LogLevel.Info, " URL: " + fAddress + " not found");
					return "NotFound";
				}
				ortus.api.DebugLog(LogLevel.Error, " HTTP Response: " + uCon.getResponseCode());
				ortus.api.DebugLog(LogLevel.Error, " HTTP GET Failed");
				return "HTTPError";
			}

			is = uCon.getInputStream();

			outStream = new BufferedOutputStream(new FileOutputStream(downloadfile));

//			buf = new byte[size];
			ortus.api.DebugLog(LogLevel.Trace2, "Starting download: " + fAddress);

                        IOUtils.copy(is, outStream);

                        outStream.flush();
                        outStream.close();
//			while ((ByteRead = is.read(buf)) != -1) {
//				outStream.write(buf, 0, ByteRead);
//				ByteWritten += ByteRead;
//			}
			long t1 = System.currentTimeMillis() - t0;
			ortus.api.DebugLog(LogLevel.Trace, "Successful Download File name:\"" + downloadfile + "\" No ofbytes: " + df.length() + " Time: " + t1 + " ms");
		} catch (Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "http download exception occured: " + e);
			try {
				if ( is != null) is.close();
				if ( outStream != null) outStream.close();
				return "DownloadException";
			} catch (IOException e2) {
				ortus.api.DebugLog(LogLevel.Error, "http close exception: " + e2);
				return "HTTPException";
			}
		}

		try {
//        ortus.api.DebugLog(LogLevel.Trace2, "HTTP: closing connection");
			is.close();
			outStream.close();
//        ortus.api.DebugLog(LogLevel.Trace2, " HTTP: closing completed");
			return "OK";
		} catch (IOException e) {
			ortus.api.DebugLog(LogLevel.Error, "http close exception: " + e);
			return "HTTPException";
		}
	}

	public static String fileDownload(String fAddress, String destinationDir) {
                String fileName="";

		int slashIndex = fAddress.lastIndexOf('/');
		int periodIndex = fAddress.lastIndexOf('.');

                if ( fAddress.contains("themoviedb") &&
                     fAddress.endsWith("jpg")) {
                    int keyend = fAddress.substring(0,slashIndex+1).lastIndexOf("/");
                    int keystart = fAddress.substring(0,keyend).lastIndexOf("/") + 1;
                    String key = fAddress.substring(keystart, keyend);
                    fileName=key.substring(key.length()-4) + "_";
                }

		fileName+=fAddress.substring(slashIndex + 1);

		if (periodIndex >= 1 && slashIndex >= 0 && slashIndex < fAddress.length() - 1) {
			return fileUrl(fAddress, fileName, destinationDir);
		} else {
			System.err.println("path or file name.");
                        return "PathError";
		}
	}

	public static void main(String[] args) {

		if (args.length == 2) {
			for (int i = 1; i < args.length; i++) {
				fileDownload(args[i], args[0]);
			}
		} else {
		}
	}

	public static int CheckURL(String URL) {
		int response = 0;
		if (URL.length() == 0) {
			System.err.println("Please provide a URL to check");
		} else {

			String urlString = URL;
			try {
				URL url = new URL(urlString);
				URLConnection connection =
					url.openConnection();
				if (connection instanceof HttpURLConnection) {
					HttpURLConnection httpConnection =
						(HttpURLConnection) connection;
					httpConnection.connect();
					response =
						httpConnection.getResponseCode();
					System.out.println(
						"Response: " + response);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return response;
	}
}
