/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import ortus.api;

/**
 *
 * @author razrsharpe
 */
public class SageMethods {
        HashMap<String,String> SageMethods = new HashMap<String,String>();
//	public class SageMethods
//   {


        public SageMethods() {
            LoadMethods("sagex.api.MediaFileAPI");
            LoadMethods("sagex.api.ShowAPI");
            LoadMethods("sagex.api.AiringAPI");
            LoadMethods("sagex.api.AlbumAPI");
        }


        private void LoadMethods(String classname) {
            try {
               Object[] x = Class.forName(classname).getMethods();
                for ( Object y : x ) {
                    Method z = (Method) y;
                    String mn = z.getName();
					SageMethods.put(mn, "All");
                    if ( mn.startsWith("Is"))
                         SageMethods.put(mn, "BoolMethod");
                    if ( mn.startsWith("Get"))
                         SageMethods.put(mn, "MethodRegex");
                }

            } catch (Exception e) {
               api.DebugLog("ERROR", "LoadFilters - Sage Filter: " + e );
            }
        }

        public String GetMethod(String method) {
            if ( SageMethods.get(method) == null )
               return "";
            else
                return SageMethods.get(method);
        }

//  }
}
