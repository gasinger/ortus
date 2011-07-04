/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.themoviedb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author jphipps
 */
public class SearchResult {
        private String title = null;
    private String original_name;
    private String alternate_name;
    private float score;
    private int popularity;
    private String type;
    private int id;
    private String imdb_id;
    private String url;
    private int rating;
    private String certification;
    private String description = "Not Available";
    private String date = null;
    private HashMap<String, List<String>> images = new HashMap<String, List<String>>();

	public SearchResult(String title) {
		this.title = title;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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
//		sb.append("Title: " + title + " id: " + metadatakey);
		if ( getDate() != null)
			sb.append(" Date: " + getDate());
		return sb.toString();
	}

    /**
     * @return the original_name
     */
    public String getOriginal_name() {
        return original_name;
    }

    /**
     * @param original_name the original_name to set
     */
    public void setOriginal_name(String original_name) {
        this.original_name = original_name;
    }

    /**
     * @return the alternate_name
     */
    public String getAlternate_name() {
        return alternate_name;
    }

    /**
     * @param alternate_name the alternate_name to set
     */
    public void setAlternate_name(String alternate_name) {
        this.alternate_name = alternate_name;
    }

    /**
     * @return the score
     */
    public float getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(float score) {
        this.score = score;
    }

    /**
     * @return the popularity
     */
    public int getPopularity() {
        return popularity;
    }

    /**
     * @param popularity the popularity to set
     */
    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the imdb_id
     */
    public String getImdb_id() {
        return imdb_id;
    }

    /**
     * @param imdb_id the imdb_id to set
     */
    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the rating
     */
    public float getRating() {
        return rating;
    }

    /**
     * @param rating the rating to set
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * @return the certification
     */
    public String getCertification() {
        return certification;
    }

    /**
     * @param certification the certification to set
     */
    public void setCertification(String certification) {
        this.certification = certification;
    }

    /**
     * @return the images
     */
    public List<String> getImages(String type) {
        return images.get(type);
    }

    /**
     * @param images the images to set
     */
    public void addImages(String imagetype, String url) {
        if ( images.get(imagetype) == null) {
            List<String> image = new ArrayList<String>();
            image.add(url);
            images.put(imagetype,image);
        } else {
            images.get(imagetype).add(url);
        }
    }
}
