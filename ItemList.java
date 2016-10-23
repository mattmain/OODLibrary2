/** 
 * @author Brahma Dathan and Sarnath Ramnath
 * @Copyright (c) 2014
 *
 * Redistribution and use with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - the use is for academic purpose only
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   - Neither the name of Brahma Dathan or Sarnath Ramnath
 *     may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * The authors do not make any claims regarding the correctness of the code in this module
 * and are not responsible for any loss or damage resulting from its use.  
 */
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Maintains a list of items with type T and key of type K. Subclassed by
 * Catalog and MemberList
 * 
 * @param <T>
 *            type of item
 * @param <K>
 *            key of item
 */
public class ItemList<T extends Matchable<K>, K> implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<T> list = new LinkedList<T>();

	public ItemList(String title, String id) {
		// TODO Auto-generated constructor stub
	}

	public ItemList() {

	}

	public boolean issue(Member member) {
		return false;
	}

	/**
	 * Checks whether an item with a given id exists.
	 * 
	 * @param key
	 *            the id of the item
	 * @return the item iff the item exists
	 * 
	 */
	public T search(K key) {
		for (T item : list) {
			if (item.matches(key)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Adds an item to the list.
	 * 
	 * @param item
	 *            the item to be added
	 * @return true iff the item could be added
	 */

	public boolean add(T item) {
		return list.add(item);
	}

	/**
	 * Removes the item from the list
	 * 
	 * @param item
	 *            the item to be removed
	 * @return true iff the item could be removed
	 */
	public boolean remove(T item) {
		return list.remove(item);
	}

	/**
	 * Returns an iterator for the collection
	 * 
	 * @return iterator for the collection
	 */
	public Iterator<T> iterator() {
		return list.iterator();
	}

}
