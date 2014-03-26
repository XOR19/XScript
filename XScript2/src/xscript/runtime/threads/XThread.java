package xscript.runtime.threads;

import java.io.IOException;
import java.util.List;

import xscript.runtime.XModifier;
import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XInputStreamSave;
import xscript.runtime.clazz.XOutputStreamSave;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.method.XMethod;
import xscript.runtime.object.XObject;
import xscript.runtime.object.XObjectProvider;


public class XThread {

	private XVirtualMachine virtualMachine;
	protected String name;
	private XMethodExecutor methodExecutor;
	private long[] result;
	private long exception;
	protected long sleepTime=0;
	protected long lastRunTime;
	protected boolean waiting;
	private byte[] userData;
	private int id;
	private long object;
	
	protected XThread(XVirtualMachine virtualMachine, String name, XMethod method, XGenericClass[] generics, long[] params, int id){
		this.virtualMachine = virtualMachine;
		this.name = name;
		this.id = id;
		call(method, generics, params);
	}
	
	protected XThread(XVirtualMachine virtualMachine, String name, XMethod method, XGenericClass[] generics, long[] params, long threadObject, int id){
		this.virtualMachine = virtualMachine;
		this.name = name;
		this.id = id;
		this.object = threadObject;
		setupThread(object);
		call(method, generics, params);
	}

	public XThread(XVirtualMachine virtualMachine, XInputStreamSave dis) throws IOException {
		this.virtualMachine = virtualMachine;
		name = dis.readUTF();
		id = dis.readInt();
		methodExecutor = new XMethodExecutor(virtualMachine, dis, null);
		int i = dis.readByte();
		if(i==-1){
			result = null;
		}else{
			result = new long[]{dis.readLong(), i};
		}
		exception = dis.readLong();
		sleepTime = dis.readLong();
		lastRunTime = virtualMachine.getTimer().getMilliSeconds()-dis.readLong();
		waiting = dis.readBoolean();
		int s = dis.readInt();
		if(s==-1){
			userData = null;
		}else{
			userData = new byte[s];
			dis.read(userData);
		}
		object = dis.readLong();
	}

	public void save(XOutputStreamSave dos) throws IOException {
		dos.writeUTF(name);
		dos.writeInt(id);
		methodExecutor.save(dos);
		if(result==null){
			dos.writeByte(-1);
		}else{
			dos.writeByte((int) result[1]);
			dos.writeLong(result[0]);
		}
		dos.writeLong(exception);
		dos.writeLong(sleepTime);
		dos.writeLong(virtualMachine.getTimer().getMilliSeconds()-lastRunTime);
		dos.writeBoolean(waiting);
		if(userData==null){
			dos.writeInt(-1);
		}else{
			dos.writeInt(userData.length);
			dos.write(userData);
		}
		dos.writeLong(object);
	}
	
	public void setUserData(byte[] userData){
		this.userData = userData;
	}
	
	public byte[] getUserData(){
		return userData;
	}
	
	protected void run(int numInstructions){
		if(methodExecutor!=null){
			XThreadProvider threadProvider = virtualMachine.getThreadProvider();
			while(numInstructions-->0 && methodExecutor!=null && getThreadState()==XThreadState.RUNNING && !threadProvider.isNewImportantInterrupt() && !threadProvider.isNewInterrupt()){
				XInstruction instruction = methodExecutor.getNextInstruction();
				while(instruction==null){
					XMethodExecutor oldMethodExecutor = methodExecutor.getParent();
					if(methodExecutor.getMethod().getReturnTypePrimitive() != XPrimitive.VOID){
						result = methodExecutor.getReturn();
						if(oldMethodExecutor!=null){
							oldMethodExecutor.push(result[0], (int) result[1]);
						}
					}
					methodExecutor.exitMethod(this);
					methodExecutor = oldMethodExecutor;
					if(oldMethodExecutor==null)
						return;
					instruction = methodExecutor.getNextInstruction();
				}
				if(instruction!=null){
					try{
						instruction.resolve(virtualMachine, this, methodExecutor);
						if(threadProvider.isNewImportantInterrupt()){
							methodExecutor.setProgramPointerBack();
							return;
						}
						instruction.run(virtualMachine, this, methodExecutor);
					}catch(XRuntimeException e){
						exception = 1;
						e.printStackTrace();
						System.err.println(methodExecutor.getMethod()+":"+methodExecutor.getLine()+":"+instruction.getSource());
						return;
					}catch(Throwable e){
						exception = 1;
						e.printStackTrace();
						System.err.println(methodExecutor.getMethod()+":"+methodExecutor.getLine()+":"+instruction.getSource());
						return;
					}
					XObject obj = virtualMachine.getObjectProvider().getObject(exception);
					if(obj!=null){
						while(methodExecutor!=null && methodExecutor.jumpToExceptionHandlePoint(obj.getXClass(), exception)){
							methodExecutor = methodExecutor.getParent();
						}
					}
				}
			}
		}
	}
	
	public long getResult(){
		if(getThreadState()==XThreadState.TERMINATED){
			return result[0];
		}
		return 0;
	}
	
	public void call(XMethod xMethod, XGenericClass[] generics, long[] params) {
		if(XModifier.isNative(xMethod.getModifier())){
			virtualMachine.getNativeProvider().call(this, methodExecutor, xMethod, generics, params);
		}else{
			methodExecutor = new XMethodExecutor(methodExecutor, this, xMethod, generics, params);
		}
	}
	
	public void callConstructor(XMethod xMethod, XGenericClass[] generics, long[] params, List<XClass> initializizedClasses) {
		if(XModifier.isNative(xMethod.getModifier())){
			virtualMachine.getNativeProvider().call(this, methodExecutor, xMethod, generics, params);
		}else{
			methodExecutor = new XMethodExecutor(methodExecutor, this, xMethod, generics, params, initializizedClasses);
		}
	}

	protected void markVisible(){
		XObject obj = virtualMachine.getObjectProvider().getObject(exception);
		if(obj!=null){
			obj.markVisible();
		}
		if(result!=null){
			if(result[1]==XPrimitive.OBJECT){
				obj = virtualMachine.getObjectProvider().getObject(result[0]);
				if(obj!=null){
					obj.markVisible();
				}
			}
		}
		if(object!=0){
			obj = virtualMachine.getObjectProvider().getObject(object);
			if(obj!=null){
				obj.markVisible();
			}
		}
		methodExecutor.markVisible();
	}
	
	public void setException(long exception){
		this.exception = exception;
	}
	
	public long getException(){
		return exception;
	}
	
	protected void sleepUpdate(){
		long currentTime = virtualMachine.getTimer().getMilliSeconds();
		sleepTime -= currentTime-lastRunTime;
		lastRunTime = currentTime;
		if(sleepTime<=0)
			sleepTime = 0;
	}
	
	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}
	
	public void sleep(long ms){
		sleepTime = ms;
	}
	
	public XThreadState getThreadState() {
		if(methodExecutor==null){
			if(exception==0)
				return XThreadState.TERMINATED;
			return XThreadState.ERRORED;
		}
		if(sleepTime>0)
			return XThreadState.SLEEPING;
		if(waiting)
			return XThreadState.WAITING;
		return XThreadState.RUNNING;
	}
	
	public int getID(){
		return id;
	}
	
	public String getName(){
		return name;
	}

	public long getThreadObject() {
		if(object==0){
			object = createThreadObject();
		}
		return object;
	}

	private long createThreadObject() {
		XObjectProvider objectProvider = virtualMachine.getObjectProvider();
		long pointer = objectProvider.createObject(this, null, new XGenericClass(virtualMachine.getClassProvider().getXClass("xscript.lang.Thread")));
		setupThread(pointer);
		return pointer;
	}
	
	private void setupThread(long pointer){
		XObject object = virtualMachine.getObjectProvider().getObject(pointer);
		XClass c = virtualMachine.getClassProvider().getXClass("xscript.lang.Thread");
		c.getField("threadID").finalSet(object, this.id);
	}

	@Override
	public String toString() {
		return getName()+":"+getThreadState();
	}
	
}
