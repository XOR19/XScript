package xscript.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class XSet<E> implements Set<E> {

	private List<? extends E> list;
	
	public XSet(List<? extends E> list){
		this.list = list;
	}
	
	public XSet(Collection<? extends E> collection){
		if(collection instanceof List){
			list = (List<? extends E>) collection;
		}else{
			list = new ArrayList<E>(collection);
		}
	}
	
	@Override
	public boolean add(E arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object value) {
		return list.contains(value);
	}

	@Override
	public boolean containsAll(Collection<?> values) {
		return list.containsAll(values);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return new SetIterator();
	}

	private class SetIterator implements Iterator<E>{

		private int pos;
		
		@Override
		public boolean hasNext() {
			return list.size()>pos;
		}

		@Override
		public E next() {
			return list.get(pos++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

}
