/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.property;

import sagex.api.Configuration;

/**
 *
 * @author Aaron
 */
public class SageServerProperty {
	public static boolean HasSageServerPropertyElement(String PropertyName, String DefaultValue, String Element) {
		if (Configuration.GetServerProperty(PropertyName, DefaultValue).contains(";" + Element + ";")) {
			return true;
		} else {
			return false;
		}
	}

	public static String SetSageServerPropertyElement(String PropertyName, String DefaultValue, String Element) {
		String CurrentElements = Configuration.GetServerProperty(PropertyName, DefaultValue);
		String result = null;
		if (CurrentElements.contains(";" + Element + ";")) {
			result = "0";
		} else {
			String NewElements = CurrentElements + ";" + Element + ";";
			Configuration.SetServerProperty(PropertyName, NewElements);
			result = "1";
		}
		return result;
	}

	public static String RemoveSageServerPropertyElement(String PropertyName, String DefaultValue, String Element) {
		String CurrElements = Configuration.GetServerProperty(PropertyName, DefaultValue);
		String ElementRemoved = null;
		String result = null;
		if (CurrElements.contains(";" + Element + ";")) {

			ElementRemoved = CurrElements.replaceAll(";" + Element + ";", "");

			Configuration.SetServerProperty(PropertyName, ElementRemoved);
			result = "1";
		} else {
			result = "0";
		}
		return result;

	}

	public static String[] GetSageServerPropertyArray(String PropertyName, String DefaultValue) {
		String[] ElementArray = null;
		String mediaElements = Configuration.GetServerProperty(PropertyName, DefaultValue);
		if (mediaElements != null) {
			ElementArray = mediaElements.split(";*;");
		} else {
			ElementArray = null;
		}

		return ElementArray;
	}
}
