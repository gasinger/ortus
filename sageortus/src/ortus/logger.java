/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import sagex.api.Configuration;

/**
 *
 * @author jphipps
 */
public class logger extends vars {
	static logger INSTANCE = null;
	private Logger log = null;

	public static logger getInstance() {
		if ( INSTANCE == null ) {
		    synchronized(logger.class) {
			if ( INSTANCE == null )
			   INSTANCE = new logger();
		    }
		}
		return INSTANCE;
	}


	public logger() {
		File ortuslog = new File("logs/ortus.log");
		if ( ortuslog.exists())
			ortuslog.delete();
		log = Logger.getLogger(this.getClass());
		InputStream is = getClass().getResourceAsStream("/ortus/resources/ortus.log4j.properties");
		
                Properties logprop = new Properties();
                try {
                        logprop.load(is);
                } catch( Exception e) {
                        System.out.println("Exception in loading stream into prop");
                }
                PropertyConfigurator.configure(logprop);

		int LogLevelInt = Integer.parseInt(Configuration.GetServerProperty("ortus/log/level", "6"));

		switch(LogLevelInt) {
			case 0: log.setLevel(Level.OFF); break;
			case 1: log.setLevel(Level.FATAL); break;
			case 2: log.setLevel(Level.ERROR); break;
			case 3: log.setLevel(Level.WARN); break;
			case 4: log.setLevel(Level.INFO); break;
			case 5: log.setLevel(Level.DEBUG); break;
			case 6: log.setLevel(Level.TRACE); break;
			default: log.setLevel(Level.ALL); break;
		}
		log.info("ortuslog: Logging Started Level: " + String.valueOf(LogLevelInt));

	}

	public void SetLog4jAll() {
		Configuration.SetServerProperty("ortus/log/level", "7");
		log.setLevel(Level.ALL);
	}
	public void SetLog4jTrace() {
		Configuration.SetServerProperty("ortus/log/level", "6");
		log.setLevel(Level.TRACE);
	}
	public void SetLog4jDebug() {
		Configuration.SetServerProperty("ortus/log/level", "5");
		log.setLevel(Level.DEBUG);
	}
	public void SetLog4jInfo() {
		Configuration.SetServerProperty("ortus/log/level", "4");
		log.setLevel(Level.INFO);
	}
	public void SetLog4jWarn() {
		Configuration.SetServerProperty("ortus/log/level", "3");
		log.setLevel(Level.WARN);
	}
	public void SetLog4jError() {
		Configuration.SetServerProperty("ortus/log/level", "2");
		log.setLevel(Level.ERROR);
	}
	public void SetLog4jFatal() {
		Configuration.SetServerProperty("ortus/log/level", "1");
		log.setLevel(Level.FATAL);
	}
	public void SetLog4jOff() {
		Configuration.SetServerProperty("ortus/log/level", "0");
		log.setLevel(Level.OFF);
	}

	public void SetDebugLevel(int debuglevel) {
                log.info("ortuslog: Logging changed to  level: " + debuglevel);
		switch(debuglevel) {
			case 0: log.setLevel(Level.OFF);
				break;
			case 1: log.setLevel(Level.FATAL);
				break;
			case 2: log.setLevel(Level.ERROR);
				break;
			case 3: log.setLevel(Level.WARN);
				break;
			case 4: log.setLevel(Level.INFO);
				break;
			case 5: log.setLevel(Level.DEBUG);
				break;
			case 6: log.setLevel(Level.TRACE);
				break;
			default: log.setLevel(Level.ALL);
				break;
		}
	}

	public void SetDebugLevel(Level debuglevel) {
		log.setLevel(debuglevel);
	}

	public Logger getLogger() {
		return log;
	}

        public void DebugLogFatal(Object... Msg) {
                if ( Msg.length == 2)
                    log.fatal(((Object[])Msg)[0], (Throwable)((Object[])Msg)[1]);
                else
                    log.fatal(Msg[0]);
        }
        public void DebugLogError(Object... Msg) {
                if ( Msg.length == 2)
                    log.error(((Object[])Msg)[0], (Throwable)((Object[])Msg)[1]);
                else
                    log.error(Msg[0]);
        }
        public void DebugLogWarning(Object... Msg) {
                if ( Msg.length == 2)
                    log.warn(((Object[])Msg)[0], (Throwable)((Object[])Msg)[1]);
                else
                    log.warn(Msg[0]);
        }
        public void DebugLogInfo(Object... Msg) {
                if ( Msg.length == 2)
                    log.info(((Object[])Msg)[0], (Throwable)((Object[])Msg)[1]);
                else
                    log.info(Msg[0]);
        }
        public void DebugLogDebug(Object... Msg) {
                if ( Msg.length == 2)
                    log.debug(((Object[])Msg)[0], (Throwable)((Object[])Msg)[1]);
                else
                    log.debug(Msg[0]);
        }
        public void DebugLogTrace(Object... Msg) {
                if ( Msg.length == 2)
                    log.trace(((Object[])Msg)[0], (Throwable)((Object[])Msg)[1]);
                else
                    log.trace(Msg[0]);
        }

        public void DebugLog(Object MsgLevel, Object MsgString) {
            DebugLog(MsgLevel, MsgString, null);
        }
        
	public void DebugLog(Object MsgLevel, Object MsgString, Throwable stacktrace) {
		

		int MsgLevelInt = 6;
		String MsgLevelStr = new String();

		if (MsgLevel instanceof String) {
//			System.out.println("Found String");
			MsgLevelStr = MsgLevel.toString().toUpperCase();
			if (MsgLevelStr.endsWith("TRACE2")) {
				MsgLevelInt = 6;
			} else if (MsgLevelStr.endsWith("TRACE")) {
				MsgLevelInt = 6;
			} else if (MsgLevelStr.endsWith("DEBUG")) {
				MsgLevelInt = 5;
			} else if (MsgLevelStr.endsWith("INFO")) {
				MsgLevelInt = 4;
			} else if (MsgLevelStr.endsWith("WARNING")) {
				MsgLevelInt = 3;
			} else if (MsgLevelStr.endsWith("ERROR")) {
				MsgLevelInt = 2;
			} else if (MsgLevelStr.endsWith("FATAL")) {
				MsgLevelInt = 1;
			} else if ( MsgLevelStr.endsWith("NONE")) {
				MsgLevelInt = 0;
			} else {
				try {
					MsgLevelInt = Integer.parseInt(MsgLevelStr);
				} catch (NumberFormatException nfe) {
					DebugLog(LogLevel.Warning, "DebugLog MsgLevel Format Exception: " + nfe.getMessage());
					return;
				}
			}
		} else if (MsgLevel instanceof Integer) {
			MsgLevelInt = (Integer) MsgLevel;
		} else if ( MsgLevel instanceof LogLevel) {
//			System.out.println("Found LogLevel");
			if (MsgLevel == LogLevel.Trace2) {
				MsgLevelInt = 6;
			} else if (MsgLevel == LogLevel.Trace) {
				MsgLevelInt = 6;
			} else if (MsgLevel == LogLevel.Debug) {
				MsgLevelInt = 5;
			} else if (MsgLevel == LogLevel.Info) {
				MsgLevelInt = 4;
			} else if (MsgLevel == LogLevel.Warning) {
				MsgLevelInt = 3;
			} else if (MsgLevel == LogLevel.Error) {
				MsgLevelInt = 2;
			} else if (MsgLevel == LogLevel.Fatal) {
				MsgLevelInt = 1;
			}else if ( MsgLevel == LogLevel.Off) {
				MsgLevelInt = 0;
			}
		} else {
			ortus.api.DebugLog(LogLevel.Warning, "DebugLog MsgLevel Format Exception: MsgLevel is not String or Integer");
			return;
		}

                switch((Integer)MsgLevelInt) {
                        case 1: if ( stacktrace == null ) log.fatal(MsgString); else  log.fatal(MsgString,stacktrace);
                                break;
                        case 2: if ( stacktrace == null ) log.error(MsgString); else  log.error(MsgString,stacktrace);
                                break;
                        case 3: if ( stacktrace == null ) log.warn(MsgString); else  log.warn(MsgString,stacktrace);
                                break;
                        case 4: if ( stacktrace == null ) log.info(MsgString); else  log.info(MsgString,stacktrace);;
                                break;
                        case 5: if ( stacktrace == null ) log.debug(MsgString); else  log.debug(MsgString,stacktrace);
                                break;
                        case 6: if ( stacktrace == null ) log.trace(MsgString); else  log.trace(MsgString,stacktrace);
                                break;
                        default: if ( stacktrace == null ) log.trace(MsgString); else  log.trace(MsgString,stacktrace);
                                break;
                }
		return;
	}

	public static void DebugLogDB(Object MsgLevel, String MsgString) {

		int MsgLevelInt = 0;
		String MsgLevelStr = new String();

		if (MsgLevel instanceof String) {
			MsgLevelStr = MsgLevel.toString().toUpperCase();
			if (MsgLevelStr.equals("TRACE2")) {
				MsgLevelInt = 5;
			} else if (MsgLevelStr.equals("TRACE")) {
				MsgLevelInt = 4;
			} else if (MsgLevelStr.equals("INFO")) {
				MsgLevelInt = 3;
			} else if (MsgLevelStr.equals("WARNING")) {
				MsgLevelInt = 2;
			} else if (MsgLevelStr.equals("ERROR")) {
				MsgLevelInt = 1;
			} else {
				try {
					MsgLevelInt = Integer.parseInt(MsgLevelStr);
				} catch (NumberFormatException nfe) {
					api.DebugLog(LogLevel.Warning, "DebugLog MsgLevel Format Exception: " + nfe.getMessage());
					return;
				}
			}
		} else if (MsgLevel instanceof Integer) {
			MsgLevelInt = (Integer) MsgLevel;
		} else {
			api.DebugLog(LogLevel.Warning, "DebugLog MsgLevel Format Exception: MsgLevel is not String or Integer");
			return;
		}

		ortus.api.executeSQL("insert into sage.syslog ( event_level, event_time, event_msg) values ( " + MsgLevelInt + ",current_timestamp,'" + MsgString + "'");

		return;
	}

	public static List<Object> GetDebugLogDB(int MsgLevel) {
		String sql = "select event_time, event_level, event_msg from sage.syslog";
		if ( MsgLevel < 9)
			sql+=" where event_level = " + MsgLevel;
		return ortus.api.executeSQLQuery(sql);
	}
}
