/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.daemon;

import java.util.TimerTask;

/**
 *
 * @author jphipps
 */
public class OrtusTask extends TimerTask {

    String taskdesc = null;
    String taskname = null;
    Object[] taskparams = null;

    public OrtusTask(String taskname) {
        this.taskname = taskname;
        this.taskdesc = taskname;
    }

    public OrtusTask(String taskname, Object[] taskparams) {
        this.taskname = taskname;
        this.taskparams = taskparams;
        this.taskdesc = taskname;
    }

    public OrtusTask(String taskdesc, String taskname, Object[] taskparams) {
        this.taskname = taskname;
        this.taskparams = taskparams;
        this.taskdesc = taskdesc;
    }

    public String GetDescription() {
        return this.taskdesc;
    }
    
    @Override
    public void run() {
        
        if ( taskparams == null)
            ortus.process.ExecuteStatic(taskname);
        else
            ortus.process.ExecuteStatic(taskname, taskparams);
    }

}
