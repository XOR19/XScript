package xscript.runtime.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import xscript.runtime.XModifier;
import xscript.runtime.XRuntimeException;
import xscript.runtime.XSet;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XField;
import xscript.runtime.clazz.XWrapper;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.threads.XThread;

public class XObject implements Map<Object, Object>, Callable<Callable<Map<String, Object>>>{

	private XGenericClass xClass;
	private byte[] data;
	private byte[] userData;
	private boolean isVisible;
	private int monitor;
	private XThread thread;
	private List<XThread> waiting;
	
	protected XObject(XGenericClass xClass){
		if(XModifier.isAbstract(xClass.getXClass().getModifier()))
			throw new XRuntimeException("Can't create Object form abstract class %s", xClass);
		if(xClass.getXClass().isArray())
			throw new XRuntimeException("%s is an array", xClass);
		this.xClass = xClass;
		data = new byte[xClass.getXClass().getObjectSize()];
	}
	
	protected XObject(XGenericClass xClass, int size) {
		if(XModifier.isAbstract(xClass.getXClass().getModifier()))
			throw new XRuntimeException("Can't create Object form abstract class %s", xClass);
		if(!(xClass.getXClass().isArray()))
			throw new XRuntimeException("%s isn't an array", xClass);
		this.xClass = xClass;
		data = new byte[xClass.getXClass().getObjectSize()+size*xClass.getXClass().getArrayElementSize()];
		xClass.getXClass().getLengthField().finalSet(this, size);
	}
	
	public XGenericClass getXClass(){
		return xClass;
	}
	
	public byte[] getData(){
		return data;
	}
	
	public boolean isArray(){
		return xClass.getXClass().isArray();
	}
	
	public int getArrayLength(){
		if(isArray()){
			return (int) xClass.getXClass().getLengthField().get(this);
		}
		return 0;
	}
	
	public long getArrayElement(int index){
		if(isArray()){
			int size = xClass.getXClass().getArrayElementSize();
			int i = xClass.getXClass().getObjectSize()+size*index;
			long l = 0;
			for(int j=0; j<size; j++){
				l <<= 8;
				l |= data[i+j];
			}
			return l;
		}
		return 0;
	}
	
	public void setArrayElement(int index, long value){
		if(isArray()){
			int size = xClass.getXClass().getArrayElementSize();
			int i = xClass.getXClass().getObjectSize()+size*index;
			for(int j=size-1; j>=0; j--){
				data[i+j] = (byte) (value & 255);
				value >>>= 8;
			}
		}
	}
	
	public byte[] getUserData(){
		return userData;
	}
	
	public void setUserData(byte[] userData){
		this.userData = userData;
	}
	
	public void resetVisibility(){
		isVisible = false;
	}
	
	public void markVisible(){
		if(!isVisible){
			isVisible = true;
			xClass.getXClass().markObjectObjectsVisible(this);
		}
	}
	
	public boolean isVisible(){
		return isVisible;
	}

	public void exitMonitor(XThread thread) {
		if(this.thread==thread){
			monitor--;
			if(monitor<=0){
				if(waiting.isEmpty()){
					this.thread = null;
					waiting = null;
				}else{
					this.thread = waiting.remove(0);
					monitor = 1;
					this.thread.setWaiting(false);
				}
			}
		}
	}

	public void wantMonitor(XThread thread) {
		if(this.thread==null){
			this.thread = thread;
			monitor = 1;
			waiting = new ArrayList<XThread>();
		}else if(this.thread==thread){
			monitor++;
		}else{
			waiting.add(thread);
			thread.setWaiting(true);
		}
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object name) {
		if(name instanceof String)
			return getField((String)name)!=null;
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return getValues().contains(value);
	}

	@Override
	public Set<Entry<Object, Object>> entrySet() {
		List<String> keys = getKeys();
		List<Entry<Object, Object>> entries = new ArrayList<Map.Entry<Object, Object>>();
		for(String key:keys){
			entries.add(new FieldEntry(key));
		}
		return new XSet<Entry<Object, Object>>(entries);
	}

	private class FieldEntry implements Entry<Object, Object>{

		private String key;
		
		public FieldEntry(String key){
			this.key = key;
		}
		
		@Override
		public String getKey() {
			return key;
		}

		@Override
		public Object getValue() {
			return get(key);
		}

		@Override
		public Object setValue(Object value) {
			return put(key, value);
		}
		
	}
	
	@Override
	public Object get(Object name) {
		if(name instanceof String){
			XField field = getField((String)name);
			return getFieldValue(field);
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Set<Object> keySet() {
		return new XSet<Object>(getKeys());
	}
	
	@Override
	public Object put(Object name, Object value) {
		Object old;
		XObjectProvider objProv = xClass.getXClass().getVirtualMachine().getObjectProvider();
		if(name instanceof String){
			XField field = getField((String)name);
			int primitive = field.getTypePrimitive();
			old = XWrapper.getJavaObject(objProv, primitive, field.get(this));
			long l = XWrapper.getXObject(objProv, primitive, value);
			field.set(this, l);
		}else{
			int index = XWrapper.castToInt(name);
			int primitive = getXClass().getXClass().getArrayPrimitive();
			old = XWrapper.getJavaObject(objProv, primitive, getArrayElement(index));
			long l = XWrapper.getXObject(objProv, primitive, value);
			setArrayElement(index, l);
		}
		return old;
	}

	@Override
	public void putAll(Map<? extends Object, ? extends Object> values) {
		for(Entry<? extends Object, ? extends Object> e:values.entrySet()){
			put(e.getKey(), e.getValue());
		}
	}

	@Override
	public Object remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return getKeys().size();
	}

	@Override
	public Collection<Object> values() {
		return new XSet<Object>(getValues());
	}
	
	private XField getField(String name){
		int split = name.lastIndexOf('.');
		XClass c = xClass.getXClass().getVirtualMachine().getClassProvider().getXClass(name.substring(0, split));
		return c.getField(name.substring(split+1));
	}
	
	private Object getFieldValue(XField field){
		XObjectProvider objProv = xClass.getXClass().getVirtualMachine().getObjectProvider();
		int primitive = field.getTypePrimitive();
		return XWrapper.getJavaObject(objProv, primitive, field.get(this));
	}
	
	private List<String> getKeys(){
		XClass c = xClass.getXClass();
		List<String> names = new ArrayList<String>();
		addClassFields(c, new ArrayList<XClass>(), names);
		return names;
	}
	
	private static void addClassFields(XClass c, List<XClass> classes, List<String> names){
		if(!classes.contains(c)){
			classes.add(c);
			XField fields[] = c.getFields();
			for(int i=0; i<fields.length; i++){
				names.add(fields[i].getName());
			}
			XClassPtr superClasses[] = c.getSuperClasses();
			for(XClassPtr superClass:superClasses){
				XClass sc = superClass.getXClassNonNull(c.getVirtualMachine());
				addClassFields(sc, classes, names);
			}
		}
	}
	
	private List<Object> getValues(){
		XClass c = xClass.getXClass();
		List<Object> values = new ArrayList<Object>();
		addClassValues(c, new ArrayList<XClass>(), values);
		return values;
	}
	
	private void addClassValues(XClass c, List<XClass> classes, List<Object> values){
		if(!classes.contains(c)){
			classes.add(c);
			XField fields[] = c.getFields();
			for(int i=0; i<fields.length; i++){
				values.add(getFieldValue(fields[i]));
			}
			XClassPtr superClasses[] = c.getSuperClasses();
			for(XClassPtr superClass:superClasses){
				XClass sc = superClass.getXClassNonNull(c.getVirtualMachine());
				addClassValues(sc, classes, values);
			}
		}
	}

	@Override
	public Callable<Map<String, Object>> call() {
		return xClass;
	}
	
}
