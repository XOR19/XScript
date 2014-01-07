package xscript.runtime.object;

import java.util.ArrayList;
import java.util.List;

import xscript.runtime.XModifier;
import xscript.runtime.XRuntimeException;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.threads.XThread;

public class XObject {

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
		data = new byte[xClass.getXClass().getObjectSize()+size];
		xClass.getXClass().getLengthField().set(this, size);
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
	
}
