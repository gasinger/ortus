/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media.metadata.item;

/**
 *
 * @author jphipps
 */
public class Cast {
    private int episodeid;
    private int personid;
    private String name;
    private String job;
    private String character;

    public Cast(int personid, String name, String job, String character) {
        this.personid = personid;
        this.name = name;
        this.job = job;
        this.character = character;
    }

    public Cast(int episodeid, int personid, String name, String job, String character) {
        this.episodeid = episodeid;
        this.personid = personid;
        this.name = name;
        this.job = job;
        this.character = character;
    }

    /**
     * @return the episodeid
     */
    public int getEpisodeid() {
        return episodeid;
    }

    /**
     * @param episodeid the episodeid to set
     */
    public void setEpisodeid(int episodeid) {
        this.episodeid = episodeid;
    }

    /**
     * @return the personid
     */
    public int getPersonid() {
        return personid;
    }

    /**
     * @param personid the personid to set
     */
    public void setPersonid(int personid) {
        this.personid = personid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the job
     */
    public String getJob() {
        return job;
    }

    /**
     * @param job the job to set
     */
    public void setJob(String job) {
        this.job = job;
    }

    /**
     * @return the character
     */
    public String getCharacter() {
        return character;
    }

    /**
     * @param character the character to set
     */
    public void setCharacter(String character) {
        this.character = character;
    }
}
