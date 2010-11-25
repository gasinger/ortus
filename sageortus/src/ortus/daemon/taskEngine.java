/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.daemon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import ortus.mq.EventListener;
import ortus.vars.LogLevel;

/**
 *
 * @author jphipps
 */
public class taskEngine extends EventListener {

        Timer OrtusTimeManager = null;
        HashMap<String,OrtusTask> OrtusTasks = new HashMap<String,OrtusTask>();

	public taskEngine() {
                super();
                ortus.api.DebugLog(ortus.vars.LogLevel.Debug, "cronDaemon: Loading");
                OrtusTimeManager = new Timer(true);
	}

        public Object[] GetTaskList() {
            return OrtusTasks.keySet().toArray();
        }

        public String GetTaskDescription(String taskid) {
            return OrtusTasks.get(taskid).GetDescription();
        }

	public boolean CreateTask(String taskid, String description, String taskname, String tasktime, long interval, Object[] params) {
	    boolean result = true;

            Connection conn = ortus.api.GetConnection();
            ResultSet rs = null;
            PreparedStatement ps = null;
            try {
		ps = conn.prepareStatement("insert into sage.tasks ( taskid, taskdescription, task, tasktime, taskinterval, taskparams) values( ?, ?, ?, ?, ?, ?)");
		ps.setString(0, taskid);
		ps.setString(1, description);
		ps.setString(2, taskname);
		ps.setString(3, tasktime);
		ps.setLong(4, interval);
		ps.setObject(5, params);
		ps.execute();
	    } catch(Exception e) {
                ortus.api.DebugLog(LogLevel.Error, "taskEngine: CreateTasks: Exception: " + e);
		Logger log = Logger.getLogger(this.getClass());
		log.fatal(e);
                result = false;
		return result;
	    }
	    
	    result = LoadTasks();

	    return result;
	}
        public boolean LoadTasks() {
            boolean result = true;

            Connection conn = ortus.api.GetConnection();
            ResultSet rs = null;
            Statement stmt = null;
            try {
		stmt = conn.createStatement();
                rs = stmt.executeQuery("select taskid, taskdescription, task, tasktime, taskinterval, taskparams from sage.tasks");
                Calendar cal = Calendar.getInstance();

                while( rs.next()) {
                    String taskid = rs.getString(1);
                    String taskdescription = rs.getString(2);
                    String task = rs.getString(3);
                    String tasktime = rs.getString(4);
                    long taskinterval = rs.getLong(5);
                    Object taskparam = rs.getObject(6);

                    ortus.api.DebugLog(LogLevel.Trace, "LoadTask: Loading taskid: " + taskid + " task: " + task + " time: " + tasktime + " Interval: " + taskinterval);
                    String[] tt = tasktime.split(":");
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tt[0]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(tt[1]));
                    cal.set(Calendar.SECOND, Integer.parseInt(tt[2]));
                    if ( OrtusTasks.get(taskid) != null) {
                        OrtusTasks.get(taskid).cancel();
                        OrtusTasks.remove(taskid);
                    }

                    if ( taskinterval == 0) {
                        Schedule(taskid, new OrtusTask(taskdescription, task, (Object[])taskparam), cal.getTime());
                    } else {
                        Schedule(taskid, new OrtusTask(taskdescription, task, (Object[])taskparam), cal.getTime(), taskinterval);
                    }
                }
            } catch(Exception e) {
                ortus.api.DebugLog(LogLevel.Error, "taskEngine: LoadTasks: Exception: " + e);
		Logger log = Logger.getLogger(this.getClass());
		log.fatal(e);
                result = false;
            } finally {
                if ( rs !=null) try { rs.close(); } catch(Exception ex) {}
                if ( stmt !=null) try { stmt.close(); } catch(Exception ex) {}
                if ( conn !=null) try { conn.close(); } catch(Exception ex) {}
            }

            return result;

        }
        public void Schedule(String taskid, OrtusTask task, long interval) {
            Calendar tt = Calendar.getInstance();
            tt.setTime(new Date());
            ortus.api.DebugLog(LogLevel.Trace, "OrtusTaskManager: Scheduled Task: " + taskid + " StartDate: " + tt.getTime().toString() + " Interval: " + interval);
            Cancel(taskid);

            OrtusTimeManager.scheduleAtFixedRate(task, tt.getTime(), interval*1000);

            OrtusTasks.put(taskid, task);
        }
        
        public void Schedule(String taskid, OrtusTask task, Date startdate) {
            Schedule(taskid, task, startdate, 0);
        }

        public void Schedule(String taskid, OrtusTask task, Date startdate, long interval) {
            Calendar now = Calendar.getInstance();
            Calendar tt = Calendar.getInstance();
            tt.setTime(startdate);

            if ( now.after(tt)) {
                tt.add(Calendar.HOUR, 24);
            }

            ortus.api.DebugLog(LogLevel.Trace, "OrtusTaskManager: Scheduled Task: " + taskid + " StartDate: " + tt.getTime().toString() + " Interval: " + interval);
            Cancel(taskid);
            if ( interval > 0)
                OrtusTimeManager.scheduleAtFixedRate(task, tt.getTime(), interval*1000);
            else
                OrtusTimeManager.schedule(task, tt.getTime());

            OrtusTasks.put(taskid, task);
        }

        public boolean Cancel(String taskid) {
            boolean result = false;
            if ( OrtusTasks.get(taskid) != null) {
                result = OrtusTasks.get(taskid).cancel();
                OrtusTasks.remove(taskid);
            }
            return result;
        }
}
