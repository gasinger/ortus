package ortus.ui.menu;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import ortus.onlinescrapper.tools.XMLHelper;


/**
 * Ortus Menu Handling Class
 * @author jphipps
 *
 */
public class menuEngine extends ortus.vars  {

	private HashMap<String,MenuItem> menu = new HashMap<String,MenuItem>();
	private int maxMenuPosition = 0;
	String MenuPath = null;
        String MenuFile = null;
	
	/**
	 * Contructor builds default menu xmlfile or load existing menu xmlfile
	 * @param Path for configuration xml file
	 */
//	public menuEngine(String MenuPath) {
//                ortus.api.DebugLog(LogLevel.Debug,"Menu: Loading Class");
//                this.MenuPath = MenuPath;
//   		InitMenu(MenuPath);
//                ortus.api.DebugLog(LogLevel.Debug,"Menu: Loading Completed");
//	}

        public menuEngine(int userid) {
                ortus.api.DebugLog(LogLevel.Debug,"Menu: Loading Class");
//                this.MenuPath = MenuPath;
   		InitMenu(userid);
                ortus.api.DebugLog(LogLevel.Debug,"Menu: Loading Completed");
	}

	public void InitMenu(int userid) {
//                this.MenuPath = MenuPath;
//                this.MenuFile = MenuPath + java.io.File.separator + "ortus_menu.xml";

                ortus.api.DebugLogTrace("InitMenu: Loading new menu for user: " + userid);
//		File xmlfile = null;
//
//		File cp = new File(MenuPath);
//		if ( ! cp.exists())
//			cp.mkdirs();
//
//		try {
//                     xmlfile = new File(MenuFile);
//
//                     if ( ! xmlfile.exists() ) {
//                             defaultMenu();
//                     }
//                } catch(Exception e) {
//                        e.printStackTrace();
//                }
		loadMenuDB(userid);
	}

	public void Shutdown() {
		return;
	}

        public void loadMenuDB() {
            loadMenuDB(ortus.api.GetCurrentUser());
        }
        
        public void loadMenuDB(int userid) {
            Connection conn = ortus.api.GetConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            String SQL = "select menu from sage.menu where userid = ?";
            menu.clear();

            try {
                ps = conn.prepareStatement(SQL);
                ps.setInt(1, userid);
                rs = ps.executeQuery();

                if ( rs.next())
                    menu = (HashMap)rs.getObject(1);
                else
                    defaultMenu(userid);
            } catch (Exception e) {
                ortus.api.DebugLog(LogLevel.Error,"writeDB: Exception:", e);
            } finally {
                if ( rs != null) try { rs.close(); } catch(Exception ex) {}
                if ( ps != null) try { ps.close(); } catch(Exception ex) {}
                if ( conn != null) try { conn.close(); } catch(Exception ex) {}
            }
        }
        
	private void LoadMenuXML(String path) {
                String menuType = null;
                String menuPath = null;
                String[] x = path.split(":");
                if ( x.length != 2)
                    return;

                menuType = x[0];
                menuPath = x[1];

                ortus.api.DebugLog(LogLevel.Trace, "LoadMenuXML: Starting from: " + menuType + " File: " + menuPath);

                menu.clear();
                
                try {
                    XMLEventReader xmlReader = null;

                    if ( menuType.equalsIgnoreCase("jar"))
                        xmlReader = XMLHelper.getEventReaderJar(menuPath);
                    else
                        xmlReader = XMLHelper.getEventReaderFile(menuPath);
                    MenuItem mi = null;

                    while((mi = parseNextMenuItem(xmlReader)) != null) {
                        ortus.api.DebugLog(LogLevel.Trace, "LoadMenu: Loading " + mi.getTitle());
                       menu.put(mi.getTitle(), mi);
                       if ( mi.getPosition() > maxMenuPosition )
                           maxMenuPosition = mi.getPosition();

                    }

                } catch (Exception e) {
                    ortus.api.DebugLog(LogLevel.Error, "LoadMenu: Exception",e);
                    return;
                }

                    ortus.api.DebugLog(LogLevel.Info, "LoadMenu: Completed loading " + menu.size() + " items");
                return;
        }

    public static MenuItem parseNextMenuItem(XMLEventReader xmlReader) throws XMLStreamException {
	MenuItem mi = null;
        SubMenuItem smi = null;
        boolean inSubMenuItem = false;

  	while (xmlReader.hasNext()) {

	    XMLEvent event = xmlReader.nextEvent();

//            ortus.api.DebugLog(LogLevel.Trace, "event: " + event.toString());
	    if (event.isStartElement()) {

		StartElement se = event.asStartElement();
		String tag = se.getName().getLocalPart();
//		ortus.api.DebugLog(LogLevel.Trace2,"parsing tag: " + tag);
		if (tag.equalsIgnoreCase("menuitem")) {
		    mi = new MenuItem();
                    Iterator<Attribute> attributes = se.getAttributes();
                    while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().toString().equalsIgnoreCase("action"))
			   mi.setAction(attribute.getValue());
      			if (attribute.getName().toString().equalsIgnoreCase("menutype"))
			   mi.setMenuType(attribute.getValue());
			if (attribute.getName().toString().equalsIgnoreCase("position"))
			   mi.setPosition(Integer.parseInt(attribute.getValue()));
			if (attribute.getName().toString().equalsIgnoreCase("title"))
			   mi.setTitle(attribute.getValue());
			if (attribute.getName().toString().equalsIgnoreCase("xpos"))
			   mi.setXpos(Integer.parseInt(attribute.getValue()));
			if (attribute.getName().toString().equalsIgnoreCase("ypos"))
			   mi.setYpos(Integer.parseInt(attribute.getValue()));
                    }
 		} else if (tag.equalsIgnoreCase("static")) {
                    MenuItemElement mie = new MenuItemElement();
                    mie.setMenuitemtype(MenuItemElementType.Static);
                    Iterator<Attribute> attributes = se.getAttributes();
                    while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().toString().equalsIgnoreCase("title"))
			   mie.setTitle(attribute.getValue());
			if (attribute.getName().toString().equalsIgnoreCase("name"))
			   mie.setName(attribute.getValue());
			if (attribute.getName().toString().equalsIgnoreCase("value"))
			   mie.setValue(attribute.getValue());
                    }
                    if ( inSubMenuItem ) {
                        smi.addMenuItemStatic(mie.getTitle(), mie.getName(), mie.getValue());
                    } else {
                        mi.addMenuItemStatic(mie.getTitle(), mie.getName(), mie.getValue());
                    }
 		} else if (tag.equalsIgnoreCase("global")) {
                    MenuItemElement mie = new MenuItemElement();
                    mie.setMenuitemtype(MenuItemElementType.Global);
                    Iterator<Attribute> attributes = se.getAttributes();
                    while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().toString().equalsIgnoreCase("title"))
			   mie.setTitle(attribute.getValue());
			if (attribute.getName().toString().equalsIgnoreCase("name"))
			   mie.setName(attribute.getValue());
			if (attribute.getName().toString().equalsIgnoreCase("value"))
			   mie.setValue(attribute.getValue());
                    }
                    if ( inSubMenuItem ) {
                        smi.addMenuItemGlobal(mie.getTitle(), mie.getName(), mie.getValue());
                    } else {
                        mi.addMenuItemGlobal(mie.getTitle(), mie.getName(), mie.getValue());
                    }
 		} else if (tag.equalsIgnoreCase("image")) {
                    MenuItemElement mie = new MenuItemElement();
                    mie.setMenuitemtype(MenuItemElementType.Image);
                    Iterator<Attribute> attributes = se.getAttributes();
                    while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().toString().equalsIgnoreCase("title"))
			   mie.setTitle(attribute.getValue());
			if (attribute.getName().toString().equalsIgnoreCase("name"))
			   mie.setName(attribute.getValue());
			if (attribute.getName().toString().equalsIgnoreCase("value"))
			   mie.setValue(attribute.getValue());
                    }
                    if ( inSubMenuItem ) {
                        smi.addMenuItemImage(mie.getTitle(), mie.getName(), mie.getValue());
                    } else {
                        mi.addMenuItemImage(mie.getTitle(), mie.getName(), mie.getValue());
                    }
 		} else if (tag.equalsIgnoreCase("property")) {
                    MenuItemElement mie = new MenuItemElement();
                    mie.setMenuitemtype(MenuItemElementType.Property);
                    Iterator<Attribute> attributes = se.getAttributes();
                    while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().toString().equalsIgnoreCase("title"))
			   mie.setTitle(attribute.getValue());
			if (attribute.getName().toString().equalsIgnoreCase("name"))
			   mie.setName(attribute.getValue());
			if (attribute.getName().toString().equalsIgnoreCase("value"))
			   mie.setValue(attribute.getValue());
                    }
                    if ( inSubMenuItem ) {
                        smi.addMenuItemProperty(mie.getTitle(), mie.getName(), mie.getValue());
                    } else {
                        mi.addMenuItemProperty(mie.getTitle(), mie.getName(), mie.getValue());
                    }
 		} else if (tag.equalsIgnoreCase("sagecommand")) {
                    MenuItemElement mie = new MenuItemElement();
                    mie.setMenuitemtype(MenuItemElementType.SageCommand);
                    Iterator<Attribute> attributes = se.getAttributes();
                    while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().toString().equalsIgnoreCase("title"))
			   mie.setTitle(attribute.getValue());
			if (attribute.getName().toString().equalsIgnoreCase("name"))
			   mie.setName(attribute.getValue());
			if (attribute.getName().toString().equalsIgnoreCase("value"))
			   mie.setValue(attribute.getValue());
                    }
                    if ( inSubMenuItem ) {
                        smi.addMenuItemSageCommand(mie.getTitle(), mie.getName(), mie.getValue());
                    } else {
                        mi.addMenuItemSageCommand(mie.getTitle(), mie.getName(), mie.getValue());
                    }
		} else if (tag.equalsIgnoreCase("submenuitem")) {
                    inSubMenuItem = true;
                    smi = new SubMenuItem();
                    Iterator<Attribute> attributes = se.getAttributes();
                    while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().toString().equalsIgnoreCase("title"))
                            smi.setTitle(attribute.getValue());
			if (attribute.getName().toString().equalsIgnoreCase("action"))
                            smi.setAction(attribute.getValue());
			if (attribute.getName().toString().equalsIgnoreCase("position"))
                            smi.setPosition(GetInt(attribute.getValue()));
                    }
                }

	    } else if (event.isEndElement()) {
                if ( event.toString().equalsIgnoreCase("</submenuitem>")) {
                    inSubMenuItem = false;
                    mi.addSubMenu(smi);
                }
		if (event.toString().equalsIgnoreCase("</menuitem>")) {
		    break;
		}
	    }
	}
	return mi;
    }

    public static int GetInt(String value) {
        int retval = 0;
        try {
            retval = Integer.parseInt(value);
        } catch ( Exception e) {}

        return retval;
    }

	/**
	 * Return a list of Main Menu Items
	 * @return Vector of menu items
	 */
	@SuppressWarnings("unchecked")
	public Object[] getMenu() {
		
		Object[] mi = menu.keySet().toArray();
		Arrays.sort(mi, new Comparator() {
		    public int compare(Object o1, Object o2) {
		    	if ( menu.get(o1).getPosition() > menu.get(o2).getPosition())
		    		return 1;
		    	else
		    		return -1;
		    }
		  });
		
		return mi;
	}
	/**
	 * Return MenuType
	 * @param m Menu Name
	 * @return Menu Type
	 */
	public String GetMenuType(String m) {
			return menu.get(m).getMenuType();
	}

	/**
	 * Set MenuType
	 * @param m Menu Name
         * @param menutype Menu Type
	 * @return Menu Type
	 */
	public void SetMenuType(String m, String mtype) {
        	menu.get(m).setMenuType(mtype);
                return;
	}
	
	/**
	 * Return Sage menu to execute for a menu item
	 * @param m Menu Name
	 * @return Sage Menu to execute
	 */
	public String GetMenuAction(String m) {
                ortus.api.DebugLog(LogLevel.Trace,"Ortus: GetMenuAction for " + m);
			return menu.get(m).getAction();
	}

	/**
	 * Return Sage menu to execute for a sub menu item
	 * @param m Menu Name
	 * @param s Sub Menu Name
	 * @return Sage menu to execute
	 */
	public String getSubMenuAction(String m, String s) {
		return menu.get(m).getSubMenuAcion(s);
	}
	
	/**
	 * Return a list of Static variables to set upon selection of a sub menu item
	 * @param m Menu Name
	 * @param s Sub Menu Name
	 * @return Sage menu to execute
	 */
        public void addMenuItemStatic(String m,String Title, String var, String val) {
            menu.get(m).addMenuItemStatic(Title,var,val);
            writeDB();
            loadMenuDB();

        }
        public void addMenuItemGlobal(String m,String Title, String var, String val) {
            menu.get(m).addMenuItemGlobal(Title,var,val);
            writeDB();
            loadMenuDB();
        }
        public void addMenuItemImage(String m,String Title, String var, String val) {
            menu.get(m).addMenuItemImage(Title, var, val);
            writeDB();
            loadMenuDB();
        }
        public void addMenuItemSageCommand(String m,String Title, String var, String val) {
            menu.get(m).addMenuItemSageCommand(Title, var, val);
            writeDB();
            loadMenuDB();
        }
        public void addMenuItemProperty(String m,String Title, String var, String val) {
            menu.get(m).addMenuItemProperty(Title, var, val);
            writeDB();
            loadMenuDB();
        }

        public void delMenuItemStatic(String m,String var) {
            menu.get(m).delMenuItemStatic(var);
            writeDB();
            loadMenuDB();
        }
        public void delMenuItemGlobal(String m,String var) {
            menu.get(m).delMenuItemGlobal(var);
            writeDB();
            loadMenuDB();
        }
        public void delMenuItemImage(String m,String var) {
            menu.get(m).delMenuItemImage(var);
            writeDB();
            loadMenuDB();
        }
        public void delMenuItemSageCommand(String m,String var) {
            menu.get(m).delMenuItemSageCommand(var);
            writeDB();
            loadMenuDB();
        }
        public void delMenuItemProperty(String m,String var) {
            menu.get(m).delMenuItemProperty(var);
            writeDB();
            loadMenuDB();
        }

        public Object getMenuItemStatic(String m,String var) {
           ortus.api.DebugLogTrace("getMenuItemStatic: Menu: " + m + " Name: "+ var);
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("Menu not found");
                return null;
            } else {
                return menu.get(m).getMenuItemStatic(var);
            }
        }
        public Object getMenuItemGlobal(String m,String var) {
            ortus.api.DebugLogTrace("getMenuItemGlobal: Menu: " + m + " Name: "+ var);
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("Menu not found");
                return null;
            } else {
                return menu.get(m).getMenuItemGlobal(var);
            }
        }
        public Object getMenuItemImage(String m, String var) {
           ortus.api.DebugLogTrace("getMenuItemImage: Menu: " + m + " Name: "+ var);
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("Menu not found");
                return null;
            } else {
                return menu.get(m).getMenuItemImage(var);
            }
        }
        public Object getMenuItemSageCommand(String m,String var) {
           ortus.api.DebugLogTrace("getMenuItemSageCommand: Menu: " + m + " Name: "+ var);
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("Menu not found");
                return null;
            } else {
                return menu.get(m).getMenuItemSageCommand(var);
            }
        }
        public Object getMenuItemProperty(String m,String var) {
           ortus.api.DebugLogTrace("getMenuItemProperty: Menu: " + m + " Name: "+ var);
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("Menu not found");
                return null;
            } else {
                return menu.get(m).getMenuItemProperty(var);
            }
        }

        public Object[] getMenuItemStatic(String m) {
            ortus.api.DebugLogTrace("getMenuItemStatic: Menu: " + m);
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("Menu not found");
                return null;
            } else { 
                return menu.get(m).getMenuItemStatic();
            }
        }
        public Object[] getMenuItemGlobal(String m) {
            ortus.api.DebugLogTrace("getMenuItemGlobal: Menu: " + m);
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("Menu not found");
                return null;
            } else {
                return menu.get(m).getMenuItemGlobal();
            }

        }
        public Object[] getMenuItemImage(String m) {
            ortus.api.DebugLogTrace("getMenuItemImage: Menu: " + m);
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("Menu not found");
                return null;
            } else {
                return menu.get(m).getMenuItemImage();
            }

        }
        public Object[] getMenuItemSageCommand(String m) {
            ortus.api.DebugLogTrace("getMenuItemSageCommand: Menu: " + m);
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("Menu not found");
                return null;
            } else {
                return menu.get(m).getMenuItemSageCommand();
            }
        }
        public Object[] getMenuItemProperty(String m) {
            ortus.api.DebugLogTrace("getMenuItemProperty: Menu: " + m);
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("Menu not found");
                return null;
            } else {
                return menu.get(m).getMenuItemProperty();
            }
        }

        public HashMap<String,HashMap<String,MenuItemElement>> getMenuItemElements(String m) {
            return menu.get(m).getMenuItemElements();
        }

        public void dumpMenuItems() {
            for ( Object y : menu.keySet().toArray()) {
                ortus.api.DebugLogTrace("Dumping Menu: " + y);
                HashMap<String, HashMap<String,MenuItemElement>> me = menu.get((String)y).getMenuItemElements();
                for ( Object ekeys : me.keySet().toArray()) {
                    ortus.api.DebugLogTrace("Dumping MenuItem Type: " + ekeys);
                    HashMap<String,MenuItemElement> mie = me.get(ekeys);
                    for ( Object skeys : mie.keySet().toArray()) {
                        MenuItemElement x = mie.get(skeys);
                        ortus.api.DebugLogTrace("skey: " + skeys + " Title: " + x.getTitle() + " Mame: " + x.getName() + " Value: " + x.getValue());
                    }
                }
            }
        }
	/**
	 * Return a sub menu for a main menu item
	 * @param m Main Menu
	 * @return Sub menu for a main menu
	 */
	public Object[] getSubMenu(String m) {
		return menu.get(m).getSubMenuSorted();
	}

	/**
	 * Delete a main menu item
	 * @param m Main Menu Title
	 * @return return code
	 */
	@SuppressWarnings("unchecked")
	public int deleteMenu(String m) {
		
		Object[] mi = menu.keySet().toArray();
		Arrays.sort(mi, new Comparator() {
		    public int compare(Object o1, Object o2) {
		    	if ( menu.get(o1).getPosition() > menu.get(o2).getPosition())
		    		return 1;
		    	else
		    		return -1;
		    }
		  });
		
		int curPosition = menu.get(m).getPosition();
		for( int x = 0; x < mi.length; x++ ) {
			if( menu.get(mi[x]).getPosition() > curPosition ) {
				menu.get(mi[x]).decPosition();
			}
		}
		
		menu.remove(m);
                maxMenuPosition--;
		
		writeDB();
		
		loadMenuDB();
		
		return 0;		
	}
	
	/**
	 * Add a new Main Menu
	 * @param mt Main Menu Title
	 * @param ma Sage menu to execute
	 */
	public void addMenu(String mt, String ma) {
		int wPos = 0;
		Object[] mk = menu.keySet().toArray();
		for ( int x = 0; x < mk.length; x++) {
			if ( menu.get(mk[x]).getPosition() > wPos)
				wPos = menu.get(mk[x]).getPosition();
		}
		
		wPos++;
		
		menu.put(mt, new MenuItem(mt,ma,wPos));
		
		writeDB();
		
		loadMenuDB();
		
		return;
	}
	
	/**
	 * Update a Main Menu title
	 * @param mt Main Menu Title
	 * @param nmt New Main Menu Title
	 * @return return code
	 */
	public void updateMenuTitle(String mt, String nmt) {
		
		if( menu.get(mt)!= null ) {
			menu.get(mt).setTitle(nmt);
                        menu.put(nmt, menu.get(mt));
                        menu.remove(mt);
			writeDB();
			loadMenuDB();
		}
		
		return;
	}
	
	/**
	 * Update a Main Menu Action
	 * @param mt Main Menu Title
	 * @param nma Sage menu name
	 * @return return code
	 */
	public int updateMenuAction(String mt, String nma) {
		
		if( menu.get(mt)!= null ) {
			menu.get(mt).setAction(nma);
			writeDB();
			loadMenuDB();
		}
		
		return 1;
	}

        public int getPosition(String mt) {
                if ( menu.get(mt) != null ) {
                    return menu.get(mt).getPosition();
                } else {
                    return -1;
                }
        }
	/**
	 * Increase the menu position
	 * @param mt Main Menu Title
	 */
	@SuppressWarnings("unchecked")
	public void incPosition(String mt) {
		if ( menu.get(mt) != null ) {
			if ( menu.get(mt).getPosition() >= maxMenuPosition ) {
				return;
			}
			
			Object[] mi = menu.keySet().toArray();
			Arrays.sort(mi, new Comparator() {
			    public int compare(Object o1, Object o2) {
			    	if ( menu.get(o1).getPosition() > menu.get(o2).getPosition())
			    		return 1;
			    	else
			    		return -1;
			    }
			  });
			
			int curPosition = menu.get(mt).getPosition();
			curPosition++;
			for( int x = 0; x < mi.length; x++ ) {
				if( menu.get(mi[x]).getPosition() == curPosition ) {
					menu.get(mi[x]).decPosition();
				}
			}
			menu.get(mt).incPosition();
			writeDB();
			loadMenuDB();
		}
		
		return;
	}
	
	/**
	 * Decrease the menu position
	 * @param mt Main Menu Title
	 */
	@SuppressWarnings("unchecked")
	public void decPosition(String mt) {
		if ( menu.get(mt) != null ) {
			if ( menu.get(mt).getPosition() == 1 ) {
				return;
			}
			
			Object[] mi = menu.keySet().toArray();
			Arrays.sort(mi, new Comparator() {
			    public int compare(Object o1, Object o2) {
			    	if ( menu.get(o1).getPosition() > menu.get(o2).getPosition())
			    		return 1;
			    	else
			    		return -1;
			    }
			  });
			
			int curPosition = menu.get(mt).getPosition();
			curPosition--;
			for( int x = 0; x < mi.length; x++ ) {
				if( menu.get(mi[x]).getPosition() == curPosition ) {
					menu.get(mi[x]).incPosition();
				}
			}
			menu.get(mt).decPosition();
			writeDB();
			loadMenuDB();
		}
		
		return;
	}
	
	/**
	 * Add a new Sub Menu Item
	 * @param mt Main Menu Title
	 * @param smt Sub Menu Title
	 * @param sma Sage menu for sub menu action
	 * @return return code
	 */
public int addSubMenu(String mt, String smt, String sma) {
	int wPos = 0;
	Object[] mk = menu.get(mt).getSubMenuSorted();
	for ( int x = 0; x < mk.length; x++) {
		if ( menu.get(mt).getSubMenuPosition((String)mk[x]) > wPos)
			wPos = menu.get(mt).getSubMenuPosition((String)mk[x]);
	}
	
	wPos++;
	
	menu.get(mt).addSubMenu(smt, sma, wPos);
	
	writeDB();
	
	loadMenuDB();
			 
	return 1;    
	}

/**
 * Update Sub Menu Title
 * @param mt Main Menu Title
 * @param smt Sub Menu Titile
 * @param nsmt New Sub Menu Title
 * @return return code
 */
public int updateSubMenuTitle(String mt, String smt, String nsmt) {
	
	if ( menu.get(mt) != null ) {
		menu.get(mt).setSubMenuTitle(smt, nsmt);
		writeDB();
		loadMenuDB();
	}
		 
	return 1;    
}

/**
 * Update a Sub Menu Item Action
 * @param mt Main Menu Title
 * @param smt Sub Menu Title
 * @param nsma Sage menu to execute
 * @return return code
 */
public int updateSubMenuAction(String mt, String smt, String nsma) {
	if ( menu.get(mt) != null ) {
		menu.get(mt).setSubMenuAction(smt, nsma);
		writeDB();
		loadMenuDB();
	}
	return 1;    
}

/**
 * Delete a Sub Menu Item
 * @param mt Main Menu Title
 * @param smt Sub Menu Title
 * @return return code
 */
public int deleteSubMenu(String mt, String smt) {
	
	if ( menu.get(mt) != null ) {
		menu.get(mt).delSubMenu(smt);
		writeDB();
		loadMenuDB();
	}
		 
   return 1;
}

        public void addSubMenuItemStatic(String m, String sm,String Title, String var, String val) {
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("addSubMenuItemStatic:Mmenu: " + m + " not found");
                return;
            }
            menu.get(m).addSubMenuItemStatic(sm,Title,var,val);
            writeDB();
            loadMenuDB();
        }
        public void addSubMenuItemGlobal(String m,String sm,String Title, String var, String val) {
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("addSubMenuItemGlobal: Menu: " + m + " not found");
                return;
            }
            menu.get(m).addSubMenuItemGlobal(sm,Title,var,val);
            writeDB();
            loadMenuDB();
        }
        public void addSubMenuItemImage(String m,String sm,String Title, String var, String val) {
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("addSubMenuItemImage: Menu: " + m + " not found");
                return;
            }
            menu.get(m).addSubMenuItemImage(sm,Title, var, val);
            writeDB();
            loadMenuDB();
        }
        public void addSubMenuItemSageCommand(String m, String sm,String Title, String var, String val) {
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("addSubMenuItemSageCommand: Menuenu: " + m + " not found");
                return;
            }
            menu.get(m).addSubMenuItemSageCommand(sm,Title, var, val);
            writeDB();
            loadMenuDB();
        }
        public void addSubMenuItemProperty(String m, String sm,String Title, String var, String val) {
            if ( menu.get(m) == null) {
                ortus.api.DebugLogError("addSubMenuItemProperty: Menuenu: " + m + " not found");
                return;
            }
            menu.get(m).addSubMenuItemProperty(sm,Title, var, val);
            writeDB();
            loadMenuDB();
        }

        public void delSubMenuItemStatic(String m, String sm,String var) {
            menu.get(m).delSubMenuItemStatic(sm,var);
            writeDB();
            loadMenuDB();
        }
        public void delSubMenuItemGlobal(String m, String sm,String var) {
            menu.get(m).delSubMenuItemGlobal(sm,var);
            writeDB();
            loadMenuDB();
        }
        public void delSubMenuItemImage(String m, String sm,String var) {
            menu.get(m).delSubMenuItemImage(sm,var);
            writeDB();
            loadMenuDB();
        }
        public void delSubMenuItemSageCommand(String m, String sm,String var) {
            menu.get(m).delSubMenuItemSageCommand(sm,var);
            writeDB();
            loadMenuDB();
        }
        public void delSubMenuItemProperty(String m, String sm,String var) {
            menu.get(m).delSubMenuItemProperty(sm,var);
            writeDB();
            loadMenuDB();
        }

        public Object getSubMenuItemStatic(String m, String sm,String var) {
            return menu.get(m).getSubMenuItemStatic(sm,var);
        }
        public Object getSubMenuItemGlobal(String m, String sm,String var) {
            return menu.get(m).getSubMenuItemGlobal(sm,var);
        }
        public Object getSubMenuItemImage(String m, String sm, String var) {
            return menu.get(m).getSubMenuItemImage(sm,var);
        }
        public Object getSubMenuItemSageCommand(String m, String sm,String var) {
            return menu.get(m).getSubMenuItemSageCommand(sm, var);
        }
        public Object getSubMenuItemProperty(String m, String sm,String var) {
            return menu.get(m).getSubMenuItemProperty(sm, var);
        }

        public Object[] getSubMenuItemStatic(String m, String sm) {
            return menu.get(m).getSubMenuItemStatic(sm);
        }
        public Object[] getSubMenuItemGlobal(String m, String sm) {
            return menu.get(m).getSubMenuItemGlobal(sm);
        }
        public Object[] getSubMenuItemImage(String m, String sm) {
            return menu.get(m).getSubMenuItemImage(sm);
        }
        public Object[] getSubMenuItemSageCommand(String m, String sm) {
            return menu.get(m).getSubMenuItemSageCommand(sm);
        }
        public Object[] getSubMenuItemProperty(String m, String sm) {
            return menu.get(m).getSubMenuItemProperty(sm);
        }


       public int getSubMenuPosition(String mt, String smt) {
                if ( menu.get(mt) != null ) {
                    return menu.get(mt).getSubMenuPosition(smt);
                } else {
                    return -1;
                }
        }
public void incSubMenuPosition(String mt, String smt) {
	if ( menu.get(mt) != null ) {
		menu.get(mt).incSubMenuPosition(smt);
		writeDB();
		loadMenuDB();
	}
	
	return;
}

public void decSubMenuPosition(String mt, String smt) {
	if ( menu.get(mt) != null ) {
		menu.get(mt).decSubMenuPosition(smt);
		writeDB();
		loadMenuDB();
	}
	
	return;
}

public void writeDB() {
    writeDB(ortus.api.GetCurrentUser());
}

public void writeDB(int userid) {
    Connection conn = ortus.api.GetConnection();
    PreparedStatement ps = null;
    String DeleteSQL = "delete from sage.menu where userid = ?";
    String SQL = "insert into sage.menu (userid, menu) values(?,?)";

    try {
        ps = conn.prepareStatement(DeleteSQL);
        ps.setInt(1, userid);
        ps.execute();

        ps.close();
        ps = conn.prepareStatement(SQL);
        ps.setInt(1, userid);
        ps.setObject(2, menu);
        ps.execute();
        ortus.mq.api.fireMQMessage(ortus.mq.vars.EvenType.Broadcast, "UserMenuReload", new Object[] { userid });
    } catch (Exception e) {
        ortus.api.DebugLog(LogLevel.Error,"writeDB: Exception:", e);
    } finally {
        if ( ps != null) try { ps.close(); } catch(Exception ex) {}
        if ( conn != null) try { conn.close(); } catch(Exception ex) {}
    }
}
    
    private void writeXML() {


        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLStreamWriter xtw = null;
        try {
            xtw = xof.createXMLStreamWriter(new FileWriter(MenuFile));
            xtw.writeStartDocument("1.0");
            xtw.writeStartElement("Menu");

            Object[] keys = menu.keySet().toArray();
            for (Object mTitle : keys) {
                xtw.writeStartElement("MenuItem");
                xtw.writeAttribute("Action", menu.get((String) mTitle).getAction());
                xtw.writeAttribute("MenuType", menu.get((String) mTitle).getMenuType());
                xtw.writeAttribute("Position", String.valueOf(menu.get((String) mTitle).getPosition()));
                xtw.writeAttribute("Title", menu.get((String) mTitle).getTitle());
                xtw.writeAttribute("XPos", String.valueOf(menu.get((String) mTitle).getXpos()));
                xtw.writeAttribute("YPos", String.valueOf(menu.get((String) mTitle).getYpos()));

                HashMap<String, HashMap<String,MenuItemElement>> me = menu.get((String)mTitle).getMenuItemElements();

                for ( Object ekeys : me.keySet().toArray()) {
                    ortus.api.DebugLogTrace("Processing MenuItem: " + ekeys);
                    HashMap<String,MenuItemElement> mie = me.get(ekeys);
                    for ( Object skeys : mie.keySet().toArray()) {
                        ortus.api.DebugLogTrace("Processing MenuItem Type: " + skeys);
                        MenuItemElement x = mie.get(skeys);
                        ortus.api.DebugLogTrace("skey: " + skeys + " x: " + x + " xtitle: " + x.getTitle() + " xname: " + x.getName());
                        xtw.writeStartElement(x.getMenuitemtype().name());
                        xtw.writeAttribute("Title", x.getTitle());
                        xtw.writeAttribute("Name", x.getName());
                        xtw.writeAttribute("Value", x.getValue());
                        xtw.writeEndElement();                        
                    }
                }
                Object[] smkeys = menu.get((String) mTitle).getSubMenu().keySet().toArray();
                for (Object sm : smkeys) {
                    xtw.writeStartElement("SubMenuItem");
                    xtw.writeAttribute("Action", menu.get((String) mTitle).getSubMenu().get((String) sm).getAction());
                    xtw.writeAttribute("Title", menu.get((String) mTitle).getSubMenu().get((String) sm).getTitle());
                    xtw.writeAttribute("Position", String.valueOf(menu.get((String) mTitle).getSubMenu().get((String) sm).getPosition()));

                    HashMap<String, HashMap<String,MenuItemElement>> sme = menu.get((String)mTitle).getSubMenu().get((String)sm).getMenuItemElements();

                    for ( Object ekeys : sme.keySet().toArray()) {
                        HashMap<String,MenuItemElement> mie = sme.get(ekeys);
                        for ( Object skeys : mie.keySet().toArray()) {
                            MenuItemElement x = mie.get(skeys);
                            xtw.writeStartElement(x.getMenuitemtype().name());
                            xtw.writeAttribute("Title", x.getTitle());
                            xtw.writeAttribute("Name", x.getName());
                            xtw.writeAttribute("Value", x.getValue());
                            xtw.writeEndElement();
                        }
                    }
                    xtw.writeEndElement();
                }
                xtw.writeEndElement();
            }

            xtw.writeEndElement();
            xtw.writeEndDocument();
            xtw.flush();
            xtw.close();
        } catch (Exception e) {
            ortus.api.DebugLog(LogLevel.Error, "WriteXML: Exception",e);
        }
        System.out.println("WriteXML: Completed Successfully");
    }

        private void defaultMenu(int userid) {
            ortus.api.DebugLogTrace("DefaultMenu: Loading default menu");
            FileWriter fw = null;

            LoadMenuXML("jar:/ortus/resources/ortus_menu.xml");
            writeDB(userid);
//            List<String> menuXML = Ortus.getInstance().LoadJarFile("/ortus/resources/ortus_menu.xml");

//            try {
//                fw = new FileWriter(MenuFile);
//                for( String x : menuXML) {
//                    fw.write(x);
//                }
//            } catch ( Exception e) {
//                ortus.api.DebugLogError("defaultMenu: Exception",e);
//            } finally {
//                if ( fw != null)
//                    try { fw.close(); } catch ( Exception ex) {}
//            }
        }

	private void defaultMenuOld() {



		    ortus.api.DebugLog(LogLevel.Trace2, "Loading Default Menu" );
			
		    menu.clear();
			
			MenuItem mi = new MenuItem("TV", "Ortus Media",1);
                        mi.setMenuType("tv");
			mi.addMenuItemStatic("param_menuname","param_menuname","MainTV");
			mi.addMenuItemStatic("param_content","param_content","Recordings");
			mi.addSubMenu("Guide","Ortus Guide",1);
			mi.addSubMenu("General Recordings","Ortus Media",2);
			mi.addSubMenuItemStatic("General Recordings","param_menuname","param_menuname","Recordings");
			mi.addSubMenuItemStatic("General Recordings","param_content", "param_content","Recordings");
			mi.addSubMenuItemStatic("General Recordings","param_selected_view_style", "param_selected_view_style","TextList");
			mi.addSubMenuItemStatic("General Recordings","param_filter_view", "param_filter_view","FILE-SYSTEM");
			mi.addSubMenuItemStatic("General Recordings", "param_sort_order","param_sort_order","AIR-DATE");
			mi.addSubMenuItemStatic("General Recordings", "param_reverse_order","param_reverse_order","true");
			mi.addSubMenuItemStatic("General Recordings", "param_filter_dvds","param_filter_dvds","false");
			mi.addSubMenuItemStatic("General Recordings", "param_filter_bluray","param_filter_bluray","false");
			mi.addSubMenuItemStatic("General Recordings", "param_filter_series","param_filter_series","None");
			mi.addSubMenuItemStatic("General Recordings", "param_filter_movies","param_filter_movies","None");
			mi.addSubMenuItemStatic("General Recordings", "param_filter_unwatched","param_filter_unwatched","None");
			mi.addSubMenu("TV Series","Ortus Media",3);
			mi.addSubMenuItemStatic("TV Series", "param_menuname","param_menuname","Series");
			mi.addSubMenuItemStatic("TV Series", "param_content","param_content","Series");
			mi.addSubMenuItemStatic("TV Series", "param_selected_view_style","param_selected_view_style","TextList");
			mi.addSubMenuItemStatic("TV Series", "param_filter_view","param_filter_view","FILE-SYSTEM");
			mi.addSubMenuItemStatic("TV Series", "param_sort_order","param_sort_order","AIR-DATE");
			mi.addSubMenuItemStatic("TV Series", "param_reverse_order","param_reverse_order","true");
			mi.addSubMenuItemStatic("TV Series", "param_filter_dvds","param_filter_dvds","false");
			mi.addSubMenuItemStatic("TV Series", "param_filter_bluray","param_filter_bluray","false");
			mi.addSubMenuItemStatic("TV Series", "param_filter_series","param_filter_series","None");
			mi.addSubMenuItemStatic("TV Series", "param_filter_movies","param_filter_movies","None");
			mi.addSubMenuItemStatic("TV Series", "param_filter_unwatched","param_filter_unwatched","None");
			mi.addSubMenu("Schedules","OrtusSchedule",4);
			mi.addSubMenu("Favorites","OrtusFavoritesManager",5);
			menu.put(mi.getTitle(), mi);
			
			mi = new MenuItem("Movies", "Ortus Media",2);
                        mi.setMenuType("movie");
			mi.addMenuItemStatic("param_menuname","MainMovies");
			mi.addMenuItemStatic("param_content","Movies");
			mi.addSubMenu("General Movies - List","Ortus Media",1);
			mi.addSubMenuItemStatic("General Movies - List", "param_menuname","MoviesList");
			mi.addSubMenuItemStatic("General Movies - List", "param_content","Movies");
			mi.addSubMenuItemStatic("General Movies - List", "param_selected_view_style","TextList");
			mi.addSubMenuItemStatic("General Movies - List", "param_filter_view","FILE-SYSTEM");
			mi.addSubMenuItemStatic("General Movies - List", "param_sort_order","ALPHA");
			mi.addSubMenuItemStatic("General Movies - List", "param_reverse_order","false");
			mi.addSubMenuItemStatic("General Movies - List", "param_filter_dvds","false");
			mi.addSubMenuItemStatic("General Movies - List", "param_filter_bluray","false");
			mi.addSubMenuItemStatic("General Movies - List", "param_filter_series","None");
			mi.addSubMenuItemStatic("General Movies - List", "param_filter_movies","None");
			mi.addSubMenuItemStatic("General Movies - List", "param_filter_unwatched","None");
			mi.addSubMenu("General Movies - Poster View","Ortus Media",2);
			mi.addSubMenuItemStatic("General Movies - Poster View", "param_menuname","MoviesPoster");
			mi.addSubMenuItemStatic("General Movies - Poster View", "param_content","Movies");
			mi.addSubMenuItemStatic("General Movies - Poster View", "param_selected_view_style","PosterFlow");
			mi.addSubMenuItemStatic("General Movies - Poster View", "param_filter_view","FILE-SYSTEM");
			mi.addSubMenuItemStatic("General Movies - Poster View", "param_sort_order","ALPHA");
			mi.addSubMenuItemStatic("General Movies - Poster View", "param_reverse_order","false");
			mi.addSubMenuItemStatic("General Movies - Poster View", "param_filter_dvds","false");
			mi.addSubMenuItemStatic("General Movies - Poster View", "param_filter_bluray","false");
			mi.addSubMenuItemStatic("General Movies - Poster View", "param_filter_series","None");
			mi.addSubMenuItemStatic("General Movies - Poster View", "param_filter_movies","None");
			mi.addSubMenuItemStatic("General Movies - Poster View", "param_filter_unwatched","None");
			mi.addSubMenu("General Movies - Wall View","Ortus Media",3);
			mi.addSubMenuItemStatic("General Movies - Wall View", "param_menuname","MoviesWall");
			mi.addSubMenuItemStatic("General Movies - Wall View", "param_content","Movies");
			mi.addSubMenuItemStatic("General Movies - Wall View", "param_selected_view_style","MovieWall");
			mi.addSubMenuItemStatic("General Movies - Wall View", "param_filter_view","FILE-SYSTEM");
			mi.addSubMenuItemStatic("General Movies - Wall View", "param_sort_order","ALPHA");
			mi.addSubMenuItemStatic("General Movies - Wall View", "param_reverse_order","false");
			mi.addSubMenuItemStatic("General Movies - Wall View", "param_filter_dvds","false");
			mi.addSubMenuItemStatic("General Movies - Wall View", "param_filter_bluray","false");
			mi.addSubMenuItemStatic("General Movies - Wall View", "param_filter_series","None");
			mi.addSubMenuItemStatic("General Movies - Wall View", "param_filter_movies","None");
			mi.addSubMenuItemStatic("General Movies - Wall View", "param_filter_unwatched","None");

			menu.put(mi.getTitle(), mi);
			
			mi = new MenuItem("Music", "Ortus Music",3);
                        mi.setMenuType("music");
			menu.put(mi.getTitle(), mi);
			
			mi = new MenuItem("Pictures", "Ortus Pictures",4);
                        mi.setMenuType("picture");
			menu.put(mi.getTitle(), mi);

                        mi = new MenuItem("Trailers", "Ortus Trailers", 5);
                        mi.setMenuType("movies");
                        menu.put(mi.getTitle(),mi);

			mi = new MenuItem("Weather", "Ortus Weather - 4 day Forecast",6);
                        mi.setMenuType("weather");
			menu.put(mi.getTitle(), mi);
			
			mi = new MenuItem("Setup", "Ortus Setup",7);
                        mi.setMenuType("setup");
        		menu.put(mi.getTitle(), mi);
			
			writeDB();
		}

	}

