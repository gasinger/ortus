package ortus.onlinescrapper.thetvdb;

/**
 *
 * @author matthew.altman
 */
public class Actor implements Comparable<Actor> {

    private String id;
    private String name;
    private String role;
    private String image;
    private int sortOrder = 0;

    public String getId() {
	    return id;
    }

    public void setId(String id) {
	    this.id=id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int compareTo(Actor other) {
        return sortOrder - other.getSortOrder();
    }
    
}
