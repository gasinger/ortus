/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.util;

/**
 *
 * @author Aaron
 */
public class math {
	public static float Round(float Rval, int Rpl) {
		float p = (float) Math.pow(10, Rpl);
		Rval = Rval * p;
		float tmp = Math.round(Rval);
		return (float) tmp / p;
	}

	public static String MD5Sum(String str) {
	   return str;
//           MessageDigest m = null;
//		try {
//			m = MessageDigest.getInstance("MD5");
//		} catch (NoSuchAlgorithmException ex) {
//			ortus.api.DebugLog(ERROR,"MD5Sum: Exception: " + ex);
//			return null;
//		}
//           m.update(str.getBytes(),0,str.length());
//           String  x = new BigInteger(1,m.digest()).toString(16);
//           while ( x.length() < 32)
//              x="0" + x;
//	   return x;
	}

}
