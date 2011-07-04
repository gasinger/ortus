/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.themoviedb;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import sagex.api.MediaFileAPI;

/**
 *
 * @author jphipps
 */
public class ImageItem extends ortus.vars implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id = 0;
    private String idtype;
    private String type;
    private String url = "";
    private String size;
    private String metadataid;
    private int width = 0;
    private int height = 0;
    private String urlField = "low_url";
    private String fileField = "low_file";
    private String heightField = "low_height";
    private String widthField = "low_width";
    private String imageSizeField = "low_imagesize";
    private Boolean originalImage = false;


    @Override
    public String toString() {
        return "ImageItem{" + "id=" + id + ",idtype=" + idtype + ",type=" + type + ",url=" + url + ",size=" + getSize() + ",width=" + getWidth() + ",height=" + getHeight() + '}';
    }

    public ImageItem() {
    }

    public void SetType(String type) {
        this.type = type.replace("backdrop","Backgrounds");
        this.type = this.type.replace("poster","Posters");
    }
    public void SetSize(String size) {
	    this.setSize(size);
    }
    public void SetUrl(String url) {
	    this.url = url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setIdType(String idType) {
        this.idtype = idType;
    }

    public String getIdType() {
        return idtype;
    }

    public ImageItem(int id, String idtype, String type, String size, String url, String metadataid, int width, int height) {
        this.id = id;
        this.idtype = idtype;
        this.type = type.replace("backdrop","Backgrounds");
        this.type = this.type.replace("poster","Posters");
        this.size = size;
        this.url = url;
        this.metadataid = metadataid;
        this.height = height;
        this.width = width;
        
        if ( size.equalsIgnoreCase("original")) {
           urlField = "high_url";
           fileField = "high_file";
           heightField = "high_height";
           widthField = "high_width";
           imageSizeField = "high_imagesize";
           originalImage = true;
        } else if (size.equalsIgnoreCase("mid") ||
                   size.equalsIgnoreCase("poster") ||
                   size.equalsIgnoreCase("profile")) {
           urlField = "medium_url";
           fileField = "medium_file";
           heightField = "medium_height";
           widthField = "medium_width";
           imageSizeField = "medium_imagesize";
        }
//        ortus.api.DebugLog(LogLevel.Trace2, " ImageItem: " + type + " url: " + url);
    }

    public boolean IsOriginal() {
        return originalImage;
    }
    
    public boolean IsPoster() {
        if( type.equals("Posters"))
            return true;
        else
            return false;
    }

    public boolean IsBackgrounds() {
        if( type.equals("Backgrounds"))
            return true;
        else
            return false;
    }

    public boolean IsBanner() {
        if ( type.equals("Banners"))
            return true;
        else
            return false;
    }

    public String GetUrl() {
        return url;
    }

    public void WriteImageDB() {

       Connection conn = ortus.api.GetConnection();

       try {
           QueryRunner qr = new QueryRunner();

           int updatecount = qr.update(conn,"update sage.fanart set " + heightField + " = ?, " + widthField + " = ?, " + urlField + " = ? where mediaid = ? and idtype = ? and metadataid = ? and type = ?",height, width, url, id,idtype,metadataid,type);
           if ( updatecount == 0)
            updatecount = qr.update(conn,"insert into sage.fanart (mediaid,idtype, type, metadataid, " + urlField + "," + heightField + "," + widthField + ") values ( ?,?,?,?,?,?,?)", id, idtype, type,metadataid, url, height, width);
       } catch ( Exception e) {
           ortus.api.DebugLog(LogLevel.Error,"ImageItem: Exception: ", e);
       } finally {
           try { DbUtils.close(conn); } catch ( Exception e) {}
       }
    }

//	public void WriteFileImageDB(Object mediafile) {
////		ortus.api.DebugLog(LogLevel.Trace2,"WriteFileImagetoDB: Writing: " + url);
//		String fafilename = url.substring(url.lastIndexOf(java.io.File.separator) + 1);
//
//		HashMap<String, String> imginfo = ortus.image.util.GetImageInfo(ortus.api.GetFanartFolder() + java.io.File.separator + url);
////		ortus.api.DebugLog(LogLevel.Trace2,"WriteFileImagetoDB: Completed imginfo: " + url);
//		if (imginfo != null) {
//			try {
//				width = Integer.parseInt(imginfo.get("width"));
//				height = Integer.parseInt(imginfo.get("height"));
//			} catch ( Exception e) {}
//
//			if (height < 200) {
//				imagetype = 1;
//			}
//			if (height < 600) {
//				imagetype = 2;
//			}
//			if (height > 599) {
//				imagetype = 3;
//			}
//
////			ortus.api.DebugLog(LogLevel.Trace, "FanartImage: ImageType: " + imagetype + " Size: Width: " + width + " Height: " + height);
//		}
//
////		ortus.api.DebugLog(LogLevel.Trace2,"WriteFileImagetoDB: Completed imginfo: " + url);
//		List<Object> result = ortus.api.executeSQLQuery("select mediaid from sage.fanart where mediaid = " + MediaFileAPI.GetMediaFileID(mediafile) + " and url like '%/" + fafilename + "'");
//		if (result.size() > 0) {
//			String SQL = "update sage.fanart set width = " + width + ", height="+height + ",imagetype = " + imagetype + ", file='" + url + "' where mediaid = " + MediaFileAPI.GetMediaFileID(mediafile) + " and url like '%/" + fafilename + "'";
//			ortus.api.executeSQL(SQL);
//		} else {
//			String SQL = "INSERT INTO sage.fanart (mediaid, width, height, imagetype, type, url, file) VALUES(" + MediaFileAPI.GetMediaFileID(mediafile) + ","+width+","+height+","+imagetype+",'" + type + "','" + url + "','" + url + "')";
//			ortus.api.executeSQL(SQL);
//		}
//	}

    public void getImage(String destination) {
        getImage(destination,null);
    }

    public void getImage(String destination, String fname) {

        String downloaddir = ortus.api.GetFanartFolder() + java.io.File.separator + destination + java.io.File.separator + type;
        String filename="";
        String results = "";

        if ( fname == null) {
            if ( (url.contains("themoviedb") || url.contains("imgobject")) &&
                 url.endsWith("jpg")) {
                int slashIndex = url.lastIndexOf('/');
                int keyend = url.substring(0,slashIndex+1).lastIndexOf("/");
                int keystart = url.substring(0,keyend).lastIndexOf("/") + 1;
                String key = url.substring(keystart, keyend);
                filename=key.substring(key.length()-4) + "_";
            }
            filename+= url.substring(url.lastIndexOf("/") + 1);

            filename = filename.replaceAll("'","''");
            results = ortus.onlinescrapper.tools.urldownload.fileUrl(url, filename,downloaddir );
        } else {
            filename = fname;
            results = ortus.onlinescrapper.tools.urldownload.fileUrl(url, filename, downloaddir);
        }
//        ortus.api.DebugLog(LogLevel.Trace2, " getImage: " + filename + " dir: " + downloaddir);

 //       File df = new File(downloaddir);
 //       if ( ! df.exists())
 //           df.mkdirs();

//        downloaddir+= java.io.File.separator + filename;
        if (results.equalsIgnoreCase("OK") ) {
            HashMap<String,String> imginfo = ortus.image.util.GetImageInfo(downloaddir + java.io.File.separator + filename);

            if ( imginfo != null) {
                    setWidth(Integer.parseInt(imginfo.get("width")));
                    setHeight(Integer.parseInt(imginfo.get("height")));

//                    if ( type.equalsIgnoreCase("posters")) {
//                        if ( getHeight() < 100)
//                            imagetype = 1;
//                        if ( getHeight() < 200)
//                            imagetype = 2;
//                        if ( getHeight() < 600)
//                            imagetype = 3;
//                        if ( getHeight() >= 600)
//                            imagetype = 4;
//                    } else if ( type.equalsIgnoreCase("backgrounds")) {
//                         if ( getWidth() < 400)
//                            imagetype = 1;
//                        if ( getWidth() < 800)
//                            imagetype = 3;
//                        if ( getWidth() >= 800)
//                            imagetype = 4;
//                    } else {
//                        if ( getHeight() < 200)
//                            imagetype = 1;
//                        if ( getHeight() < 600)
//                            imagetype = 2;
//                        if ( getHeight() > 599)
//                            imagetype = 3;
//                    }
            }

//            if ( type.equalsIgnoreCase("posters")) {
//                if ( url.contains("-thumb"))
//                    imagetype = 1;
//                if ( url.contains("-cover"))
//                    imagetype = 2;
//                if ( url.contains("-mid"))
//                    imagetype = 3;
//                if ( url.contains("-original"))
//                    imagetype = 4;
//            } else if ( type.equalsIgnoreCase("backgrounds")) {
//                 if ( url.contains("-thumb"))
//                    imagetype = 1;
//                if ( url.contains("-poster"))
//                    imagetype = 3;
//                if ( url.contains("-original"))
//                    imagetype = 4;
//            }

            long filesize = 0;
            File faf = new File(downloaddir + java.io.File.separator + filename);
            if ( faf.exists()) {
                filesize = faf.length();
            }

            ortus.api.DebugLog(LogLevel.Trace,"FanartImage: Type: " + type + " Size: Width: " + getWidth() + " Height: " + getHeight());
            String SQL ="UPDATE sage.fanart SET " + widthField + " = " + getWidth() + ", " + heightField + "="+getHeight() + "," + fileField + " = '"+ destination.replaceAll("'","''") + java.io.File.separator + type.replaceAll("'","''") + java.io.File.separator + filename + "', " + imageSizeField + " = " + filesize + "  WHERE mediaid = " + id + " and idtype = '" + idtype + "' and type = '" + type.replaceAll("'","''") + "' and metadataid = '" + metadataid + "'";
            int success = ortus.api.executeSQL(SQL);
        }

        return ;
    }
//    public void getImageFanart(Object mediafile, String destination) {
//
//        String downloaddir = ortus.api.GetFanartFolder() + java.io.File.separator + destination;
//        String filename = url.substring(url.lastIndexOf("/") + 1);
//
//	filename = filename.replaceAll("'","''");
////        ortus.api.DebugLog(LogLevel.Trace2, " getImage: " + filename + " dir: " + downloaddir);
//
// //       File df = new File(downloaddir);
// //       if ( ! df.exists())
// //           df.mkdirs();
//
////        downloaddir+= java.io.File.separator + filename;
//        if ( ortus.onlinescrapper.tools.urldownload.fileDownload(url, downloaddir ).equals("OK")) {
//            HashMap<String,String> imginfo = ortus.image.util.GetImageInfo(downloaddir + java.io.File.separator + filename);
//
//            if ( imginfo != null) {
//                    setWidth(Integer.parseInt(imginfo.get("width")));
//                    setHeight(Integer.parseInt(imginfo.get("height")));
//
//                    if ( type.equalsIgnoreCase("posters")) {
//                        if ( getHeight() < 100)
//                            imagetype = 1;
//                        if ( getHeight() < 200)
//                            imagetype = 2;
//                        if ( getHeight() < 600)
//                            imagetype = 3;
//                        if ( getHeight() >= 600)
//                            imagetype = 4;
//                    } else if ( type.equalsIgnoreCase("backgrounds")) {
//                         if ( getWidth() < 400)
//                            imagetype = 1;
//                        if ( getWidth() < 800)
//                            imagetype = 3;
//                        if ( getWidth() >= 800)
//                            imagetype = 4;
//                    } else {
//                        if ( getHeight() < 200)
//                            imagetype = 1;
//                        if ( getHeight() < 600)
//                            imagetype = 2;
//                        if ( getHeight() > 599)
//                            imagetype = 3;
//                    }
//            }
//
//            if ( type.equalsIgnoreCase("posters")) {
//                if ( url.contains("-thumb"))
//                    imagetype = 1;
//                if ( url.contains("-cover"))
//                    imagetype = 2;
//                if ( url.contains("-mid"))
//                    imagetype = 3;
//                if ( url.contains("-original"))
//                    imagetype = 4;
//            } else if ( type.equalsIgnoreCase("backgrounds")) {
//                 if ( url.contains("-thumb"))
//                    imagetype = 1;
//                if ( url.contains("-poster"))
//                    imagetype = 3;
//                if ( url.contains("-original"))
//                    imagetype = 4;
//            }
//
//            long filesize = 0;
//            File faf = new File(downloaddir + java.io.File.separator + filename);
//            if ( faf.exists()) {
//                filesize = faf.length();
//            }
//
//            ortus.api.DebugLog(LogLevel.Trace,"FanartImage: Type: " + type + " ImageType: " + imagetype + " Size: Width: " + getWidth() + " Height: " + getHeight());
//            String SQL ="UPDATE sage.fanart SET width = " + getWidth() + ", height="+getHeight() + ",imagetype = " + imagetype + ",file = '"+ destination.replaceAll("'","''") + java.io.File.separator + type.replaceAll("'","''") + java.io.File.separator + filename + "', imagesize = " + filesize + "  WHERE mediaid = " + id + " and idtype = '" + idtype + "' and type = '" + type.replaceAll("'","''") + "' and url = '" + url + "'";
//            int success = ortus.api.executeSQL(SQL);
//        }
//
//        return ;
//    }

    /**
     * @return the size
     */
    public String getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the metadataid
     */
    public String getMetadataid() {
        return metadataid;
    }

    /**
     * @param metadataid the metadataid to set
     */
    public void setMetadataid(String metadataid) {
        this.metadataid = metadataid;
    }
}
