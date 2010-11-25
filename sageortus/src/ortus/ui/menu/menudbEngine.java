package ortus.ui.menu;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import sagex.UIContext;


/**
 * Ortus Menu Handling Class
 * @author jphipps
 *
 */
public class menudbEngine extends ortus.vars  {

//	public enum MenuType {
//		General,
//		TV,
//		Movie,
//		Music,
//		Weather,
//		Pictures,
//		Setup
//	}
//	/**
//	 * Contructor builds default menu xmlfile or load existing menu xmlfile
//	 * @param Path for configuration xml file
//	 */
//	public menudbEngine(String MenuPath) {
//                ortus.api.DebugLog(LogLevel.Trace2,"Menu: Loading Class");
//	}
//
//	public void Shutdown() {
//		return;
//	}
//
//	/**
//	 * Return a list of Main Menu Items
//	 * @return Vector of menu items
//	 */
//
//	public Object[] getMenu() {
//		getMenu("main");
//	}
//
//	@SuppressWarnings("unchecked")
//	public List getMenu(String menuname) {
//		return ortus.api.executeSQLQuery("select menuid from sage.menuitems order by position");
//	}
//	/**
//	 * Return MenuType
//	 * @param m Menu Name
//	 * @return Menu Type
//	 */
//	public String GetMenuType(String m) {
//			List result = ortus.api.executeSQLQuery("select menutype from sage.menuitems where menuid = " + m);
//			if ( result != null)
//				return (String)result.get(0);
//			else
//				return "";
//	}
//
//	/**
//	 * Set MenuType
//	 * @param m Menu Name
//         * @param menutype Menu Type
//	 * @return Menu Type
//	 */
//	public void SetMenuType(String m, String mtype) {
//        	ortus.api.executeSQL("update sage.menuitems set menutype = '" + mtype + "'");
//	}
//
//	/**
//	 * Return Sage menu to execute for a menu item
//	 * @param m Menu Name
//	 * @return Sage Menu to execute
//	 */
//	public String GetMenuAction(String m) {
//		List result = ortus.api.executeSQLQuery("select action from sage.menuitems where menuid = " + m);
//			if ( result != null)
//				return (String)result.get(0);
//			else
//				return "";
//	}
//	/**
//	 * Return a list of Static variables to set upon selection of a menu item
//	 * @param m Menu Name
//	 * @return HashMap of Static variables/values
//	 */
//	public HashMap<String, String> getMenuStatics(String m) {
//		List<List> result = ortus.api.executeSQLQueryArray("select name, value from sage.menuitemsstatics where menuid = " + m);
//		if ( result == null)
//			return null;
//
//		HashMap<String,String> retval = new HashMap<String,String>();
//
//		for ( List x : result) {
//			retval.put(x.get(0), x.get(1));
//		}
//
//		return retval;
//	}
//	/**
//	 * Delete a main menu item
//	 * @param m Main Menu Title
//	 * @return return code
//	 */
//	@SuppressWarnings("unchecked")
//	public void deleteMenu(String m) {
//		ortus.api.executeSQL("delete from sage.menu where menuid = " + m);
//		ortus.api.executeSQL("delete from sage.menuitems where menuid = " + m);
//		ortus.api.executeSQL("delete from sage.menuitemstatics where menuid = " + m);
//	}
//
//	/**
//	 * Add a new Main Menu
//	 * @param mt Main Menu Title
//	 * @param ma Sage menu to execute
//	 */
//	public String addMenu(String mt, String ma) {
//		UUID menuid = new UUID();
//
//		return;
//	}
//
//	/**
//	 * Update a Main Menu title
//	 * @param mt Main Menu Title
//	 * @param nmt New Main Menu Title
//	 * @return return code
//	 */
//	public void updateMenuTitle(String mt, String nmt) {
//
//		if( menu.get(mt)!= null ) {
//			menu.get(mt).setTitle(nmt);
//			writexml();
//			LoadMenu();
//		}
//
//		return;
//	}
//
//	/**
//	 * Update a Main Menu Action
//	 * @param mt Main Menu Title
//	 * @param nma Sage menu name
//	 * @return return code
//	 */
//	public int updateMenuAction(String mt, String nma) {
//
//		if( menu.get(mt)!= null ) {
//			menu.get(mt).setAction(nma);
//			writexml();
//			LoadMenu();
//		}
//
//		return 1;
//	}
//
//	/**
//	 * Add a new Static Variable/Value for a main menu item
//	 * @param mt Main Menu Title
//	 * @param gn Static Variable Name
//	 * @param gv Static Variable Value
//	 * @return return code
//	 */
//	public int addMenuStatic(String mt, String gn, String gv) {
//
//		if( menu.get(mt)!= null ) {
//			menu.get(mt).addStatic(gn, gv);
//			writexml();
//			LoadMenu();
//		}
//
//		return 1;
//	}
//
//	/**
//	 * Update a Main Menu Static variable
//	 * @param mt Main Menu Title
//	 * @param gn Static Variable Name
//	 * @param gv Static Variable Value
//	 * @return return code
//	 */
//	public int updateMenuStatic(String mt, String gn, String gv) {
//
//		if( menu.get(mt)!= null ) {
//			menu.get(mt).addStatic(gn, gv);
//			writexml();
//			LoadMenu();
//		}
//		return 1;
//	}
//
//	/**
//	 * Delete a Static Variable for a Main Menu Item
//	 * @param mt Main Menu Title
//	 * @param gn Static Variable Name
//	 * @return return code
//	 */
//	public int deleteMenuStatic(String mt, String gn) {
//
//		if( menu.get(mt)!= null ) {
//			menu.get(mt).delStatic(gn);
//			writexml();
//			LoadMenu();
//		}
//
//		return 1;
//	}
//
//	/**
//	 * Increase the menu position
//	 * @param mt Main Menu Title
//	 */
//	@SuppressWarnings("unchecked")
//	public void incPosition(String mt) {
//		if ( menu.get(mt) != null ) {
//			if ( menu.get(mt).getPosition() >= maxMenuPosition ) {
//				return;
//			}
//
//			Object[] mi = menu.keySet().toArray();
//			Arrays.sort(mi, new Comparator() {
//			    public int compare(Object o1, Object o2) {
//			    	if ( menu.get(o1).getPosition() > menu.get(o2).getPosition())
//			    		return 1;
//			    	else
//			    		return -1;
//			    }
//			  });
//
//			int curPosition = menu.get(mt).getPosition();
//			curPosition++;
//			for( int x = 0; x < mi.length; x++ ) {
//				if( menu.get(mi[x]).getPosition() == curPosition ) {
//					menu.get(mi[x]).decPosition();
//				}
//			}
//			menu.get(mt).incPosition();
//			writexml();
//			LoadMenu();
//		}
//
//		return;
//	}
//
//	/**
//	 * Decrease the menu position
//	 * @param mt Main Menu Title
//	 */
//	@SuppressWarnings("unchecked")
//	public void decPosition(String mt) {
//		if ( menu.get(mt) != null ) {
//			if ( menu.get(mt).getPosition() == 1 ) {
//				return;
//			}
//
//			Object[] mi = menu.keySet().toArray();
//			Arrays.sort(mi, new Comparator() {
//			    public int compare(Object o1, Object o2) {
//			    	if ( menu.get(o1).getPosition() > menu.get(o2).getPosition())
//			    		return 1;
//			    	else
//			    		return -1;
//			    }
//			  });
//
//			int curPosition = menu.get(mt).getPosition();
//			curPosition--;
//			for( int x = 0; x < mi.length; x++ ) {
//				if( menu.get(mi[x]).getPosition() == curPosition ) {
//					menu.get(mi[x]).incPosition();
//				}
//			}
//			menu.get(mt).decPosition();
//			writexml();
//			LoadMenu();
//		}
//
//		return;
//	}
//
//	/**
//	 * Add a new Sub Menu Item
//	 * @param mt Main Menu Title
//	 * @param smt Sub Menu Title
//	 * @param sma Sage menu for sub menu action
//	 * @return return code
//	 */
//public int addSubMenu(String mt, String smt, String sma) {
//	int wPos = 0;
//	Object[] mk = menu.get(mt).getSubMenu();
//	for ( int x = 0; x < mk.length; x++) {
//		if ( menu.get(mt).getSubMenuPosition((String)mk[x]) > wPos)
//			wPos = menu.get(mt).getSubMenuPosition((String)mk[x]);
//	}
//
//	wPos++;
//
//	menu.get(mt).addSubMenu(smt, sma, wPos);
//
//	writexml();
//
//	LoadMenu();
//
//	return 1;
//	}
//
///**
// * Update Sub Menu Title
// * @param mt Main Menu Title
// * @param smt Sub Menu Titile
// * @param nsmt New Sub Menu Title
// * @return return code
// */
//public int updateSubMenuTitle(String mt, String smt, String nsmt) {
//
//	if ( menu.get(mt) != null ) {
//		menu.get(mt).setSubMenuTitle(smt, nsmt);
//		writexml();
//		LoadMenu();
//	}
//
//	return 1;
//}
//
///**
// * Update a Sub Menu Item Action
// * @param mt Main Menu Title
// * @param smt Sub Menu Title
// * @param nsma Sage menu to execute
// * @return return code
// */
//public int updateSubMenuAction(String mt, String smt, String nsma) {
//	if ( menu.get(mt) != null ) {
//		menu.get(mt).setSubMenuAction(smt, nsma);
//		writexml();
//		LoadMenu();
//	}
//	return 1;
//}
//
///**
// * Delete a Sub Menu Item
// * @param mt Main Menu Title
// * @param smt Sub Menu Title
// * @return return code
// */
//public int deleteSubMenu(String mt, String smt) {
//
//	if ( menu.get(mt) != null ) {
//		menu.get(mt).delSubMenu(smt);
//		writexml();
//		LoadMenu();
//	}
//
//   return 1;
//}
//
///**
// * Add new Static variable for a Sub Menu Item
// * @param mt Main Menu Title
// * @param smt Sub Menu Title
// * @param gn Static Variable Name
// * @param gv Static Variable Value
// * @return return code
// */
//public int addSubMenuStatic(String mt, String smt, String gn, String gv) {
//
//	if ( menu.get(mt) != null ) {
//		menu.get(mt).addSubMenuStatic(smt, gn, gv);
//		writexml();
//		LoadMenu();
//	}
//
//		return 1;
//	}
//
///**
// * Update a Static Variable for a Sub Menu Item
// * @param mt Main Menu Title
// * @param smt Sub Menu Title
// * @param gn Static Variable Name
// * @param gv Static Variable Value
// * @return return code
// */
//public int updateSubMenuStatic(String mt, String smt, String gn, String gv) {
//
//	if ( menu.get(mt) != null ) {
//		menu.get(mt).addSubMenuStatic(smt, gn, gv);
//		writexml();
//		LoadMenu();
//	}
//
//	return 1;
//}
//
///**
// * Delete a Sub Menu Static Variable
// * @param mt Main Menu Title
// * @param smt Sub Menu Title
// * @param gn Static Variable Name
// * @return return code
// */
//public int deleteSubMenuStatic(String mt, String smt, String gn) {
//
//	if ( menu.get(mt) != null ) {
//		menu.get(mt).delSubMenuStatic(smt, gn);
//		writexml();
//		LoadMenu();
//	}
//
//	return 1;
//}
//
//public void incSubMenuPosition(String mt, String smt) {
//	if ( menu.get(mt) != null ) {
//		menu.get(mt).incSubMenuPosition(smt);
//		writexml();
//		LoadMenu();
//	}
//
//	return;
//}
//
//public void decSubMenuPosition(String mt, String smt) {
//	if ( menu.get(mt) != null ) {
//		menu.get(mt).decSubMenuPosition(smt);
//		writexml();
//		LoadMenu();
//	}
//
//	return;
//}
//
//@SuppressWarnings("unchecked")
//private void writexml() {
//
//	    dom = db.newDocument();
//
//		Element root = dom.createElement("Menu");
//		dom.appendChild(root);
//
//		Object[] mi = menu.keySet().toArray();
//		Arrays.sort(mi, new Comparator() {
//		    public int compare(Object o1, Object o2) {
//		    	if ( menu.get(o1).getPosition() > menu.get(o2).getPosition())
//		    		return 1;
//		    	else
//		    		return -1;
//		    }
//		  });
//
//		for ( int x = 0; x < mi.length; x++) {
//			menu.get(mi[x]).writeXML(dom);
//		}
//
//		OutputFormat format = new OutputFormat(dom);
//		format.setIndenting(true);
//		format.setLineSeparator("\r\n");
//
//		try {
//			XMLSerializer serializer = new XMLSerializer( new FileOutputStream(new File(MenuPath + java.io.File.separator + "ortus_menu.xml")),format);
//			serializer.serialize(dom);
//		} catch(IOException ie) {
//			ortus.api.DebugLog(new UIContext("SAGETV_PROCESS_LOCAL_UI") , "WriteXML: " + ie );
//		}
//	}
//
//	private void defaultMenu() {
//		    ortus.api.DebugLog(LogLevel.Trace2, "Loading Default Menu" );
//
//		    menu.clear();
//
//			MenuItem mi = new MenuItem("TV", "Ortus Media",1);
//                        mi.setMenuType("tv");
//			mi.addStatic("param_menuname","MainTV");
//			mi.addStatic("param_content","Recordings");
//			mi.addSubMenu("Guide","Ortus Guide",1);
//			mi.addSubMenu("General Recordings","Ortus Media",2);
//			mi.addSubMenuStatic("General Recordings", "param_menuname","Recordings");
//			mi.addSubMenuStatic("General Recordings", "param_content","Recordings");
//			mi.addSubMenuStatic("General Recordings", "param_selected_view_style","TextList");
//			mi.addSubMenuStatic("General Recordings", "param_filter_view","FILE-SYSTEM");
//			mi.addSubMenuStatic("General Recordings", "param_sort_order","AIR-DATE");
//			mi.addSubMenuStatic("General Recordings", "param_reverse_order","true");
//			mi.addSubMenuStatic("General Recordings", "param_filter_dvds","false");
//			mi.addSubMenuStatic("General Recordings", "param_filter_bluray","false");
//			mi.addSubMenuStatic("General Recordings", "param_filter_series","None");
//			mi.addSubMenuStatic("General Recordings", "param_filter_movies","None");
//			mi.addSubMenuStatic("General Recordings", "param_filter_unwatched","None");
//			mi.addSubMenu("TV Series","Ortus Media",3);
//			mi.addSubMenuStatic("TV Series", "param_menuname","Series");
//			mi.addSubMenuStatic("TV Series", "param_content","Series");
//			mi.addSubMenuStatic("TV Series", "param_selected_view_style","TextList");
//			mi.addSubMenuStatic("TV Series", "param_filter_view","FILE-SYSTEM");
//			mi.addSubMenuStatic("TV Series", "param_sort_order","AIR-DATE");
//			mi.addSubMenuStatic("TV Series", "param_reverse_order","true");
//			mi.addSubMenuStatic("TV Series", "param_filter_dvds","false");
//			mi.addSubMenuStatic("TV Series", "param_filter_bluray","false");
//			mi.addSubMenuStatic("TV Series", "param_filter_series","None");
//			mi.addSubMenuStatic("TV Series", "param_filter_movies","None");
//			mi.addSubMenuStatic("TV Series", "param_filter_unwatched","None");
//			mi.addSubMenu("Schedules","OrtusSchedule",4);
//			mi.addSubMenu("Favorites","OrtusFavoritesManager",5);
//			menu.put(mi.getTitle(), mi);
//
//			mi = new MenuItem("Movies", "Ortus Media",2);
//                        mi.setMenuType("movie");
//			mi.addStatic("param_menuname","MainMovies");
//			mi.addStatic("param_content","Movies");
//			mi.addSubMenu("General Movies - List","Ortus Media",1);
//			mi.addSubMenuStatic("General Movies - List", "param_menuname","MoviesList");
//			mi.addSubMenuStatic("General Movies - List", "param_content","Movies");
//			mi.addSubMenuStatic("General Movies - List", "param_selected_view_style","TextList");
//			mi.addSubMenuStatic("General Movies - List", "param_filter_view","FILE-SYSTEM");
//			mi.addSubMenuStatic("General Movies - List", "param_sort_order","ALPHA");
//			mi.addSubMenuStatic("General Movies - List", "param_reverse_order","false");
//			mi.addSubMenuStatic("General Movies - List", "param_filter_dvds","false");
//			mi.addSubMenuStatic("General Movies - List", "param_filter_bluray","false");
//			mi.addSubMenuStatic("General Movies - List", "param_filter_series","None");
//			mi.addSubMenuStatic("General Movies - List", "param_filter_movies","None");
//			mi.addSubMenuStatic("General Movies - List", "param_filter_unwatched","None");
//			mi.addSubMenu("General Movies - Poster View","Ortus Media",2);
//			mi.addSubMenuStatic("General Movies - Poster View", "param_menuname","MoviesPoster");
//			mi.addSubMenuStatic("General Movies - Poster View", "param_content","Movies");
//			mi.addSubMenuStatic("General Movies - Poster View", "param_selected_view_style","PosterFlow");
//			mi.addSubMenuStatic("General Movies - Poster View", "param_filter_view","FILE-SYSTEM");
//			mi.addSubMenuStatic("General Movies - Poster View", "param_sort_order","ALPHA");
//			mi.addSubMenuStatic("General Movies - Poster View", "param_reverse_order","false");
//			mi.addSubMenuStatic("General Movies - Poster View", "param_filter_dvds","false");
//			mi.addSubMenuStatic("General Movies - Poster View", "param_filter_bluray","false");
//			mi.addSubMenuStatic("General Movies - Poster View", "param_filter_series","None");
//			mi.addSubMenuStatic("General Movies - Poster View", "param_filter_movies","None");
//			mi.addSubMenuStatic("General Movies - Poster View", "param_filter_unwatched","None");
//			mi.addSubMenu("General Movies - Wall View","Ortus Media",3);
//			mi.addSubMenuStatic("General Movies - Wall View", "param_menuname","MoviesWall");
//			mi.addSubMenuStatic("General Movies - Wall View", "param_content","Movies");
//			mi.addSubMenuStatic("General Movies - Wall View", "param_selected_view_style","MovieWall");
//			mi.addSubMenuStatic("General Movies - Wall View", "param_filter_view","FILE-SYSTEM");
//			mi.addSubMenuStatic("General Movies - Wall View", "param_sort_order","ALPHA");
//			mi.addSubMenuStatic("General Movies - Wall View", "param_reverse_order","false");
//			mi.addSubMenuStatic("General Movies - Wall View", "param_filter_dvds","false");
//			mi.addSubMenuStatic("General Movies - Wall View", "param_filter_bluray","false");
//			mi.addSubMenuStatic("General Movies - Wall View", "param_filter_series","None");
//			mi.addSubMenuStatic("General Movies - Wall View", "param_filter_movies","None");
//			mi.addSubMenuStatic("General Movies - Wall View", "param_filter_unwatched","None");
//
//			menu.put(mi.getTitle(), mi);
//
//			mi = new MenuItem("Music", "Ortus Music",3);
//                        mi.setMenuType("music");
//			menu.put(mi.getTitle(), mi);
//
//			mi = new MenuItem("Pictures", "Ortus Pictures",4);
//                        mi.setMenuType("picture");
//			menu.put(mi.getTitle(), mi);
//
//			mi = new MenuItem("Weather", "Ortus Weather - 4 day Forecast",5);
//                        mi.setMenuType("weather");
//			menu.put(mi.getTitle(), mi);
//
//			mi = new MenuItem("Setup", "Ortus Setup",6);
//                        mi.setMenuType("setup");
//        		menu.put(mi.getTitle(), mi);
//
//			writexml();
//		}
//
//	}
///**
// * MenuItem XML Menu Object for storing information about a Menu
// * @author jphipps
// *
// */
//class MenuItem extends ortus.ortusvars {
//	private HashMap<String,SubMenuItem> SubMenuItems = new HashMap<String,SubMenuItem>();
//	private HashMap<String,String> MenuStatics = new HashMap<String,String>();
//	private String Title;
//	private String Action;
//	private int Position=0;
//	private int Xpos=0;
//	private int Ypos=0;
//	private int maxSubMenuPosition = 0;
//        private int MenuType = 0;
//
//	public MenuItem() {
//	}
//
//	/**
//	 *  Constructor for a new Menu Item Object built from the xmlfile
//	 * @param el xml element for a Menu Object
//	 * @return New Menu Object
//	 */
//	public MenuItem(Element el) {
//		Title = el.getAttribute("Title");
//		Position = Integer.parseInt(el.getAttribute("Position"));
//		Xpos = Integer.parseInt(el.getAttribute("Xpos"));
//		Ypos = Integer.parseInt(el.getAttribute("Ypos"));
//		Action = el.getAttribute("Action");
//                MenuType = Integer.parseInt(el.getAttribute("MenuType"));
//		NodeList nl = el.getElementsByTagName("MenuStatic");
//
//		if ( nl != null && nl.getLength() > 0) {
//			 for ( int i = 0; i< nl.getLength();i++){
//                            Element gel = (Element)nl.item(i);
//                            MenuStatics.put(gel.getAttribute("Title"), getTextValue(gel,"Value"));
//                            ortus.api.DebugLog(LogLevel.Trace, "Ortus: MenuItem: Staic: Title: " + gel.getAttribute("Title") + " value: " + getTextValue(gel,"Value") );
//			 }
//		 }
//
//		NodeList smi = el.getElementsByTagName("SubMenuItem");
//
//		if ( smi != null && smi.getLength() > 0) {
//			 for ( int i = 0; i< smi.getLength();i++){
//				 Element gel = (Element)smi.item(i);
//                 SubMenuItems.put(gel.getAttribute("Title"), new SubMenuItem(gel));
//                 if ( SubMenuItems.get(gel.getAttribute("Title")).getPosition() > maxSubMenuPosition )
//                	 maxSubMenuPosition = SubMenuItems.get(gel.getAttribute("Title")).getPosition();
//
//			 }
//		 }
//
//                ortus.api.DebugLog(LogLevel.Trace2, "Ortus: MenuItem: Title: " + Title );
//                ortus.api.DebugLog(LogLevel.Trace2, "Ortus: MenuItem: Action: " + Action );
//                ortus.api.DebugLog(LogLevel.Trace2, "Ortus: MenuItem: Title: " + Title );
//
//
//	//	 Global.ortus.api.DebugLog(new UIContext("SAGETV_PROCESS_LOCAL_UI"),  "new MenuItem - SubMenus: " + SubMenuItems );
//	}
//
//	/**
//	 * Constructor for a new Menu Ojbect
//	 * @param mt Menu Title
//	 * @param  act Action
//	 * @return New Menu Object
//	 */
//	public MenuItem(String mt, String act) {
//		 Title = mt;
//		 Action = act;
//		 ortus.api.DebugLog(LogLevel.Trace2, "Ortus: MenuItem new MenuItem - Titile: " + Title );
//	}
//
//	/**
//	 * Constructor for a new Menu Ojbect
//	 * @param mt Menu Title
//	 * @param  act Action
//	 * @return New Menu Object
//	 */
//	public MenuItem(String mt, String act, int pos) {
//		 Title = mt;
//		 Action = act;
//		 Position = pos;
//		 ortus.api.DebugLog(LogLevel.Trace2, "Ortus: MenuItem: new MenuItem - Titile: " + Title );
//	}
//
//	private static String getTextValue( Element e, String tagName) {
//		 String textVal = null;
//
//		 NodeList nl = e.getElementsByTagName(tagName);
//		 if( nl != null && nl.getLength() > 0) {
//			 Element e1 = (Element)nl.item(0);
//			 textVal = e1.getFirstChild().getNodeValue();
//		 }
//		 return textVal;
//	 }
//
//	public void setMenuType(String menutype) {
//            String mt = menutype.toLowerCase();
//            if ( mt.equals("general"))
//                MenuType = 0;
//            else if ( mt.equals("tv"))
//                MenuType = 1;
//            else if ( mt.equals("movie"))
//                MenuType = 2;
//            else if ( mt.equals("music"))
//                MenuType = 3;
//            else if ( mt.equals("weather"))
//                MenuType = 4;
//            else if ( mt.equals("pictures"))
//                MenuType = 5;
//            else if ( mt.equals("setup"))
//                MenuType = 9;
//
//            return;
//        }
//
//         public String getMenuType() {
//             if ( MenuType == 0)
//                 return "general";
//             else if ( MenuType == 1)
//                 return "tv";
//             else if ( MenuType == 2)
//                 return "movie";
//             else if ( MenuType == 3)
//                 return "music";
//             else if ( MenuType == 4)
//                 return "weather";
//             else if ( MenuType == 5)
//                 return "picture";
//             else if ( MenuType == 9)
//                 return "setup";
//             return "general";
//         }
//
//	/**
//	 *  Return the Title of a Menu Object
//	 * @return Menu Title for the Menu Object
//	 */
//	public String getTitle() {
//		return Title;
//	}
//
//	/**
//	 *  Set the Menu title for a Menu Object
//	 * @param mt New Menu title
//	 */
//	public void setTitle(String mt) {
//		Title = mt;
//		return;
//	}
//
//	public void setAction(String menu) {
//		Action = menu;
//	}
//
//	public String getAction() {
//		return Action;
//	}
//
//	public void setPosition(int x) {
//		Position = x;
//	}
//
//	public void incPosition() {
//		Position++;
//	}
//
//	public void decPosition() {
//		if ( Position > 1 )
//			Position--;
//	}
//
//	public int getPosition() {
//		return Position;
//	}
//
//	public void setXpos(int x) {
//		Xpos = x;
//	}
//
//	public int getXpos() {
//		return Xpos;
//	}
//
//	public void setYpos(int x) {
//		Ypos = x;
//	}
//
//	public int getYpos() {
//		return Ypos;
//	}
//
//	public void addStatic(String var, String val) {
//		MenuStatics.put(var,val);
//	}
//
//	public void delStatic(String var) {
//		MenuStatics.remove(var);
//	}
//
//	public HashMap<String,String> getStatics() {
//		return MenuStatics;
//	}
//
//	public void addSubMenu(String menu, String action) {
//		SubMenuItems.put(menu,new SubMenuItem(menu,action));
//	}
//
//	public void addSubMenu(String menu, String action,int pos) {
//		SubMenuItems.put(menu,new SubMenuItem(menu,action,pos));
//	}
//
//	@SuppressWarnings("unchecked")
//	public void delSubMenu(String menu) {
//
//		Object[] mi = SubMenuItems.keySet().toArray();
//		Arrays.sort(mi, new Comparator() {
//		    public int compare(Object o1, Object o2) {
//		    	if ( SubMenuItems.get(o1).getPosition() > SubMenuItems.get(o2).getPosition())
//		    		return 1;
//		    	else
//		    		return -1;
//		    }
//		  });
//
//		int curPosition = SubMenuItems.get(menu).getPosition();
//
//		for( int x = 0; x < mi.length; x++ ) {
//			if( SubMenuItems.get(mi[x]).getPosition() > curPosition ) {
//				SubMenuItems.get(mi[x]).decPosition();
//			}
//		}
//		SubMenuItems.remove(menu);
//		return;
//	}
//
//	@SuppressWarnings("unchecked")
//	public Object[] getSubMenu() {
//		Object[] mi = null;
//
//		try {
//		mi = SubMenuItems.keySet().toArray();
//		Arrays.sort(mi, new Comparator() {
//		    public int compare(Object o1, Object o2) {
//		    	if ( SubMenuItems.get(o1).getPosition() > SubMenuItems.get(o2).getPosition())
//		    		return 1;
//		    	else
//		    		return -1;
//		    }
//		  });
//
//		} catch (Exception e) {
//			 ortus.api.DebugLog(new UIContext("SAGETV_PROCESS_LOCAL_UI"), "getSubMenuExcpetion: " + e );
//		}
//
//		return mi;
//	}
//
//	public void setSubMenuTitle(String sm, String title) {
//		SubMenuItems.get(sm).setTitle(title);
//		SubMenuItems.put(title, SubMenuItems.get(sm));
//		SubMenuItems.remove(sm);
//		return;
//	}
//
//	public void setSubMenuAction( String sm, String sma) {
//		SubMenuItems.get(sm).setAction(sma);
//		return;
//	}
//
//	public String getSubMenuAcion(String sm) {
//		return (String)SubMenuItems.get(sm).getAction();
//	}
//
//	public int getSubMenuPosition(String sm) {
//		return (int)SubMenuItems.get(sm).getPosition();
//	}
//
//	public HashMap<String,String>getSubMenuStatics(String sm) {
//		return SubMenuItems.get(sm).getStatics();
//	}
//
//	public void addSubMenuStatic(String sm, String var, String val) {
//		SubMenuItems.get(sm).addStatic(var, val);
//		return;
//	}
//
//	public void delSubMenuStatic(String sm, String var) {
//		SubMenuItems.get(sm).delStatic(var);
//		return;
//	}
//
//	@SuppressWarnings("unchecked")
//	public void incSubMenuPosition(String sm) {
//
//		if ( SubMenuItems.get(sm).getPosition() == maxSubMenuPosition ) {
//			return;
//		}
//
//		Object[] mi = SubMenuItems.keySet().toArray();
//		Arrays.sort(mi, new Comparator() {
//		    public int compare(Object o1, Object o2) {
//		    	if ( SubMenuItems.get(o1).getPosition() > SubMenuItems.get(o2).getPosition())
//		    		return 1;
//		    	else
//		    		return -1;
//		    }
//		  });
//
//		int curPosition = SubMenuItems.get(sm).getPosition();
//		curPosition++;
//		for( int x = 0; x < mi.length; x++ ) {
//			if( SubMenuItems.get(mi[x]).getPosition() == curPosition ) {
//				SubMenuItems.get(mi[x]).decPosition();
//			}
//		}
//		SubMenuItems.get(sm).incPosition();
//		return;
//	}
//
//	@SuppressWarnings("unchecked")
//	public void decSubMenuPosition(String sm) {
//		if ( SubMenuItems.get(sm).getPosition() == 1 ) {
//			return;
//		}
//		Object[] mi = SubMenuItems.keySet().toArray();
//		Arrays.sort(mi, new Comparator() {
//		    public int compare(Object o1, Object o2) {
//		    	if ( SubMenuItems.get(o1).getPosition() > SubMenuItems.get(o2).getPosition())
//		    		return 1;
//		    	else
//		    		return -1;
//		    }
//		  });
//
//		int curPosition = SubMenuItems.get(sm).getPosition();
//		curPosition--;
//		for( int x = 0; x < mi.length; x++ ) {
//			if( SubMenuItems.get(mi[x]).getPosition() == curPosition ) {
//				SubMenuItems.get(mi[x]).incPosition();
//			}
//		}
//		SubMenuItems.get(sm).decPosition();
//		return;
//	}
//
//	@SuppressWarnings("unchecked")
//	public int writeXML(Document dom) {
//		ortus.api.DebugLog(LogLevel.Trace, "Ortus: WriteXML: MenuItem - Writing XML" );
//		Element root= dom.getDocumentElement();
//		Element el = dom.createElement("MenuItem");
//
//		el.setAttribute("Title", Title);
//		el.setAttribute("Action", Action);
//                el.setAttribute("MenuType", Integer.toString(MenuType));
//		el.setAttribute("Position", Integer.toString(Position));
//		el.setAttribute("Xpos", Integer.toString(Xpos));
//		el.setAttribute("Ypos", Integer.toString(Xpos));
//		root.appendChild(el);
//
//		Object[] Statics = MenuStatics.keySet().toArray();
//		for ( int x = 0; x < Statics.length;x++) {
//			createNode(dom,el,"MenuStatic", (String)Statics[x], MenuStatics.get(Statics[x]));
//		}
//
//		Object[] smi = SubMenuItems.keySet().toArray();
//		Arrays.sort(smi, new Comparator() {
//		    public int compare(Object o1, Object o2) {
//		    	if ( SubMenuItems.get(o1).getPosition() > SubMenuItems.get(o2).getPosition())
//		    		return 1;
//		    	else
//		    		return -1;
//		    }
//		  });
//
//		for ( int x = 0; x < smi.length;x++) {
//			SubMenuItems.get(smi[x]).writeXML(dom,el);
//		}
//
//		return 0;
//	}
//
//	private Element createNode(Document dom, Element parent, String en, String name, String action) {
//		Element MenuItemElement = dom.createElement(en);
//		MenuItemElement.setAttribute("Title", name);
//		parent.appendChild(MenuItemElement);
//		Element MenuActionElement = dom.createElement("Value");
//		Text nameText = dom.createTextNode(action);
//		MenuActionElement.appendChild(nameText);
//		MenuItemElement.appendChild(MenuActionElement);
//
//		return MenuItemElement;
//	}
//}
//
//class SubMenuItem extends ortus.ortusvars {
//        /*
//         * MediaTypes:
//         *      0 - General
//         *      1 - TV
//         *      2 - Movies
//         *      3 - Music
//         *      4 - Weather
//         *      5 - Picutres
//         *      9 - Setup
//       */
//
//	private String Title;
//	private String Action;
//
//	private int Position = 0;
//	private HashMap<String,String> MenuStatics = new HashMap<String,String>();
//
//	SubMenuItem() {
//	}
//
//	/**
//	 * Return Sage menu to execute for a sub menu item
//	 * @param m Menu Name
//	 * @param s Sub Menu Name
//	 * @return Sage menu to execute
//	 */
//	SubMenuItem(Element el) {
//
//	    Title = el.getAttribute("Title");
//	    Action = el.getAttribute("Action");
//
//	    Position = Integer.parseInt(el.getAttribute("Position"));
//		NodeList nl = el.getElementsByTagName("SubMenuStatic");
//
//		if ( nl != null && nl.getLength() > 0) {
//			 for ( int i = 0; i< nl.getLength();i++){
//				 Element gel = (Element)nl.item(i);
//                 MenuStatics.put(gel.getAttribute("Title"), getTextValue(gel,"Value"));
//			 }
//		 }
//
//	//	 Global.ortus.api.DebugLog(new UIContext("SAGETV_PROCESS_LOCAL_UI"), "new SubMenuItem: Title: " + Title + " Action: " + Action );
//	}
//
//	SubMenuItem(String title, String action) {
//		Title = title;
//		Action = action;
//	}
//
//	SubMenuItem(String title, String action, int pos) {
//		Title = title;
//		Action = action;
//		Position = pos;
//	}
//
//	private static String getTextValue( Element e, String tagName) {
//		 String textVal = null;
//
//		 NodeList nl = e.getElementsByTagName(tagName);
//		 if( nl != null && nl.getLength() > 0) {
//			 Element e1 = (Element)nl.item(0);
//			 textVal = e1.getFirstChild().getNodeValue();
//		 }
//		 return textVal;
//	 }
//
//
//	public void setTitle(String title) {
//		Title = title;
//	}
//
//	public String getTitle() {
//		return Title;
//	}
//
//	public void setAction(String action) {
//		Action = action;
//	}
//
//	public String getAction() {
//		return Action;
//	}
//
//	public void setPosition(int x) {
//		Position = x;
//	}
//
//	public void incPosition() {
//		Position++;
//	}
//
//	public void decPosition() {
//		Position--;
//	}
//
//	public int getPosition() {
//		return Position;
//	}
//
//	public void addStatic(String var, String val) {
//		MenuStatics.put(var,val);
//	}
//
//	public HashMap<String,String> getStatics() {
//		return MenuStatics;
//	}
//
//	public void delStatic(String var) {
//		MenuStatics.remove(var);
//	}
//
//	/**
//	 *  Write the submenu xml document
//	 * @param dom Documnet Object for the xml file
//	 * @return return code ( always 0 )
//	 */
//	public int writeXML(Document dom, Element pel) {
//		ortus.api.DebugLog(LogLevel.Trace, "Ortus: SubMenuItem: Writing XML" );
//
//		Element el = dom.createElement("SubMenuItem");
//
//		el.setAttribute("Title", Title);
//		el.setAttribute("Action", Action);
//		el.setAttribute("Position", Integer.toString(Position));
//		pel.appendChild(el);
//
//		Object[] Statics = MenuStatics.keySet().toArray();
//		for ( int x = 0; x < Statics.length;x++) {
//			createNode(dom,el,"SubMenuStatic", (String)Statics[x], MenuStatics.get(Statics[x]));
//		}
//
//		return 0;
//	}
//
//	private Element createNode(Document dom, Element parent, String en, String name, String action) {
//		Element MenuItemElement = dom.createElement(en);
//		MenuItemElement.setAttribute("Title", name);
//		parent.appendChild(MenuItemElement);
//		Element MenuActionElement = dom.createElement("Value");
//		Text nameText = dom.createTextNode(action);
//		MenuActionElement.appendChild(nameText);
//		MenuItemElement.appendChild(MenuActionElement);
//
//		return MenuItemElement;
//	}
}


