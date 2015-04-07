package xscript;

import java.util.AbstractList;

public class XArrayList<T> extends AbstractList<T>{

	private T[] array;
	
	@SafeVarargs
	public XArrayList(T...array){
		this.array = array;
	}
	
	@Override
	public T get(int index) {
		return array[index];
	}

	@Override
	public int size() {
		return array.length;
	}
	
}