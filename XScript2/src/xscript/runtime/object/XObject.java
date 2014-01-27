package xscript.runtime.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import xscript.runtime.XMap;
import xscript.runtime.XModifier;
import xscript.runtime.XRuntimeException;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XField;
import xscript.runtime.clazz.XWrapper;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.threads.XThread;

public class XObject extends XMap<Object, Object> implements Callable<Callable<Map<String, Object>>>{

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
	public String toString() {
		return getXClass().toString();
	}

	@Override
	public boolean containsKey(Object name) {
		if(name instanceof String)
			return getField((String)name)!=null;
		return false;
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
	
	protected List<Object> getKeys(){
		XClass c = xClass.getXClass();
		List<Object> names = new ArrayList<Object>();
		addClassFields(c, new ArrayList<XClass>(), names);
		return names;
	}
	
	private static void addClassFields(XClass c, List<XClass> classes, List<Object> names){
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
	
	protected List<Object> getValues(){
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
