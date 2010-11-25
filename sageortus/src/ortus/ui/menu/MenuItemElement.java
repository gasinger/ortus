/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.ui.menu;

/**
 *
 * @author jphipps
 */
public class MenuItemElement {
    private MenuItemElementType menuitemtype;
    private String Title = "";
    private String Name = "";
    private String Value = "";

    public MenuItemElement() {
        
    }
    public MenuItemElement(MenuItemElementType mit, String Title, String Name, String Value) {
        this.menuitemtype = mit;
        this.Title = Title;
        this.Name = Name;
        this.Value = Value;
        ortus.api.DebugLogTrace("New MenuItemElement: Type: " + mit.name() + " Title: " + Title + " Name: " + Name + " Value: " + Value);
    }
    /**
     * @return the menuitemtype
     */
    public MenuItemElementType getMenuitemtype() {
        return menuitemtype;
    }

    /**
     * @param menuitemtype the menuitemtype to set
     */
    public void setMenuitemtype(MenuItemElementType menuitemtype) {
        this.menuitemtype = menuitemtype;
    }

    /**
     * @return the Title
     */
    public String getTitle() {
        return Title;
    }

    /**
     * @param Title the Title to set
     */
    public void setTitle(String Title) {
        this.Title = Title;
    }

    /**
     * @return the Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @param Name the Name to set
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return Value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String Value) {
        this.Value = Value;
    }
}
