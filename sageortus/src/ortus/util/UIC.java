package ortus.util;

import ortus.vars;
import sagex.UIContext;
import sagex.api.Global;

/**
 *
 * @author asharpe
 */
public class UIC extends vars
{

	private static InheritableThreadLocal UIContext = new InheritableThreadLocal();

	public static String GetUIContextName() {
		if ( Global.GetUIContextName() != null)
			return Global.GetUIContextName();

		StoreUIContextFromThread();
//		System.out.println("GetUIContextName: " + UIContext.get());
		return (String) UIContext.get();
	}

	public static void StoreUIContextName(String UIC_Str) {
		//System.out.println("StoreUIContextName:" + UIC_Str);
		UIContext.set(UIC_Str);
		return;
	}

	public static UIContext GetUIContext() {
		return new UIContext(GetUIContextName());
	}

	public static void StoreUIContextFromThread() {

		if (UIContext.get() == null) {

			Thread t = Thread.currentThread();
			String tname = t.getName();
			ortus.api.DebugLog(LogLevel.Trace, "StoreUIContextFromThread: Thread Name: " + tname);

			String uiname = tname.substring(tname.lastIndexOf("-") + 1);
			ortus.api.DebugLog(LogLevel.Trace, "StoreUIContextFromThread: UIName: " + uiname);
			if (uiname.equals("0")) {
			} else if (uiname.equals("SAGETV_PROCESS_LOCAL_UI")) {
			} else if (uiname.length() == 12) {
				int i;
				int j;
				String str1;
				for (i = 0; i <= 10; i += 2) {
					str1 = uiname.substring(i, i + 2);
					try {
						j = Integer.parseInt(str1, 16);
					} catch (java.lang.NumberFormatException e) {
						ortus.api.DebugLog(LogLevel.Error, "StoreUIContextFromThread could not find a valid UIContext. " +
								"Use ortus.api.StoreUIContextName( GetUIContextName() ) from the STV before the offending method.");
						return;
					}
				}
			} else {
				ortus.api.DebugLog(LogLevel.Error, "StoreUIContextFromThread could not find a valid UIContext. " +
						"Use ortus.api.StoreUIContextName( GetUIContextName() ) from the STV before the offending method.");
				return;

			}
			ortus.api.DebugLog(LogLevel.Trace, "StoreUIContextFromThread found and stored a valid UIContext:" + uiname);
			StoreUIContextName(uiname);
			return;
		}
		return;
	}
}
