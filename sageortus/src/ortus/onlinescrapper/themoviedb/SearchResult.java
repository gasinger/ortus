/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.themoviedb;

/**
 *
 * @author jphipps
 */
public class SearchResult {
	String title = null;
	String date = null;
	String metadatakey = null;
        String description = "Not Available";

	public SearchResult(String title, String metadatakey) {
		this.title = title;
		this.metadatakey = metadatakey;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMetadatakey() {
		return metadatakey;
	}

	public void setMetadatakey(String metadatakey) {
		this.metadatakey = metadatakey;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
        
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Title: " + title + " id: " + metadatakey);
		if ( date != null)
			sb.append(" Date: " + date);
		return sb.toString();
	}
}
