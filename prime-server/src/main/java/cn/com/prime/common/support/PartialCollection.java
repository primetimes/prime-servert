package cn.com.prime.common.support;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author xichen
 */
@SuppressWarnings({"unchecked","serial","unused"})
public class PartialCollection implements Collection, Serializable {

	public List realDataSet = null;

	int total = 0;

	int offset = 0;
	
	public int lastPage = 1;
	
public Map<String,String> pageMap;
	
	public Map<String, String> getPageMap() {
		return pageMap;
	}

	public void setPageMap(Map<String, String> pageMap) {
		this.pageMap = pageMap;
	}

	/**
	 * @param realDataSet
	 * @param offset
	 * @param pageMap
	 */
	public PartialCollection(List realDataSet, int offset,Map<String,String> pageMap) {
		super();
		this.realDataSet = realDataSet;
		this.offset = offset;
		this.pageMap = pageMap;
		setLastPage();
	}
	
	/**
	 * @param realDataSet
	 * @param total
	 * @param offset
	 */
	public PartialCollection(List realDataSet, int total, int offset) {
		super();
		this.realDataSet = realDataSet;
		this.total = total;
		this.offset = offset;
		setLastPage();
	}

	public boolean add(Object o) {
		throw new IllegalStateException("The collection is immutable!");
	}

	public boolean addAll(Collection c) {
		return realDataSet.addAll(c);
	}

	public void clear() {
		realDataSet.clear();
	}

	public boolean contains(Object o) {
		return realDataSet.contains(o);
	}

	public boolean containsAll(Collection c) {

		return realDataSet.containsAll(c);
	}

	public boolean isEmpty() {

		return realDataSet.isEmpty();
	}

	public Iterator iterator() {
		return new Itr();
	}

	public boolean remove(Object o) {
		throw new IllegalStateException("The collection is immutable!");
	}

	public boolean removeAll(Collection c) {
		throw new IllegalStateException("The collection is immutable!");
	}

	public boolean retainAll(Collection c) {
		throw new IllegalStateException("The collection is immutable!");
	}

	public int size() {
		return total;
	}
	
	public int getTotal() {
		return total;
	}

	public Object[] toArray() {
		Object[] array = new Object[size()];
		Object[] realArray = realDataSet.toArray();
		for (int i = 0; i < realArray.length; i++) {
			array[offset + i] = realArray[i];
		}
		return array;
	}

	public Object[] toArray(Object[] a) {
		if (a.length < total)
			a = (Object[]) java.lang.reflect.Array.newInstance(a.getClass()
					.getComponentType(), total);

		Object[] realArray = realDataSet.toArray();
		for (int i = 0; i < realArray.length; i++) {
			a[offset + i] = realArray[i];
		}

		if (a.length > total)
			a[total] = null;
		return a;
	}

	private class Itr implements Iterator {
		/**
		 * Index of element to be returned by subsequent call to next.
		 */
		int cursor = 0;

		/**
		 * Index of element returned by most recent call to next or previous.
		 * Reset to -1 if this element is deleted by a call to remove.
		 */
		int lastRet = -1;

		public boolean hasNext() {
			return cursor != size();
		}

		public Object next() {
			checkForComodification();
			try {
				Object next = null;
				if (cursor < offset || cursor >= offset + realDataSet.size())
					next = null;
				else
					next = realDataSet.get(cursor - offset);
				lastRet = cursor++;
				return next;
			} catch (IndexOutOfBoundsException e) {
				throw new NoSuchElementException();
			}
		}

		public void remove() {
			throw new IllegalStateException("The collection is immutable!");
		}

		final void checkForComodification() {

		}
	}

	public List getRealDataSet() {
		return realDataSet;
	}

	public void setRealDataSet(List realDataSet) {
		this.realDataSet = realDataSet;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	private void setLastPage(){
		int pageSize = 10;
		this.lastPage = total%pageSize==0?total/pageSize:total/pageSize+1;
	}
	
	public int getLastPage(){
		return lastPage;
	}
}
