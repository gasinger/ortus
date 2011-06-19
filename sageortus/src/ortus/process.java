/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author jphipps
 */
public class process extends vars {

        public static Object ExecuteStatic(String execclassname) {
            return ExecuteStatic(execclassname, new Object[] {});
        }
        
	public static Object ExecuteStatic(String execclassname, Object[] args) {
            int methodoffset = execclassname.lastIndexOf(".");
            String classname = execclassname.substring(0,methodoffset);
            String methodname = execclassname.substring(methodoffset+1);
            Class[] parmtypes = new Class[args.length];
            for ( int x = 0; x < args.length; x++) {
                parmtypes[x] = args[x].getClass();
            }                                       

            try { 
                Class ec = Class.forName(classname);
                Method meth = ec.getMethod(methodname,parmtypes);
                Object retobj;
                if ( args.length == 0)
                    retobj = meth.invoke(null, (Object[]) null);
                else
                    retobj = meth.invoke(null, args);
                return retobj;
	    } catch (Exception e) {
		ortus.api.DebugLog(LogLevel.Trace,"Process: execute: " + execclassname + " Exception: " + e);
                Logger log = Logger.getLogger("ortus.process");
                log.error(e);
		return null;
	    }
	}

	public static Object execute(String methd, Object[] args) {
	    try {
                    String classname = methd.substring(0, methd.lastIndexOf("."));
                    String methname = methd.substring(methd.lastIndexOf(".")+1);

//                    ortus.api.DebugLog(LogLevel.Trace, "ortus.process.execute: classname: " + classname);
//                    ortus.api.DebugLog(LogLevel.Trace, "ortus.process.execute: methodname: " + methname);

                    if ( args == null) {
                      args = new Object[0];  
                    } 
                    
                    Class[] parameterTypes = new Class[args.length];
                    
                    for ( int x = 0; x < args.length; x++) {
                        parameterTypes[x]=args[x].getClass();
//                        ortus.api.DebugLog(LogLevel.Trace, "ortus.process.execute: parm: " + args[x].getClass());
                    }

                    Class cls = Class.forName(classname);
                    Method m = null;
                    if ( parameterTypes.length == 0)
                        m = cls.getMethod(methname, (Class[])null);
                    else
                        m = cls.getMethod(methname, parameterTypes);

                    Object retval = null;
                    if ( m == null) {
                        ortus.api.DebugLog(LogLevel.Error,"method not found for: " + methd + " with " + parameterTypes.length + " parameters");
                    } else {
                        if ( m.getParameterTypes().length == 0)
                            retval = m.invoke(null, (Object[])null);
                        else
                            retval = m.invoke(null, args);
                    }

                    return retval;
                        
	    } catch (Exception e) {
		ortus.api.DebugLog(LogLevel.Trace2,"Process: execute: " + methd + " Exception: " + e);
		return null;
	    }
	}

	public static Object executeRemote(String methd, Object[] args) {
	    if ( ortus.api.IsRemoteHost() ) {
                return ortus.daemon.api.executecCMD((String)ortus.api.GetProperty("remotehost", null), methd, args);
	    } else {
                return execute(methd, args);
            }
	}
}