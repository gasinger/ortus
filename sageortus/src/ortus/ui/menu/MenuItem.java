/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.ui.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import sagex.UIContext;

/**
 * MenuItem XML Menu Object for storing information about a Menu
 * @author jphipps
 *
 */
class MenuItem extends ortus.vars implements Serializable {
	private HashMap<String,SubMenuItem> SubMenuItems = new HashMap<String,SubMenuItem>();
	private HashMap<String,HashMap<String,MenuItemElement>> MenuItemElements = new HashMap<String,HashMap<String,MenuItemElement>>();
	private String Title;
	private String Action;
	private int Position=0;
	private int Xpos=0;
	private int Ypos=0;
	private int maxSubMenuPosition = 0;
        private int MenuType = 0;

	public MenuItem() {
	}

	/**
	 * Constructor for a new Menu Ojbect
	 * @param mt Menu Title
	 * @param  act Action
	 * @return New Menu Object
	 */
	public MenuItem(String mt, String act) {
		 Title = mt;
		 Action = act;
		 ortus.api.DebugLog(LogLevel.Trace, "Ortus: MenuItem new MenuItem - Titile: " + Title );
	}

	/**
	 * Constructor for a new Menu Ojbect
	 * @param mt Menu Title
	 * @param  act Action
	 * @return New Menu Object
	 */
	public MenuItem(String mt, String act, int pos) {
		 Title = mt;
		 Action = act;
		 Position = pos;
		 ortus.api.DebugLog(LogLevel.Trace, "Ortus: MenuItem: new MenuItem - Titile: " + Title );
	}

	public void setMenuType(String menutype) {
            String mt = menutype.toLowerCase();
            if ( mt.equals("general"))
                MenuType = 0;
            else if ( mt.equals("tv"))
                MenuType = 1;
            else if ( mt.equals("movie"))
                MenuType = 2;
            else if ( mt.equals("music"))
                MenuType = 3;
            else if ( mt.equals("weather"))
                MenuType = 4;
            else if ( mt.equals("pictures"))
                MenuType = 5;
            else if ( mt.equals("setup"))
                MenuType = 9;

            return;
        }

         public String getMenuType() {
             if ( MenuType == 0)
                 return "general";
             else if ( MenuType == 1)
                 return "tv";
             else if ( MenuType == 2)
                 return "movie";
             else if ( MenuType == 3)
                 return "music";
             else if ( MenuType == 4)
                 return "weather";
             else if ( MenuType == 5)
                 return "picture";
             else if ( MenuType == 9)
                 return "setup";
             return "general";
         }

	/**
	 *  Return the Title of a Menu Object
	 * @return Menu Title for the Menu Object
	 */
	public String getTitle() {
		return Title;
	}

	/**
	 *  Set the Menu title for a Menu Object
	 * @param mt New Menu title
	 */
	public void setTitle(String mt) {
		Title = mt;
		return;
	}

	public void setAction(String menu) {
		Action = menu;
	}

	public String getAction() {
		return Action;
	}

	public void setPosition(int x) {
		Position = x;
	}

	public void incPosition() {
		Position++;
	}

	public void decPosition() {
		if ( Position > 1 )
			Position--;
	}

	public int getPosition() {
		return Position;
	}

	public void setXpos(int x) {
		Xpos = x;
	}

	public int getXpos() {
		return Xpos;
	}

	public void setYpos(int x) {
		Ypos = x;
	}

	public int getYpos() {
		return Ypos;
	}

        public void addMenuItemStatic(String Title, String var, String val) {
            addMenuItemElement(MenuItemElementType.Static,Title,var,val);
        }
        public void addMenuItemStatic(String var, String val) {
            addMenuItemElement(MenuItemElementType.Static,var,var,val);
        }
        public void addMenuItemGlobal(String Title, String var, String val) {
            addMenuItemElement(MenuItemElementType.Global,Title,var,val);
        }
        public void addMenuItemImage(String Title, String var, String val) {
            addMenuItemElement(MenuItemElementType.Image,Title,var,val);
        }
        public void addMenuItemSageCommand(String Title, String var, String val) {
            addMenuItemElement(MenuItemElementType.SageCommand,Title,var,val);
        }
        public void addMenuItemProperty(String Title, String var, String val) {
            addMenuItemElement(MenuItemElementType.Property,Title,var,val);
        }

        public void delMenuItemStatic(String var) {
            delMenuItemElement(MenuItemElementType.Static,var);
        }
        public void delMenuItemGlobal(String var) {
            delMenuItemElement(MenuItemElementType.Global,var);
        }
        public void delMenuItemImage(String var) {
            delMenuItemElement(MenuItemElementType.Image,var);
        }
        public void delMenuItemSageCommand(String var) {
            delMenuItemElement(MenuItemElementType.SageCommand,var);
        }
        public void delMenuItemProperty(String var) {
            delMenuItemElement(MenuItemElementType.Property,var);
        }

        public Object getMenuItemStatic(String var) {
            return getMenuItemElement(MenuItemElementType.Static,var);
        }
        public Object getMenuItemGlobal(String var) {
            return getMenuItemElement(MenuItemElementType.Global,var);
        }
        public Object getMenuItemImage(String var) {
            return getMenuItemElement(MenuItemElementType.Image,var);
        }
        public Object getMenuItemSageCommand(String var) {
            return getMenuItemElement(MenuItemElementType.SageCommand,var);
        }
        public Object getMenuItemProperty(String var) {
            return getMenuItemElement(MenuItemElementType.Property,var);
        }

        public Object[] getMenuItemStatic() {
            return getMenuItemElements(MenuItemElementType.Static);
        }
        public Object[] getMenuItemGlobal() {
            return getMenuItemElements(MenuItemElementType.Global);
        }
        public Object[] getMenuItemImage() {
            return getMenuItemElements(MenuItemElementType.Image);
        }
        public Object[] getMenuItemSageCommand() {
            return getMenuItemElements(MenuItemElementType.SageCommand);
        }
        public Object[] getMenuItemProperty() {
            return getMenuItemElements(MenuItemElementType.Property);
        }

        public HashMap<String,HashMap<String,MenuItemElement>> getMenuItemElements() {
            return MenuItemElements;
        }

	private void addMenuItemElement(MenuItemElementType mit, String Title, String var, String val) {
            if ( Title == null || var == null || val == null) {
                ortus.api.DebugLogError("addMenuItemElement: Failed...   Title: " + Title + " Var: " + var + " Val: " + val);
                return;
            }
            if ( MenuItemElements.get(mit.name()) == null) {
                MenuItemElements.put(mit.name(),new HashMap<String,MenuItemElement>());
            }
            MenuItemElements.get(mit.name()).put(var, new MenuItemElement(mit,Title,var,val));
	}

	private void delMenuItemElement(MenuItemElementType mit, String var) {
            if ( MenuItemElements.get(mit.name()) != null ) {
                MenuItemElements.get(mit.name()).remove(var);
            }
	}

        private Object getMenuItemElement(MenuItemElementType mit,String var) {
            if ( MenuItemElements.get(mit.name()) != null )
                 return MenuItemElements.get(mit.name()).get(var).getValue();
            else
                return null;
        }

        private Object[] getMenuItemElements(MenuItemElementType mit) {
            if ( MenuItemElements.get(mit.name()) != null )
                 return MenuItemElements.get(mit.name()).keySet().toArray();
            else
                return null;
        }

        public void addSubMenu(SubMenuItem smi) {
            SubMenuItems.put(smi.getTitle(), smi);
        }
        
	public void addSubMenu(String menu, String action) {
		SubMenuItems.put(menu,new SubMenuItem(menu,action));
	}

	public void addSubMenu(String menu, String action,int pos) {
		SubMenuItems.put(menu,new SubMenuItem(menu,action,pos));
                maxSubMenuPosition++;
	}

	@SuppressWarnings("unchecked")
	public void delSubMenu(String menu) {

		Object[] mi = SubMenuItems.keySet().toArray();
		Arrays.sort(mi, new Comparator() {
		    public int compare(Object o1, Object o2) {
		    	if ( SubMenuItems.get(o1).getPosition() > SubMenuItems.get(o2).getPosition())
		    		return 1;
		    	else
		    		return -1;
		    }
		  });

		int curPosition = SubMenuItems.get(menu).getPosition();

		for( int x = 0; x < mi.length; x++ ) {
			if( SubMenuItems.get(mi[x]).getPosition() > curPosition ) {
				SubMenuItems.get(mi[x]).decPosition();
			}
		}
		SubMenuItems.remove(menu);
                maxSubMenuPosition--;
		return;
	}

	@SuppressWarnings("unchecked")
        public HashMap<String,SubMenuItem> getSubMenu() {
            return SubMenuItems;
        }
        
	public Object[] getSubMenuSorted() {
		Object[] mi = null;

		try {
		mi = SubMenuItems.keySet().toArray();
		Arrays.sort(mi, new Comparator() {
		    public int compare(Object o1, Object o2) {
		    	if ( SubMenuItems.get(o1).getPosition() > SubMenuItems.get(o2).getPosition())
		    		return 1;
		    	else
		    		return -1;
		    }
		  });

		} catch (Exception e) {
			 ortus.api.DebugLog(new UIContext("SAGETV_PROCESS_LOCAL_UI"), "getSubMenuExcpetion: " + e );
		}

		return mi;
	}

	public void setSubMenuTitle(String sm, String title) {
		SubMenuItems.get(sm).setTitle(title);
		SubMenuItems.put(title, SubMenuItems.get(sm));
		SubMenuItems.remove(sm);
		return;
	}

	public void setSubMenuAction( String sm, String sma) {
		SubMenuItems.get(sm).setAction(sma);
		return;
	}

	public String getSubMenuAcion(String sm) {
		return (String)SubMenuItems.get(sm).getAction();
	}

	public int getSubMenuPosition(String sm) {
		return (int)SubMenuItems.get(sm).getPosition();
	}

        public void addSubMenuItemStatic(String sm,String Title, String var, String val) {
            if ( SubMenuItems.get(sm) == null) {
                ortus.api.DebugLogError("addSubMenuItemStatic: Submenu: " + sm + " not found");
                return;
            }
            SubMenuItems.get(sm).addMenuItemStatic(Title,var,val);
        }
        public void addSubMenuItemStatic(String sm,String var, String val) {
            if ( SubMenuItems.get(sm) == null) {
                ortus.api.DebugLogError("addSubMenuItemStatic: Submenu: " + sm + " not found");
                return;
            }

            SubMenuItems.get(sm).addMenuItemStatic(var,var,val);
        }

        public void addSubMenuItemGlobal(String sm,String Title, String var, String val) {
            if ( SubMenuItems.get(sm) == null) {
                ortus.api.DebugLogError("addSubMenuItemGlobal: Submenu: " + sm + " not found");
                return;
            }

            SubMenuItems.get(sm).addMenuItemGlobal(Title,var,val);
        }
        public void addSubMenuItemImage(String sm,String Title, String var, String val) {
            if ( SubMenuItems.get(sm) == null) {
                ortus.api.DebugLogError("addSubMenuItemImage: Submenu: " + sm + " not found");
                return;
            }

            SubMenuItems.get(sm).addMenuItemImage(Title, var, val);
        }
        public void addSubMenuItemSageCommand(String sm,String Title, String var, String val) {
            if ( SubMenuItems.get(sm) == null) {
                ortus.api.DebugLogError("addSubMenuItemSageCommand: Submenu: " + sm + " not found");
                return;
            }

            SubMenuItems.get(sm).addMenuItemSageCommand(Title, var, val);
        }
        public void addSubMenuItemProperty(String sm,String Title, String var, String val) {
            if ( SubMenuItems.get(sm) == null) {
                ortus.api.DebugLogError("addSubMenuItemProperty: Submenu: " + sm + " not found");
                return;
            }

            SubMenuItems.get(sm).addMenuItemProperty(Title, var, val);
        }

        public void delSubMenuItemStatic(String sm,String var) {
            SubMenuItems.get(sm).delMenuItemStatic(var);
        }
        public void delSubMenuItemGlobal(String sm,String var) {
            SubMenuItems.get(sm).delMenuItemGlobal(var);
        }
        public void delSubMenuItemImage(String sm,String var) {
            SubMenuItems.get(sm).delMenuItemImage(var);
        }
        public void delSubMenuItemSageCommand(String sm,String var) {
            SubMenuItems.get(sm).delMenuItemSageCommand(var);
        }
        public void delSubMenuItemProperty(String sm,String var) {
            SubMenuItems.get(sm).delMenuItemProperty(var);
        }

        public Object getSubMenuItemStatic(String sm,String var) {
            return SubMenuItems.get(sm).getMenuItemStatic(var);
        }
        public Object getSubMenuItemGlobal(String sm,String var) {
            return SubMenuItems.get(sm).getMenuItemGlobal(var);
        }
        public Object getSubMenuItemImage(String sm, String var) {
            return SubMenuItems.get(sm).getMenuItemImage(var);
        }
        public Object getSubMenuItemSageCommand(String sm,String var) {
            return SubMenuItems.get(sm).getMenuItemSageCommand(var);
        }
        public Object getSubMenuItemProperty(String sm,String var) {
            return SubMenuItems.get(sm).getMenuItemProperty(var);
        }

        public Object[] getSubMenuItemStatic(String sm) {
            return SubMenuItems.get(sm).getMenuItemStatic();
        }
        public Object[] getSubMenuItemGlobal(String sm) {
            return SubMenuItems.get(sm).getMenuItemGlobal();
        }
        public Object[] getSubMenuItemImage(String sm) {
            return SubMenuItems.get(sm).getMenuItemImage();
        }
        public Object[] getSubMenuItemSageCommand(String sm) {
            return SubMenuItems.get(sm).getMenuItemSageCommand();
        }
        public Object[] getSubMenuItemProperty(String sm) {
            return SubMenuItems.get(sm).getMenuItemProperty();
        }

        public HashMap<String,HashMap<String,MenuItemElement>> getSubMenuItmElements(String sm) {
            return SubMenuItems.get(sm).getMenuItemElements();
        }
	@SuppressWarnings("unchecked")
	public void incSubMenuPosition(String sm) {

		if ( SubMenuItems.get(sm).getPosition() == maxSubMenuPosition ) {
			return;
		}

		Object[] mi = SubMenuItems.keySet().toArray();
		Arrays.sort(mi, new Comparator() {
		    public int compare(Object o1, Object o2) {
		    	if ( SubMenuItems.get(o1).getPosition() > SubMenuItems.get(o2).getPosition())
		    		return 1;
		    	else
		    		return -1;
		    }
		  });

		int curPosition = SubMenuItems.get(sm).getPosition();
		curPosition++;
		for( int x = 0; x < mi.length; x++ ) {
			if( SubMenuItems.get(mi[x]).getPosition() == curPosition ) {
				SubMenuItems.get(mi[x]).decPosition();
			}
		}
		SubMenuItems.get(sm).incPosition();
		return;
	}

	@SuppressWarnings("unchecked")
	public void decSubMenuPosition(String sm) {
		if ( SubMenuItems.get(sm).getPosition() == 1 ) {
			return;
		}
		Object[] mi = SubMenuItems.keySet().toArray();
		Arrays.sort(mi, new Comparator() {
		    public int compare(Object o1, Object o2) {
		    	if ( SubMenuItems.get(o1).getPosition() > SubMenuItems.get(o2).getPosition())
		    		return 1;
		    	else
		    		return -1;
		    }
		  });

		int curPosition = SubMenuItems.get(sm).getPosition();
		curPosition--;
		for( int x = 0; x < mi.length; x++ ) {
			if( SubMenuItems.get(mi[x]).getPosition() == curPosition ) {
				SubMenuItems.get(mi[x]).incPosition();
			}
		}
		SubMenuItems.get(sm).decPosition();
		return;
	}
}

class SubMenuItem extends ortus.vars implements Serializable {
        /*
         * MediaTypes:
         *      0 - General
         *      1 - TV
         *      2 - Movies
         *      3 - Music
         *      4 - Weather
         *      5 - Picutres
         *      9 - Setup
       */

	private String Title;
	private String Action;

	private int Position = 0;
        private HashMap<String,HashMap<String,MenuItemElement>> MenuItemElements = new HashMap<String,HashMap<String,MenuItemElement>>();

	SubMenuItem() {
	}

	/**
	 * Return Sage menu to execute for a sub menu item
	 * @param m Menu Name
	 * @param s Sub Menu Name
	 * @return Sage menu to execute
	 */

	SubMenuItem(String title, String action) {
		Title = title;
		Action = action;
	}

	SubMenuItem(String title, String action, int pos) {
		Title = title;
		Action = action;
		Position = pos;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getTitle() {
		return Title;
	}

	public void setAction(String action) {
		Action = action;
	}

	public String getAction() {
		return Action;
	}

	public void setPosition(int x) {
		Position = x;
	}

	public void incPosition() {
		Position++;
	}

	public void decPosition() {
		Position--;
	}

	public int getPosition() {
		return Position;
	}

        public void addMenuItemStatic(String Title, String var, String val) {
            addMenuItemElement(MenuItemElementType.Static,Title,var,val);
        }
       public void addMenuItemStatic(String var, String val) {
            addMenuItemElement(MenuItemElementType.Static,var,var,val);
        }        
        public void addMenuItemGlobal(String Title, String var, String val) {
            addMenuItemElement(MenuItemElementType.Global,Title,var,val);
        }
        public void addMenuItemImage(String Title, String var, String val) {
            addMenuItemElement(MenuItemElementType.Image,Title,var,val);
        }
        public void addMenuItemSageCommand(String Title, String var, String val) {
            addMenuItemElement(MenuItemElementType.SageCommand,Title,var,val);
        }
        public void addMenuItemProperty(String Title, String var, String val) {
            addMenuItemElement(MenuItemElementType.Property,Title,var,val);
        }

        public void delMenuItemStatic(String var) {
            delMenuItemElement(MenuItemElementType.Static,var);
        }
        public void delMenuItemGlobal(String var) {
            delMenuItemElement(MenuItemElementType.Global,var);
        }
        public void delMenuItemImage(String var) {
            delMenuItemElement(MenuItemElementType.Image,var);
        }
        public void delMenuItemSageCommand(String var) {
            delMenuItemElement(MenuItemElementType.SageCommand,var);
        }
        public void delMenuItemProperty(String var) {
            delMenuItemElement(MenuItemElementType.Property,var);
        }

        public Object getMenuItemStatic(String var) {
            return getMenuItemElement(MenuItemElementType.Static,var);
        }
        public Object getMenuItemGlobal(String var) {
            return getMenuItemElement(MenuItemElementType.Global,var);
        }
        public Object getMenuItemImage(String var) {
            return getMenuItemElement(MenuItemElementType.Image,var);
        }
        public Object getMenuItemSageCommand(String var) {
            return getMenuItemElement(MenuItemElementType.SageCommand,var);
        }
        public Object getMenuItemProperty(String var) {
            return getMenuItemElement(MenuItemElementType.Property,var);
        }

        public Object[] getMenuItemStatic() {
            return getMenuItemElements(MenuItemElementType.Static);
        }
        public Object[] getMenuItemGlobal() {
            return getMenuItemElements(MenuItemElementType.Global);
        }
        public Object[] getMenuItemImage() {
            return getMenuItemElements(MenuItemElementType.Image);
        }
        public Object[] getMenuItemSageCommand() {
            return getMenuItemElements(MenuItemElementType.SageCommand);
        }
        public Object[] getMenuItemProperty() {
            return getMenuItemElements(MenuItemElementType.Property);
        }

        public HashMap<String,HashMap<String,MenuItemElement>> getMenuItemElements() {
            return MenuItemElements;
        }
	private void addMenuItemElement(MenuItemElementType mit, String Title, String var, String val) {
            if ( MenuItemElements.get(mit.name()) == null) {
                MenuItemElements.put(mit.name(),new HashMap<String,MenuItemElement>());
            }
            MenuItemElements.get(mit.name()).put(var, new MenuItemElement(mit,Title,var,val));
	}

	private void delMenuItemElement(MenuItemElementType mit, String var) {
            if ( MenuItemElements.get(mit.name()) != null ) {
                MenuItemElements.get(mit.name()).remove(var);
            }
	}

        private Object getMenuItemElement(MenuItemElementType mit,String var) {
            if ( MenuItemElements.get(mit.name()) != null )
                 return MenuItemElements.get(mit.name()).get(var).getValue();
            else
                return null;
        }

        private Object[] getMenuItemElements(MenuItemElementType mit) {
            if ( MenuItemElements.get(mit.name()) != null )
                 return MenuItemElements.get(mit.name()).keySet().toArray();
            else
                return null;
        }

}

