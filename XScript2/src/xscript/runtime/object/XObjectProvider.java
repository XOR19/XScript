package xscript.runtime.object;

import java.io.IOException;

import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XField;
import xscript.runtime.clazz.XInputStreamSave;
import xscript.runtime.clazz.XOutputStreamSave;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XObjectProvider {

	private XVirtualMachine virtualMachine;
	private XObject[] objects;
	
	public XObjectProvider(XVirtualMachine virtualMachine, int memSize){
		this.virtualMachine = virtualMachine;
		objects = new XObject[memSize];
	}
	
	public XObjectProvider(XVirtualMachine virtualMachine, XInputStreamSave dis) throws IOException {
		this.virtualMachine = virtualMachine;
		objects = new XObject[dis.readInt()];
		for(int i=0; i<objects.length; i++){
			if(dis.readBoolean()){
				objects[i] = new XObject(virtualMachine, dis);
			}
		}
	}

	public void save(XOutputStreamSave dos) throws IOException {
		gc();
		dos.writeInt(objects.length);
		for(int i=0; i<objects.length; i++){
			if(objects[i]==null){
				dos.writeBoolean(false);
			}else{
				dos.writeBoolean(true);
				objects[i].save(dos);
			}
		}
	}
	
	public XObject getObject(long pointer) {
		if(pointer<=0 || pointer>objects.length)
			return null;
		return objects[(int) pointer-1];
	}
	
	public long getPointer(XObject object){
		if(object==null)
			return 0;
		for(int i=0; i<objects.length; i++){
			if(objects[i] == object)
				return i+1;
		}
		return 0;
	}
	
	public void gc() {
		long startTest1 = System.nanoTime();
		for(int i=0; i<objects.length; i++){
			if(objects[i]!=null){
				objects[i].resetVisibility();
			}
		}
		virtualMachine.getClassProvider().markVisible();
		virtualMachine.getThreadProvider().markVisible();
		int n=0;
		for(int i=0; i<objects.length; i++){
			if(objects[i]!=null && !objects[i].isVisible()){
				n++;
				objects[i] = null;
			}
		}
		long endTest1 = System.nanoTime();
		System.out.println("gc deleted "+n+" objects in "+(endTest1-startTest1)+"ns");
	}
	
	private long getNextFreePointer(){
		for(int i=0; i<objects.length; i++){
			if(objects[i]==null){
				return i+1;
			}
		}
		gc();
		for(int i=0; i<objects.length; i++){
			if(objects[i]==null){
				return i+1;
			}
		}
		return 0;
	}
	
	protected long createPointerForObject(XObject xObject) {
		long pointer = getNextFreePointer();
		if(pointer==0)
			throw new XRuntimeException("Out of memory");
		objects[(int) (pointer-1)] = xObject;
		return pointer;
	}
	
	public long createObject(XThread thread, XMethodExecutor methodExecutor, XGenericClass xClass){
		return createPointerForObject(new XObject(thread, methodExecutor, xClass));
	}
	
	public long createArray(XThread thread, XMethodExecutor methodExecutor, XGenericClass xClass, int size){
		return createPointerForObject(new XObject(thread, methodExecutor, xClass, size));
	}

	public long createString(XThread thread, XMethodExecutor methodExecutor, String value) {
		XClass sc = virtualMachine.getClassProvider().getXClass("xscript.lang.String");
		XGenericClass gc = new XGenericClass(sc);
		long s = createObject(thread, methodExecutor, gc);
		XGenericClass ac = new XGenericClass(virtualMachine.getClassProvider().getXClass("xscript.lang.ArrayChar"));
		long v = createArray(thread, methodExecutor, ac, value.length());
		if(getObject(s)==null){
			s = createObject(thread, methodExecutor, gc);
		}
		XObject array = getObject(v);
		for(int i=0; i<value.length(); i++){
			array.setArrayElement(i, value.charAt(i));
		}
		XField fvalue = sc.getField("value");
		fvalue.finalSet(getObject(s), v);
		return s;
	}

	public String getString(XObject obj) {
		if(obj==null)
			return "null";
		XClass sc = virtualMachine.getClassProvider().getXClass("xscript.lang.String");
		if(obj.getXClass().getXClass()==sc){
			XField fvalue = sc.getField("value");
			long v = fvalue.get(obj);
			XObject array = getObject(v);
			String out = "";
			int size = array.getArrayLength();
			for(int i=0; i<size; i++){
				out += (char)array.getArrayElement(i);
			}
			return out;
		}
		return obj.getXClass()+":"+Long.toHexString(getPointer(obj));
	}
	
	
	
}
