/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.property;

/**
 *
 * @author jphipps
 */
public interface IProperty {

	/**
	 * Get the value of a property
	 * @param PropertyName OrtusProperty name
	 * @return Value of property name
	 */
	Object GetProperty(String PropertyName, Object defaultvalue);

        void Load();

        void Load(Object userid);
        
	void Reload(Object newpath);

	/**
	 * Remove a property from the ortus.properties file
	 * @param PropertyName OrtusProperty to delete
	 */
	void RemoveProperty(String PropertyName);

	/**
	 * Set the value of a property
	 * @param PropertyKey OrtusProperty to modify
	 * @param PropertyValue OrtusProperty value
	 */
	void SetProperty(String PropertyKey, String PropertyValue);

	/**
	 * Shutdown procedure for the propeties file to store memory properteies to the ortus.properties file
	 */
	void Shutdown();

        void StoreProperty();

}
