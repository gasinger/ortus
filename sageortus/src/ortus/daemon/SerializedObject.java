/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.daemon;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author jphipps
 */
public class SerializedObject implements Serializable {
    private Object so;
    private String ss;
    private boolean sb;

    public SerializedObject(Object so) {
        this.so = so;
    }

    public SerializedObject(String ss) {
        this.ss = ss;
    }

    public SerializedObject(boolean sb) {
        this.sb = sb;
    }

    public void setBoolean(boolean sb ) {
        this.sb=sb;
    }
    public void setObjet(Object so) {
        this.so = so;
    }

    public void setString(String ss) {
        this.ss = ss;
    }

    public Object toObject() {
        return so;
    }
    public String toString() {
        return ss;
    }
    public boolean toBoolean() {
        return sb;
    }
}
