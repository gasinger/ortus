/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.web.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.CharBuffer;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author jphipps
 */
public class utils {
    static  Logger log = Logger.getLogger(ortus.web.resources.utils.class);

    public static void returnFile(HttpServletResponse resp, String fileName) {
        byte[] buffer = new byte[1024];
        OutputStream out = null;
        FileInputStream in = null;
        MimetypesFileTypeMap mft = new MimetypesFileTypeMap();

        log.trace("returnFile: Name: " + fileName);
        File inFile = new File(fileName);


        if ( ! inFile.exists()) {
            log.error("returnFile: Not found");
            resp.setStatus(404);
            return;
        }

        resp.setContentType(mft.getContentType(inFile));
        
        try {
            out = resp.getOutputStream();

            in = new FileInputStream(inFile);

            while ( in.read(buffer) > 0) {
                out.write(buffer);
            }
        } catch ( Exception e ) {
            resp.setStatus(500);
            return;
        } finally {
            if( in != null) try { in.close(); } catch (Exception ex) {}
            if( out != null) try { out.close(); } catch (Exception ex) {}
        }

        return;
    }
}
