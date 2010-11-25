/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ortus.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.Collections;
import ortus.vars;

import sagex.UIContext;
import sagex.api.Configuration;
import sagex.api.Utility;



public class SageProperty extends vars
{
	public static void SetSageProperty(String property, String value) {
		Configuration.SetProperty(property, value);
		return;

	}

	public static String GetSageProperty(String property, String defvalue) {
		return Configuration.GetProperty(property, defvalue);
	}

	public static void RemoveSageProperty(String property) {
		Configuration.RemoveProperty(property);
		return;
	}

	public static void RemoveSagePropertyAndChildren(String property) {
		Configuration.RemovePropertyAndChildren(property);
	}

	public static String[] GetSageSubpropertiesThatAreLeaves(String property){
		return Configuration.GetSubpropertiesThatAreLeaves(property);
	}


	public static String[] GetSageSubpropertiesThatAreBranches(String property){
		return Configuration.GetSubpropertiesThatAreBranches(property);
	}

	// For making arrays out of properties items with ";name;" separation. Great when you want to use a property
	// item as an array. Has,Get,Set and remove calls.
	public static boolean HasSagePropertyElement(String PropertyName, String DefaultValue, String Element) {
		if (ortus.api.GetSageProperty(PropertyName, DefaultValue).contains(";" + Element + ";")) {
			return true;
		} else {
			return false;
		}
	}

	public static String SetSagePropertyElement(String PropertyName, String DefaultValue, String Element) {
		String CurrentElements = ortus.api.GetSageProperty(PropertyName, DefaultValue);
		String result = null;
		if (CurrentElements.contains(";" + Element + ";")) {
			result = "0";
		} else {
			String NewElements = CurrentElements + ";" + Element + ";";
			ortus.api.SetSageProperty(PropertyName, NewElements);
			result = "1";
		}
		return result;
	}

	public static String RemoveSagePropertyElement(String PropertyName, String DefaultValue, String Element) {
		String CurrElements = ortus.api.GetSageProperty(PropertyName, DefaultValue);
		String ElementRemoved = null;
		String result = null;
		if (CurrElements.contains(";" + Element + ";")) {

			ElementRemoved = CurrElements.replaceAll(";" + Element + ";", "");

			ortus.api.SetSageProperty(PropertyName, ElementRemoved);
			result = "1";
		} else {
			result = "0";
		}
		return result;

	}

	public static String[] GetSagePropertyArray(String PropertyName, String DefaultValue) {
		String[] ElementArray = null;
		String mediaElements = ortus.api.GetSageProperty(PropertyName, DefaultValue);
		if (mediaElements != null) {
			ElementArray = mediaElements.split(";*;");
		} else {
			ElementArray = null;
		}

		return ElementArray;
	}

	public static void LoadSagePropertyFile(String filename) {

		FileInputStream PropFile = null;
		Properties props = new Properties();

		try {
			PropFile = new FileInputStream(filename);
			ortus.api.DebugLog(LogLevel.Trace2, "Property File Open Success: " + filename);
		} catch (FileNotFoundException ex) {
			ortus.api.DebugLog(LogLevel.Error, "Property File Open Error: "+ ex);
		}
		try {
			props.load(PropFile);
			ortus.api.DebugLog(LogLevel.Trace2, "Property File Load Success: " + filename);
		} catch (IOException ex) {
			ortus.api.DebugLog(LogLevel.Error, "Property File Load Error: "+ ex);
		}
		try {
			PropFile.close();
			ortus.api.DebugLog(LogLevel.Trace2, "Property File Close Success: " + filename);
		} catch (IOException ex) {
			ortus.api.DebugLog(LogLevel.Error, "Property File Close Error: "+ ex);
		}

		Enumeration propsenum = props.propertyNames();

		for (; propsenum.hasMoreElements(); ) {
			String propName = (String)propsenum.nextElement();
			String propVal = (String)props.get(propName);
			ortus.api.DebugLog(LogLevel.Trace, "Setting Property: " + propName + " = " + propVal);
			ortus.api.SetSageProperty(propName, propVal);
		}

	}

	public static boolean SaveSagePropertyFile(String filename, String parentprop, String DoNotSaveProps){

		ArrayList propkeys = ortus.api.GetSagePropertyAndChildren(parentprop);
		SortedProperties props = new SortedProperties();


		FileOutputStream propfile = null;

		String[] DoNotSavePropsArray = DoNotSaveProps.split(",");
		for (String s : DoNotSavePropsArray){
			propkeys.remove(s);
			propkeys.remove(parentprop.concat("/").concat(s));
		}
		ortus.api.DebugLog(LogLevel.Trace2, "SageSaveProperyFile, Props to save: " + propkeys);

		for (String propkey : (String[])propkeys.toArray(new String[0])){
			props.setProperty(propkey, ortus.api.GetSageProperty(propkey, ""));
		}

		ortus.api.DebugLog(LogLevel.Info,"SageSavePropertyFile, Filename: " + filename);

		try{
			propfile = new FileOutputStream(filename);
			props.store(propfile, "TVE Settings file");
			propfile.close();
			ortus.api.DebugLog(LogLevel.Info, "SageSavePropertyFile, Success");
			return true;
		}
		catch(IOException ioe){
			ortus.api.DebugLog(LogLevel.Error, "SageSavePropertyFile, I/O Exception: " + ioe);
			return false;
		}


	}

	public static ArrayList GetGetPropertyAndChildren(String parentprop){
		ArrayList props = new ArrayList();
		String[] branches = ortus.api.GetSageSubpropertiesThatAreBranches(parentprop);
		String[] leaves = ortus.api.GetSageSubpropertiesThatAreLeaves(parentprop);

		for (String s : branches){
			props.addAll(GetGetPropertyAndChildren(parentprop.concat("/").concat(s)));
		}

		//ortus.api.DebugLog(LogLevel.Trace2, "props = " + props);

		for (String s : leaves){
			String propadd = parentprop.concat("/").concat(s);
			//ortus.api.DebugLog(LogLevel.Trace2, "Adding Property to List: " + propadd);
			props.add(propadd);
		}

		return props;


	}

}

//class SortedProperties extends Properties
//{
//
//	@SuppressWarnings("unchecked")
//	public synchronized Enumeration keys() {
//		Enumeration keysEnum = super.keys();
//		Vector keyList = new Vector();
//		while (keysEnum.hasMoreElements()) {
//			keyList.add(keysEnum.nextElement());
//		}
//		Collections.sort(keyList);
//		return keyList.elements();
//	}
//}
