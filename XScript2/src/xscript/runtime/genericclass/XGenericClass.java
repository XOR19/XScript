package xscript.runtime.genericclass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.Callable;

import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XClassTable;

public class XGenericClass implements List<Object>, Callable<Map<String, Object>> {

	private XClass xClass;
	private XGenericClass[] generics;
	
	public XGenericClass(XClass xClass){
		this.xClass = xClass;
		if(xClass.getGenericParams()!=0 && xClass.getGenericParams()!=-1){
			throw new XRuntimeException("Can't create a generic class of %s without generic params, need %s generic params", xClass, xClass.getGenericParams());
		}
	}
	
	public XGenericClass(XClass xClass, XGenericClass[] generics) {
		this.xClass = xClass;
		this.generics = generics;
		if(xClass.getGenericParams()!=generics.length && xClass.getGenericParams()!=-1){
			throw new XRuntimeException("Can't create a generic class of %s with %s generic params, need %s generic params", xClass, generics.length, xClass.getGenericParams());
		}
	}

	public XGenericClass(XVirtualMachine virtualMachine, DataInputStream dis) throws IOException {
		String className = dis.readUTF();
		xClass = virtualMachine.getClassProvider().getLoadedXClass(className);
		int s = dis.readInt();
		if(s==-1){
			generics = null;
		}else{
			generics = new XGenericClass[s];
			for(int i=0; i<s; i++){
				generics[i] = new XGenericClass(virtualMachine, dis);
			}
		}
	}

	public void save(DataOutputStream dos) throws IOException {
		dos.writeUTF(xClass.getName());
		if(generics==null){
			dos.writeInt(-1);
		}else{
			dos.writeInt(generics.length);
			for(XGenericClass generic:generics){
				generic.save(dos);
			}
		}
	}
	
	public XClass getXClass() {
		return xClass;
	}

	public XGenericClass getGeneric(int genericID) {
		return generics[genericID];
	}

	public boolean canCastTo(XGenericClass to) {
		XClass oClass = to.getXClass();
		if(!xClass.canCastTo(oClass))
			return false;
		if(to.generics==null)
			return true;
		XClassTable classTable = oClass.getClassTable(xClass);
		for(int i=0; i<to.generics.length; i++){
			//if(!classTable.getGenericPtr(i).getXClass(xClass.getVirtualMachine(), generics[i], null).equals(to.generics[i])){
				//return false;
			//}
		}
		return true;
	}
	
	@Override
	public String toString() {
		String ret = xClass.toString();
		if(generics!=null && generics.length>0){
			ret += "<"+generics[0];
			for(int i=1; i<generics.length; i++){
				ret += ", "+generics[i];
			}
			ret += ">";
		}
		return ret;
	}

	@Override
	public Map<String, Object> call() {
		return xClass;
	}

	@Override
	public boolean add(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends Object> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends Object> arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object value) {
		if(generics==null)
			return false;
		for(XGenericClass gc:generics){
			if(value==gc)
				return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> values) {
		Iterator<?> i = values.iterator();
		while(i.hasNext()){
			if(!contains(i.next()))
				return false;
		}
		return true;
	}

	@Override
	public Object get(int generic) {
		if(generics==null)
			return null;
		return generics[generic];
	}

	@Override
	public int indexOf(Object value) {
		if(generics==null)
			return -1;
		for(int i=0; i<generics.length; i++){
			if(value==generics[i])
				return i;
		}
		return -1;
	}

	@Override
	public boolean isEmpty() {
		return size()==0;
	}

	@Override
	public Iterator<Object> iterator() {
		return listIterator();
	}

	@Override
	public int lastIndexOf(Object value) {
		return indexOf(value);
	}

	@Override
	public ListIterator<Object> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<Object> listIterator(int pos) {
		return new ArrayIterator<Object>(generics, pos);
	}

	private static class ArrayIterator<T> implements ListIterator<T>{
		
		private T[] array;
		
		private int pos;
		
		public ArrayIterator(T[] array, int pos){
			this.array = array;
			this.pos = pos;
		}

		@Override
		public void add(T e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return array.length>pos;
		}

		@Override
		public boolean hasPrevious() {
			return pos>0;
		}

		@Override
		public T next() {
			return array[pos++];
		}

		@Override
		public int nextIndex() {
			return pos;
		}

		@Override
		public T previous() {
			return array[--pos];
		}

		@Override
		public int previousIndex() {
			return pos-1;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(T e) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(int arg0) {
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
	public Object set(int arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return generics==null?0:generics.length;
	}

	@Override
	public List<Object> subList(int from, int to) {
		List<Object> list = new ArrayList<Object>();
		for(int i=from; i<to; i++){
			list.add(generics[i]);
		}
		return list;
	}

	@Override
	public Object[] toArray() {
		if(generics==null)
			return null;
		return Arrays.copyOf(generics, generics.length);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] to) {
		if(generics==null)
			return null;
		if(to.length<generics.length){
			to = (T[]) Array.newInstance(to.getClass().getComponentType(), generics.length);
		}
		System.arraycopy(generics, 0, to, 0, generics.length);
		return to;
	}

}
