/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 *
 * @author jphipps
 */
public class backupFilter implements FilenameFilter {
    @Override
    public boolean accept(File file, String string) {
          if ( file.getName().startsWith("wiz-backup") ||
                 file.getName().startsWith("ortusdb-"))
                 return true;
            else
                return false;
    }

}
