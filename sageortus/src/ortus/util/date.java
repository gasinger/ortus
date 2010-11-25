/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.util;

import java.text.ParseException;
import java.util.Date;

import ortus.*;
/**
 *
 * @author Aaron
 */
public class date extends vars{
	public static long GetEpochFromDate(String timestamp) {
		try {
			return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(timestamp).getTime();
		} catch (ParseException ex) {
			return 0;
		}
	}

        public static long GetCurrentEpoch() {
            return new Date().getTime();
        }

        public static String GetFormatDate(String current_date, String output_format) {
            long etime = 0;

            try {
                etime = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(current_date).getTime();
            } catch(Exception e) {
                etime = new Date().getTime();
            }

            return new java.text.SimpleDateFormat(output_format).format(etime);
        }
}
