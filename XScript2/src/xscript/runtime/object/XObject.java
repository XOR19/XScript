package xscript.runtime.object;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import xscript.runtime.XMap;
import xscript.runtime.XModifier;
import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XField;
import xscript.runtime.clazz.XInputStreamSave;
import xscript.runtime.clazz.XOutputStreamSave;
import xscript.runtime.clazz.XWrapper;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.nativemethod.XNativeFactory;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XObject extends XMap<Object, Object> implements Callable<Callable<Map<String, Object>>>{

	private static class WaitingInfo{
		XThread thread;
		int monitor;
		boolean notified;
		
		WaitingInfo(XThread thread, int monitor) {
			this.thread = thread;
			this.monitor = monitor;
		}

		WaitingInfo(XVirtualMachine virtualMachine, XInputStreamSave dis) throws IOException {
			thread = virtualMachine.getThreadProvider().getThread(dis.readInt());
			monitor = dis.readInt();
			notified = dis.readBoolean();
		}

		public void save(XOutputStreamSave dos) throws IOException {
			dos.writeInt(thread.getID());
			dos.writeInt(monitor);
			dos.writeBoolean(notified);
		}
	}
	
	private XGenericClass xClass;
	private byte[] data;
	private byte[] userData;
	private boolean isVisible;
	private int monitor;
	private XThread thread;
	private List<XThread> waitingEntry;
	private List<WaitingInfo> waiting;
	private Object nativeObject;
	private int references;
	
	protected XObject(XThread thread, XMethodExecutor methodExecutor, XGenericClass xClass){
		if(XModifier.isAbstract(xClass.getXClass().getModifier()))
			throw new XRuntimeException("Can't create Object form abstract class %s", xClass);
		if(xClass.getXClass().isArray())
			throw new XRuntimeException("%s is an array", xClass);
		this.xClass = xClass;
		data = new byte[xClass.getXClass().getObjectSize()];
		XNativeFactory factory = xClass.getXClass().getNativeFactory();
		if(factory!=null){
			nativeObject = factory.makeObject(xClass.getXClass().getVirtualMachine(), thread, methodExecutor, xClass, this);
		}
	}
	
	protected XObject(XThread thread, XMethodExecutor methodExecutor, XGenericClass xClass, int size) {
		if(XModifier.isAbstract(xClass.getXClass().getModifier()))
			throw new XRuntimeException("Can't create Object form abstract class %s", xClass);
		if(!(xClass.getXClass().isArray()))
			throw new XRuntimeException("%s isn't an array", xClass);
		this.xClass = xClass;
		data = new byte[xClass.getXClass().getObjectSize()+size*xClass.getXClass().getArrayElementSize()];
		xClass.getXClass().getLengthField().finalSet(this, size);
		XNativeFactory factory = xClass.getXClass().getNativeFactory();
		if(factory!=null){
			nativeObject = factory.makeObject(xClass.getXClass().getVirtualMachine(), thread, methodExecutor, xClass, this);
		}
	}
	
	public XObject(XVirtualMachine virtualMachine, XInputStreamSave dis) throws IOException {
		xClass = new XGenericClass(virtualMachine, dis);
		data = new byte[dis.readInt()];
		dis.read(data);
		int s = dis.readInt();
		if(s==-1){
			userData = null;
		}else{
			userData = new byte[s];
			dis.read(userData);
		}
		monitor = dis.readInt();
		if(monitor>0){
			thread = virtualMachine.getThreadProvider().getThread(dis.readInt());
			s = dis.readInt();
			if(s>0){
				waitingEntry = new ArrayList<XThread>();
				for(int i=0; i<s; i++){
					waitingEntry.add(virtualMachine.getThreadProvider().getThread(dis.readInt()));
				}
			}
		}else{
			thread = null;
			waitingEntry = null;
		}
		s = dis.readInt();
		if(s>0){
			waiting = new ArrayList<WaitingInfo>();
			for(int i=0; i<s; i++){
				waiting.add(new WaitingInfo(virtualMachine, dis));
			}
		}
		references = dis.readInt();
	}

	public void save(XOutputStreamSave dos) throws IOException {
		xClass.save(dos);
		dos.writeInt(data.length);
		dos.write(data);
		if(userData==null){
			dos.writeInt(-1);
		}else{
			dos.writeInt(userData.length);
			dos.write(userData);
		}
		dos.writeInt(monitor);
		if(monitor>0){
			dos.writeInt(thread.getID());
			if(waitingEntry==null){
				dos.writeInt(0);
			}else{
				dos.writeInt(waitingEntry.size());
				for(XThread waitingt:waitingEntry){
					dos.writeInt(waitingt.getID());
				}
			}
		}
		if(waiting==null){
			dos.writeInt(0);
		}else{
			dos.writeInt(waiting.size());
			for(WaitingInfo info:waiting){
				info.save(dos);
			}
		}
		dos.writeInt(references);
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
	
	public Object getNativeObject(){
		return nativeObject;
	}
	
	public void setNativeObject(Object nativeObject){
		this.nativeObject = nativeObject;
	}
	
	public long getArrayElement(int index){
		if(isArray()){
			int size = xClass.getXClass().getArrayElementSize();
			int i = xClass.getXClass().getObjectSize()+size*index;
			long l = 0;
			for(int j=0; j<size; j++){
				l <<= 8;
				l |= data[i+j] & 255;
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
		if(!isVisible && references<=0){
			isVisible = true;
			xClass.getXClass().markObjectObjectsVisible(this);
		}
	}
	
	public boolean isVisible(){
		return isVisible || references>0;
	}

	public void exitMonitor(XThread thread) {
		if(this.thread==thread){
			monitor--;
			if(monitor<=0){
				startWaitingThread();
			}
		}
	}

	public void wantMonitor(XThread thread) {
		if(this.thread==null){
			this.thread = thread;
			monitor = 1;
		}else if(this.thread==thread){
			monitor++;
		}else{
			if(waitingEntry==null){
				waitingEntry = new ArrayList<XThread>();
			}
			waitingEntry.add(thread);
			thread.setWaiting(true);
		}
	}
	
	public void monitorWait(XThread thread){
		if(this.thread==thread){
			if(waiting==null){
				waiting = new ArrayList<WaitingInfo>();
			}
			thread.setWaiting(true);
			waiting.add(new WaitingInfo(thread, monitor));
			startWaitingThread();
		}
	}
	
	public void monitorNotify(XThread thread){
		if(this.thread==thread){
			if(waiting!=null){
				waiting.get(0).notified = true;
			}
		}
	}
	
	public void monitorNotifyAll(XThread thread) {
		if(this.thread==thread){
			if(waiting!=null){
				for(WaitingInfo info:waiting){
					info.notified = true;
				}
			}
		}
	}
	
	private void startWaitingThread(){
		if(waiting!=null){
			Iterator<WaitingInfo> i = this.waiting.iterator();
			while(i.hasNext()){
				WaitingInfo info = i.next();
				if(info.notified){
					this.thread = info.thread;
					this.monitor = info.monitor;
					this.thread.setWaiting(false);
					i.remove();
					if(this.waiting.isEmpty())
						this.waiting = null;
					return;
				}
			}
		}
		if(waitingEntry!=null){
			this.thread = waitingEntry.remove(0);
			if(waitingEntry.isEmpty())
				waitingEntry = null;
			monitor = 1;
			this.thread.setWaiting(false);
			return;
		}
		this.thread = null;
		monitor = 0;
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
		if("+".equals(name)){
			addRef();
			return this;
		}else if("-".equals(name)){
			release();
			return this;
		}
		if(name instanceof String){
			XField field = getField((String)name);
			return getFieldValue(field);
		}else if(name instanceof Integer){
			XObjectProvider objProv = xClass.getXClass().getVirtualMachine().getObjectProvider();
			int index = XWrapper.castToInt(name);
			int primitive = getXClass().getXClass().getArrayPrimitive();
			return XWrapper.getJavaObject(objProv, primitive, getArrayElement(index));
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
		if(split==-1){
			return xClass.getXClass().getField(name);
		}else{
			XClass c = xClass.getXClass().getVirtualMachine().getClassProvider().getXClass(name.substring(0, split));
			return c.getField(name.substring(split+1));
		}
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
	
	public void addRef(){
		references++;
	}
	
	public void release(){
		references--;
		if(references<0)
			references = 0;
	}
	
}
