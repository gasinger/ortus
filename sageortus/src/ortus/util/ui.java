/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import ortus.media.OrtusMedia;
import ortus.vars;
import sagex.UIContext;
import sagex.api.Global;
import sagex.api.Configuration;
import sagex.api.MediaFileAPI;
import sagex.UIContext;
/**
 *
 * @author Aaron
 */
public class ui extends vars {
	/*
	 * Given a position relative to the full visible screen will return the position taking into account overscan.
	 * Good for "Background Components" that need to line up with non background panel widgets.
	 * @param A - the UI position (0.00 - 1.00)
	 * @param wrt0 - true = A is wrt 0 (top), false = A is wrt 1 (bottom)
	 */
	public static double GetVerticalPositionWithOverscan(double A, boolean wrt0)
	{
		double B = 0;

		if (A < 0) {
			A = 0;
		}
		if (A > 1.0) {
			A = 1.0;
		}

		UIContext UIC = ortus.api.GetUIContext();

		double Dim = Global.GetFullUIHeight(UIC);
		double Offset = Configuration.GetOverscanOffsetY(UIC);
		double Scale = Configuration.GetOverscanScaleHeight(UIC);

		if (wrt0) {
			B = Offset / Dim + A * Scale;
		} else {
			B = 1 - (Offset / Dim + (1 - A) * Scale);
		}

		if (B < 0) {
			B = 0;
		}
		if (B > 1.0) {
			B = 1.0;
		}

		ortus.api.DebugLog(LogLevel.Trace, "Vertical Position With Overscan");
		ortus.api.DebugLog(LogLevel.Trace, "Input = '" + A + "'");
		ortus.api.DebugLog(LogLevel.Trace, "FullUIWidth = '" + Dim + "'");
		ortus.api.DebugLog(LogLevel.Trace, "OverscanOffsetX = '" + Offset + "'");
		ortus.api.DebugLog(LogLevel.Trace, "OverscanScaleWidth = '" + Scale + "'");
		ortus.api.DebugLog(LogLevel.Trace, "Output = '" + B + "'");

		return B;
	}

	/*
	 * Given a relative position will return the true position taking into account overscan.
	 * Good for "Background Components" that need to line up with non background panel widgets.
	 * @param A - the UI position (0.00 - 1.00)
	 * @param wrt0 - true = A is wrt 0 (left), false = A is wrt 1 (right)
	 */
	public static double GetHorizontalPositionWithOverscan(double A, boolean wrt0)
	{
		double B = 0;

		if (A < 0) {
			A = 0;
		}
		if (A > 1.0) {
			A = 1.0;
		}

		UIContext UIC = ortus.api.GetUIContext();

		double Dim = Global.GetFullUIWidth(UIC);
		double Offset = Configuration.GetOverscanOffsetX(UIC);
		double Scale = Configuration.GetOverscanScaleWidth(UIC);

		if (wrt0) {
			B = Offset / Dim + A * Scale;
		} else {
			B = 1 - (Offset / Dim + (1 - A) * Scale);
		}

		if (B < 0) {
			B = 0;
		}
		if (B > 1.0) {
			B = 1.0;
		}

		ortus.api.DebugLog(LogLevel.Trace, "Horizontal Pos With Overscan");
		ortus.api.DebugLog(LogLevel.Trace, "Input = '" + A + "'");
		ortus.api.DebugLog(LogLevel.Trace, "FullUIWidth = '" + Dim + "'");
		ortus.api.DebugLog(LogLevel.Trace, "OverscanOffsetX = '" + Offset + "'");
		ortus.api.DebugLog(LogLevel.Trace, "OverscanScaleWidth = '" + Scale + "'");
		ortus.api.DebugLog(LogLevel.Trace, "Output = '" + B + "'");
		return B;
	}

	public static Object GetMediaObject(Object o1, Object o2) {
           try {
		if (MediaFileAPI.IsMediaFileObject(o1))
			return o1;

                if ( o1 instanceof OrtusMedia)
                    return o1;

                 if ( o2 instanceof HashMap) {
                    if ( ((HashMap)o2).get(o1) != null) {
                        if ( ((List)((HashMap)o2).get(o1)).get(0) != null)
                            return ((List)((HashMap)o2).get(o1)).get(0);
                        else  {
                            ortus.api.DebugLog(LogLevel.Error, "GetMediaObject: Empty Array Exception: o1: " + o1 + " o2: " + o2);
                            return null;
                        }
                    }  else {
                        ortus.api.DebugLog(LogLevel.Error, "GetMediaObject: Key Missing Exception: o1: " + o1 + " o2: " + o2);
                        return null;
                    }
                }  else {
                        ortus.api.DebugLog(LogLevel.Error, "GetMediaObject: Missing HashMap Exception: o1: " + o1 + " o2: " + o2);
                        return null;
                }
            } catch (Exception e) {
                ortus.api.DebugLogError("GetMediaOjbect: Exception: ", e);
                return null;
            }
	}

	public static boolean IsClient() {
                String UICname = Global.GetUIContextName();
                if ( UICname == null) {
                    UICname="";
                }
//		if ( UICname.equalsIgnoreCase("SAGETV_PROCESS_LOCAL_UI") || Global.IsClient(new UIContext(Global.GetUIContextName())) || Global.IsServerUI(new UIContext(Global.GetUIContextName())) || Global.IsDesktopUI(new UIContext(Global.GetUIContextName())))
                if ( UICname.equalsIgnoreCase("SAGETV_PROCESS_LOCAL_UI") || Global.IsClient() || Global.IsServerUI() || Global.IsDesktopUI())
			return true;
		else
			return false;
	}

	public static boolean IsExtender() {
//                ortus.api.DebugLog(LogLevel.Info, "IsExtender(): IsRemoteUI(): " + Global.IsRemoteUI(new UIContext(Global.GetUIContextName())));
//                ortus.api.DebugLog(LogLevel.Info, "IsExtender(): IsDesktopUI():" + Global.IsDesktopUI(new UIContext(Global.GetUIContextName())));

		if ( Global.IsRemoteUI(new UIContext(Global.GetUIContextName())) && ! Global.IsDesktopUI(new UIContext(Global.GetUIContextName())))
			return true;
		else
			return false;
	}

        public static boolean IsServer() {
		if ( ! Global.IsServerUI() &&
		     ! Global.IsClient() &&
		     ! Global.IsDesktopUI() &&
		     ! Global.IsRemoteUI())
			return true;
		else
			return false;
	}
}
