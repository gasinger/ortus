/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sagex.h2;

/**
 *
 * @author jphipps
 */
public class api {

        public static String EpochToDate(long epoch) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date (epoch));
        }
        public static String EpochToTimestamp(long epoch) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date (epoch));
        }
	public static long TimestampToEpoch(java.sql.Timestamp ts) {
		return ts.getTime();
	}
	public static long DateToEpoch(java.sql.Date ts) {
		return ts.getTime();
	}
}
