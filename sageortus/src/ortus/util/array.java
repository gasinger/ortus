/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.util;

import ortus.*;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 *
 * @author Aaron
 */
public class array extends vars
{
	@SuppressWarnings("unchecked")
	/*
	 * returns an Object Array of a passed in Vector, Array, List
	 * (converts to type Object[])
	 *
	 * @param Array Array, List, Vector, or Map
	 * -in case of Map returns an array of the KeySet
	 */
	public static Object[] toArray(Object Arr)
	{
		try {
			if (Arr.getClass().isArray()) {
				return (Object[]) Arr;
			} else if (Arr instanceof Vector) {
				return ((Vector) Arr).toArray();
			} else if (Arr instanceof List) {
				return ((List) Arr).toArray();
			} else if (Arr instanceof Map) {
				return (((Map) Arr).keySet()).toArray();
			} else {
				ortus.api.DebugLog(LogLevel.Error, "ortus_api_toArray(): Unknown Array Type");
				return null;
			}
		} catch (Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "ortus_api_toArray(): Failed!");
			return null;
		}
	}

	public static List toList(Object obj)
	{
		return Arrays.asList(toArray(obj));

	}

	public static List GetMapKeysAsList(Map m){
		return new ArrayList(m.keySet());
	}

	public static List MoveElementToIndex(List list, Object element, int index){
		if (list.contains(element)){
			list.remove(element);
			if (index == -1){
				list.add(element);
			} else {
				list.add(index, element);
			}
		}
		return list;
	}

	/*
	 * Adds blank (null) elements to beginning and end of a passed array.
	 * Good for creating blank "filler" items for tables and the like.
	 *
	 * @param Arr - Sage MediaFiles, Airings, Shows or Maps in a Vector, list, map, or Array
	 * @param i1 - the number of blank elements to prepad Arr with. 0 for none.
	 * @param i2 - the number of blank elements to append to Arr. 0 for none.
	 */
	public static Object[] AddArrayElements(Object Arr, int i1, int i2)
	{
		Object[] Arr0 = toArray(Arr);
		int length = Arr0.length;
		Object[] Arr1 = new Object[length + i1 + i2];

		System.arraycopy(Arr0, 0, Arr1, i1, length);

		return Arr1;
	}

	public static List RemoveElements(Object mediafiles1, Object mediafiles2)
	{
		List superset = toList(mediafiles1);
		List subset = toList(mediafiles2);

		for (Object element: subset){
			if (superset.contains(element)){
				superset.remove(element);
			}
		}

		return superset;

	}

        public static List GetArrayLimit(Object[] x, int limit) {
            List result = new ArrayList();

            int count = 0;
            for ( Object z : x ) {
                count++;
                result.add(z);
                if ( count == limit)
                    break;
            }

            return result;

        }
}
